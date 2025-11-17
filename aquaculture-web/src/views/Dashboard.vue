<template>
  <div class="dashboard">
    <!-- 系统管理员仪表盘：全系统统计、用户管理、系统监控 -->
    <template v-if="isAdminRole">
      <!-- 全系统统计卡片 -->
      <el-row :gutter="20" class="stat-row">
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #409eff;">
              <el-icon size="30"><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.planCount || 0 }}</div>
                <div class="stat-label">养殖计划</div>
            </div>
          </div>
        </el-card>
      </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
          <div class="stat-item">
              <div class="stat-icon" style="background-color: #67c23a;">
              <el-icon size="30"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.yieldCount || 0 }}</div>
              <div class="stat-label">产量统计</div>
            </div>
          </div>
        </el-card>
      </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #e6a23c;">
                <el-icon size="30"><User /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.userCount || 0 }}</div>
                <div class="stat-label">系统用户</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #909399;">
                <el-icon size="30"><MapLocation /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.areaCount || 0 }}</div>
                <div class="stat-label">养殖区域</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 系统监控卡片 -->
      <el-row :gutter="20" class="stat-row">
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #409eff;">
                <el-icon size="30"><Box /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.feedInventoryCount || 0 }}</div>
                <div class="stat-label">饲料种类</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #67c23a;">
                <el-icon size="30"><ShoppingCart /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.salesCount || 0 }}</div>
                <div class="stat-label">销售记录</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #f56c6c;">
                <el-icon size="30"><Warning /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.pendingPlanCount || 0 }}</div>
                <div class="stat-label">待审批计划</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #909399;">
                <el-icon size="30"><Collection /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.breedCount || 0 }}</div>
                <div class="stat-label">养殖品种</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 内容区域：用户管理、系统监控、操作日志 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
          <!-- 待审批计划 -->
          <el-card shadow="hover" class="content-card equal-content-height">
            <template #header>
              <div class="card-header">
                <span><el-icon><Clock /></el-icon> 待审批计划</span>
                <el-button v-if="canAccess(['plan:approve']) && statistics.pendingPlanCount > 0" type="text" size="small" @click="goToPage('/plan?status=0')">
                  查看全部
                </el-button>
              </div>
            </template>
            <div class="pending-list">
              <div v-if="statistics.pendingPlanCount > 0" class="pending-item">
                <el-icon class="pending-icon" style="color: #f56c6c;"><Document /></el-icon>
                <div class="pending-content">
                  <div class="pending-title">待审批养殖计划</div>
                  <div class="pending-count">共 {{ statistics.pendingPlanCount }} 条</div>
                </div>
                <el-button v-if="canAccess(['plan:approve'])" type="primary" size="small" @click="goToPage('/plan?status=0')">去审批</el-button>
              </div>
              <div v-else class="pending-empty">
                <el-icon><CircleCheck /></el-icon>
                <span>暂无待审批计划</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
          <!-- 计划状态统计 -->
          <el-card shadow="hover" class="content-card equal-content-height">
            <template #header>
              <div class="card-header">
                <span><el-icon><DataAnalysis /></el-icon> 计划状态统计</span>
              </div>
            </template>
            <div class="stats-list">
              <div class="stats-item">
                <div class="stats-label">进行中计划</div>
                <div class="stats-value">
                  <span class="stats-number">{{ statistics.ongoingPlanCount || 0 }}</span>
                </div>
              </div>
              <div class="stats-item">
                <div class="stats-label">已完成计划</div>
                <div class="stats-value">
                  <span class="stats-number">{{ statistics.completedPlanCount || 0 }}</span>
                </div>
              </div>
              <div class="stats-item" v-if="statistics.planCount > 0">
                <div class="stats-label">计划完成率</div>
                <div class="stats-value">
                  <el-progress 
                    :percentage="completionRate" 
                    :color="completionRate >= 80 ? '#67c23a' : completionRate >= 60 ? '#e6a23c' : '#f56c6c'"
                    :stroke-width="12"
                  />
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
          <!-- 最近操作日志 -->
          <el-card shadow="hover" class="content-card equal-content-height">
            <template #header>
              <div class="card-header">
                <span><el-icon><List /></el-icon> 最近操作</span>
                <el-button type="text" size="small" @click="goToPage('/operLog')">查看全部</el-button>
              </div>
            </template>
            <div class="log-list">
              <div v-if="recentLogs.length > 0">
                <div 
                  v-for="log in recentLogs" 
                  :key="log.logId" 
                  class="log-item"
                  @click="goToPage('/operLog')"
                >
                  <div class="log-icon">
                    <el-icon><Document /></el-icon>
                  </div>
                  <div class="log-content">
                    <div class="log-title">{{ log.module }} - {{ log.operContent }}</div>
                    <div class="log-meta">{{ formatTime(log.operTime) }}</div>
                  </div>
                  <el-tag :type="log.operResult === 1 ? 'success' : 'danger'" size="small">
                    {{ log.operResult === 1 ? '成功' : '失败' }}
                  </el-tag>
                </div>
              </div>
              <div v-else class="log-empty">
                <el-icon><List /></el-icon>
                <span>暂无操作记录</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 部门管理员仪表盘：部门统计、部门计划、部门人员 -->
    <template v-else-if="isDepartmentManagerRole">
      <!-- 部门统计卡片 -->
      <el-row :gutter="20" class="stat-row">
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #409eff;">
                <el-icon size="30"><Document /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.planCount || 0 }}</div>
                <div class="stat-label">部门计划</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #67c23a;">
                <el-icon size="30"><DataAnalysis /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.yieldCount || 0 }}</div>
                <div class="stat-label">部门产量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #e6a23c;">
                <el-icon size="30"><User /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.userCount || 0 }}</div>
                <div class="stat-label">部门人员</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #909399;">
                <el-icon size="30"><MapLocation /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.areaCount || 0 }}</div>
                <div class="stat-label">部门区域</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 内容区域：部门计划、部门人员、部门统计 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
          <!-- 待审批计划 -->
          <el-card shadow="hover" class="content-card equal-content-height">
            <template #header>
              <div class="card-header">
                <span><el-icon><Clock /></el-icon> 待审批计划</span>
                <el-button v-if="canAccess(['plan:approve']) && statistics.pendingPlanCount > 0" type="text" size="small" @click="goToPage('/plan?status=0')">
                  查看全部
                </el-button>
              </div>
            </template>
            <div class="pending-list">
              <div v-if="statistics.pendingPlanCount > 0" class="pending-item">
                <el-icon class="pending-icon" style="color: #f56c6c;"><Document /></el-icon>
                <div class="pending-content">
                  <div class="pending-title">待审批部门计划</div>
                  <div class="pending-count">共 {{ statistics.pendingPlanCount }} 条</div>
                </div>
                <el-button v-if="canAccess(['plan:approve'])" type="primary" size="small" @click="goToPage('/plan?status=0')">去审批</el-button>
              </div>
              <div v-else class="pending-empty">
                <el-icon><CircleCheck /></el-icon>
                <span>暂无待审批计划</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
          <!-- 最近计划 -->
          <el-card shadow="hover" class="content-card equal-content-height" v-if="canAccess(['plan:view'])">
            <template #header>
              <div class="card-header">
                <span><el-icon><Document /></el-icon> 最近计划</span>
                <el-button type="text" size="small" @click="goToPage('/plan')">查看全部</el-button>
              </div>
            </template>
            <div class="recent-list">
              <div v-if="recentPlans.length > 0">
                <div 
                  v-for="plan in recentPlans" 
                  :key="plan.planId" 
                  class="recent-item"
                  @click="goToPage(`/plan`)"
                >
                  <div class="recent-icon">
                    <el-icon><Document /></el-icon>
                  </div>
                  <div class="recent-content">
                    <div class="recent-title">{{ plan.planName }}</div>
                    <div class="recent-meta">{{ formatTime(plan.createTime) }}</div>
                  </div>
                  <el-tag :type="getPlanStatusType(plan.status)" size="small">{{ getPlanStatusText(plan.status) }}</el-tag>
                </div>
              </div>
              <div v-else class="recent-empty">
                <el-icon><Document /></el-icon>
                <span>暂无计划</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
          <!-- 计划状态统计 -->
          <el-card shadow="hover" class="content-card equal-content-height">
            <template #header>
              <div class="card-header">
                <span><el-icon><DataAnalysis /></el-icon> 计划状态统计</span>
              </div>
            </template>
            <div class="stats-list">
              <div class="stats-item">
                <div class="stats-label">进行中计划</div>
                <div class="stats-value">
                  <span class="stats-number">{{ statistics.ongoingPlanCount || 0 }}</span>
                </div>
              </div>
              <div class="stats-item">
                <div class="stats-label">已完成计划</div>
                <div class="stats-value">
                  <span class="stats-number">{{ statistics.completedPlanCount || 0 }}</span>
                </div>
              </div>
              <div class="stats-item" v-if="statistics.planCount > 0">
                <div class="stats-label">计划完成率</div>
                <div class="stats-value">
                  <el-progress 
                    :percentage="completionRate" 
                    :color="completionRate >= 80 ? '#67c23a' : completionRate >= 60 ? '#e6a23c' : '#f56c6c'"
                    :stroke-width="12"
                  />
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 普通操作员仪表盘：我的计划、我的任务、我的消息 -->
    <template v-else-if="isOperatorRole">
      <!-- 我的数据统计卡片 -->
      <el-row :gutter="20" class="stat-row">
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #409eff;">
                <el-icon size="30"><Document /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.planCount || 0 }}</div>
                <div class="stat-label">我的计划</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #67c23a;">
                <el-icon size="30"><DataAnalysis /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.yieldCount || 0 }}</div>
                <div class="stat-label">我的产量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #f56c6c;">
              <el-icon size="30"><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.pendingPlanCount || 0 }}</div>
                <div class="stat-label">待办任务</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #909399;">
                <el-icon size="30"><MapLocation /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.areaCount || 0 }}</div>
                <div class="stat-label">我的区域</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 内容区域：我的计划、我的消息、我的任务 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
          <!-- 未读消息 -->
          <el-card shadow="hover" class="content-card equal-content-height">
            <template #header>
              <div class="card-header">
                <span><el-icon><Bell /></el-icon> 我的消息</span>
                <el-badge :value="statistics.unreadMessageCount || 0" :hidden="!statistics.unreadMessageCount" />
              </div>
            </template>
            <div class="message-list">
              <div v-if="recentMessages.length > 0">
                <div 
                  v-for="msg in recentMessages" 
                  :key="msg.messageId" 
                  class="message-item"
                  @click="goToPage('/message')"
                >
                  <div class="message-icon">
                    <el-icon><Bell /></el-icon>
                  </div>
                  <div class="message-content">
                    <div class="message-title">{{ msg.messageTitle }}</div>
                    <div class="message-time">{{ formatTime(msg.createTime) }}</div>
                  </div>
                  <el-badge v-if="msg.status === 0" :value="''" class="message-badge" />
                </div>
              </div>
              <div v-else class="message-empty">
                <el-icon><CircleCheck /></el-icon>
                <span>暂无未读消息</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
          <!-- 最近计划 -->
          <el-card shadow="hover" class="content-card equal-content-height" v-if="canAccess(['plan:view'])">
            <template #header>
              <div class="card-header">
                <span><el-icon><Document /></el-icon> 我的计划</span>
                <el-button type="text" size="small" @click="goToPage('/plan')">查看全部</el-button>
              </div>
            </template>
            <div class="recent-list">
              <div v-if="recentPlans.length > 0">
                <div 
                  v-for="plan in recentPlans" 
                  :key="plan.planId" 
                  class="recent-item"
                  @click="goToPage(`/plan`)"
                >
                  <div class="recent-icon">
                    <el-icon><Document /></el-icon>
                  </div>
                  <div class="recent-content">
                    <div class="recent-title">{{ plan.planName }}</div>
                    <div class="recent-meta">{{ formatTime(plan.createTime) }}</div>
                  </div>
                  <el-tag :type="getPlanStatusType(plan.status)" size="small">{{ getPlanStatusText(plan.status) }}</el-tag>
                </div>
              </div>
              <div v-else class="recent-empty">
                <el-icon><Document /></el-icon>
                <span>暂无计划</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
          <!-- 功能模块 -->
          <el-card shadow="hover" class="content-card equal-content-height">
            <template #header>
              <div class="card-header">
                <span><el-icon><Grid /></el-icon> 功能模块</span>
              </div>
            </template>
            <div class="module-grid">
              <div class="module-item" v-if="canAccess(['plan:view'])" @click="goToPage('/plan')">
                <el-icon><Document /></el-icon>
                <span>养殖计划</span>
              </div>
              <div class="module-item" v-if="canAccess(['yield:view'])" @click="goToPage('/yield')">
                <el-icon><DataAnalysis /></el-icon>
                <span>产量统计</span>
              </div>
              <div class="module-item" v-if="canAccess(['feed:purchase:view'])" @click="goToPage('/feed')">
                <el-icon><Box /></el-icon>
                <span>饲料管理</span>
              </div>
              <div class="module-item" v-if="canAccess(['production:feeding:view'])" @click="goToPage('/production')">
                <el-icon><Edit /></el-icon>
                <span>生产记录</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 决策层仪表盘：汇总报表、趋势分析、审批事项 -->
    <template v-else-if="isDecisionMakerRole">
      <!-- 汇总报表卡片 -->
      <el-row :gutter="20" class="stat-row">
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #409eff;">
                <el-icon size="30"><Document /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.planCount || 0 }}</div>
                <div class="stat-label">总计划数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #67c23a;">
                <el-icon size="30"><DataAnalysis /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.yieldCount || 0 }}</div>
                <div class="stat-label">总产量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #e6a23c;">
                <el-icon size="30"><ShoppingCart /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.salesCount || 0 }}</div>
                <div class="stat-label">总销售</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
          <el-card class="stat-card equal-height" shadow="hover">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #909399;">
                <el-icon size="30"><MapLocation /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.areaCount || 0 }}</div>
                <div class="stat-label">养殖区域</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
      <!-- 内容区域：审批事项、趋势分析、汇总报表 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
          <!-- 审批事项 -->
          <el-card shadow="hover" class="content-card equal-content-height">
            <template #header>
              <div class="card-header">
                <span><el-icon><Clock /></el-icon> 审批事项</span>
                <el-button v-if="canAccess(['plan:approve']) && statistics.pendingPlanCount > 0" type="text" size="small" @click="goToPage('/plan?status=0')">
                  查看全部
                </el-button>
              </div>
            </template>
            <div class="pending-list">
              <div v-if="statistics.pendingPlanCount > 0" class="pending-item">
                <el-icon class="pending-icon" style="color: #f56c6c;"><Document /></el-icon>
                <div class="pending-content">
                  <div class="pending-title">待审批计划</div>
                  <div class="pending-count">共 {{ statistics.pendingPlanCount }} 条</div>
                </div>
                <el-button v-if="canAccess(['plan:approve'])" type="primary" size="small" @click="goToPage('/plan?status=0')">去审批</el-button>
              </div>
              <div v-else class="pending-empty">
                <el-icon><CircleCheck /></el-icon>
                <span>暂无待审批事项</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
          <!-- 趋势分析 -->
          <el-card shadow="hover" class="content-card equal-content-height">
            <template #header>
              <div class="card-header">
                <span><el-icon><DataAnalysis /></el-icon> 趋势分析</span>
              </div>
            </template>
            <div class="stats-list">
              <div class="stats-item">
                <div class="stats-label">进行中计划</div>
                <div class="stats-value">
                  <span class="stats-number">{{ statistics.ongoingPlanCount || 0 }}</span>
                </div>
              </div>
              <div class="stats-item">
                <div class="stats-label">已完成计划</div>
                <div class="stats-value">
                  <span class="stats-number">{{ statistics.completedPlanCount || 0 }}</span>
                </div>
              </div>
              <div class="stats-item" v-if="statistics.planCount > 0">
                <div class="stats-label">完成率</div>
                <div class="stats-value">
                  <el-progress 
                    :percentage="completionRate" 
                    :color="completionRate >= 80 ? '#67c23a' : completionRate >= 60 ? '#e6a23c' : '#f56c6c'"
                    :stroke-width="12"
                  />
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
          <!-- 汇总报表 -->
          <el-card shadow="hover" class="content-card equal-content-height">
            <template #header>
              <div class="card-header">
                <span><el-icon><Grid /></el-icon> 汇总报表</span>
              </div>
            </template>
            <div class="summary-stats">
              <div class="summary-item">
                <div class="summary-label">计划总数</div>
                <div class="summary-value">{{ statistics.planCount || 0 }}</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">产量总数</div>
                <div class="summary-value">{{ statistics.yieldCount || 0 }}</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">销售总数</div>
                <div class="summary-value">{{ statistics.salesCount || 0 }}</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">区域总数</div>
                <div class="summary-value">{{ statistics.areaCount || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 系统信息（所有角色可见，底部） -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span><el-icon><InfoFilled /></el-icon> 系统信息</span>
            </div>
          </template>
          <div class="system-info-bottom">
            <div class="info-row">
              <div class="info-item-inline">
                <span class="info-label">系统名称：</span>
                <span class="info-value">水产养殖管理系统</span>
              </div>
              <div class="info-item-inline">
                <span class="info-label">当前用户：</span>
                <span class="info-value">{{ userStore.userInfo?.realName || userStore.userInfo?.username || '未登录' }}</span>
              </div>
              <div class="info-item-inline">
                <span class="info-label">用户角色：</span>
                <span class="info-value">{{ userStore.userInfo?.roleName || '-' }}</span>
              </div>
              <div class="info-item-inline">
                <span class="info-label">登录时间：</span>
                <span class="info-value">{{ new Date().toLocaleString() }}</span>
              </div>
            </div>
          </div>
    </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermission } from '@/composables/usePermission'
import { getStatistics, getRecentPlans, getRecentLogs, getRecentMessages } from '@/api/dashboard'
import { ElMessage } from 'element-plus'
import {
  User,
  Document,
  DataAnalysis,
  Warning,
  MapLocation,
  Collection,
  Box,
  ShoppingCart,
  InfoFilled,
  Clock,
  CircleCheck,
  Edit,
  Grid,
  Bell,
  List
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const { hasAnyPermission, hasPermission } = usePermission()

// 基于权限判断角色
// 系统管理员：有系统管理权限
const isAdminRole = computed(() => {
  return hasPermission('system:user:view') && hasPermission('system:role:view')
})

// 部门管理员：有用户管理和部门管理权限，但没有系统角色权限
const isDepartmentManagerRole = computed(() => {
  return hasPermission('system:user:view') && 
         hasPermission('department:view') && 
         !hasPermission('system:role:view')
})

// 普通操作员：有计划和产量权限，但没有系统管理权限
const isOperatorRole = computed(() => {
  return hasPermission('plan:view') && 
         hasPermission('yield:view') && 
         !hasPermission('system:user:view')
})

// 决策层：有计划和产量权限，还有销售权限，但没有系统管理权限
const isDecisionMakerRole = computed(() => {
  return hasPermission('plan:view') && 
         hasPermission('yield:view') && 
         hasPermission('sales:record:view') && 
         !hasPermission('system:user:view')
})

const statistics = ref({
  userCount: 0,
  planCount: 0,
  yieldCount: 0,
  pendingPlanCount: 0,
  areaCount: 0,
  breedCount: 0,
  feedInventoryCount: 0,
  salesCount: 0,
  ongoingPlanCount: 0,
  completedPlanCount: 0,
  unreadMessageCount: 0
})

const recentPlans = ref([])
const recentLogs = ref([])
const recentMessages = ref([])

// 计算完成率
const completionRate = computed(() => {
  const total = statistics.value.planCount || 0
  const completed = statistics.value.completedPlanCount || 0
  if (total === 0) return 0
  return Math.round((completed / total) * 100)
})

// 权限检查
const canAccess = (permissions) => {
  if (!permissions || !Array.isArray(permissions)) return true
  return hasAnyPermission(permissions)
}

// 跳转页面
const goToPage = (path) => {
  router.push(path)
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
}

// 获取计划状态文本
const getPlanStatusText = (status) => {
  const statusMap = {
    0: '待审批',
    1: '已批准',
    2: '已拒绝',
    3: '进行中',
    4: '已完成'
  }
  return statusMap[status] || '未知'
}

// 获取计划状态类型
const getPlanStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'info',
    2: 'danger',
    3: 'success',
    4: 'success'
  }
  return typeMap[status] || 'info'
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getStatistics()
    if (res.code === 200) {
      statistics.value = { ...statistics.value, ...res.data }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

// 加载最近计划
const loadRecentPlans = async () => {
  if (!canAccess(['plan:view'])) return
  try {
    const res = await getRecentPlans(5)
    if (res.code === 200) {
      recentPlans.value = res.data || []
    }
  } catch (error) {
    console.error('加载最近计划失败:', error)
  }
}

// 加载最近操作日志（有日志查看权限的用户）
const loadRecentLogs = async () => {
  if (!hasPermission('log:view')) return
  try {
    const res = await getRecentLogs(10)
    if (res.code === 200) {
      recentLogs.value = res.data || []
    }
  } catch (error) {
    console.error('加载最近操作日志失败:', error)
  }
}

// 加载未读消息
const loadRecentMessages = async () => {
  try {
    const res = await getRecentMessages(5)
    if (res.code === 200) {
      recentMessages.value = res.data || []
    }
  } catch (error) {
    console.error('加载未读消息失败:', error)
  }
}

onMounted(() => {
  loadStatistics()
  loadRecentPlans()
  loadRecentLogs()
  loadRecentMessages()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
  min-height: calc(100vh - 120px);
  padding-bottom: 20px;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 0;
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
}

.stat-card.equal-height {
  height: 120px;
  display: flex;
  flex-direction: column;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 20px;
  height: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.content-card {
  margin-bottom: 20px;
  height: 100%;
}

.content-card.equal-content-height {
  min-height: 320px;
  display: flex;
  flex-direction: column;
}

.content-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  font-size: 16px;
}

.card-header .el-icon {
  margin-right: 5px;
}

.pending-list {
  min-height: 100px;
  flex: 1;
}

.pending-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background-color: #fef0f0;
  border: 1px solid #fde2e2;
  border-radius: 8px;
}

.pending-icon {
  font-size: 24px;
}

.pending-content {
  flex: 1;
}

.pending-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
  font-weight: 500;
}

.pending-count {
  font-size: 12px;
  color: #909399;
}

.pending-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #909399;
  flex: 1;
}

.pending-empty .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
  color: #c0c4cc;
}

.stats-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  flex: 1;
}

.stats-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.stats-label {
  font-size: 14px;
  color: #909399;
}

.stats-value {
  display: flex;
  align-items: center;
}

.stats-number {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.message-list {
  min-height: 120px;
  max-height: 250px;
  overflow-y: auto;
  flex: 1;
}

.message-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 8px;
  border: 1px solid #f0f0f0;
}

.message-item:hover {
  background-color: #f5f7fa;
  border-color: #409eff;
}

.message-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #ecf5ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409eff;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

.message-badge {
  flex-shrink: 0;
}

.message-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #909399;
  flex: 1;
}

.message-empty .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
  color: #c0c4cc;
}

.recent-list {
  min-height: 120px;
  max-height: 250px;
  overflow-y: auto;
  flex: 1;
}

.recent-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 8px;
  border: 1px solid #f0f0f0;
}

.recent-item:hover {
  background-color: #f5f7fa;
  border-color: #409eff;
}

.recent-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #e6f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409eff;
  flex-shrink: 0;
}

.recent-content {
  flex: 1;
  min-width: 0;
}

.recent-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recent-meta {
  font-size: 12px;
  color: #909399;
}

.recent-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #909399;
  flex: 1;
}

.recent-empty .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
  color: #c0c4cc;
}

.log-list {
  min-height: 120px;
  max-height: 250px;
  overflow-y: auto;
  flex: 1;
}

.log-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 8px;
  border: 1px solid #f0f0f0;
}

.log-item:hover {
  background-color: #f5f7fa;
  border-color: #409eff;
}

.log-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #f0f9ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409eff;
  flex-shrink: 0;
}

.log-content {
  flex: 1;
  min-width: 0;
}

.log-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.log-meta {
  font-size: 12px;
  color: #909399;
}

.log-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #909399;
  flex: 1;
}

.log-empty .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
  color: #c0c4cc;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  flex: 1;
}

.module-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e4e7ed;
  min-height: 100px;
}

.module-item:hover {
  background-color: #ecf5ff;
  border-color: #409eff;
  transform: translateY(-3px);
}

.module-item .el-icon {
  font-size: 32px;
  color: #409eff;
  margin-bottom: 10px;
}

.module-item span {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.summary-stats {
  display: flex;
  flex-direction: column;
  gap: 20px;
  flex: 1;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.summary-label {
  font-size: 14px;
  color: #909399;
}

.summary-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.system-info-bottom {
  padding: 10px 0;
}

.info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  align-items: center;
  justify-content: flex-start;
}

.info-item-inline {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}

.info-item-inline .info-label {
  color: #909399;
  font-size: 14px;
}

.info-item-inline .info-value {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .stat-value {
    font-size: 24px;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
  }

  .module-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .info-row {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .info-item-inline {
    width: 100%;
    justify-content: space-between;
  }

  .content-card.equal-content-height {
    min-height: auto;
  }

  .stat-card.equal-height {
    height: auto;
  }
}
</style>
