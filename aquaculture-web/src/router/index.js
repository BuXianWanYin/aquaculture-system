import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { ROLE_NAMES, hasRole } from '@/constants/role'

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
        meta: { title: '仪表盘' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户管理', roles: [ROLE_NAMES.ADMIN] }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/role/RoleList.vue'),
        meta: { title: '角色管理', roles: [ROLE_NAMES.ADMIN] }
      },
      {
        path: 'permission',
        name: 'Permission',
        component: () => import('@/views/permission/PermissionList.vue'),
        meta: { title: '权限管理', roles: [ROLE_NAMES.ADMIN] }
      },
      {
        path: 'department',
        name: 'Department',
        component: () => import('@/views/department/DepartmentList.vue'),
        meta: { title: '部门管理', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.DEPARTMENT_MANAGER] }
      },
      {
        path: 'breed',
        name: 'Breed',
        component: () => import('@/views/breed/BreedList.vue'),
        meta: { title: '养殖品种管理', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.OPERATOR] }
      },
      {
        path: 'area',
        name: 'Area',
        component: () => import('@/views/area/AreaList.vue'),
        meta: { title: '养殖区域管理', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.OPERATOR] }
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/equipment/EquipmentList.vue'),
        meta: { title: '设备管理', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.OPERATOR] }
      },
      {
        path: 'plan',
        name: 'Plan',
        component: () => import('@/views/plan/PlanList.vue'),
        meta: { title: '养殖计划管理', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.OPERATOR, ROLE_NAMES.DECISION_MAKER, ROLE_NAMES.DEPARTMENT_MANAGER] }
      },
      {
        path: 'yield',
        name: 'Yield',
        component: () => import('@/views/yield/YieldList.vue'),
        meta: { title: '产量统计管理', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.OPERATOR, ROLE_NAMES.DECISION_MAKER, ROLE_NAMES.DEPARTMENT_MANAGER] }
      },
      {
        path: 'feed',
        name: 'Feed',
        component: () => import('@/views/feed/FeedManagement.vue'),
        meta: { title: '饲料管理', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.OPERATOR, ROLE_NAMES.DEPARTMENT_MANAGER] }
      },
      {
        path: 'disease',
        name: 'Disease',
        component: () => import('@/views/disease/DiseaseManagement.vue'),
        meta: { title: '病害防控管理', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.OPERATOR, ROLE_NAMES.DEPARTMENT_MANAGER] }
      },
      {
        path: 'production',
        name: 'Production',
        component: () => import('@/views/production/ProductionManagement.vue'),
        meta: { title: '日常生产记录', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.OPERATOR, ROLE_NAMES.DEPARTMENT_MANAGER] }
      },
      {
        path: 'sales',
        name: 'Sales',
        component: () => import('@/views/sales/SalesManagement.vue'),
        meta: { title: '销售管理', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.OPERATOR, ROLE_NAMES.DECISION_MAKER, ROLE_NAMES.DEPARTMENT_MANAGER] }
      },
      {
        path: 'operLog',
        name: 'OperLog',
        component: () => import('@/views/operLog/OperLogList.vue'),
        meta: { title: '操作日志管理', roles: [ROLE_NAMES.ADMIN] }
      },
      {
        path: 'statistic',
        name: 'Statistic',
        component: () => import('@/views/statistic/StatisticList.vue'),
        meta: { title: '数据报表与分析', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.DECISION_MAKER] }
      },
      {
        path: 'message',
        name: 'Message',
        component: () => import('@/views/message/MessageList.vue'),
        meta: { title: '消息通知', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.OPERATOR, ROLE_NAMES.DECISION_MAKER] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人信息', roles: [ROLE_NAMES.ADMIN, ROLE_NAMES.OPERATOR, ROLE_NAMES.DECISION_MAKER] }
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
  
  // 检查角色权限（基于角色名称，避免硬编码角色ID）
  if (to.meta.roles && userStore.userInfo) {
    const userRoleName = userStore.userInfo.roleName
    if (!hasRole(to.meta.roles, userRoleName)) {
      ElMessage.warning('您没有权限访问该页面')
      next('/dashboard')
      return
    }
  }
  
  next()
})

export default router

