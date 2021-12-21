// 开发环境不使用懒加载, 因为懒加载页面太多的话会造成webpack热更新太慢, 所以只有开发环境使用懒加载
const _import = require('./import-' + process.env.NODE_ENV)

function getProps (route) {
  return route.query;
}

// 系统生成路由
const routers = [
  { path: '/login', component: _import('login/index'), name: 'login', props: getProps, desc: '登录' },
  {
    path: '/',
    component: _import('layout/index'),
    name: 'main',
    props: getProps,
    redirect: {
      name: 'welcome'
    },
    meta: {
      title: '主页',
      showOnly: true
    },
    children: [
      {path: 'welcome', component: _import('welcome/index'), name: 'welcome', meta: {title: '欢迎'}},
      {path: 'formUaaUser', component: _import('generated/formUaaUser/index'), name: 'formUaaUser', props: getProps, meta: {title: '用户管理'}},
      {path: 'formAuthClient', component: _import('generated/formAuthClient/index'), name: 'formAuthClient', props: getProps, meta: {title: '应用管理'}},
      {path: 'formUaaOperator', component: _import('generated/formUaaOperator/index'), name: 'formUaaOperator', props: getProps, meta: {title: '操作员管理'}}
    ]
  }
];

export default routers;
