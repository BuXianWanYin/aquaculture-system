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

