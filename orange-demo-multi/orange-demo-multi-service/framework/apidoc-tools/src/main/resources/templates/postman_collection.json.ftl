<#import  "postman_common.ftl" as Common>
{
	"info": {
		"_postman_id": "92b51dc5-3611-49ac-8d94-a0718dba5bf1",
		"name": "${project.projectName}",
		"schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
	},
	"item": [
		<#list project.serviceList as service>
		{
			"name": "${service.serviceName}",
			"item": [
				<#if service.groupedClassMap?size gt 0>
				<#list service.groupedClassMap?keys as groupName>
				<#assign groupedClassList=service.groupedClassMap[groupName] />
				{
					"name": "${groupName}",
					"item": [
						<#list groupedClassList as apiClass>
						{
							<@Common.generateControllerRequest service apiClass 7/>
						}<#if apiClass_has_next>,</#if>
						</#list>
					],
					"protocolProfileBehavior": {},
					"_postman_isSubFolder": true
				}<#if groupName_has_next>,</#if>
				</#list>
				</#if>
				<#list service.defaultGroupClassSet as apiClass>
				{
					<@Common.generateControllerRequest service apiClass 5/>
				}<#if apiClass_has_next>,</#if>
				</#list>
			],
			"protocolProfileBehavior": {},
			"_postman_isSubFolder": true
		}<#if service_has_next>,</#if>
		</#list><#-- project.serviceList as service -->
	],
	"protocolProfileBehavior": {}
}
