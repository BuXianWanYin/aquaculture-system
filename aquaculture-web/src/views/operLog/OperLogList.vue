<template>
  <div class="oper-log-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志管理</span>
          <div>
            <el-button type="danger" @click="handleClear" :disabled="!canViewLog">
              <el-icon><Delete /></el-icon>
              清空日志
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作模块">
          <el-select v-model="searchForm.module" placeholder="请选择模块" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="用户管理" value="用户管理" />
            <el-option label="角色管理" value="角色管理" />
            <el-option label="养殖品种管理" value="养殖品种管理" />
            <el-option label="养殖区域管理" value="养殖区域管理" />
            <el-option label="设备管理" value="设备管理" />
            <el-option label="养殖计划管理" value="养殖计划管理" />
            <el-option label="产量统计管理" value="产量统计管理" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operType" placeholder="请选择操作类型" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="新增" value="新增" />
            <el-option label="修改" value="修改" />
            <el-option label="删除" value="删除" />
            <el-option label="审核" value="审核" />
            <el-option label="登录" value="登录" />
            <el-option label="登出" value="登出" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="请输入关键词" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 400px;"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 日志表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="真实姓名" min-width="120" />
        <el-table-column prop="module" label="操作模块" min-width="120" />
        <el-table-column prop="operType" label="操作类型" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getOperTypeTagType(row.operType)">
              {{ row.operType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operContent" label="操作内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operTime" label="操作时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.operTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" min-width="130" />
        <el-table-column prop="operResult" label="操作结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.operResult === 1 ? 'success' : 'danger'">
              {{ row.operResult === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" v-if="canViewLog">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="操作日志详情"
      width="800px"
    >
      <el-descriptions :column="2" border v-if="logDetail">
        <el-descriptions-item label="日志ID">{{ logDetail.logId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ logDetail.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ logDetail.realName }}</el-descriptions-item>
        <el-descriptions-item label="操作模块">{{ logDetail.module }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ logDetail.operType }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ formatDateTime(logDetail.operTime) }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ logDetail.ipAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作结果">
          <el-tag :type="logDetail.operResult === 1 ? 'success' : 'danger'">
            {{ logDetail.operResult === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作内容" :span="2">
          <div style="word-break: break-all;">{{ logDetail.operContent }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2" v-if="logDetail.errorMsg">
          <div style="word-break: break-all; color: red;">{{ logDetail.errorMsg }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2" v-if="logDetail.remark">
          <div style="word-break: break-all;">{{ logDetail.remark }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { getOperLogList, getOperLogById, deleteOperLog, clearOperLog } from '@/api/operLog'
import { formatDateTime } from '@/utils/date'
import { useUserStore } from '@/stores/user'
import { usePermission } from '@/composables/usePermission'

const userStore = useUserStore()
const { hasPermission } = usePermission()
const canViewLog = computed(() => {
  return hasPermission('log:view')
})

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const logDetail = ref(null)
const dateRange = ref(null)

const searchForm = reactive({
  module: '',
  operType: '',
  keyword: '',
  startTime: null,
  endTime: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取操作类型标签类型
const getOperTypeTagType = (operType) => {
  const typeMap = {
    '新增': 'success',
    '修改': 'warning',
    '删除': 'danger',
    '审核': 'info',
    '登录': 'success',
    '登出': 'info'
  }
  return typeMap[operType] || ''
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      module: searchForm.module || undefined,
      operType: searchForm.operType || undefined,
      keyword: searchForm.keyword || undefined,
      startTime: searchForm.startTime || undefined,
      endTime: searchForm.endTime || undefined
    }
    const res = await getOperLogList(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.startTime = dateRange.value[0]
    searchForm.endTime = dateRange.value[1]
  } else {
    searchForm.startTime = null
    searchForm.endTime = null
  }
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.module = ''
  searchForm.operType = ''
  searchForm.keyword = ''
  searchForm.startTime = null
  searchForm.endTime = null
  dateRange.value = null
  handleSearch()
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getOperLogById(row.logId)
    if (res.code === 200) {
      logDetail.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载详情失败')
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该日志吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteOperLog(row.logId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 清空日志
const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有操作日志吗？此操作不可恢复！', '警告', {
      type: 'warning',
      confirmButtonText: '确定清空',
      cancelButtonText: '取消'
    })
    const res = await clearOperLog()
    if (res.code === 200) {
      ElMessage.success('清空成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

// 分页
const handleSizeChange = () => {
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.oper-log-list {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

