import { findMenuItem } from './utils';

export default {
  getCollapse: (state) => {
    return state.isCollapse;
  },
  getClientHeight: (state) => {
    return state.documentClientHeight;
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
    return state.menuList;
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
  }
}
