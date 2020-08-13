import Vue from 'vue';
import Router from 'vue-router';
import routers from './systemRouters.js';
import dialog from '@/components/Dialog';

Vue.use(Router)

const router = new Router({
  mode: 'hash',
  routes: routers
});

/**
 * 路由跳转的时候判断token是否存在
 */
router.beforeResolve((to, from, next) => {
  if (to.name === 'login') {
    next();
  } else {
    let token = sessionStorage.getItem('token');
    if (!token || !/\S/.test(token)) {
      dialog.closeAll();
      next({ name: 'login' })
    } else {
      next();
    }
  }
});

export default router;
