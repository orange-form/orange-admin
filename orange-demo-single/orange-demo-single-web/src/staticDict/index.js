/**
 * 常量字典数据
 */
import Vue from 'vue';

class DictionaryBase extends Map {
  constructor (name, dataList, keyId = 'id', symbolId = 'symbol') {
    super();
    this.showName = name;
    this.setList(dataList, keyId, symbolId);
  }

  setList (dataList, keyId = 'id', symbolId = 'symbol') {
    this.clear();
    if (Array.isArray(dataList)) {
      dataList.forEach((item) => {
        this.set(item[keyId], item);
        if (item[symbolId] != null) {
          Object.defineProperty(this, item[symbolId], {
            get: function () {
              return item[keyId];
            }
          });
        }
      });
    }
  }

  getList (valueId = 'name', parentIdKey = 'parentId', filter) {
    let temp = [];
    this.forEach((value, key) => {
      let obj = {
        id: key,
        name: (typeof value === 'string') ? value : value[valueId],
        parentId: value[parentIdKey]
      };
      if (typeof filter !== 'function' || filter(obj)) {
        temp.push(obj);
      }
    });

    return temp;
  }

  getValue (id, valueId = 'name') {
    // 如果id为boolean类型，则自动转换为0和1
    if (typeof id === 'boolean') {
      id = id ? 1 : 0;
    }
    return (this.get(id) || {})[valueId];
  }
}

const SysUserStatus = new DictionaryBase('用户状态', [
  {
    id: 0,
    name: '正常状态',
    symbol: 'NORMAL'
  },
  {
    id: 1,
    name: '锁定状态',
    symbol: 'LOCKED'
  }
]);
Vue.prototype.SysUserStatus = SysUserStatus;

const SysUserType = new DictionaryBase('用户类型', [
  {
    id: 0,
    name: '管理员',
    symbol: 'ADMIN'
  },
  {
    id: 1,
    name: '系统操作员',
    symbol: 'SYSTEM'
  },
  {
    id: 2,
    name: '普通操作员',
    symbol: 'OPERATOR'
  }
]);
Vue.prototype.SysUserType = SysUserType;

const Subject = new DictionaryBase('学科', [
  {
    id: 0,
    name: '语文',
    symbol: 'CHINESE'
  },
  {
    id: 1,
    name: '数学',
    symbol: 'MATCH'
  },
  {
    id: 2,
    name: '英语',
    symbol: 'ENGLISH'
  }
]);
Vue.prototype.Subject = Subject;

const StudentActionType = new DictionaryBase('学生行为', [
  {
    id: 0,
    name: '充值',
    symbol: 'RECHARGE'
  },
  {
    id: 1,
    name: '购课',
    symbol: 'BUY_COURSE'
  },
  {
    id: 2,
    name: '上课签到',
    symbol: 'SIGNIN_COURSE'
  },
  {
    id: 3,
    name: '上课签退',
    symbol: 'SIGNOUT_COURSE'
  },
  {
    id: 4,
    name: '看视频课',
    symbol: 'WATCH_VIDEO'
  },
  {
    id: 5,
    name: '做作业',
    symbol: 'DO_PAPER'
  },
  {
    id: 6,
    name: '刷题',
    symbol: 'REFRESH_EXERCISE'
  },
  {
    id: 7,
    name: '献花',
    symbol: 'PRESENT_FLOWER'
  },
  {
    id: 8,
    name: '购买视频',
    symbol: 'BUY_VIDEO_COURSE'
  },
  {
    id: 9,
    name: '购买鲜花',
    symbol: 'BUY_FLOWER'
  },
  {
    id: 10,
    name: '购买作业',
    symbol: 'BUY_PAPER'
  }
]);
Vue.prototype.StudentActionType = StudentActionType;

const DeviceType = new DictionaryBase('设备类型', [
  {
    id: 0,
    name: 'iOS',
    symbol: 'IOS'
  },
  {
    id: 1,
    name: 'Android',
    symbol: 'ANDROID'
  },
  {
    id: 2,
    name: 'PC',
    symbol: 'PC'
  }
]);
Vue.prototype.DeviceType = DeviceType;

const Gender = new DictionaryBase('性别', [
  {
    id: 1,
    name: '男',
    symbol: 'MALE'
  },
  {
    id: 0,
    name: '女',
    symbol: 'FEMALE'
  }
]);
Vue.prototype.Gender = Gender;

const ExpLevel = new DictionaryBase('经验等级', [
  {
    id: 0,
    name: '初级学员',
    symbol: 'LOWER'
  },
  {
    id: 1,
    name: '中级学员',
    symbol: 'MIDDLE'
  },
  {
    id: 2,
    name: '高级学员',
    symbol: 'HIGH'
  }
]);
Vue.prototype.ExpLevel = ExpLevel;

const StudentStatus = new DictionaryBase('学生状态', [
  {
    id: 0,
    name: '正常',
    symbol: 'NORMAL'
  },
  {
    id: 1,
    name: '锁定',
    symbol: 'LOCKED'
  },
  {
    id: 2,
    name: '注销',
    symbol: 'DELETED'
  }
]);
Vue.prototype.StudentStatus = StudentStatus;

const ClassStatus = new DictionaryBase('班级状态', [
  {
    id: 1,
    name: '正常',
    symbol: 'NORAML'
  },
  {
    id: -1,
    name: '解散',
    symbol: 'DELETED'
  }
]);
Vue.prototype.ClassStatus = ClassStatus;

const ClassLevel = new DictionaryBase('班级级别', [
  {
    id: 0,
    name: '初级班',
    symbol: 'NORMAL'
  },
  {
    id: 1,
    name: '中级班',
    symbol: 'MIDDLE'
  },
  {
    id: 2,
    name: '高级班',
    symbol: 'HIGH'
  }
]);
Vue.prototype.ClassLevel = ClassLevel;

const CourseDifficult = new DictionaryBase('课程难度', [
  {
    id: 0,
    name: '容易',
    symbol: 'NORMAL'
  },
  {
    id: 1,
    name: '普通',
    symbol: 'MIDDLE'
  },
  {
    id: 2,
    name: '困难',
    symbol: 'HIGH'
  }
]);
Vue.prototype.CourseDifficult = CourseDifficult;

const SysPermModuleType = new DictionaryBase('权限分组类型', [
  {
    id: 0,
    name: '分组模块',
    symbol: 'GROUP'
  }, {
    id: 1,
    name: '接口模块',
    symbol: 'CONTROLLER'
  }
]);
Vue.prototype.SysPermModuleType = SysPermModuleType;

const SysPermCodeType = new DictionaryBase('权限字类型', [{
  id: 0,
  name: '表单',
  symbol: 'FORM'
}, {
  id: 1,
  name: '片段',
  symbol: 'FRAGMENT'
}, {
  id: 2,
  name: '操作',
  symbol: 'OPERATION'
}]);
Vue.prototype.SysPermCodeType = SysPermCodeType;

const SysMenuType = new DictionaryBase('菜单类型', [
  {
    id: 0,
    name: '目录',
    symbol: 'DIRECTORY'
  },
  {
    id: 1,
    name: '表单',
    symbol: 'MENU'
  },
  {
    id: 2,
    name: '片段',
    symbol: 'FRAGMENT'
  },
  {
    id: 3,
    name: '按钮',
    symbol: 'BUTTON'
  }
]);
Vue.prototype.SysMenuType = SysMenuType;
export {
  DictionaryBase,
  SysUserStatus,
  SysUserType,
  Subject,
  StudentActionType,
  DeviceType,
  Gender,
  ExpLevel,
  StudentStatus,
  ClassStatus,
  ClassLevel,
  CourseDifficult,
  SysPermModuleType,
  SysPermCodeType,
  SysMenuType
}
