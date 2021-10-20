import * as StaticDict from '@/staticDict';
import { SysOnlineDictType, SysCustomWidgetOperationType } from '@/staticDict/onlineStaticDict.js';
import { OnlineOperation } from '@/api/onlineController.js';
import ajax from '@/core/http/index.js';

function getTableDictData (sender, dictId, dictParams) {
  return new Promise((resolve, reject) => {
    let filterDtoList = Object.keys(dictParams).map(key => {
      return {
        columnName: key,
        columnValue: dictParams[key]
      }
    });
    let params = {
      dictId: dictId,
      filterDtoList: filterDtoList
    }
    OnlineOperation.listDict(sender, params).then(res => {
      resolve(res.data);
    }).catch(e => {
      reject(e);
    });
  });
}

function getDictDataByUrl (url, params, dictInfo, methods = 'get') {
  return new Promise((resolve, reject) => {
    ajax.doUrl(url, methods, params).then(res => {
      if (Array.isArray(res.data)) {
        resolve(res.data.map(item => {
          return {
            id: item[dictInfo.keyColumnName],
            name: item[dictInfo.valueColumnName],
            parentId: item[dictInfo.parentKeyColumnName]
          }
        }));
      } else {
        reject();
      }
    }).catch(e => {
      reject(e);
    });
  });
}

function getUrlDictData (dictInfo, dictParams) {
  let url = dictInfo.dictListUrl;
  if (url != null && url !== '') {
    return getDictDataByUrl(url, dictParams, dictInfo, 'get');
  } else {
    console.error('字典 [' + dictInfo.dictName + '] url为空');
    return Promise.reject();
  }
}

/**
 * 获取字典数据
 * @param {*} dictInfo 字典配置对象
 * @param {*} params 获取字典时传入的参数，仅对于数据表字典和URL字典有效
 * @returns 字典数据
 */
function getDictDataList (sender, dictInfo, dictParams) {
  let dictData = JSON.parse(dictInfo.dictDataJson);
  switch (dictInfo.dictType) {
    case SysOnlineDictType.TABLE:
      return getTableDictData(sender, dictInfo.dictId, dictParams);
    case SysOnlineDictType.URL:
      return getUrlDictData(dictInfo, dictParams);
    case SysOnlineDictType.CUSTOM:
      if (dictData != null && Array.isArray(dictData.dictData)) {
        return Promise.resolve(dictData.dictData);
      } else {
        return Promise.reject(new Error('获取自定义字典数据错误！'));
      }
    case SysOnlineDictType.STATIC:
      if (dictData != null && dictData.staticDictName != null && StaticDict[dictData.staticDictName] != null) {
        return Promise.resolve(StaticDict[dictData.staticDictName].getList());
      } else {
        return Promise.reject(new Error('未知的静态字典！'));
      }
    default:
      return Promise.reject(new Error('未知的字典类型！'));
  }
}

function getOperationPermCode (widget, operation) {
  let datasourceVariableName = (widget.datasource || {}).variableName;
  let temp = 'view';
  switch (operation.type) {
    case SysCustomWidgetOperationType.ADD:
    case SysCustomWidgetOperationType.EDIT:
    case SysCustomWidgetOperationType.DELETE:
      temp = 'edit';
      break;
    default:
      temp = 'view';
  }
  return 'online:' + datasourceVariableName + ':' + temp;
}

export {
  getDictDataList,
  getDictDataByUrl,
  getOperationPermCode
}
