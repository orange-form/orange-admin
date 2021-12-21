/**
 * 常量字典数据
 */
import Vue from 'vue';

class DictionaryBase extends Map {
  constructor (dataList, keyId = 'id', symbolId = 'symbol') {
    super();
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
    return (this.get(id) || {})[valueId];
  }
}

const UserStatus = new DictionaryBase([
  {
    id: 0,
    name: '正常',
    symbol: 'USED'
  },
  {
    id: 1,
    name: '锁定',
    symbol: 'LOCKED'
  }
]);
Vue.prototype.UserStatus = UserStatus;

const UserType = new DictionaryBase([
  {
    id: 0,
    name: '管理员',
    symbol: 'ADMIN'
  },
  {
    id: 1,
    name: '普通操作员',
    symbol: 'NORMAL'
  }
]);
Vue.prototype.UserType = UserType;

export {
  DictionaryBase,
  UserStatus,
  UserType
}
