import Vue from 'vue';
import Router from 'vue-router';
import routers from './systemRouters.js';
import dialog from '@/components/Dialog';
import { getToken } from '@/utils';

Vue.use(Router)

const router = new Router({
  mode: 'hash',
  routes: routers
});

const originalPush = Router.prototype.push;
Router.prototype.push = function push (location) {
  return originalPush.call(this, location).catch(e => {});
}

const originalReplace = Router.prototype.replace;
Router.prototype.replace = function push (location, onComplete, onAbort) {
  return originalReplace.call(this, location, onComplete, onAbort).catch(e => {});
}

/**
 * 路由跳转的时候判断token是否存在
 */
router.beforeResolve((to, from, next) => {
  if (to.name === 'login') {
    next();
  } else {
    let token = getToken();
    if (!token || !/\S/.test(token)) {
      dialog.closeAll();
      next({ name: 'login' })
    } else {
      next();
    }
  }
});

export default router;
