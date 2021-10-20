package com.flow.demo.apidoc.tools.codeparser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.flow.demo.common.core.object.Tuple2;
import com.flow.demo.apidoc.tools.exception.ApiCodeConfigParseException;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 解析项目中的接口信息，以及关联的Model、Dto和Mapper，主要用于生成接口文档。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class ApiCodeParser {

    private static final String PATH_SEPERATOR = "/";
    private static final String REQUEST_MAPPING = "RequestMapping";
    private static final String FULL_REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    private static final String GET_MAPPING = "GetMapping";
    private static final String FULL_GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
    private static final String POST_MAPPING = "PostMapping";
    private static final String FULL_POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
    private static final String VALUE_PROP = "value";
    private static final String REQUIRED_PROP = "required";
    private static final String DELETED_COLUMN = "DeletedFlagColumn";

    /**
     * 忽略微服务间标准调用接口的导出。
     */
    private static final Set<String> IGNORED_API_METHOD_SET = new HashSet<>(8);

    static {
        IGNORED_API_METHOD_SET.add("listByIds");
        IGNORED_API_METHOD_SET.add("getById");
        IGNORED_API_METHOD_SET.add("existIds");
        IGNORED_API_METHOD_SET.add("existId");
        IGNORED_API_METHOD_SET.add("deleteById");
        IGNORED_API_METHOD_SET.add("deleteBy");
        IGNORED_API_METHOD_SET.add("listBy");
        IGNORED_API_METHOD_SET.add("listMapBy");
        IGNORED_API_METHOD_SET.add("listByNotInList");
        IGNORED_API_METHOD_SET.add("getBy");
        IGNORED_API_METHOD_SET.add("countBy");
        IGNORED_API_METHOD_SET.add("aggregateBy");
    }

    /**
     * 基础配置。
     */
    private ApiCodeConfig config;
    /**
     * 工程对象。
     */
    private ApiProject apiProject;
    /**
     * 项目中所有的解析后Java文件，key是Java对象的全名，如：com.flow.demo.xxxx.Student。
     */
    private final Map<String, JavaClass> projectJavaClassMap = new HashMap<>(128);
    /**
     * 存储服务数据。key为配置的serviceName。
     */
    private final Map<String, InternalServiceData> serviceDataMap = new HashMap<>(8);

    /**
     * 构造函数。
     *
     * @param config 配置对象。
     */
    public ApiCodeParser(ApiCodeConfig config) {
        this.config = config;
        // 验证配置中的数据是否正确，出现错误直接抛出运行时异常。
        this.verifyConfigData();
        // 将配置文件中所有目录相关的参数，全部规格化处理，后续的使用中不用再做处理了。
        this.normalizeConfigPath();
        for (ApiCodeConfig.ServiceConfig serviceConfig : config.getServiceList()) {
            InternalServiceData serviceData = new InternalServiceData();
            // 仅有微服务项目，需要添加服务路由路径。
            if (StrUtil.isNotBlank(serviceConfig.getServiceRequestPath())) {
                String serviceRequestPath = "";
                if (!serviceRequestPath.equals(PATH_SEPERATOR)) {
                    serviceRequestPath = normalizePath(serviceConfig.getServiceRequestPath());
                }
                serviceData.setServiceRequestPath(serviceRequestPath);
            }
            serviceDataMap.put(serviceConfig.getServiceName(), serviceData);
        }
    }

    /**
     * 执行解析操作。
     *
     * @return 解析后的工程对象。
     */
    public ApiProject doParse() throws IOException {
        // 先把工程完整编译一遍，以便工程内的Java对象的引用信息更加完整。
        this.parseProject();
        // 开始逐级推演。
        apiProject = new ApiProject();
        apiProject.setProjectName(config.getProjectName());
        apiProject.setMicroService(config.getMicroService());
        apiProject.setServiceList(new LinkedList<>());
        for (ApiCodeConfig.ServiceConfig serviceConfig : config.getServiceList()) {
            ApiService apiService = this.parseService(serviceConfig);
            apiProject.getServiceList().add(apiService);
        }
        return apiProject;
    }

    private void parseProject() throws IOException {
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        javaProjectBuilder.setEncoding(StandardCharsets.UTF_8.name());
        javaProjectBuilder.addSourceTree(new File(config.getProjectRootPath()));
        // 全部导入，便于后续解析中使用和检索。
        for (JavaClass javaClass : javaProjectBuilder.getClasses()) {
            projectJavaClassMap.put(javaClass.getFullyQualifiedName(), javaClass);
        }
    }

    private ApiService parseService(ApiCodeConfig.ServiceConfig serviceConfig) {
        InternalServiceData serviceData = serviceDataMap.get(serviceConfig.getServiceName());
        ApiService apiService = new ApiService();
        apiService.setServiceName(serviceConfig.getServiceName());
        apiService.setShowName(serviceConfig.getShowName());
        apiService.setPort(serviceConfig.getPort());
        List<ApiCodeConfig.ControllerInfo> controllerInfoList = serviceConfig.getControllerInfoList();
        // 准备解析接口文件
        for (ApiCodeConfig.ControllerInfo controllerInfo : controllerInfoList) {
            JavaProjectBuilder javaControllerBuilder = new JavaProjectBuilder();
            javaControllerBuilder.addSourceTree(new File(controllerInfo.getPath()));
            for (JavaClass javaClass : javaControllerBuilder.getClasses()) {
                if (controllerInfo.getSkipControllers() != null
                        && controllerInfo.getSkipControllers().contains(javaClass.getName())) {
                    continue;
                }
                ApiClass apiClass = this.parseApiClass(controllerInfo, javaClass.getFullyQualifiedName(), serviceData);
                if (apiClass != null) {
                    // 如果配置中，为当前ControllerInfo添加了groupName属性，
                    // 所有的生成后接口都会位于serviceName/groupName子目录，否则，都直接位于当前服务的子目录。
                    if (StrUtil.isBlank(apiClass.getGroupName())) {
                        apiService.getDefaultGroupClassSet().add(apiClass);
                    } else {
                        Set<ApiClass> groupedClassList = apiService.getGroupedClassMap()
                                .computeIfAbsent(apiClass.getGroupName(), k -> new TreeSet<>());
                        groupedClassList.add(apiClass);
                    }
                }
            }
        }
        return apiService;
    }

    private ApiClass parseApiClass(
            ApiCodeConfig.ControllerInfo controllerInfo,
            String classFullname,
            InternalServiceData serviceData) {
        // 去包含工程全部Class的Map中，找到当前ControllerClass。
        // 之所以这样做，主要是因为全工程分析controller文件，会包含更多更精确的对象关联信息。
        JavaClass controllerClass = this.projectJavaClassMap.get(classFullname);
        List<JavaAnnotation> classAnnotations = controllerClass.getAnnotations();
        boolean hasControllerAnnotation = false;
        String requestPath = "";
        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getValue();
            if (this.isRequestMapping(annotationName) && annotation.getNamedParameter(VALUE_PROP) != null) {
                requestPath = StrUtil.removeAll(
                        annotation.getNamedParameter(VALUE_PROP).toString(), "\"");
                if (requestPath.equals(PATH_SEPERATOR) || StrUtil.isBlank(requestPath)) {
                    requestPath = "";
                } else {
                    requestPath = normalizePath(requestPath);
                }
            }
            if (isController(annotationName)) {
                hasControllerAnnotation = true;
            }
        }
        if (!hasControllerAnnotation) {
            return null;
        }
        requestPath = serviceData.getServiceRequestPath() + requestPath;
        ApiClass apiClass = new ApiClass();
        apiClass.setName(controllerClass.getName());
        apiClass.setFullName(controllerClass.getFullyQualifiedName());
        apiClass.setComment(controllerClass.getComment());
        apiClass.setGroupName(controllerInfo.getGroupName());
        apiClass.setRequestPath(requestPath);
        List<ApiMethod> methodList = this.parseApiMethodList(apiClass, controllerClass);
        apiClass.setMethodList(methodList);
        return apiClass;
    }

    private boolean needToIgnore(JavaMethod method) {
        return !method.isPublic() || method.isStatic() || IGNORED_API_METHOD_SET.contains(method.getName());
    }

    private List<ApiMethod> parseApiMethodList(ApiClass apiClass, JavaClass javaClass) {
        List<ApiMethod> apiMethodList = new LinkedList<>();
        List<JavaMethod> methodList = javaClass.getMethods();
        for (JavaMethod method : methodList) {
            if (this.needToIgnore(method)) {
                continue;
            }
            List<JavaAnnotation> methodAnnotations = method.getAnnotations();
            Tuple2<String, String> result = this.parseRequestPathAndHttpMethod(methodAnnotations);
            String methodRequestPath = result.getFirst();
            String httpMethod = result.getSecond();
            if (StrUtil.isNotBlank(methodRequestPath)) {
                ApiMethod apiMethod = new ApiMethod();
                apiMethod.setName(method.getName());
                apiMethod.setComment(method.getComment());
                apiMethod.setHttpMethod(httpMethod);
                methodRequestPath = StrUtil.removeAll(methodRequestPath, "\"");
                methodRequestPath = apiClass.getRequestPath() + normalizePath(methodRequestPath);
                apiMethod.setRequestPath(methodRequestPath);
                apiMethod.setPathList(StrUtil.splitTrim(apiMethod.getRequestPath(), PATH_SEPERATOR));
                if (apiMethod.getRequestPath().contains("/listDict")) {
                    apiMethod.setListDictUrl(true);
                } else if (apiMethod.getRequestPath().endsWith("/list")
                        || apiMethod.getRequestPath().endsWith("/listWithGroup")
                        || apiMethod.getRequestPath().contains("/listNotIn")
                        || apiMethod.getRequestPath().contains("/list")) {
                    apiMethod.setListUrl(true);
                } else if (apiMethod.getRequestPath().contains("/doLogin")) {
                    apiMethod.setLoginUrl(true);
                }
                JavaClass returnClass = method.getReturns();
                if (returnClass.isVoid()) {
                    apiMethod.setReturnString("void");
                } else {
                    apiMethod.setReturnString(returnClass.getGenericValue());
                }
                apiMethodList.add(apiMethod);
                List<ApiArgument> apiArgumentList = this.parseApiMethodArgumentList(method);
                apiMethod.setArgumentList(apiArgumentList);
                this.classifyArgumentList(apiMethod, apiArgumentList);
            }
        }
        return apiMethodList;
    }

    private void classifyArgumentList(ApiMethod apiMethod, List<ApiArgument> apiArgumentList) {
        for (ApiArgument arg : apiArgumentList) {
            if (arg.getAnnotationType() == ApiArgumentAnnotationType.REQUEST_PARAM) {
                if (arg.uploadFileParam) {
                    apiMethod.getUploadParamArgumentList().add(arg);
                } else {
                    apiMethod.getQueryParamArgumentList().add(arg);
                }
            }
            if (arg.getAnnotationType() != ApiArgumentAnnotationType.REQUEST_PARAM) {
                apiMethod.getJsonParamArgumentList().add(arg);
            }
        }
    }

    private Tuple2<String, String> parseRequestPathAndHttpMethod(List<JavaAnnotation> methodAnnotations) {
        for (JavaAnnotation annotation : methodAnnotations) {
            String annotationName = annotation.getType().getValue();
            if (GET_MAPPING.equals(annotationName) || FULL_GET_MAPPING.equals(annotationName)) {
                String methodRequestPath = annotation.getNamedParameter(VALUE_PROP).toString();
                String httpMethod = "GET";
                return new Tuple2<>(methodRequestPath, httpMethod);
            }
            if (POST_MAPPING.equals(annotationName) || FULL_POST_MAPPING.equals(annotationName)) {
                String methodRequestPath = annotation.getNamedParameter(VALUE_PROP).toString();
                String httpMethod = "POST";
                return new Tuple2<>(methodRequestPath, httpMethod);
            }
        }
        return new Tuple2<>(null, null);
    }

    private List<ApiArgument> parseApiMethodArgumentList(JavaMethod javaMethod) {
        List<ApiArgument> apiArgumentList = new LinkedList<>();
        List<JavaParameter> parameterList = javaMethod.getParameters();
        if (CollUtil.isEmpty(parameterList)) {
            return apiArgumentList;
        }
        for (JavaParameter parameter : parameterList) {
            String typeName = parameter.getType().getValue();
            // 该类型的参数为Validator的验证结果对象，因此忽略。
            if ("BindingResult".equals(typeName) || this.isServletArgument(typeName)) {
                continue;
            }
            ApiArgument apiArgument = this.parseApiMethodArgument(parameter);
            apiArgumentList.add(apiArgument);
        }
        return apiArgumentList;
    }

    private String parseMethodArgmentComment(JavaParameter parameter) {
        String comment = null;
        JavaExecutable executable = parameter.getExecutable();
        List<DocletTag> tags = executable.getTagsByName("param");
        if (CollUtil.isNotEmpty(tags)) {
            for (DocletTag tag : tags) {
                if (tag.getValue().startsWith(parameter.getName())) {
                    comment = StrUtil.removePrefix(tag.getValue(), parameter.getName()).trim();
                    break;
                }
            }
        }
        return comment;
    }

    private ApiArgument parseApiMethodArgument(JavaParameter parameter) {
        String typeName = parameter.getType().getValue();
        ApiArgument apiArgument = new ApiArgument();
        ApiArgumentAnnotation argumentAnnotation =
                this.parseArgumentAnnotationTypeAndName(parameter.getAnnotations(), parameter.getName());
        apiArgument.setAnnotationType(argumentAnnotation.getType());
        apiArgument.setName(argumentAnnotation.getName());
        apiArgument.setTypeName(typeName);
        apiArgument.setFullTypeName(parameter.getFullyQualifiedName());
        if (argumentAnnotation.getType() == ApiArgumentAnnotationType.REQUEST_PARAM) {
            apiArgument.setRequired(argumentAnnotation.isRequired());
        }
        String comment = parseMethodArgmentComment(parameter);
        apiArgument.setComment(comment);
        // 文件上传字段，是必填参数。
        if ("MultipartFile".equals(typeName)) {
            apiArgument.setUploadFileParam(true);
            apiArgument.setRequired(true);
            return apiArgument;
        }
        // 对于内置类型，则无需继续处理了。所有和内置类型参数相关的处理，应该在之前完成。
        if (this.verifyAndSetBuiltinParam(apiArgument, typeName)) {
            return apiArgument;
        }
        // 判断是否为集合类型的参数。
        if (this.isCollectionType(typeName)) {
            apiArgument.setCollectionParam(true);
            if (parameter.getType() instanceof DefaultJavaParameterizedType) {
                DefaultJavaParameterizedType javaType = (DefaultJavaParameterizedType) parameter.getType();
                JavaType genericType = javaType.getActualTypeArguments().get(0);
                ApiModel apiModel = this.buildApiModelForArgument(genericType.getFullyQualifiedName());
                apiArgument.setModelData(apiModel);
                apiArgument.setFullTypeName(parameter.getGenericFullyQualifiedName());
                apiArgument.setTypeName(parameter.getGenericValue());
            }
        } else {
            ApiModel apiModel = this.buildApiModelForArgument(parameter.getFullyQualifiedName());
            apiArgument.setModelData(apiModel);
        }
        return apiArgument;
    }

    private boolean verifyAndSetBuiltinParam(ApiArgument apiArgument, String typeName) {
        if ("MyOrderParam".equals(typeName)) {
            apiArgument.setOrderParam(true);
        } else if ("MyPageParam".equals(typeName)) {
            apiArgument.setPageParam(true);
        } else if ("MyGroupParam".equals(typeName)) {
            apiArgument.setGroupParam(true);
        } else if ("MyQueryParam".equals(typeName)) {
            apiArgument.setQueryParam(true);
        } else if ("MyAggregationParam".equals(typeName)) {
            apiArgument.setAggregationParam(true);
        }
        return apiArgument.isOrderParam()
                || apiArgument.isPageParam()
                || apiArgument.isGroupParam()
                || apiArgument.isQueryParam()
                || apiArgument.isAggregationParam();
    }

    private ApiArgumentAnnotation parseArgumentAnnotationTypeAndName(
            List<JavaAnnotation> annotationList, String defaultName) {
        ApiArgumentAnnotation argumentAnnotation = new ApiArgumentAnnotation();
        argumentAnnotation.setType(ApiArgumentAnnotationType.REQUEST_PARAM);
        argumentAnnotation.setName(defaultName);
        for (JavaAnnotation annotation : annotationList) {
            String annotationName = annotation.getType().getValue();
            if ("RequestBody".equals(annotationName)) {
                argumentAnnotation.setType(ApiArgumentAnnotationType.REQUEST_BODY);
                return argumentAnnotation;
            } else if ("MyRequestBody".equals(annotationName)) {
                String annotationValue = this.getArgumentNameFromAnnotationValue(annotation, VALUE_PROP);
                argumentAnnotation.setType(ApiArgumentAnnotationType.MY_REQUEST_BODY);
                argumentAnnotation.setName(annotationValue != null ? annotationValue : defaultName);
                return argumentAnnotation;
            } else if ("RequestParam".equals(annotationName)) {
                String annotationValue = this.getArgumentNameFromAnnotationValue(annotation, VALUE_PROP);
                argumentAnnotation.setType(ApiArgumentAnnotationType.REQUEST_PARAM);
                argumentAnnotation.setName(annotationValue != null ? annotationValue : defaultName);
                String requiredValue = this.getArgumentNameFromAnnotationValue(annotation, REQUIRED_PROP);
                if (StrUtil.isNotBlank(requiredValue)) {
                    argumentAnnotation.setRequired(Boolean.parseBoolean(requiredValue));
                }
                return argumentAnnotation;
            }
        }
        // 缺省为@RequestParam
        return argumentAnnotation;
    }

    private String getArgumentNameFromAnnotationValue(JavaAnnotation annotation, String attribute) {
        Object value = annotation.getNamedParameter(attribute);
        if (value == null) {
            return null;
        }
        String paramAlias = value.toString();
        if (StrUtil.isNotBlank(paramAlias)) {
            paramAlias = StrUtil.removeAll(paramAlias, "\"");
        }
        return paramAlias;
    }

    private ApiModel buildApiModelForArgument(String fullJavaClassName) {
        // 先从当前服务内的Model中找，如果参数是Model类型的对象，微服务和单体行为一致。
        ApiModel apiModel = apiProject.getFullNameModelMap().get(fullJavaClassName);
        if (apiModel != null) {
            return apiModel;
        }
        // 判断工程全局对象映射中是否包括该对象类型，如果不包含，就直接返回了。
        JavaClass modelClass = projectJavaClassMap.get(fullJavaClassName);
        if (modelClass == null) {
            return apiModel;
        }
        // 先行解析对象中的字段。
        apiModel = parseModel(modelClass);
        apiProject.getFullNameModelMap().put(fullJavaClassName, apiModel);
        return apiModel;
    }

    private ApiModel parseModel(JavaClass javaClass) {
        ApiModel apiModel = new ApiModel();
        apiModel.setName(javaClass.getName());
        apiModel.setFullName(javaClass.getFullyQualifiedName());
        apiModel.setComment(javaClass.getComment());
        apiModel.setFieldList(new LinkedList<>());
        List<JavaField> fieldList = javaClass.getFields();
        for (JavaField field : fieldList) {
            if (field.isStatic()) {
                continue;
            }
            ApiField apiField = new ApiField();
            apiField.setName(field.getName());
            apiField.setComment(field.getComment());
            apiField.setTypeName(field.getType().getSimpleName());
            apiModel.getFieldList().add(apiField);
        }
        return apiModel;
    }

    private void verifyConfigData() {
        if (StrUtil.isBlank(config.getProjectName())) {
            throw new ApiCodeConfigParseException("ProjectName field can't be EMPTY.");
        }
        if (StrUtil.isBlank(config.getBasePackage())) {
            throw new ApiCodeConfigParseException("BasePackage field can't be EMPTY.");
        }
        if (StrUtil.isBlank(config.getProjectRootPath())) {
            throw new ApiCodeConfigParseException("ProjectRootPath field can't be EMPTY.");
        }
        if (!FileUtil.exist(config.getProjectRootPath())) {
            throw new ApiCodeConfigParseException(
                    "ProjectRootPath doesn't exist, please check ./resources/export-api-config.json as DEFAULT.");
        }
        if (config.getMicroService() == null) {
            throw new ApiCodeConfigParseException("MicroService field can't be NULL.");
        }
        if (CollUtil.isEmpty(config.getServiceList())) {
            throw new ApiCodeConfigParseException("ServiceList field can't be EMPTY.");
        }
        this.verifyServiceConfig(config.getServiceList());
    }

    private void verifyServiceConfig(List<ApiCodeConfig.ServiceConfig> serviceConfigList) {
        Set<String> serviceNameSet = new HashSet<>(8);
        Set<String> servicePathSet = new HashSet<>(8);
        for (ApiCodeConfig.ServiceConfig serviceConfig : serviceConfigList) {
            if (StrUtil.isBlank(serviceConfig.getServiceName())) {
                throw new ApiCodeConfigParseException("One of the ServiceName Field in Services List is NULL.");
            }
            String serviceName = serviceConfig.getServiceName();
            if (StrUtil.isBlank(serviceConfig.getServicePath())) {
                throw new ApiCodeConfigParseException(
                        "The ServicePath Field in Service [" + serviceName + "] is NULL.");
            }
            if (serviceNameSet.contains(serviceName)) {
                throw new ApiCodeConfigParseException("The ServiceName [" + serviceName + "] is duplicated.");
            }
            serviceNameSet.add(serviceName);
            if (servicePathSet.contains(serviceConfig.getServicePath())) {
                throw new ApiCodeConfigParseException(
                        "The ServicePath [" + serviceConfig.getServicePath() + "] is duplicated.");
            }
            servicePathSet.add(serviceConfig.getServicePath());
            if (StrUtil.isBlank(serviceConfig.getPort())) {
                throw new ApiCodeConfigParseException(
                        "The Port Field in Service [" + serviceName + "] is NULL.");
            }
            this.verifyServiceControllerConfig(serviceConfig.getControllerInfoList(), serviceName);
        }
    }

    private void verifyServiceControllerConfig(
            List<ApiCodeConfig.ControllerInfo> controllerInfoList, String serviceName) {
        if (CollUtil.isEmpty(controllerInfoList)) {
            throw new ApiCodeConfigParseException(
                    "The ControllerInfoList Field of Service [" + serviceName + "] is EMPTY");
        }
        for (ApiCodeConfig.ControllerInfo controllerInfo : controllerInfoList) {
            if (StrUtil.isBlank(controllerInfo.getPath())) {
                throw new ApiCodeConfigParseException(
                        "One of the ControllerInfo.Path Field of Service [" + serviceName + "] is EMPTY");
            }
        }
    }

    private void normalizeConfigPath() {
        config.setProjectRootPath(normalizePath(config.getProjectRootPath()));
        for (ApiCodeConfig.ServiceConfig serviceConfig : config.getServiceList()) {
            serviceConfig.setServicePath(config.getProjectRootPath() + normalizePath(serviceConfig.getServicePath()));
            for (ApiCodeConfig.ControllerInfo controllerInfo : serviceConfig.getControllerInfoList()) {
                controllerInfo.setPath(serviceConfig.getServicePath() + normalizePath(controllerInfo.getPath()));
            }
        }
    }

    private String normalizePath(String path) {
        if (!path.startsWith(PATH_SEPERATOR)) {
            path = PATH_SEPERATOR + path;
        }
        return StrUtil.removeSuffix(path, PATH_SEPERATOR);
    }

    private boolean isCollectionType(String typeName) {
        return "List".equals(typeName) || "Set".equals(typeName) || "Collection".equals(typeName);
    }

    private boolean isServletArgument(String typeName) {
        return "HttpServletResponse".equals(typeName) || "HttpServletRequest".equals(typeName);
    }

    private boolean isController(String annotationName) {
        return "Controller".equals(annotationName)
                || "org.springframework.stereotype.Controller".equals(annotationName)
                || "RestController".equals(annotationName)
                || "org.springframework.web.bind.annotation.RestController".equals(annotationName);
    }

    private boolean isRequiredColumn(String annotationName) {
        return "NotNull".equals(annotationName)
                || "javax.validation.constraints.NotNull".equals(annotationName)
                || "NotBlank".equals(annotationName)
                || "javax.validation.constraints.NotBlank".equals(annotationName)
                || "NotEmpty".equals(annotationName)
                || "javax.validation.constraints.NotEmpty".equals(annotationName);
    }

    private boolean isRequestMapping(String name) {
        return REQUEST_MAPPING.equals(name) || FULL_REQUEST_MAPPING.equals(name);
    }

    @Data
    public static class ApiProject {
        private String projectName;
        private Boolean microService;
        private List<ApiService> serviceList;
        private Map<String, ApiModel> fullNameModelMap = new HashMap<>(32);
        private Map<String, ApiModel> simpleNameModelMap = new HashMap<>(32);
    }

    @Data
    public static class ApiService {
        private String serviceName;
        private String showName;
        private String port;
        private Set<ApiClass> defaultGroupClassSet = new TreeSet<>();
        private Map<String, Set<ApiClass>> groupedClassMap = new LinkedHashMap<>();
    }

    @Data
    public static class ApiClass implements Comparable<ApiClass> {
        private String name;
        private String fullName;
        private String groupName;
        private String comment;
        private String requestPath;
        private List<ApiMethod> methodList;

        @Override
        public int compareTo(ApiClass o) {
            return this.name.compareTo(o.name);
        }
    }

    @Data
    public static class ApiMethod {
        private String name;
        private String comment;
        private String returnString;
        private String requestPath;
        private String httpMethod;
        private boolean listDictUrl = false;
        private boolean listUrl = false;
        private boolean loginUrl = false;
        private List<String> pathList = new LinkedList<>();
        private List<ApiArgument> argumentList;
        private List<ApiArgument> queryParamArgumentList = new LinkedList<>();
        private List<ApiArgument> jsonParamArgumentList = new LinkedList<>();
        private List<ApiArgument> uploadParamArgumentList = new LinkedList<>();
    }

    @Data
    public static class ApiArgument {
        private String name;
        private String typeName;
        private String fullTypeName;
        private String comment;
        private Integer annotationType;
        private boolean required = true;
        private boolean uploadFileParam = false;
        private boolean collectionParam = false;
        private boolean orderParam = false;
        private boolean pageParam = false;
        private boolean groupParam = false;
        private boolean queryParam = false;
        private boolean aggregationParam = false;
        private boolean jsonData = false;
        private ApiModel modelData;
    }

    @Data
    public static class ApiArgumentAnnotation {
        private String name;
        private Integer type;
        private boolean required = true;
    }

    @Data
    public static class ApiModel {
        private String name;
        private String fullName;
        private String comment;
        private List<ApiField> fieldList;
    }

    @Data
    public static class ApiField {
        private String name;
        private String comment;
        private String typeName;
        private boolean requiredColumn = false;
    }

    public static final class ApiArgumentAnnotationType {
        public static final int REQUEST_PARAM = 0;
        public static final int REQUEST_BODY = 1;
        public static final int MY_REQUEST_BODY = 2;

        private ApiArgumentAnnotationType() {
        }
    }

    @Data
    private static class InternalServiceData {
        private String serviceRequestPath = "";
    }
}
