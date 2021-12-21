<#macro doIndent level><#if level != 0><#list 0..(level-1) as i>  </#list></#if></#macro>

<#macro generateControllerRequest service apiClass indentLevel>
<@doIndent indentLevel/>"name": "${apiClass.name}",
<@doIndent indentLevel/>"item": [
                            <#list apiClass.methodList as apiMethod>
<@doIndent indentLevel/>	{
<@doIndent indentLevel/>		"name": "${apiMethod.name}",
                                <#if apiMethod.loginUrl>
<@doIndent indentLevel/>        "event": [
<@doIndent indentLevel/>            {
<@doIndent indentLevel/>                "listen": "test",
<@doIndent indentLevel/>                "script": {
<@doIndent indentLevel/>                    "id": "${freemarkerUtils.generateGuid()}",
<@doIndent indentLevel/>                    "type": "text/javascript",
<@doIndent indentLevel/>                    "exec": [
<@doIndent indentLevel/>                        "pm.test(\"登录操作\", function () {",
<@doIndent indentLevel/>                        "    var jsonData = pm.response.json();",
<@doIndent indentLevel/>                        "    var token = jsonData.data.tokenData;",
<@doIndent indentLevel/>                        "    pm.environment.set(\"token\", token);",
<@doIndent indentLevel/>                        "    console.log(\"login token \" + token);",
<@doIndent indentLevel/>                       "});",
<@doIndent indentLevel/>                       ""
<@doIndent indentLevel/>                    ]
<@doIndent indentLevel/>                }
<@doIndent indentLevel/>            },
<@doIndent indentLevel/>            {
<@doIndent indentLevel/>                "listen": "prerequest",
<@doIndent indentLevel/>                "script": {
<@doIndent indentLevel/>                    "id": "${freemarkerUtils.generateGuid()}",
<@doIndent indentLevel/>                    "type": "text/javascript",
<@doIndent indentLevel/>                    "exec": [
<@doIndent indentLevel/>                        ""
<@doIndent indentLevel/>                    ]
<@doIndent indentLevel/>                }
<@doIndent indentLevel/>            }
<@doIndent indentLevel/>        ],
                                </#if>
<@doIndent indentLevel/>		"request": {
<@doIndent indentLevel/>			"method": "${apiMethod.httpMethod}",
                                    <#if apiMethod.loginUrl>
<@doIndent indentLevel/>            "header": [],
                                    <#else>
<@doIndent indentLevel/>			"header": [
<@doIndent indentLevel/>				{
<@doIndent indentLevel/>					"key": "Authorization",
<@doIndent indentLevel/>					"value": "{{token}}",
<@doIndent indentLevel/>					"type": "text"
<@doIndent indentLevel/>				}
<@doIndent indentLevel/>			],
                                    </#if>
<@doIndent indentLevel/>			"url": {
<@doIndent indentLevel/>				"raw": "http://{{host}}:${service.port}/${apiMethod.requestPath}",
<@doIndent indentLevel/>				"protocol": "http",
<@doIndent indentLevel/>				"host": [
<@doIndent indentLevel/>					"{{host}}"
<@doIndent indentLevel/>				],
<@doIndent indentLevel/>				"port": "${service.port}",
<@doIndent indentLevel/>				"path": [
                                            <#list apiMethod.pathList as path>
<@doIndent indentLevel/>					"${path}"<#if path_has_next>,</#if>
                                            </#list>
<@doIndent indentLevel/>				]<#if apiMethod.queryParamArgumentList?size gt 0>,</#if>
                                        <#if apiMethod.queryParamArgumentList?size gt 0>
<@doIndent indentLevel/>				"query": [
                                            <#list apiMethod.queryParamArgumentList as apiArgument>
                                            <#if apiArgument.modelData??>
                                            <#list apiArgument.modelData.tableFieldList as apiField>
<@doIndent indentLevel/>					{
<@doIndent indentLevel/>						"key": "${apiField.name}",
<@doIndent indentLevel/>						"value": ""
<@doIndent indentLevel/>					}<#if apiArgument_has_next || apiField_has_next>,</#if>
                                            </#list>
                                            <#else>
<@doIndent indentLevel/>					{
<@doIndent indentLevel/>						"key": "${apiArgument.name}",
<@doIndent indentLevel/>						"value": ""
<@doIndent indentLevel/>					}<#if apiArgument_has_next>,</#if>
                                            </#if>
                                            </#list>
<@doIndent indentLevel/>				]
                                        </#if>
<@doIndent indentLevel/>			}<#if (apiMethod.httpMethod == "POST" && apiMethod.jsonParamArgumentList?size gt 0) || apiMethod.uploadParamArgumentList?size gt 0>,</#if>
                                    <#if apiMethod.uploadParamArgumentList?size gt 0>
<@doIndent indentLevel/>			"body": {
<@doIndent indentLevel/>				"mode": "formdata",
<@doIndent indentLevel/>				"formdata": [
                                    	    <#list apiMethod.uploadParamArgumentList as apiArgument>
<@doIndent indentLevel/>					{
<@doIndent indentLevel/>						"key": "${apiArgument.name}",
<@doIndent indentLevel/>						"type": "file",
<@doIndent indentLevel/>						"src": []
<@doIndent indentLevel/>					}<#if apiArgument_has_next>,</#if>
                                    	    </#list>
<@doIndent indentLevel/>				]
<@doIndent indentLevel/>			}<#if apiMethod.httpMethod == "POST" && apiMethod.jsonParamArgumentList?size gt 0>,</#if>
                                    </#if><#-- apiMethod.uploadParamArgumentList?size gt 0 -->
                                    <#if apiMethod.httpMethod == "POST" && apiMethod.jsonParamArgumentList?size gt 0>
<@doIndent indentLevel/>			"body": {
<@doIndent indentLevel/>				"mode": "raw",
                                        <#if !apiMethod.loginUrl>
<@doIndent indentLevel/>				"raw": "{\n<#list apiMethod.jsonParamArgumentList as apiArgument><#if apiArgument.modelData??><#if apiArgument.collectionParam>\t\"${apiArgument.name}\" : [\n\t\t{\n<#list apiArgument.modelData.fieldList as apiField><#if apiMethod.listUrl>\t\t\t\"${apiField.name}\" : \"\"<#if apiField_has_next>,</#if>\n<#else>\t\t\t\"${apiField.name}\" : \"<#if apiField.typeName == "Integer" || apiField.typeName == "Long">0</#if>\"<#if apiField_has_next>,</#if>\n</#if><#-- apiMethod.listUrl --></#list>\t\t}\n\t]<#if apiArgument_has_next>,</#if>\n<#else><#-- apiArgument.collectionParam -->\t\"${apiArgument.name}\" : {\n<#list apiArgument.modelData.fieldList as apiField><#if apiMethod.listUrl>\t\t\"${apiField.name}\" : \"\"<#if apiField_has_next>,</#if>\n<#else>\t\t\"${apiField.name}\" : \"<#if apiField.typeName == "Integer" || apiField.typeName == "Long">0</#if>\"<#if apiField_has_next>,</#if>\n</#if><#-- apiMethod.listUrl --></#list>\t}<#if apiArgument_has_next>,</#if>\n</#if><#-- apiArgument.collectionParam --><#elseif apiArgument.orderParam>\t\"${apiArgument.name}\" : [\n\t\t{\n\t\t\t\"fieldName\" : \"\",\n\t\t\t\"asc\" : \"true\"\n\t\t}\n\t]<#if apiArgument_has_next>,</#if>\n<#elseif apiArgument.groupParam>\t\"${apiArgument.name}\" : [\n\t\t{\n\t\t\t\"fieldName\" : \"\",\n\t\t\t\"aliasName\" : \"\",\n\t\t\t\"dateAggregateBy\" : \"\"\n\t\t}\n\t]<#if apiArgument_has_next>,</#if>\n<#elseif apiArgument.pageParam>\t\"${apiArgument.name}\" : {\n\t\t\"pageNum\": \"1\",\n\t\t\"pageSize\": \"10\"\n\t}<#if apiArgument_has_next>,</#if>\n<#elseif apiArgument.queryParam || apiArgument.aggregationParam>\t\"${apiArgument.name}\" : {\n\t}<#if apiArgument_has_next>,</#if>\n<#else><#if apiArgument.collectionParam>\t\"${apiArgument.name}\" : [  ]<#if apiArgument_has_next>,</#if>\n<#else>\t\"${apiArgument.name}\" : \"\"<#if apiArgument_has_next>,</#if>\n</#if></#if><#-- apiArgument.modelData?? --></#list><#-- apiMethod.jsonParamArgumentList?size gt 0 -->}\n",
                                        <#else>
<@doIndent indentLevel/>                "raw": "{\n    \"loginName\":\"admin\",\n    \"password\":\"IP3ccke3GhH45iGHB5qP9p7iZw6xUyj28Ju10rnBiPKOI35sc%2BjI7%2FdsjOkHWMfUwGYGfz8ik31HC2Ruk%2Fhkd9f6RPULTHj7VpFdNdde2P9M4mQQnFBAiPM7VT9iW3RyCtPlJexQ3nAiA09OqG%2F0sIf1kcyveSrulxembARDbDo%3D\"\n}",
                                        </#if>
<@doIndent indentLevel/>				"options": {
<@doIndent indentLevel/>					"raw": {
<@doIndent indentLevel/>						"language": "json"
<@doIndent indentLevel/>					}
<@doIndent indentLevel/>				}
<@doIndent indentLevel/>			}
                                    </#if>
<@doIndent indentLevel/>		},
<@doIndent indentLevel/>		"response": []
<@doIndent indentLevel/>	}<#if apiMethod_has_next>,</#if>
                            </#list><#-- apiClass.methodList as apiMethod -->
<@doIndent indentLevel/>],
<@doIndent indentLevel/>"protocolProfileBehavior": {},
<@doIndent indentLevel/>"_postman_isSubFolder": true
</#macro>
