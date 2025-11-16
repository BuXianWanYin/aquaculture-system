<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <h2>水产养殖系统</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/message" v-if="canAccess(['message:send', 'message:view'])">
          <el-icon><Bell /></el-icon>
          <span>消息通知</span>
        </el-menu-item>
        <el-sub-menu index="user" v-if="canAccess(['system:user:view', 'system:role:view', 'system:permission:view'])">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户与权限管理</span>
          </template>
          <el-menu-item index="/user" v-if="canAccess(['system:user:view'])">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/role" v-if="canAccess(['system:role:view'])">
            <el-icon><UserFilled /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/permission" v-if="canAccess(['system:permission:view'])">
            <el-icon><Key /></el-icon>
            <span>权限管理</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/department" v-if="canAccess(['department:view'])">
          <el-icon><OfficeBuilding /></el-icon>
          <span>部门管理</span>
        </el-menu-item>
        <el-sub-menu index="base" v-if="canAccess(['breed:view', 'area:view', 'equipment:view'])">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>基础数据管理</span>
          </template>
          <el-menu-item index="/breed" v-if="canAccess(['breed:view'])">
            <el-icon><Collection /></el-icon>
            <span>养殖品种</span>
          </el-menu-item>
          <el-menu-item index="/area" v-if="canAccess(['area:view'])">
            <el-icon><MapLocation /></el-icon>
            <span>养殖区域</span>
          </el-menu-item>
          <el-menu-item index="/equipment" v-if="canAccess(['equipment:view'])">
            <el-icon><Tools /></el-icon>
            <span>设备管理</span>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="plan" v-if="canAccess(['plan:view', 'yield:view'])">
          <template #title>
            <el-icon><Folder /></el-icon>
            <span>养殖管理</span>
          </template>
          <el-menu-item index="/plan" v-if="canAccess(['plan:view'])">
            <el-icon><Document /></el-icon>
            <span>养殖计划管理</span>
          </el-menu-item>
          <el-menu-item index="/yield" v-if="canAccess(['yield:view'])">
            <el-icon><DataAnalysis /></el-icon>
            <span>产量统计管理</span>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="production" v-if="canAccess(['feed:purchase:view', 'feed:inventory:view', 'feed:usage:view', 'disease:record:view', 'disease:prevention:view', 'disease:medicine:view', 'production:feeding:view', 'production:inspection:view'])">
          <template #title>
            <el-icon><Edit /></el-icon>
            <span>生产管理</span>
          </template>
          <el-menu-item index="/feed" v-if="canAccess(['feed:purchase:view', 'feed:inventory:view', 'feed:usage:view'])">
            <el-icon><Box /></el-icon>
            <span>饲料管理</span>
          </el-menu-item>
          <el-menu-item index="/disease" v-if="canAccess(['disease:record:view', 'disease:prevention:view', 'disease:medicine:view'])">
            <el-icon><Warning /></el-icon>
            <span>病害防控管理</span>
          </el-menu-item>
          <el-menu-item index="/production" v-if="canAccess(['production:feeding:view', 'production:inspection:view'])">
            <el-icon><Edit /></el-icon>
            <span>日常生产记录</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/sales" v-if="canAccess(['sales:customer:view', 'sales:record:view'])">
          <el-icon><ShoppingCart /></el-icon>
          <span>销售管理</span>
        </el-menu-item>
        <el-menu-item index="/statistic" v-if="canAccess(['statistics:view'])">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据报表与分析</span>
        </el-menu-item>
        <el-menu-item index="/operLog" v-if="canAccess(['log:view'])">
          <el-icon><Document /></el-icon>
          <span>操作日志管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">仪表盘</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><Avatar /></el-icon>
              <span>{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermission } from '@/composables/usePermission'
import { ElMessage } from 'element-plus'
import { getCurrentUser } from '@/api/user'
import {
  House,
  User,
  UserFilled,
  Collection,
  MapLocation,
  Tools,
  Avatar,
  ArrowDown,
  Document,
  DataAnalysis,
  Key,
  Bell,
  OfficeBuilding,
  Box,
  Warning,
  Edit,
  ShoppingCart,
  Setting,
  Folder
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { hasAnyPermission } = usePermission()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '首页')

// 权限检查函数（基于权限字符，而不是角色名称）
const canAccess = (permissions) => {
  if (!permissions || !Array.isArray(permissions)) return true
  return hasAnyPermission(permissions)
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
}

// 组件挂载时刷新用户信息，确保获取最新权限
onMounted(async () => {
  if (userStore.token) {
    try {
      const res = await getCurrentUser()
      if (res.code === 200 && res.data) {
        // 更新用户信息，包括最新的权限列表
        userStore.setUserInfo(res.data)
      }
    } catch (error) {
      // 如果获取用户信息失败（如token过期），静默处理
      console.error('刷新用户信息失败:', error)
    }
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4a;
  color: #ffffff;
}

.logo h2 {
  font-size: 18px;
  font-weight: 600;
}

.sidebar-menu {
  border: none;
  height: calc(100vh - 60px);
}

.header {
  background-color: #ffffff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
}

.user-info:hover {
  color: #409eff;
}

.main-content {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>

