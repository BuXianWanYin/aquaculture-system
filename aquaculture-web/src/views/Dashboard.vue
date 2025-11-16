<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #409eff;">
              <el-icon size="30"><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">0</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #67c23a;">
              <el-icon size="30"><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">0</div>
              <div class="stat-label">计划总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #e6a23c;">
              <el-icon size="30"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">0</div>
              <div class="stat-label">产量统计</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #f56c6c;">
              <el-icon size="30"><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">0</div>
              <div class="stat-label">待处理事项</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card class="welcome-card" style="margin-top: 20px;">
      <h2>欢迎使用水产养殖管理系统</h2>
      <p>请从左侧菜单选择功能模块</p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.welcome-card {
  text-align: center;
  padding: 40px;
}

.welcome-card h2 {
  color: #303133;
  margin-bottom: 10px;
}

.welcome-card p {
  color: #909399;
  font-size: 14px;
}
</style>

