import { initUserInfo } from './utils';
import { getObjectFromSessionStorage, treeDataTranslate } from '@/utils';

export default {
  // 是否左侧菜单折叠
  isCollapse: false,
  // 是否多栏目
  supportColumn: false,
  // 是否多标签
  supportTags: false,
  // 浏览器客户区高度
  documentClientHeight: 100,
  // 用户登录信息
  userInfo: initUserInfo(),
  // 缓存页面
  cachePages: getObjectFromSessionStorage('cachePages', []),
  // 当前标签
  tagList: getObjectFromSessionStorage('tagList', []),
  // 菜单列表
  menuList: treeDataTranslate(getObjectFromSessionStorage('menuList', []), 'menuId', 'parentId'),
  // 当前菜单
  currentMenuId: getObjectFromSessionStorage('currentMenuId', undefined),
  // 当前栏目
  currentColumnId: getObjectFromSessionStorage('currentColumnId', undefined)
}
