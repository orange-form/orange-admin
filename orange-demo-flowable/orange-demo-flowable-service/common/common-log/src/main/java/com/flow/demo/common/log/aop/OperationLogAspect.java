package com.flow.demo.common.log.aop;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flow.demo.common.core.constant.ApplicationConstant;
import com.flow.demo.common.core.object.ResponseResult;
import com.flow.demo.common.core.object.TokenData;
import com.flow.demo.common.core.util.ContextUtil;
import com.flow.demo.common.core.util.IpUtil;
import com.flow.demo.common.core.util.MyCommonUtil;
import com.flow.demo.common.log.annotation.OperationLog;
import com.flow.demo.common.log.config.OperationLogProperties;
import com.flow.demo.common.log.model.SysOperationLog;
import com.flow.demo.common.log.model.constant.SysOperationLogType;
import com.flow.demo.common.log.service.SysOperationLogService;
import com.flow.demo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 操作日志记录处理AOP对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class OperationLogAspect {

    @Value("${spring.application.name}")
    private String serviceName;
    @Autowired
    private SysOperationLogService operationLogService;
    @Autowired
    private OperationLogProperties properties;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 错误信息、请求参数和应答结果字符串的最大长度。
     */
    private final static int MAX_LENGTH = 2000;

    /**
     * 所有controller方法。
     */
    @Pointcut("execution(public * com.flow.demo..controller..*(..))")
    public void operationLogPointCut() {
        // 空注释，避免sonar警告
    }

    @Around("operationLogPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 计时。
        long start = System.currentTimeMillis();
        HttpServletRequest request = ContextUtil.getHttpRequest();
        HttpServletResponse response = ContextUtil.getHttpResponse();
        String traceId = this.getTraceId(request);
        request.setAttribute(ApplicationConstant.HTTP_HEADER_TRACE_ID, traceId);
        // 将流水号通过应答头返回给前端，便于问题精确定位。
        response.setHeader(ApplicationConstant.HTTP_HEADER_TRACE_ID, traceId);
        MDC.put(ApplicationConstant.HTTP_HEADER_TRACE_ID, traceId);
        TokenData tokenData = TokenData.takeFromRequest();
        // 为log4j2日志设定变量，使日志可以输出更多有价值的信息。
        if (tokenData != null) {
            MDC.put("sessionId", tokenData.getSessionId());
            MDC.put("userId", tokenData.getUserId().toString());
        }
        String[] parameterNames = this.getParameterNames(joinPoint);
        Object[] args = joinPoint.getArgs();
        JSONObject jsonArgs = new JSONObject();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (this.isNormalArgs(arg)) {
                String parameterName = parameterNames[i];
                jsonArgs.put(parameterName, arg);
            }
        }
        String params = jsonArgs.toJSONString();
        SysOperationLog operationLog = null;
        OperationLog operationLogAnnotation = null;
        boolean saveOperationLog = properties.isEnabled();
        if (saveOperationLog) {
            operationLogAnnotation = getOperationLogAnnotation(joinPoint);
            saveOperationLog = (operationLogAnnotation != null);
        }
        if (saveOperationLog) {
            operationLog = this.buildSysOperationLog(operationLogAnnotation, joinPoint, params, traceId, tokenData);
        }
        Object result;
        log.info("开始请求，url={}, reqData={}", request.getRequestURI(), params);
        try {
            // 调用原来的方法
            result = joinPoint.proceed();
            String respData = result == null ? "null" : JSON.toJSONString(result);
            Long elapse = System.currentTimeMillis() - start;
            if (saveOperationLog) {
                this.operationLogPostProcess(operationLogAnnotation, respData, operationLog, result);
            }
            log.info("请求完成, url={}，elapse={}ms, respData={}", request.getRequestURI(), elapse, respData);
        } catch (Exception e) {
            if (saveOperationLog) {
                operationLog.setSuccess(false);
                operationLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, MAX_LENGTH));
            }
            log.error("请求报错，url={}, reqData={}, error={}", request.getRequestURI(), params, e.getMessage());
            throw e;
        } finally {
            if (saveOperationLog) {
                operationLog.setElapse(System.currentTimeMillis() - start);
                operationLogService.saveNewAsync(operationLog);
            }
        }
        return result;
    }

    private SysOperationLog buildSysOperationLog(
            OperationLog operationLogAnnotation,
            ProceedingJoinPoint joinPoint,
            String params,
            String traceId,
            TokenData tokenData) {
        HttpServletRequest request = ContextUtil.getHttpRequest();
        SysOperationLog operationLog = new SysOperationLog();
        operationLog.setLogId(idGenerator.nextLongId());
        operationLog.setTraceId(traceId);
        operationLog.setDescription(operationLogAnnotation.description());
        operationLog.setOperationType(operationLogAnnotation.type());
        operationLog.setServiceName(this.serviceName);
        operationLog.setApiClass(joinPoint.getTarget().getClass().getName());
        operationLog.setApiMethod(operationLog.getApiClass() + "." + joinPoint.getSignature().getName());
        operationLog.setRequestMethod(request.getMethod());
        operationLog.setRequestUrl(request.getRequestURI());
        if (tokenData != null) {
            operationLog.setRequestIp(tokenData.getLoginIp());
        } else {
            operationLog.setRequestIp(IpUtil.getRemoteIpAddress(request));
        }
        operationLog.setOperationTime(new Date());
        if (params != null) {
            if (params.length() <= MAX_LENGTH) {
                operationLog.setRequestArguments(params);
            } else {
                operationLog.setRequestArguments(StringUtils.substring(params, 0, MAX_LENGTH));
            }
        }
        if (tokenData != null) {
            // 对于非多租户系统，该值为空可以忽略。
            operationLog.setTenantId(tokenData.getTenantId());
            operationLog.setSessionId(tokenData.getSessionId());
            operationLog.setOperatorId(tokenData.getUserId());
            operationLog.setOperatorName(tokenData.getLoginName());
        }
        return operationLog;
    }

    private void operationLogPostProcess(
            OperationLog operationLogAnnotation, String respData, SysOperationLog operationLog, Object result) {
        if (operationLogAnnotation.saveResponse()) {
            if (respData.length() <= MAX_LENGTH) {
                operationLog.setResponseResult(respData);
            } else {
                operationLog.setResponseResult(StringUtils.substring(respData, 0, MAX_LENGTH));
            }
        }
        // 处理大部分返回ResponseResult的接口。
        if (!(result instanceof ResponseResult)) {
            if (ContextUtil.hasRequestContext()) {
                operationLog.setSuccess(ContextUtil.getHttpResponse().getStatus() == HttpServletResponse.SC_OK);
            }
            return;
        }
        ResponseResult<?> responseResult = (ResponseResult<?>) result;
        operationLog.setSuccess(responseResult.isSuccess());
        if (!responseResult.isSuccess()) {
            operationLog.setErrorMsg(responseResult.getErrorMessage());
        }
        if (operationLog.getOperationType().equals(SysOperationLogType.LOGIN)) {
            // 对于登录操作，由于在调用登录方法之前，没有可用的TokenData。
            // 因此如果登录成功，可再次通过TokenData.takeFromRequest()获取TokenData。
            if (operationLog.getSuccess()) {
                // 这里为了保证LoginController.doLogin方法，一定将TokenData存入Request.Attribute之中，
                // 我们将不做空值判断，一旦出错，开发者可在调试时立刻发现异常，并根据这里的注释进行修复。
                TokenData tokenData = TokenData.takeFromRequest();
                // 对于非多租户系统，为了保证代码一致性，仍可保留对tenantId的赋值代码。
                operationLog.setTenantId(tokenData.getTenantId());
                operationLog.setSessionId(tokenData.getSessionId());
                operationLog.setOperatorId(tokenData.getUserId());
                operationLog.setOperatorName(tokenData.getLoginName());
            } else {
                HttpServletRequest request = ContextUtil.getHttpRequest();
                // 登录操作需要特殊处理，无论是登录成功还是失败，都要记录operator_name字段。
                operationLog.setOperatorName(request.getParameter("loginName"));
            }
        }
    }

    private String[] getParameterNames(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        return methodSignature.getParameterNames();
    }

    private OperationLog getOperationLogAnnotation(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        return method.getAnnotation(OperationLog.class);
    }

    private String getTraceId(HttpServletRequest request) {
        // 获取请求流水号。
        // 对于微服务系统，为了保证traceId在全调用链的唯一性，因此在网关的过滤器中创建了该值。
        String traceId = request.getHeader(ApplicationConstant.HTTP_HEADER_TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = MyCommonUtil.generateUuid();
        }
        return traceId;
    }

    private boolean isNormalArgs(Object o) {
        if (o instanceof List) {
            List<?> list = (List<?>) o;
            if (CollUtil.isNotEmpty(list)) {
                return !(list.get(0) instanceof MultipartFile);
            }
        }
        return !(o instanceof HttpServletRequest)
                && !(o instanceof HttpServletResponse)
                && !(o instanceof MultipartFile);
    }
}
