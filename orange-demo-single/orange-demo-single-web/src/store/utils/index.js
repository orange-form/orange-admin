import { getObjectFromSessionStorage } from '@/utils';

function findMenuItem (menuItem, menuId, path) {
  if (Array.isArray(path)) path.push(menuItem);
  if ((menuItem.menuId + '') === (menuId + '')) return menuItem;

  let bFind = false;
  let findItem = null;
  if (Array.isArray(menuItem.children)) {
    for (let i = 0; i < menuItem.children.length; i++) {
      findItem = findMenuItem(menuItem.children[i], menuId, path);
      if (findItem != null) {
        bFind = true;
        break;
      }
    }
  }

  if (!bFind && Array.isArray(path)) path.pop();
  return bFind ? findItem : null;
}

function initUserInfo (userInfo) {
  if (userInfo == null) userInfo = getObjectFromSessionStorage('userInfo');

  if (userInfo != null && userInfo.permCodeList != null && Array.isArray(userInfo.permCodeList)) {
    userInfo.permCodeSet = new Set(userInfo.permCodeList);
  }

  return userInfo;
}

export {
  findMenuItem,
  initUserInfo
}
