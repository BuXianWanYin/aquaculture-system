import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false, title: '用户登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false, title: '用户注册' }
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
        meta: { title: '用户管理', permissions: ['system:user:view'] }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/role/RoleList.vue'),
        meta: { title: '角色管理', permissions: ['system:role:view'] }
      },
      {
        path: 'permission',
        name: 'Permission',
        component: () => import('@/views/permission/PermissionList.vue'),
        meta: { title: '权限管理', permissions: ['system:permission:view'] }
      },
      {
        path: 'department',
        name: 'Department',
        component: () => import('@/views/department/DepartmentList.vue'),
        meta: { title: '部门管理', permissions: ['department:view'] }
      },
      {
        path: 'breed',
        name: 'Breed',
        component: () => import('@/views/breed/BreedList.vue'),
        meta: { title: '养殖品种管理', permissions: ['breed:view'] }
      },
      {
        path: 'area',
        name: 'Area',
        component: () => import('@/views/area/AreaList.vue'),
        meta: { title: '养殖区域管理', permissions: ['area:view'] }
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/equipment/EquipmentList.vue'),
        meta: { title: '设备管理', permissions: ['equipment:view'] }
      },
      {
        path: 'plan',
        name: 'Plan',
        component: () => import('@/views/plan/PlanList.vue'),
        meta: { title: '养殖计划管理', permissions: ['plan:view'] }
      },
      {
        path: 'yield',
        name: 'Yield',
        component: () => import('@/views/yield/YieldList.vue'),
        meta: { title: '产量统计管理', permissions: ['yield:view'] }
      },
      {
        path: 'feed',
        name: 'Feed',
        component: () => import('@/views/feed/FeedManagement.vue'),
        meta: { title: '饲料管理', permissions: ['feed:purchase:view', 'feed:inventory:view', 'feed:usage:view'] }
      },
      {
        path: 'disease',
        name: 'Disease',
        component: () => import('@/views/disease/DiseaseManagement.vue'),
        meta: { title: '病害防控管理', permissions: ['disease:record:view', 'disease:prevention:view', 'disease:medicine:view'] }
      },
      {
        path: 'production',
        name: 'Production',
        component: () => import('@/views/production/ProductionManagement.vue'),
        meta: { title: '日常生产记录', permissions: ['production:feeding:view', 'production:inspection:view'] }
      },
      {
        path: 'sales',
        name: 'Sales',
        component: () => import('@/views/sales/SalesManagement.vue'),
        meta: { title: '销售管理', permissions: ['sales:customer:view', 'sales:record:view'] }
      },
      {
        path: 'operLog',
        name: 'OperLog',
        component: () => import('@/views/operLog/OperLogList.vue'),
        meta: { title: '操作日志管理', permissions: ['log:view'] }
      },
      {
        path: 'statistic',
        name: 'Statistic',
        component: () => import('@/views/statistic/StatisticList.vue'),
        meta: { title: '数据报表与分析', permissions: ['statistics:view'] }
      },
      {
        path: 'message',
        name: 'Message',
        component: () => import('@/views/message/MessageList.vue'),
        meta: { title: '消息通知', permissions: ['message:send', 'message:view'] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人信息' }
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
  
  // 设置动态页面标题
  const defaultTitle = '水产养殖计划与产量统计系统'
  const pageTitle = to.meta.title || defaultTitle
  document.title = pageTitle ? `${pageTitle} - ${defaultTitle}` : defaultTitle
  
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
  
  // 检查权限（基于权限代码，而不是角色名称）
  if (to.meta.permissions && userStore.userInfo) {
    const userPermissions = userStore.userInfo.permissions || []
    if (!userPermissions || userPermissions.length === 0) {
      ElMessage.warning('您没有权限访问该页面')
      next('/dashboard')
      return
    }
    
    // 如果包含通配符，表示拥有所有权限
    const hasAllPermissions = userPermissions.includes('*')
    
    // 检查是否具有所需权限（任意一个即可）
    const requiredPermissions = Array.isArray(to.meta.permissions) 
      ? to.meta.permissions 
      : [to.meta.permissions]
    
    const hasPermission = hasAllPermissions || requiredPermissions.some(perm => userPermissions.includes(perm))
    
    if (!hasPermission) {
      ElMessage.warning('您没有权限访问该页面')
      next('/dashboard')
      return
    }
  }
  
  next()
})

// 路由后置守卫 - 确保标题更新
router.afterEach((to) => {
  const defaultTitle = '水产养殖计划与产量统计系统'
  const pageTitle = to.meta.title || defaultTitle
  document.title = pageTitle ? `${pageTitle} - ${defaultTitle}` : defaultTitle
})

export default router

