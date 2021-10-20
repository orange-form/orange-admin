import JSEncrypt from 'jsencrypt';
// eslint-disable-next-line no-unused-vars
// import Cookies from 'js-cookie';

/**
 * 列表数据转换树形数据
 * @param {Array} data 要转换的列表
 * @param {String} id 主键字段字段名
 * @param {String} pid 父字段字段名
 * @returns {Array} 转换后的树数据
 */
export function treeDataTranslate (data, id = 'id', pid = 'parentId') {
  var res = []
  var temp = {}
  for (var i = 0; i < data.length; i++) {
    temp[data[i][id]] = data[i]
  }
  for (var k = 0; k < data.length; k++) {
    if (temp[data[k][pid]] && data[k][id] !== data[k][pid]) {
      if (!temp[data[k][pid]]['children']) {
        temp[data[k][pid]]['children'] = []
      }
      if (!temp[data[k][pid]]['_level']) {
        temp[data[k][pid]]['_level'] = 1
      }
      data[k]['_level'] = temp[data[k][pid]]._level + 1
      data[k]['_parent'] = data[k][pid]
      temp[data[k][pid]]['children'].push(data[k])
    } else {
      res.push(data[k])
    }
  }

  return res
}
/**
 * 获取字符串字节长度（中文算2个字符）
 * @param {String} str 要获取长度的字符串
 */
export function getStringLength (str) {
  return str.replace(/[\u4e00-\u9fa5\uff00-\uffff]/g, '**').length
}
/**
 * 获取uuid
 */
export function getUUID () {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    return (c === 'x' ? (Math.random() * 16 | 0) : ('r&0x3' | '0x8')).toString(16)
  });
}
/**
 * 大小驼峰变换函数
 * @param name 要转换的字符串
 * @param type 转换的类型0：转换成小驼峰，1：转换成大驼峰
 */
export function nameTranslate (name, type) {
  name = name.toLowerCase();
  let nameArray = name.split('_');
  nameArray.forEach((item, index) => {
    if (index === 0) {
      name = type === 1 ? item.slice(0, 1).toUpperCase() + item.slice(1) : item;
    } else {
      name = name + item.slice(0, 1).toUpperCase() + item.slice(1);
    }
  });

  nameArray = name.split('-');
  nameArray.forEach((item, index) => {
    if (index === 0) {
      name = type === 1 ? item.slice(0, 1).toUpperCase() + item.slice(1) : item;
    } else {
      name = name + item.slice(0, 1).toUpperCase() + item.slice(1);
    }
  });
  return name;
}
/**
 * 通过id从树中获取指定的节点
 * @param {Object} node 根节点
 * @param {String|Nubmer} id 键值
 * @param {Array} list 保存查询路径
 * @param {String} idKey 主键字段名
 * @param {String} childKey 子节点字段名
 */
function findNode (node, id, list, idKey = 'id', childKey = 'children') {
  if (Array.isArray(list)) list.push(node[idKey]);
  if (node[idKey] === id) {
    return node;
  }

  if (node[childKey] != null && Array.isArray(node[childKey])) {
    for (let i = 0; i < node[childKey].length; i++) {
      let tempNode = findNode(node[childKey][i], id, list, idKey, childKey);
      if (tempNode) return tempNode;
    }
  }

  if (Array.isArray(list)) list.pop();
}
/**
 * 通过id返回从根节点到指定节点的路径
 * @param {Array} treeRoot 树根节点数组
 * @param {*} id 要查询的节点的id
 * @param {*} idKey 主键字段名
 * @param {*} childKey 子节点字段名
 */
export function findTreeNodePath (treeRoot, id, idKey = 'id', childKey = 'children') {
  let tempList = [];
  for (let i = 0; i < treeRoot.length; i++) {
    if (findNode(treeRoot[i], id, tempList, idKey, childKey)) {
      return tempList;
    }
  }

  return [];
}
/**
 * 通过id从树中查找节点
 * @param {Array} treeRoot 根节点数组
 * @param {*} id 要查找的节点的id
 * @param {*} idKey 主键字段名
 * @param {*} childKey 子节点字段名
 */
export function findTreeNode (treeRoot, id, idKey = 'id', childKey = 'children') {
  for (let i = 0; i < treeRoot.length; i++) {
    let tempNode = findNode(treeRoot[i], id, undefined, idKey, childKey);
    if (tempNode) return tempNode;
  }
}
/**
 * 把Object转换成query字符串
 * @param {Object} params 要转换的Object
 */
export function objectToQueryString (params) {
  if (params == null) {
    return null;
  } else {
    return Object.keys(params).map((key) => {
      if (params[key] !== undefined) {
        return `${key}=${params[key]}`;
      } else {
        return undefined;
      }
    }).filter(item => item != null).join('&');
  }
}
/**
 * 从数组中查找某一项
 * @param {Array} list 要查找的数组
 * @param {String} id 要查找的节点id
 * @param {String} idKey 主键字段名（如果为null则直接比较）
 * @param {Boolean} removeItem 是否从数组中移除查找到的节点
 * @returns {Object} 找到返回节点，没找到返回undefined
 */
export function findItemFromList (list, id, idKey, removeItem = false) {
  if (Array.isArray(list) && list.length > 0 && id != null) {
    for (let i = 0; i < list.length; i++) {
      let item = list[i];
      if (((idKey == null || idKey === '') && item === id) || (idKey != null && item[idKey] === id)) {
        if (removeItem) list.splice(i, 1);
        return item;
      }
    }
  }
  return null;
}
/**
 * 将数据保存到sessionStorage
 * @param {*} key sessionStorage的键值
 * @param {*} value 要保存的数据
 */
export function setObjectToSessionStorage (key, value) {
  if (key == null || key === '') return false;
  if (value == null) {
    window.sessionStorage.removeItem(key);
    return true;
  } else {
    let jsonObj = {
      data: value
    }
    window.sessionStorage.setItem(key, JSON.stringify(jsonObj));
    return true;
  }
}
/**
 * 从sessionStorage里面获取数据
 * @param {String} key 键值
 * @param {*} defaultValue 默认值
 */
export function getObjectFromSessionStorage (key, defaultValue) {
  let jsonObj = null;
  try {
    jsonObj = JSON.parse(window.sessionStorage.getItem(key));
    jsonObj = (jsonObj || {}).data;
  } catch (e) {
    jsonObj = defaultValue;
  };
  return (jsonObj != null) ? jsonObj : defaultValue;
}
/**
 * 判读字符串是否一个数字
 * @param {String} str 要判断的字符串
 */
export function isNumber (str) {
  let num = Number.parseFloat(str);
  if (Number.isNaN(num)) return false;
  return num.toString() === str;
}
/**
 * 生成随机数
 * @param {Integer} min 随机数最小值
 * @param {Integer} max 随机数最大值
 */
export function random (min, max) {
  let base = Math.random();
  return min + base * (max - min);
}
/**
 * 加密
 * @param {*} value 要加密的字符串
 */
const publicKey = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpC4QMnbTrQOFriJJCCFFWhlruBJThAEBfRk7pRx1jsAhyNVL3CqJb0tRvpnbCnJhrRAEPdgFHXv5A0RrvFp+5Cw7QoFH6O9rKB8+0H7+aVQeKITMUHf/XMXioymw6Iq4QfWd8RhdtM1KM6eGTy8aU7SO2s69Mc1LXefg/x3yw6wIDAQAB';
export function encrypt (value) {
  if (value == null || value === '') return null;
  let encrypt = new JSEncrypt();
  encrypt.setPublicKey(publicKey);
  return encodeURIComponent(encrypt.encrypt(value));
}

export function getToken () {
  return sessionStorage.getItem('token');
  // return Cookies.get('token');
}

export function setToken (token) {
  if (token == null || token === '') {
    sessionStorage.removeItem('token');
    // Cookies.remove('token');
  } else {
    sessionStorage.setItem('token', token);
    // Cookies.set('token', token);
  }
}

export function traversalTree (treeNode, callback, childrenKey = 'children') {
  if (treeNode != null && Array.isArray(treeNode[childrenKey]) && treeNode[childrenKey].length > 0) {
    treeNode[childrenKey].forEach(childNode => {
      traversalTree(childNode, callback, childrenKey);
    });
  }
  return typeof callback === 'function' ? callback(treeNode) : undefined;
}

export class TreeTableImpl {
  constructor (dataList, options) {
    this.options = {
      idKey: options ? options.idKey : 'id',
      nameKey: options ? options.nameKey : 'name',
      parentIdKey: options ? options.parentIdKey : 'parentId',
      isLefeCallback: options ? options.isLefeCallback : undefined,
      checkStrictly: options ? options.checkStrictly : false
    }

    this.dataList = Array.isArray(dataList) ? dataList : [];
    this.dataMap = new Map();
    this.dataList.forEach(item => {
      this.dataMap.set(item[this.options.idKey], item);
    });
    // 表格选中行
    this.checkedRows = undefined;
    this.onCheckedRowChange = this.onCheckedRowChange.bind(this);
  }

  /**
   * 过滤表格数据
   * @param {string} filterString 过滤条件字符串
   * @param {boolean} onlyChecked 是否只显示选中节点
   * @returns {array} 过滤后的表格数据列表
   */
  getFilterTableData (filterString, onlyChecked = false) {
    let { idKey, nameKey, parentIdKey, isLefeCallback } = this.options;
    let tempMap = new Map();
    let parentIdList = [];
    this.dataList.forEach(item => {
      if ((filterString == null || filterString === '' || item[nameKey].indexOf(filterString) !== -1) &&
        (!onlyChecked || (this.checkedRows != null && this.checkedRows.get(item[idKey])))) {
        if (isLefeCallback == null || !isLefeCallback(item)) {
          parentIdList.push(item[idKey]);
        }
        // 将命中节点以及它的父节点都设置为命中
        let tempItem = item;
        do {
          tempMap.set(tempItem[idKey], tempItem);
          tempItem = this.dataMap.get(tempItem[parentIdKey]);
        } while (tempItem != null)
      }
    });

    return this.dataList.map(item => {
      let disabled = true;
      
      if (parentIdList.indexOf(item[parentIdKey]) !== -1 || tempMap.get(item[idKey]) != null) {
        if (parentIdList.indexOf(item[parentIdKey]) !== -1 && (isLefeCallback == null || !isLefeCallback(item))) {
          parentIdList.push(item[idKey]);
        }
        disabled = false;
      }

      return {
        ...item,
        __disabled: disabled
      }
    });
  }

  /**
   * 获取表格树数据，计算选中状态
   * @param {array} dataList 表格列表数据
   */
  getTableTreeData (dataList, checkedRows) {
    let { idKey, parentIdKey, checkStrictly } = this.options;
    let treeData = [];
    function calcPermCodeTreeAttribute (treeNode, checkedRows) {
      let checkedItem = checkedRows == null ? null : checkedRows.get(treeNode[idKey]);
      treeNode.__checked = checkedItem != null;
      // 是否所有子权限字都被选中
      let allChildChecked = true;
      // 是否任意子权限字被选中
      let hasChildChecked = false;
      // 如果存在子权限字
      if (Array.isArray(treeNode.children) && treeNode.children.length > 0) {
        treeNode.children.forEach(item => {
          let isChecked = calcPermCodeTreeAttribute(item, checkedRows);
          hasChildChecked = hasChildChecked || isChecked;
          allChildChecked = allChildChecked && isChecked;
        });
      } else {
        allChildChecked = false;
      }
      treeNode.__indeterminate = !checkStrictly && hasChildChecked && !allChildChecked;
      treeNode.__checked = treeNode.__checked || (allChildChecked && !checkStrictly);
      return treeNode.__checked || treeNode.__indeterminate;
    }

    if (Array.isArray(dataList)) {
      treeData = treeDataTranslate(dataList.map(item => {
        return {...item};
      }), idKey, parentIdKey);
      treeData.forEach(item => {
        calcPermCodeTreeAttribute(item, checkedRows);
      });
    }
    
    return treeData;
  }

  /**
   * 树表格行选中状态改变
   * @param {object} row 选中状态改变行数据
   */
  onCheckedRowChange (row) {
    if (this.checkedRows == null) {
      this.checkedRows = new Map();
    } else {
      let temp = new Map();
      this.checkedRows.forEach((item, key) => {
        temp.set(key, item);
      });
      this.checkedRows = temp;
    }
    let { idKey } = this.options;
    if (!row.__checked || row.__indeterminate) {
      // 节点之前未被选中或者之前为半选状态，修改当前节点以及子节点为选中状态
      this.checkedRows.set(row[idKey], row);
      if (Array.isArray(row.children) && !this.options.checkStrictly) {
        row.children.forEach(childNode => {
          traversalTree(childNode, (node) => {
            this.checkedRows.set(node[idKey], node);
          });
        });
      }
    } else {
      // 节点之前为选中状态，修改节点以及子节点为未选中状态
      this.checkedRows.delete(row[idKey]);
      if (Array.isArray(row.children) && !this.options.checkStrictly) {
        row.children.forEach(childNode => {
          traversalTree(childNode, (node) => {
            this.checkedRows.delete(node[idKey]);
          });
        });
      }
    }
  }

  /**
   * 获取所有选中的权限字节点
   * @param {array} treeData 树数据
   * @param {boolean} includeHalfChecked 是否包含半选节点，默认为false
   * @returns {array} 选中节点列表
   */
  getCheckedRows (treeData, includeHalfChecked = false) {
    let checkedRows = [];

    function traversalCallback (node) {
      if (node == null) return;
      if (node.__checked || (includeHalfChecked && node.__indeterminate)) {
        checkedRows.push(node);
      }
    }

    if (Array.isArray(treeData) && treeData.length > 0) {
      treeData.forEach(permCode => {
        traversalTree(permCode, traversalCallback, 'children');
      });
    }

    return checkedRows;
  }

  /**
   * 设置选中节点
   * @param {array} checkedRows
   */
  setCheckedRows (checkedRows) {
    this.checkedRows = new Map();
    if (Array.isArray(checkedRows)) {
      checkedRows.forEach(item => {
        let node = this.dataMap.get(item[this.options.idKey]);
        if (node != null) {
          this.checkedRows.set(node[this.options.idKey], node);
        }
      });
    }
  }
  /**
   * 根据id获取表格行
   * @param {*} id
   */
  getTableRow (id) {
    return this.dataMap.get(id);
  }
}
