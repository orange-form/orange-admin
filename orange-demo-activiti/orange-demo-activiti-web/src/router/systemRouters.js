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
      {path: 'formSysDept', component: _import('upms/formSysDept/index'), name: 'formSysDept', meta: {title: '部门管理'}},
      {path: 'formSysRole', component: _import('upms/formSysRole/index'), name: 'formSysRole', meta: {title: '角色管理'}},
      {path: 'formSysDataPerm', component: _import('upms/formSysDataPerm/index'), name: 'formSysDataPerm', meta: {title: '数据权限管理'}},
      {path: 'formSysMenu', component: _import(state.supportColumn ? 'upms/formSysMenu/formSysColumnMenu' : 'upms/formSysMenu/index'), name: 'formSysMenu', meta: {title: '菜单列表'}},
      {path: 'formSysDict', component: _import('upms/formDictManagement/index'), name: 'formSysDict', meta: {title: '字典管理'}},
      {path: 'formSysPermCode', component: _import('upms/formSysPermCode/index'), name: 'formSysPermCode', meta: {title: '权限字管理'}},
      {path: 'formSysPerm', component: _import('upms/formSysPerm/index'), name: 'formSysPerm', meta: {title: '权限资源管理'}},
      {path: 'formSysLoginUser', component: _import('upms/formSysLoginUser/index'), name: 'formSysLoginUser', meta: {title: '在线用户'}},
      // 岗位模块路由配置
      {path: 'formSysPost', component: _import('upms/formSysPost/index'), name: 'formSysPost', meta: {title: '岗位管理'}},
      {path: 'formSysDeptPost', component: _import('upms/formSysDeptPost/index'), name: 'formSysDeptPost', props: getProps, meta: {title: '设置部门岗位'}},
      // 在线表单模块路由配置
      {path: 'formOnlineDict', component: _import('onlineForm/formOnlineDict/index'), name: 'formOnlineDict', props: getProps, meta: {title: '在线表单字典管理'}},
      {path: 'formOnlinePage', component: _import('onlineForm/formOnlinePage/index'), name: 'formOnlinePage', props: getProps, meta: {title: '在线表单管理'}},
      {path: 'onlineForm', component: _import('onlineForm/index'), name: 'onlineForm', props: getProps, meta: {title: '在线表单'}},
      // 工作流模块路由配置
      {path: 'formMessage', component: _import('workflow/formMessage/index'), name: 'formMessage', meta: {title: '催办消息'}},
      {path: 'formFlowCategory', component: _import('workflow/flowCategory/formFlowCategory'), name: 'formFlowCategory', props: getProps, meta: {title: '流程分类管理'}},
      {path: 'formFlowEntry', component: _import('workflow/flowEntry/formFlowEntry'), name: 'formFlowEntry', props: getProps, meta: {title: '流程设计'}},
      {path: 'formAllInstance', component: _import('workflow/taskManager/formAllInstance'), name: 'formAllInstance', props: getProps, meta: {title: '流程实例'}},
      {path: 'formMyTask', component: _import('workflow/taskManager/formMyTask'), name: 'formMyTask', props: getProps, meta: {title: '我的待办'}},
      {path: 'formMyApprovedTask', component: _import('workflow/taskManager/formMyApprovedTask'), name: 'formMyApprovedTask', props: getProps, meta: {title: '已办任务'}},
      {path: 'formMyHistoryTask', component: _import('workflow/taskManager/formMyHistoryTask'), name: 'formMyHistoryTask', props: getProps, meta: {title: '历史流程'}},
      {
        path: 'handlerFlowTask',
        component: _import('workflow/handlerFlowTask/index'),
        name: 'handlerFlowTask',
        props: getProps,
        meta: {title: '流程处理'},
        children: [
          // 静态表单路由设置
        ]
      },
      {path: 'welcome', component: _import('welcome/index'), name: 'welcome', meta: {title: '欢迎'}}
    ]
  }
];

export default routers;
