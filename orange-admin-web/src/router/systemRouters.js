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
      {path: 'formSysUser', component: _import('upms/formSysUser/index'), name: 'formSysUser', meta: {title: '用户管理'}},
      {path: 'formSysDept', component: _import('upms/formSysDept/index'), name: 'formSysDept', meta: {title: '部门管理'}},
      {path: 'formSysRole', component: _import('upms/formSysRole/index'), name: 'formSysRole', meta: {title: '角色管理'}},
      {path: 'formSysDataPerm', component: _import('upms/formSysDataPerm/index'), name: 'formSysDataPerm', meta: {title: '数据权限管理'}},
      {path: 'formSysMenu', component: _import('upms/formSysMenu/index'), name: 'formSysMenu', meta: {title: '菜单列表'}},
      {path: 'formSysDict', component: _import('upms/formDictManagement/index'), name: 'formSysDict', meta: {title: '字典管理'}},
      {path: 'formSysPermCode', component: _import('upms/formSysPermCode/index'), name: 'formSysPermCode', meta: {title: '权限字管理'}},
      {path: 'formSysPerm', component: _import('upms/formSysPerm/index'), name: 'formSysPerm', meta: {title: '权限资源管理'}},
      {path: 'formTeacher', component: _import('generated/formTeacher/index'), name: 'formTeacher', props: getProps, meta: {title: '老师管理'}},
      {path: 'formCreateTeacher', component: _import('generated/formCreateTeacher/index'), name: 'formCreateTeacher', props: getProps, meta: {title: '新建老师'}},
      {path: 'formEditTeacher', component: _import('generated/formEditTeacher/index'), name: 'formEditTeacher', props: getProps, meta: {title: '编辑老师'}},
      {path: 'formTeacherTransStats', component: _import('generated/formTeacherTransStats/index'), name: 'formTeacherTransStats', props: getProps, meta: {title: '老师个人统计'}}
    ]
  }
];

export default routers;
