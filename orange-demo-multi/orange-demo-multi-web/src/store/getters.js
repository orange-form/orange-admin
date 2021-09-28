import { findMenuItem } from './utils';
import * as StaticDict from '@/staticDict';

export default {
  getMultiTags: (state) => {
    return state.supportTags;
  },
  getCollapse: (state) => {
    return state.isCollapse;
  },
  getClientHeight: (state) => {
    return state.documentClientHeight;
  },
  getClientWidth: (state) => {
    return state.documentClienWidth;
  },
  getMainContextHeight: (state) => {
    return state.documentClientHeight - (state.supportTags ? 130 : 90);
  },
  getOnlineFormCache: (state) => {
    return state.onlineFormCache;
  },
  getUserInfo: (state) => {
    return state.userInfo;
  },
  getCachePages: (state) => {
    return state.cachePages;
  },
  getTagList: (state) => {
    return state.tagList;
  },
  getMenuList: (state) => {
    if (state.supportColumn) {
      if (state.currentColumnId == null || state.currentColumnId === '') return [];
      for (let i = 0; i < state.menuList.length; i++) {
        if (state.menuList[i].menuId === state.currentColumnId) {
          return state.menuList[i].children || [];
        }
      }

      return [];
    } else {
      return state.menuList;
    }
  },
  getColumnList: (state) => {
    if (!state.supportColumn) return [];
    return state.menuList.map(menu => {
      if (menu.menuType === StaticDict.SysMenuType.DIRECTORY) {
        return {
          columnId: menu.menuId,
          columnName: menu.menuName
        }
      }
    }).filter(item => item != null);
  },
  getCurrentMenuId: (state) => {
    return state.currentMenuId;
  },
  getMenuItem: (state) => {
    if (Array.isArray(state.menuList)) {
      for (let i = 0; i < state.menuList.length; i++) {
        let temp = findMenuItem(state.menuList[i], state.currentMenuId);
        if (temp != null) return temp;
      }
    }
    return null;
  },
  getCurrentMenuPath: (state) => {
    let menuPath = [];
    if (Array.isArray(state.menuList)) {
      for (let i = 0; i < state.menuList.length; i++) {
        let temp = findMenuItem(state.menuList[i], state.currentMenuId, menuPath);
        if (temp != null) break;
      }
    }

    return menuPath;
  },
  getMultiColumn: (state) => {
    return state.supportColumn;
  },
  getCurrentColumnId: (state) => {
    return state.currentColumnId;
  }
}
