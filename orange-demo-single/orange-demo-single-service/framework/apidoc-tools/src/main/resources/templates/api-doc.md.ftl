## 用户登录
### 登录接口
#### 登录
- **URI:** /admin/upms/login/doLogin
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
loginName|string|true|用户名
password|string|true|加密后的用户密码

#### 退出
- **URI:** /admin/upms/login/logout
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token

#### 修改密码
- **URI:** /admin/upms/login/changePassword
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
oldPass|string|true|加密后的原用户密码
newPass|string|true|加密后的新用户密码
<#list project.serviceList as service>

## ${service.showName}
<#list service.defaultGroupClassSet as apiClass>
### ${apiClass.name}
<#list apiClass.methodList as apiMethod>
#### ${apiMethod.name}
- **URI:** ${apiMethod.requestPath}
- **Type:** ${apiMethod.httpMethod}
- **Content-Type:** <#if apiMethod.httpMethod == "GET" || apiMethod.queryParamArgumentList?size gt 0 || apiMethod.uploadParamArgumentList?size gt 0>multipart/form-data<#else>application/json; chartset=utf-8</#if>
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
<#if apiMethod.queryParamArgumentList?size gt 0 || apiMethod.uploadParamArgumentList?size gt 0>
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
<#list apiMethod.queryParamArgumentList as apiArgument>
<#if apiArgument.modelData??>
<#list apiArgument.modelData.tableFieldList as apiField>
${apiField.name}|${apiField.typeName}|<#if apiMethod.listDictUrl>false<#else><#if apiField.requiredColumn>true<#else>false</#if></#if>|${apiField.comment}
</#list>
<#else>
${apiArgument.name}|${apiArgument.typeName}|<#if apiMethod.listDictUrl>false<#else><#if apiArgument.required>true<#else>false</#if></#if>|${apiArgument.comment}
</#if><#-- apiArgument.modelData?? -->
</#list>
</#if>
<#list apiMethod.uploadParamArgumentList as apiArgument>
${apiArgument.name}|File|true|${apiArgument.comment}
</#list>
<#if apiMethod.jsonParamArgumentList?size gt 0>
- **Request-Body:**
``` json
{
<#list apiMethod.jsonParamArgumentList as apiArgument>
<#if apiArgument.modelData??>
    <#if apiArgument.collectionParam>
    "${apiArgument.name}" : [
        {
            <#if apiMethod.listUrl>
            <#list apiArgument.modelData.filteredFieldList as apiField>
            "${apiField.name}" : "${apiField.typeName} | false | <#if apiField.name == "searchString">模糊搜索字符串。<#else>${apiField.comment}</#if>"<#if apiField_has_next>,</#if>
            </#list>
            <#else><#-- apiMethod.listUrl -->
            <#list apiArgument.modelData.tableFieldList as apiField>
            <#if !apiMethod.addUrl || !apiField.primaryKey>
            "${apiField.name}" : "${apiField.typeName} | <#if apiField.requiredColumn>true<#else>false</#if> | ${apiField.comment}"<#if apiField_has_next>,</#if>
            </#if>
            </#list>
            </#if><#-- apiMethod.listUrl -->
        }
    ]<#if apiArgument_has_next>,</#if>
    <#else><#-- apiArgument.collectionParam -->
    "${apiArgument.name}" : {
        <#if apiMethod.listUrl>
        <#list apiArgument.modelData.filteredFieldList as apiField>
        "${apiField.name}" : "${apiField.typeName} | false | <#if apiField.name == "searchString">模糊搜索字符串。<#else>${apiField.comment}</#if>"<#if apiField_has_next>,</#if>
        </#list>
        <#else><#-- apiMethod.listUrl -->
        <#list apiArgument.modelData.tableFieldList as apiField>
        <#if !apiMethod.addUrl || !apiField.primaryKey>
        "${apiField.name}" : "${apiField.typeName} | <#if apiField.requiredColumn>true<#else>false</#if> | ${apiField.comment}"<#if apiField_has_next>,</#if>
        </#if>
        </#list>
        </#if><#-- apiMethod.listUrl -->
    }<#if apiArgument_has_next>,</#if>
    </#if><#-- apiArgument.collectionParam -->
<#elseif apiArgument.orderParam>
    "${apiArgument.name}" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ]<#if apiArgument_has_next>,</#if>
<#elseif apiArgument.groupParam>
    "${apiArgument.name}" : [
        {
            "fieldName" : "String | false | 分组字段名",
            "aliasName" : "String | false | 分组字段别名",
            "dateAggregateBy" : "String | false | 是否按照日期聚合，可选项(day|month|year)"
        }
    ]<#if apiArgument_has_next>,</#if>
<#elseif apiArgument.pageParam>
    "${apiArgument.name}" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }<#if apiArgument_has_next>,</#if>
<#elseif apiArgument.queryParam || apiArgument.aggregationParam>
    ${apiArgument.name}" : {

    }<#if apiArgument_has_next>,</#if>
<#else><#-- apiArgument.modelData?? -->
    <#if apiArgument.collectionParam>
    "${apiArgument.name}" : [ "${apiArgument.typeName} | ${apiArgument.required}<#if apiArgument.comment??> | ${apiArgument.comment}</#if>" ]<#if apiArgument_has_next>,</#if>
    <#else>
    "${apiArgument.name}" : "${apiArgument.typeName} | ${apiArgument.required}<#if apiArgument.comment??> | ${apiArgument.comment}</#if>"<#if apiArgument_has_next>,</#if>
    </#if>
</#if><#-- apiArgument.modelData?? -->
</#list>
}
```
</#if>
</#list><#-- apiClass.methodList as apiMethod -->
</#list><#-- upmsClassList as apiClass -->
</#list>
