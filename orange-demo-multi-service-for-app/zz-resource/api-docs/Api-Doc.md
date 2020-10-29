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

## 用户权限管理服务
### SysUserController
#### add
- **URI:** /admin/upms/sysUser/add
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "sysUser" : {
        "loginName" : "String | true | 登录用户名。",
        "password" : "String | true | 用户密码。",
        "showName" : "String | true | 用户显示名称。",
        "userType" : "Integer | true | 用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)。",
        "headImageUrl" : "String | false | 用户头像的Url。",
        "userStatus" : "Integer | true | 用户状态(0: 正常 1: 锁定)。",
        "createUserId" : "Long | false | 创建用户Id。",
        "createUsername" : "String | false | 创建用户名。",
        "createTime" : "Date | false | 创建时间。",
        "updateTime" : "Date | false | 更新时间。"
    }
}
```
#### update
- **URI:** /admin/upms/sysUser/update
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "sysUser" : {
        "userId" : "Long | true | 用户Id。",
        "loginName" : "String | true | 登录用户名。",
        "password" : "String | true | 用户密码。",
        "showName" : "String | true | 用户显示名称。",
        "userType" : "Integer | true | 用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)。",
        "headImageUrl" : "String | false | 用户头像的Url。",
        "userStatus" : "Integer | true | 用户状态(0: 正常 1: 锁定)。",
        "createUserId" : "Long | false | 创建用户Id。",
        "createUsername" : "String | false | 创建用户名。",
        "createTime" : "Date | false | 创建时间。",
        "updateTime" : "Date | false | 更新时间。"
    }
}
```
#### resetPassword
- **URI:** /admin/upms/sysUser/resetPassword
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "userId" : "Long | true | 指定用户主键Id。"
}
```
#### delete
- **URI:** /admin/upms/sysUser/delete
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "userId" : "Long | true | 删除对象主键Id。"
}
```
#### list
- **URI:** /admin/upms/sysUser/list
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "sysUserFilter" : {
        "loginName" : "String | false | 登录用户名。",
        "showName" : "String | false | 用户显示名称。",
        "userStatus" : "Integer | false | 用户状态(0: 正常 1: 锁定)。",
        "createTimeStart" : "String | false | createTime 范围过滤起始值(>=)。",
        "createTimeEnd" : "String | false | createTime 范围过滤结束值(<=)。"
    },
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### view
- **URI:** /admin/upms/sysUser/view
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
userId|Long|true|指定对象主键Id。

## 班级服务
### AreaCodeController
#### listDictAreaCode
- **URI:** /admin/CourseClass/areaCode/listDictAreaCode
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
#### listDictAreaCodeByParentId
- **URI:** /admin/CourseClass/areaCode/listDictAreaCodeByParentId
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
parentId|Long|false|上级行政区划Id。
#### view
- **URI:** /admin/CourseClass/areaCode/view
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
areaId|Long|true|行政区划Id。
### CourseController
#### add
- **URI:** /admin/CourseClass/course/add
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "course" : {
        "courseName" : "String | true | 课程名称。",
        "price" : "BigDecimal | true | 课程价格。",
        "description" : "String | false | 课程描述。",
        "difficulty" : "Integer | true | 课程难度(0: 容易 1: 普通 2: 很难)。",
        "gradeId" : "Integer | true | 年级Id。",
        "subjectId" : "Integer | true | 学科Id。",
        "classHour" : "Integer | true | 课时数量。",
        "pictureUrl" : "String | true | 多张课程图片地址。",
        "createUserId" : "Long | false | 创建用户Id。",
        "createTime" : "Date | false | 创建时间。",
        "updateTime" : "Date | false | 最后修改时间。"
    }
}
```
#### update
- **URI:** /admin/CourseClass/course/update
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "course" : {
        "courseId" : "Long | true | 主键Id。",
        "courseName" : "String | true | 课程名称。",
        "price" : "BigDecimal | true | 课程价格。",
        "description" : "String | false | 课程描述。",
        "difficulty" : "Integer | true | 课程难度(0: 容易 1: 普通 2: 很难)。",
        "gradeId" : "Integer | true | 年级Id。",
        "subjectId" : "Integer | true | 学科Id。",
        "classHour" : "Integer | true | 课时数量。",
        "pictureUrl" : "String | true | 多张课程图片地址。",
        "createUserId" : "Long | false | 创建用户Id。",
        "createTime" : "Date | false | 创建时间。",
        "updateTime" : "Date | false | 最后修改时间。"
    }
}
```
#### delete
- **URI:** /admin/CourseClass/course/delete
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "courseId" : "Long | true | 删除对象主键Id。"
}
```
#### list
- **URI:** /admin/CourseClass/course/list
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "courseFilter" : {
        "courseName" : "String | false | 课程名称。",
        "difficulty" : "Integer | false | 课程难度(0: 容易 1: 普通 2: 很难)。",
        "gradeId" : "Integer | false | 年级Id。",
        "subjectId" : "Integer | false | 学科Id。",
        "priceStart" : "BigDecimal | false | price 范围过滤起始值(>=)。",
        "priceEnd" : "BigDecimal | false | price 范围过滤结束值(<=)。",
        "classHourStart" : "Integer | false | classHour 范围过滤起始值(>=)。",
        "classHourEnd" : "Integer | false | classHour 范围过滤结束值(<=)。",
        "updateTimeStart" : "String | false | updateTime 范围过滤起始值(>=)。",
        "updateTimeEnd" : "String | false | updateTime 范围过滤结束值(<=)。"
    },
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### view
- **URI:** /admin/CourseClass/course/view
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
courseId|Long|true|指定对象主键Id。
#### download
- **URI:** /admin/CourseClass/course/download
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
courseId|Long|false|附件所在记录的主键Id。
fieldName|String|true|附件所属的字段名。
filename|String|true|文件名。如果没有提供该参数，就从当前记录的指定字段中读取。
asImage|Boolean|true|下载文件是否为图片。
#### upload
- **URI:** /admin/CourseClass/course/upload
- **Type:** POST
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
fieldName|String|true|上传文件名。
asImage|Boolean|true|是否作为图片上传。如果是图片，今后下载的时候无需权限验证。否则就是附件上传，下载时需要权限验证。
uploadFile|File|true|上传文件对象。
#### listDictCourse
- **URI:** /admin/CourseClass/course/listDictCourse
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
courseId|Long|false|主键Id。
courseName|String|false|课程名称。
price|BigDecimal|false|课程价格。
description|String|false|课程描述。
difficulty|Integer|false|课程难度(0: 容易 1: 普通 2: 很难)。
gradeId|Integer|false|年级Id。
subjectId|Integer|false|学科Id。
classHour|Integer|false|课时数量。
pictureUrl|String|false|多张课程图片地址。
createUserId|Long|false|创建用户Id。
createTime|Date|false|创建时间。
updateTime|Date|false|最后修改时间。
### GradeController
#### add
- **URI:** /admin/CourseClass/grade/add
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "grade" : {
        "gradeName" : "String | true | 年级名称。"
    }
}
```
#### update
- **URI:** /admin/CourseClass/grade/update
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "grade" : {
        "gradeId" : "Integer | true | 主键Id。",
        "gradeName" : "String | true | 年级名称。"
    }
}
```
#### delete
- **URI:** /admin/CourseClass/grade/delete
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "gradeId" : "Integer | true | 删除对象主键Id。"
}
```
#### view
- **URI:** /admin/CourseClass/grade/view
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
gradeId|Integer|true|指定对象主键Id。
#### listDictGrade
- **URI:** /admin/CourseClass/grade/listDictGrade
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
#### reloadCachedData
- **URI:** /admin/CourseClass/grade/reloadCachedData
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
### SchoolInfoController
#### add
- **URI:** /admin/CourseClass/schoolInfo/add
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "schoolInfo" : {
        "schoolName" : "String | true | 学校名称。",
        "provinceId" : "Long | true | 所在省Id。",
        "cityId" : "Long | true | 所在城市Id。"
    }
}
```
#### update
- **URI:** /admin/CourseClass/schoolInfo/update
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "schoolInfo" : {
        "schoolId" : "Long | true | 学校Id。",
        "schoolName" : "String | true | 学校名称。",
        "provinceId" : "Long | true | 所在省Id。",
        "cityId" : "Long | true | 所在城市Id。"
    }
}
```
#### delete
- **URI:** /admin/CourseClass/schoolInfo/delete
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "schoolId" : "Long | true | 删除对象主键Id。"
}
```
#### list
- **URI:** /admin/CourseClass/schoolInfo/list
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "schoolInfoFilter" : {
        "schoolName" : "String | false | 学校名称。",
        "provinceId" : "Long | false | 所在省Id。",
        "cityId" : "Long | false | 所在城市Id。"
    },
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### view
- **URI:** /admin/CourseClass/schoolInfo/view
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
schoolId|Long|true|指定对象主键Id。
#### listDictSchoolInfo
- **URI:** /admin/CourseClass/schoolInfo/listDictSchoolInfo
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
schoolId|Long|false|学校Id。
schoolName|String|false|学校名称。
provinceId|Long|false|所在省Id。
cityId|Long|false|所在城市Id。
### StudentClassController
#### add
- **URI:** /admin/CourseClass/studentClass/add
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "studentClass" : {
        "className" : "String | true | 班级名称。",
        "schoolId" : "Long | true | 学校Id。",
        "leaderId" : "Long | true | 学生班长Id。",
        "finishClassHour" : "Integer | true | 已完成课时数量。",
        "classLevel" : "Integer | true | 班级级别(0: 初级班 1: 培优班 2: 冲刺提分班 3: 竞赛班)。",
        "createUserId" : "Long | false | 创建用户。",
        "createTime" : "Date | false | 班级创建时间。"
    }
}
```
#### update
- **URI:** /admin/CourseClass/studentClass/update
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "studentClass" : {
        "classId" : "Long | true | 班级Id。",
        "className" : "String | true | 班级名称。",
        "schoolId" : "Long | true | 学校Id。",
        "leaderId" : "Long | true | 学生班长Id。",
        "finishClassHour" : "Integer | true | 已完成课时数量。",
        "classLevel" : "Integer | true | 班级级别(0: 初级班 1: 培优班 2: 冲刺提分班 3: 竞赛班)。",
        "createUserId" : "Long | false | 创建用户。",
        "createTime" : "Date | false | 班级创建时间。"
    }
}
```
#### delete
- **URI:** /admin/CourseClass/studentClass/delete
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "classId" : "Long | true | 删除对象主键Id。"
}
```
#### list
- **URI:** /admin/CourseClass/studentClass/list
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "studentClassFilter" : {
        "className" : "String | false | 班级名称。",
        "schoolId" : "Long | false | 学校Id。",
        "classLevel" : "Integer | false | 班级级别(0: 初级班 1: 培优班 2: 冲刺提分班 3: 竞赛班)。"
    },
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### view
- **URI:** /admin/CourseClass/studentClass/view
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
classId|Long|true|指定对象主键Id。
#### listNotInClassCourse
- **URI:** /admin/CourseClass/studentClass/listNotInClassCourse
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "classId" : "Long | true | 主表主键Id。",
    "courseFilter" : {
        "courseName" : "String | false | 课程名称。",
        "difficulty" : "Integer | false | 课程难度(0: 容易 1: 普通 2: 很难)。",
        "gradeId" : "Integer | false | 年级Id。",
        "subjectId" : "Integer | false | 学科Id。",
        "priceStart" : "BigDecimal | false | price 范围过滤起始值(>=)。",
        "priceEnd" : "BigDecimal | false | price 范围过滤结束值(<=)。",
        "classHourStart" : "Integer | false | classHour 范围过滤起始值(>=)。",
        "classHourEnd" : "Integer | false | classHour 范围过滤结束值(<=)。",
        "updateTimeStart" : "String | false | updateTime 范围过滤起始值(>=)。",
        "updateTimeEnd" : "String | false | updateTime 范围过滤结束值(<=)。"
    },
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### listClassCourse
- **URI:** /admin/CourseClass/studentClass/listClassCourse
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "classId" : "Long | true | 主表主键Id。",
    "courseFilter" : {
        "courseName" : "String | false | 课程名称。",
        "difficulty" : "Integer | false | 课程难度(0: 容易 1: 普通 2: 很难)。",
        "gradeId" : "Integer | false | 年级Id。",
        "subjectId" : "Integer | false | 学科Id。",
        "priceStart" : "BigDecimal | false | price 范围过滤起始值(>=)。",
        "priceEnd" : "BigDecimal | false | price 范围过滤结束值(<=)。",
        "classHourStart" : "Integer | false | classHour 范围过滤起始值(>=)。",
        "classHourEnd" : "Integer | false | classHour 范围过滤结束值(<=)。",
        "updateTimeStart" : "String | false | updateTime 范围过滤起始值(>=)。",
        "updateTimeEnd" : "String | false | updateTime 范围过滤结束值(<=)。"
    },
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### addClassCourse
- **URI:** /admin/CourseClass/studentClass/addClassCourse
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "classId" : "Long | true | 主表主键Id。",
    "classCourseList" : [
        {
            "classId" : "Long | true | 班级Id。",
            "courseId" : "Long | true | 课程Id。",
            "courseOrder" : "Integer | true | 课程顺序(数值越小越靠前)。"
        }
    ]
}
```
#### updateClassCourse
- **URI:** /admin/CourseClass/studentClass/updateClassCourse
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "classCourse" : {
        "classId" : "Long | true | 班级Id。",
        "courseId" : "Long | true | 课程Id。",
        "courseOrder" : "Integer | true | 课程顺序(数值越小越靠前)。"
    }
}
```
#### viewClassCourse
- **URI:** /admin/CourseClass/studentClass/viewClassCourse
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
classId|Long|true|主表主键Id。
courseId|Long|true|从表主键Id。
#### deleteClassCourse
- **URI:** /admin/CourseClass/studentClass/deleteClassCourse
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "classId" : "Long | true | 主表主键Id。",
    "courseId" : "Long | true | 关联表主键Id。"
}
```
#### listNotInClassStudent
- **URI:** /admin/CourseClass/studentClass/listNotInClassStudent
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "classId" : "Long | true | 主表主键Id。",
    "studentFilter" : {
        "provinceId" : "Long | false | 所在省份Id。",
        "cityId" : "Long | false | 所在城市Id。",
        "districtId" : "Long | false | 区县Id。",
        "gradeId" : "Integer | false | 年级Id。",
        "schoolId" : "Long | false | 校区Id。",
        "status" : "Integer | false | 学生状态 (0: 正常 1: 锁定 2: 注销)。",
        "birthdayStart" : "String | false | birthday 范围过滤起始值(>=)。",
        "birthdayEnd" : "String | false | birthday 范围过滤结束值(<=)。",
        "registerTimeStart" : "String | false | registerTime 范围过滤起始值(>=)。",
        "registerTimeEnd" : "String | false | registerTime 范围过滤结束值(<=)。",
        "searchString" : "String | false | 模糊搜索字符串。"
    },
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### listClassStudent
- **URI:** /admin/CourseClass/studentClass/listClassStudent
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "classId" : "Long | true | 主表主键Id。",
    "studentFilter" : {
        "provinceId" : "Long | false | 所在省份Id。",
        "cityId" : "Long | false | 所在城市Id。",
        "districtId" : "Long | false | 区县Id。",
        "gradeId" : "Integer | false | 年级Id。",
        "schoolId" : "Long | false | 校区Id。",
        "status" : "Integer | false | 学生状态 (0: 正常 1: 锁定 2: 注销)。",
        "birthdayStart" : "String | false | birthday 范围过滤起始值(>=)。",
        "birthdayEnd" : "String | false | birthday 范围过滤结束值(<=)。",
        "registerTimeStart" : "String | false | registerTime 范围过滤起始值(>=)。",
        "registerTimeEnd" : "String | false | registerTime 范围过滤结束值(<=)。",
        "searchString" : "String | false | 模糊搜索字符串。"
    },
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### addClassStudent
- **URI:** /admin/CourseClass/studentClass/addClassStudent
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "classId" : "Long | true | 主表主键Id。",
    "classStudentList" : [
        {
            "classId" : "Long | true | 班级Id。",
            "studentId" : "Long | true | 学生Id。"
        }
    ]
}
```
#### deleteClassStudent
- **URI:** /admin/CourseClass/studentClass/deleteClassStudent
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "classId" : "Long | true | 主表主键Id。",
    "studentId" : "Long | true | 关联表主键Id。"
}
```
### StudentController
#### add
- **URI:** /admin/CourseClass/student/add
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "student" : {
        "loginMobile" : "String | true | 登录手机。",
        "studentName" : "String | true | 学生姓名。",
        "provinceId" : "Long | true | 所在省份Id。",
        "cityId" : "Long | true | 所在城市Id。",
        "districtId" : "Long | true | 区县Id。",
        "gender" : "Integer | true | 学生性别 (0: 女生 1: 男生)。",
        "birthday" : "Date | true | 生日。",
        "experienceLevel" : "Integer | true | 经验等级 (0: 初级 1: 中级 2: 高级 3: 资深)。",
        "totalCoin" : "Integer | true | 总共充值学币数量。",
        "leftCoin" : "Integer | true | 可用学币数量。",
        "gradeId" : "Integer | true | 年级Id。",
        "schoolId" : "Long | true | 校区Id。",
        "registerTime" : "Date | false | 注册时间。",
        "status" : "Integer | true | 学生状态 (0: 正常 1: 锁定 2: 注销)。"
    }
}
```
#### update
- **URI:** /admin/CourseClass/student/update
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "student" : {
        "studentId" : "Long | true | 学生Id。",
        "loginMobile" : "String | true | 登录手机。",
        "studentName" : "String | true | 学生姓名。",
        "provinceId" : "Long | true | 所在省份Id。",
        "cityId" : "Long | true | 所在城市Id。",
        "districtId" : "Long | true | 区县Id。",
        "gender" : "Integer | true | 学生性别 (0: 女生 1: 男生)。",
        "birthday" : "Date | true | 生日。",
        "experienceLevel" : "Integer | true | 经验等级 (0: 初级 1: 中级 2: 高级 3: 资深)。",
        "totalCoin" : "Integer | true | 总共充值学币数量。",
        "leftCoin" : "Integer | true | 可用学币数量。",
        "gradeId" : "Integer | true | 年级Id。",
        "schoolId" : "Long | true | 校区Id。",
        "registerTime" : "Date | false | 注册时间。",
        "status" : "Integer | true | 学生状态 (0: 正常 1: 锁定 2: 注销)。"
    }
}
```
#### delete
- **URI:** /admin/CourseClass/student/delete
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "studentId" : "Long | true | 删除对象主键Id。"
}
```
#### list
- **URI:** /admin/CourseClass/student/list
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "studentFilter" : {
        "provinceId" : "Long | false | 所在省份Id。",
        "cityId" : "Long | false | 所在城市Id。",
        "districtId" : "Long | false | 区县Id。",
        "gradeId" : "Integer | false | 年级Id。",
        "schoolId" : "Long | false | 校区Id。",
        "status" : "Integer | false | 学生状态 (0: 正常 1: 锁定 2: 注销)。",
        "birthdayStart" : "String | false | birthday 范围过滤起始值(>=)。",
        "birthdayEnd" : "String | false | birthday 范围过滤结束值(<=)。",
        "registerTimeStart" : "String | false | registerTime 范围过滤起始值(>=)。",
        "registerTimeEnd" : "String | false | registerTime 范围过滤结束值(<=)。",
        "searchString" : "String | false | 模糊搜索字符串。"
    },
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### view
- **URI:** /admin/CourseClass/student/view
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
studentId|Long|true|指定对象主键Id。
#### listDictStudent
- **URI:** /admin/CourseClass/student/listDictStudent
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
studentId|Long|false|学生Id。
loginMobile|String|false|登录手机。
studentName|String|false|学生姓名。
provinceId|Long|false|所在省份Id。
cityId|Long|false|所在城市Id。
districtId|Long|false|区县Id。
gender|Integer|false|学生性别 (0: 女生 1: 男生)。
birthday|Date|false|生日。
experienceLevel|Integer|false|经验等级 (0: 初级 1: 中级 2: 高级 3: 资深)。
totalCoin|Integer|false|总共充值学币数量。
leftCoin|Integer|false|可用学币数量。
gradeId|Integer|false|年级Id。
schoolId|Long|false|校区Id。
registerTime|Date|false|注册时间。
status|Integer|false|学生状态 (0: 正常 1: 锁定 2: 注销)。

## 统计服务
### CourseTransStatsController
#### list
- **URI:** /admin/stats/courseTransStats/list
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "courseTransStatsFilter" : {
        "subjectId" : "Integer | false | 科目Id。",
        "gradeId" : "Integer | false | 年级Id。",
        "statsDateStart" : "String | false | statsDate 范围过滤起始值(>=)。",
        "statsDateEnd" : "String | false | statsDate 范围过滤结束值(<=)。"
    },
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### listWithGroup
- **URI:** /admin/stats/courseTransStats/listWithGroup
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "courseTransStatsFilter" : {
        "subjectId" : "Integer | false | 科目Id。",
        "gradeId" : "Integer | false | 年级Id。",
        "statsDateStart" : "String | false | statsDate 范围过滤起始值(>=)。",
        "statsDateEnd" : "String | false | statsDate 范围过滤结束值(<=)。"
    },
    "groupParam" : [
        {
            "fieldName" : "String | false | 分组字段名",
            "aliasName" : "String | false | 分组字段别名",
            "dateAggregateBy" : "String | false | 是否按照日期聚合，可选项(day|month|year)"
        }
    ],
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### view
- **URI:** /admin/stats/courseTransStats/view
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
statsId|Long|true|指定对象主键Id。
### StudentActionStatsController
#### list
- **URI:** /admin/stats/studentActionStats/list
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "studentActionStatsFilter" : {
        "gradeId" : "Integer | false | 年级Id。",
        "provinceId" : "Long | false | 学生所在省Id。",
        "cityId" : "Long | false | 学生所在城市Id。",
        "statsDateStart" : "String | false | statsDate 范围过滤起始值(>=)。",
        "statsDateEnd" : "String | false | statsDate 范围过滤结束值(<=)。"
    },
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### listWithGroup
- **URI:** /admin/stats/studentActionStats/listWithGroup
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "studentActionStatsFilter" : {
        "gradeId" : "Integer | false | 年级Id。",
        "provinceId" : "Long | false | 学生所在省Id。",
        "cityId" : "Long | false | 学生所在城市Id。",
        "statsDateStart" : "String | false | statsDate 范围过滤起始值(>=)。",
        "statsDateEnd" : "String | false | statsDate 范围过滤结束值(<=)。"
    },
    "groupParam" : [
        {
            "fieldName" : "String | false | 分组字段名",
            "aliasName" : "String | false | 分组字段别名",
            "dateAggregateBy" : "String | false | 是否按照日期聚合，可选项(day|month|year)"
        }
    ],
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### view
- **URI:** /admin/stats/studentActionStats/view
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
statsId|Long|true|指定对象主键Id。
### StudentActionTransController
#### add
- **URI:** /admin/stats/studentActionTrans/add
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "studentActionTrans" : {
        "studentId" : "Long | true | 学生Id。",
        "studentName" : "String | true | 学生名称。",
        "schoolId" : "Long | true | 学生校区。",
        "gradeId" : "Integer | true | 年级Id。",
        "actionType" : "Integer | true | 行为类型(0: 充值  1: 购课 2: 上课签到 3: 上课签退 4: 看视频课 5: 做作业 6: 刷题 7: 献花)。",
        "deviceType" : "Integer | true | 设备类型(0: iOS 1: Android 2: PC)。",
        "watchVideoSeconds" : "Integer | false | 看视频秒数。",
        "flowerCount" : "Integer | false | 购买献花数量。",
        "paperCount" : "Integer | false | 购买作业数量。",
        "videoCount" : "Integer | false | 购买视频数量。",
        "courseCount" : "Integer | false | 购买课程数量。",
        "coinCount" : "Integer | false | 充值学币数量。",
        "exerciseCorrectFlag" : "Integer | false | 做题是否正确标记。",
        "createTime" : "Date | true | 发生时间。"
    }
}
```
#### update
- **URI:** /admin/stats/studentActionTrans/update
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "studentActionTrans" : {
        "transId" : "Long | true | 主键Id。",
        "studentId" : "Long | true | 学生Id。",
        "studentName" : "String | true | 学生名称。",
        "schoolId" : "Long | true | 学生校区。",
        "gradeId" : "Integer | true | 年级Id。",
        "actionType" : "Integer | true | 行为类型(0: 充值  1: 购课 2: 上课签到 3: 上课签退 4: 看视频课 5: 做作业 6: 刷题 7: 献花)。",
        "deviceType" : "Integer | true | 设备类型(0: iOS 1: Android 2: PC)。",
        "watchVideoSeconds" : "Integer | false | 看视频秒数。",
        "flowerCount" : "Integer | false | 购买献花数量。",
        "paperCount" : "Integer | false | 购买作业数量。",
        "videoCount" : "Integer | false | 购买视频数量。",
        "courseCount" : "Integer | false | 购买课程数量。",
        "coinCount" : "Integer | false | 充值学币数量。",
        "exerciseCorrectFlag" : "Integer | false | 做题是否正确标记。",
        "createTime" : "Date | true | 发生时间。"
    }
}
```
#### delete
- **URI:** /admin/stats/studentActionTrans/delete
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "transId" : "Long | true | 删除对象主键Id。"
}
```
#### list
- **URI:** /admin/stats/studentActionTrans/list
- **Type:** POST
- **Content-Type:** application/json; chartset=utf-8
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Body:**
``` json
{
    "studentActionTransFilter" : {
        "studentId" : "Long | false | 学生Id。",
        "schoolId" : "Long | false | 学生校区。",
        "gradeId" : "Integer | false | 年级Id。",
        "actionType" : "Integer | false | 行为类型(0: 充值  1: 购课 2: 上课签到 3: 上课签退 4: 看视频课 5: 做作业 6: 刷题 7: 献花)。",
        "deviceType" : "Integer | false | 设备类型(0: iOS 1: Android 2: PC)。",
        "createTimeStart" : "String | false | createTime 范围过滤起始值(>=)。",
        "createTimeEnd" : "String | false | createTime 范围过滤结束值(<=)。"
    },
    "orderParam" : [
        {
            "fieldName" : "String | false | 排序字段名",
            "asc" : "Boolean | false | 是否升序"
        }
    ],
    "pageParam" : {
        "pageNum": "Integer | false | 分页页号",
        "pageSize": "Integer | false | 每页数据量"
    }
}
```
#### view
- **URI:** /admin/stats/studentActionTrans/view
- **Type:** GET
- **Content-Type:** multipart/form-data
- **Request-Headers:**
Name|Type|Description
--|--|--
Authorization|String|身份验证的Token
- **Request-Parameters:**
Parameter|Type|Required|Description
--|--|--|--
transId|Long|true|指定对象主键Id。
