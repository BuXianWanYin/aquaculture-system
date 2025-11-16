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
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/user" v-if="canAccess([1, 2])">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/role" v-if="canAccess([1])">
          <el-icon><UserFilled /></el-icon>
          <span>角色管理</span>
        </el-menu-item>
        <el-sub-menu index="base" v-if="canAccess([1, 2, 3])">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>基础数据管理</span>
          </template>
          <el-menu-item index="/breed">
            <el-icon><Collection /></el-icon>
            <span>养殖品种</span>
          </el-menu-item>
          <el-menu-item index="/area">
            <el-icon><MapLocation /></el-icon>
            <span>养殖区域</span>
          </el-menu-item>
          <el-menu-item index="/equipment">
            <el-icon><Tools /></el-icon>
            <span>设备管理</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/plan" v-if="canAccess([1, 2, 3, 4])">
          <el-icon><Document /></el-icon>
          <span>养殖计划管理</span>
        </el-menu-item>
        <el-menu-item index="/yield" v-if="canAccess([1, 2, 3, 4])">
          <el-icon><DataAnalysis /></el-icon>
          <span>产量统计管理</span>
        </el-menu-item>
        <el-menu-item index="/operLog" v-if="canAccess([1, 2])">
          <el-icon><Document /></el-icon>
          <span>操作日志管理</span>
        </el-menu-item>
        <el-menu-item index="/statistic" v-if="canAccess([1, 2, 4])">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据报表与分析</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
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
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import {
  House,
  User,
  UserFilled,
  Setting,
  Collection,
  MapLocation,
  Tools,
  Avatar,
  ArrowDown,
  Document,
  DataAnalysis
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '首页')

// 权限检查函数
const canAccess = (roles) => {
  if (!roles || !userStore.userInfo) return true
  return roles.includes(userStore.userInfo.roleId)
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
}
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

