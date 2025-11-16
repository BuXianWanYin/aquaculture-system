<template>
  <div class="statistic-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>数据报表与分析</span>
          <el-button type="primary" @click="handleAdd" v-if="hasPermission">
            <el-icon><Plus /></el-icon>
            新增统计
          </el-button>
        </div>
      </template>
      
      <!-- 统计卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #409eff;">
                <el-icon size="30"><Document /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ planStats.totalCount || 0 }}</div>
                <div class="stat-label">计划总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #67c23a;">
                <el-icon size="30"><CircleCheck /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ planStats.completedCount || 0 }}</div>
                <div class="stat-label">已完成</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #e6a23c;">
                <el-icon size="30"><Loading /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ planStats.inProgressCount || 0 }}</div>
                <div class="stat-label">执行中</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon" style="background-color: #f56c6c;">
                <el-icon size="30"><Clock /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ planStats.pendingCount || 0 }}</div>
                <div class="stat-label">待审批</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 图表区域 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="chart-header">
                <span>月度产量趋势</span>
                <el-date-picker
                  v-model="chartDateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  size="small"
                  @change="loadMonthlyTrend"
                />
              </div>
            </template>
            <div ref="monthlyTrendChartRef" style="height: 300px; width: 100%;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="chart-header">
                <span>品种产量占比</span>
                <el-date-picker
                  v-model="chartDateRange2"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  size="small"
                  @change="loadBreedRatio"
                />
              </div>
            </template>
            <div ref="breedRatioChartRef" style="height: 300px; width: 100%;"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="24">
          <el-card>
            <template #header>
              <div class="chart-header">
                <span>区域产量对比</span>
                <el-date-picker
                  v-model="chartDateRange3"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  size="small"
                  @change="loadAreaComparison"
                />
              </div>
            </template>
            <div ref="areaComparisonChartRef" style="height: 300px; width: 100%;"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="统计指标">
          <el-input v-model="searchForm.statisticName" placeholder="请输入统计指标" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item label="统计维度">
          <el-select v-model="searchForm.statisticDimension" placeholder="请选择维度" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="部门" value="部门" />
            <el-option label="区域" value="区域" />
            <el-option label="品种" value="品种" />
            <el-option label="时间" value="时间" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 统计结果表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="statisticName" label="统计指标" min-width="150" />
        <el-table-column prop="statisticDimension" label="统计维度" min-width="100" />
        <el-table-column prop="dimensionValue" label="维度值" min-width="120" />
        <el-table-column prop="statisticValue" label="统计值" min-width="120">
          <template #default="{ row }">
            {{ row.statisticValue }} {{ row.statisticUnit || '' }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" min-width="120">
          <template #default="{ row }">
            {{ formatDate(row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="endDate" label="结束日期" min-width="120">
          <template #default="{ row }">
            {{ formatDate(row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="calculateTime" label="计算时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.calculateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" v-if="hasPermission">
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
    
    <!-- 统计结果表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="statisticFormRef"
        :model="statisticForm"
        :rules="statisticRules"
        label-width="100px"
      >
        <el-form-item label="统计指标" prop="statisticName">
          <el-input v-model="statisticForm.statisticName" placeholder="请输入统计指标名称" />
        </el-form-item>
        <el-form-item label="统计维度" prop="statisticDimension">
          <el-select v-model="statisticForm.statisticDimension" placeholder="请选择统计维度" style="width: 100%;">
            <el-option label="部门" value="部门" />
            <el-option label="区域" value="区域" />
            <el-option label="品种" value="品种" />
            <el-option label="时间" value="时间" />
          </el-select>
        </el-form-item>
        <el-form-item label="维度值" prop="dimensionValue">
          <el-input v-model="statisticForm.dimensionValue" placeholder="请输入维度值" />
        </el-form-item>
        <el-form-item label="统计值" prop="statisticValue">
          <el-input-number v-model="statisticForm.statisticValue" :precision="2" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="统计单位" prop="statisticUnit">
          <el-input v-model="statisticForm.statisticUnit" placeholder="请输入统计单位" />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="statisticDateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Document, CircleCheck, Loading, Clock } from '@element-plus/icons-vue'
import { 
  getStatisticList, saveStatistic, updateStatistic, deleteStatistic,
  getMonthlyYieldTrend, getBreedYieldRatio, getAreaYieldComparison, getPlanCompletionStats
} from '@/api/statistic'
import { formatDateTime, formatDate } from '@/utils/date'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts'

const userStore = useUserStore()
const hasPermission = computed(() => {
  return userStore.userInfo && (userStore.userInfo.roleId === 1 || userStore.userInfo.roleId === 2)
})

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增统计')
const isEdit = ref(false)
const statisticFormRef = ref(null)
const dateRange = ref(null)
const chartDateRange = ref(null)
const chartDateRange2 = ref(null)
const chartDateRange3 = ref(null)
const statisticDateRange = ref(null)

const planStats = ref({
  totalCount: 0,
  completedCount: 0,
  inProgressCount: 0,
  pendingCount: 0,
  completionRate: 0
})

const searchForm = reactive({
  statisticName: '',
  statisticDimension: '',
  dimensionValue: '',
  startDate: null,
  endDate: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const statisticForm = reactive({
  statisticId: null,
  statisticName: '',
  statisticDimension: '',
  dimensionValue: '',
  startDate: null,
  endDate: null,
  statisticValue: null,
  statisticUnit: ''
})

const statisticRules = {
  statisticName: [
    { required: true, message: '请输入统计指标名称', trigger: 'blur' }
  ],
  statisticDimension: [
    { required: true, message: '请选择统计维度', trigger: 'change' }
  ],
  statisticValue: [
    { required: true, message: '请输入统计值', trigger: 'blur' }
  ]
}

const monthlyTrendChartRef = ref(null)
const breedRatioChartRef = ref(null)
const areaComparisonChartRef = ref(null)
let monthlyTrendChart = null
let breedRatioChart = null
let areaComparisonChart = null

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      statisticName: searchForm.statisticName || undefined,
      statisticDimension: searchForm.statisticDimension || undefined,
      dimensionValue: searchForm.dimensionValue || undefined,
      startDate: searchForm.startDate || undefined,
      endDate: searchForm.endDate || undefined
    }
    const res = await getStatisticList(params)
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

// 加载计划完成情况
const loadPlanStats = async () => {
  try {
    const res = await getPlanCompletionStats()
    if (res.code === 200) {
      planStats.value = res.data || {}
    }
  } catch (error) {
    console.error('加载计划统计失败', error)
  }
}

// 加载月度产量趋势
const loadMonthlyTrend = async () => {
  try {
    const params = {}
    if (chartDateRange.value && chartDateRange.value.length === 2) {
      params.startDate = chartDateRange.value[0]
      params.endDate = chartDateRange.value[1]
    }
    const res = await getMonthlyYieldTrend(params)
    if (res.code === 200) {
      const data = res.data || []
      const months = data.map(item => item.month)
      const yields = data.map(item => item.yield)
      
      const option = {
        title: { text: '月度产量趋势', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: months },
        yAxis: { type: 'value', name: '产量(公斤)' },
        series: [{
          data: yields,
          type: 'line',
          smooth: true,
          areaStyle: {}
        }]
      }
      
      await nextTick()
      if (!monthlyTrendChart) {
        monthlyTrendChart = echarts.init(document.querySelector('.statistic-list .el-card:nth-child(2) .el-card__body > div'))
      }
      monthlyTrendChart.setOption(option)
    }
  } catch (error) {
    console.error('加载月度趋势失败', error)
  }
}

// 加载品种产量占比
const loadBreedRatio = async () => {
  try {
    const params = {}
    if (chartDateRange2.value && chartDateRange2.value.length === 2) {
      params.startDate = chartDateRange2.value[0]
      params.endDate = chartDateRange2.value[1]
    }
    const res = await getBreedYieldRatio(params)
    if (res.code === 200) {
      const data = res.data || []
      const chartData = data.map(item => ({
        value: item.yield,
        name: `品种${item.breedId}`
      }))
      
      const option = {
        title: { text: '品种产量占比', left: 'center' },
        tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c}公斤 ({d}%)' },
        series: [{
          name: '产量占比',
          type: 'pie',
          radius: '60%',
          data: chartData,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      }
      
      await nextTick()
      if (!breedRatioChart) {
        breedRatioChart = echarts.init(document.querySelector('.statistic-list .el-card:nth-child(3) .el-card__body > div'))
      }
      breedRatioChart.setOption(option)
    }
  } catch (error) {
    console.error('加载品种占比失败', error)
  }
}

// 加载区域产量对比
const loadAreaComparison = async () => {
  try {
    const params = {}
    if (chartDateRange3.value && chartDateRange3.value.length === 2) {
      params.startDate = chartDateRange3.value[0]
      params.endDate = chartDateRange3.value[1]
    }
    const res = await getAreaYieldComparison(params)
    if (res.code === 200) {
      const data = res.data || []
      const areas = data.map(item => `区域${item.areaId}`)
      const yields = data.map(item => item.yield)
      
      const option = {
        title: { text: '区域产量对比', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: areas },
        yAxis: { type: 'value', name: '产量(公斤)' },
        series: [{
          data: yields,
          type: 'bar',
          itemStyle: { color: '#409eff' }
        }]
      }
      
      await nextTick()
      if (!areaComparisonChart) {
        areaComparisonChart = echarts.init(document.querySelector('.statistic-list .el-card:nth-child(4) .el-card__body > div'))
      }
      areaComparisonChart.setOption(option)
    }
  } catch (error) {
    console.error('加载区域对比失败', error)
  }
}

// 搜索
const handleSearch = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.startDate = dateRange.value[0]
    searchForm.endDate = dateRange.value[1]
  } else {
    searchForm.startDate = null
    searchForm.endDate = null
  }
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.statisticName = ''
  searchForm.statisticDimension = ''
  searchForm.dimensionValue = ''
  searchForm.startDate = null
  searchForm.endDate = null
  dateRange.value = null
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增统计'
  dialogVisible.value = true
  resetForm()
}

// 查看
const handleView = (row) => {
  isEdit.value = true
  dialogTitle.value = '查看统计'
  Object.assign(statisticForm, {
    statisticId: row.statisticId,
    statisticName: row.statisticName,
    statisticDimension: row.statisticDimension,
    dimensionValue: row.dimensionValue,
    startDate: row.startDate,
    endDate: row.endDate,
    statisticValue: row.statisticValue,
    statisticUnit: row.statisticUnit
  })
  if (row.startDate && row.endDate) {
    statisticDateRange.value = [row.startDate, row.endDate]
  }
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该统计结果吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteStatistic(row.statisticId)
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

// 提交表单
const handleSubmit = async () => {
  if (!statisticFormRef.value) return
  await statisticFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (statisticDateRange.value && statisticDateRange.value.length === 2) {
          statisticForm.startDate = statisticDateRange.value[0]
          statisticForm.endDate = statisticDateRange.value[1]
        }
        
        let res
        if (isEdit.value) {
          res = await updateStatistic(statisticForm)
        } else {
          res = await saveStatistic(statisticForm)
        }
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
          dialogVisible.value = false
          loadData()
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(statisticForm, {
    statisticId: null,
    statisticName: '',
    statisticDimension: '',
    dimensionValue: '',
    startDate: null,
    endDate: null,
    statisticValue: null,
    statisticUnit: ''
  })
  statisticDateRange.value = null
  statisticFormRef.value?.clearValidate()
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
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
  loadPlanStats()
  loadMonthlyTrend()
  loadBreedRatio()
  loadAreaComparison()
})
</script>

<style scoped>
.statistic-list {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-card {
  margin-bottom: 0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 50px;
  height: 50px;
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
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart-header {
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

