import state from '../store/state.js';
// 开发环境不使用懒加载, 因为懒加载页面太多的话会造成webpack热更新太慢
const _import = require('./import-' + process.env.NODE_ENV)

function getProps (route) {
  return route.query;
}

// 系统生成路由
const routers = [
  { path: '/login', component: _import('login/index'), name: 'login', props: getProps, desc: '登录' },
  {
    path: '/',
    component: _import('login/index'),
    name: 'root'
  },
  {
    path: '/main',
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
      {path: 'formSysUser', component: _import('upms/formSysUser/index'), name: 'formSysUser', meta: {title: '用户管理'}},
      {path: 'formSysRole', component: _import('upms/formSysRole/index'), name: 'formSysRole', meta: {title: '角色管理'}},
      {path: 'formSysMenu', component: _import(state.supportColumn ? 'upms/formSysMenu/formSysColumnMenu' : 'upms/formSysMenu/index'), name: 'formSysMenu', meta: {title: '菜单列表'}},
      {path: 'formSysDict', component: _import('upms/formDictManagement/index'), name: 'formSysDict', meta: {title: '字典管理'}},
      {path: 'formSysPermCode', component: _import('upms/formSysPermCode/index'), name: 'formSysPermCode', meta: {title: '权限字管理'}},
      {path: 'formSysPerm', component: _import('upms/formSysPerm/index'), name: 'formSysPerm', meta: {title: '权限资源管理'}},
      {path: 'welcome', component: _import('welcome/index'), name: 'welcome', meta: {title: '欢迎'}},
      {path: 'formSchool', component: _import('generated/formSchool/index'), name: 'formSchool', props: getProps, meta: {title: '校区管理'}},
      {path: 'formStudent', component: _import('generated/formStudent/index'), name: 'formStudent', props: getProps, meta: {title: '学生管理'}},
      {path: 'formCourse', component: _import('generated/formCourse/index'), name: 'formCourse', props: getProps, meta: {title: '课程管理'}},
      {path: 'formCourseStats', component: _import('generated/formCourseStats/index'), name: 'formCourseStats', props: getProps, meta: {title: '课程统计'}},
      {path: 'formStudentActionStats', component: _import('generated/formStudentActionStats/index'), name: 'formStudentActionStats', props: getProps, meta: {title: '学生行为统计'}},
      {path: 'formStudentActionDetail', component: _import('generated/formStudentActionDetail/index'), name: 'formStudentActionDetail', props: getProps, meta: {title: '学生行为详情'}},
      {path: 'formSetClassStudent', component: _import('generated/formSetClassStudent/index'), name: 'formSetClassStudent', props: getProps, meta: {title: '设置班级学生'}},
      {path: 'formSetClassCourse', component: _import('generated/formSetClassCourse/index'), name: 'formSetClassCourse', props: getProps, meta: {title: '设置班级课程'}},
      {path: 'formClass', component: _import('generated/formClass/index'), name: 'formClass', props: getProps, meta: {title: '班级管理'}}
    ]
  }
];

export default routers;
