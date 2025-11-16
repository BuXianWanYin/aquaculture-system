import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/Index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户管理', roles: [1, 2] }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/role/RoleList.vue'),
        meta: { title: '角色管理', roles: [1] }
      },
      {
        path: 'permission',
        name: 'Permission',
        component: () => import('@/views/permission/PermissionList.vue'),
        meta: { title: '权限管理', roles: [1] }
      },
      {
        path: 'breed',
        name: 'Breed',
        component: () => import('@/views/breed/BreedList.vue'),
        meta: { title: '养殖品种管理', roles: [1, 2, 3] }
      },
      {
        path: 'area',
        name: 'Area',
        component: () => import('@/views/area/AreaList.vue'),
        meta: { title: '养殖区域管理', roles: [1, 2, 3] }
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/equipment/EquipmentList.vue'),
        meta: { title: '设备管理', roles: [1, 2, 3] }
      },
      {
        path: 'plan',
        name: 'Plan',
        component: () => import('@/views/plan/PlanList.vue'),
        meta: { title: '养殖计划管理', roles: [1, 2, 3, 4] }
      },
      {
        path: 'yield',
        name: 'Yield',
        component: () => import('@/views/yield/YieldList.vue'),
        meta: { title: '产量统计管理', roles: [1, 2, 3, 4] }
      },
      {
        path: 'operLog',
        name: 'OperLog',
        component: () => import('@/views/operLog/OperLogList.vue'),
        meta: { title: '操作日志管理', roles: [1, 2] }
      },
      {
        path: 'statistic',
        name: 'Statistic',
        component: () => import('@/views/statistic/StatisticList.vue'),
        meta: { title: '数据报表与分析', roles: [1, 2, 4] }
      },
      {
        path: 'message',
        name: 'Message',
        component: () => import('@/views/message/MessageList.vue'),
        meta: { title: '消息通知', roles: [1, 2, 3, 4] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人信息', roles: [1, 2, 3, 4] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 检查是否需要认证
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
    return
  }
  
  // 已登录用户访问登录页，重定向到首页
  if (to.path === '/login' && userStore.token) {
    next('/')
    return
  }
  
  // 检查角色权限
  if (to.meta.roles && userStore.userInfo) {
    const userRole = userStore.userInfo.roleId
    if (!to.meta.roles.includes(userRole)) {
      ElMessage.warning('您没有权限访问该页面')
      next('/dashboard')
      return
    }
  }
  
  next()
})

export default router

