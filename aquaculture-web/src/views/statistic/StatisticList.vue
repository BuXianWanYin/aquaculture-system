<template>
  <div class="statistic-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>数据报表与分析</span>
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
        <el-col :span="12">
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
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="chart-header">
                <span>部门产量对比</span>
                <el-date-picker
                  v-model="chartDateRange4"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  size="small"
                  @change="loadDepartmentComparison"
                />
              </div>
            </template>
            <div ref="departmentComparisonChartRef" style="height: 300px; width: 100%;"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { Document, CircleCheck, Loading, Clock } from '@element-plus/icons-vue'
import { 
  getMonthlyYieldTrend, getBreedYieldRatio, getAreaYieldComparison, getPlanCompletionStats,
  getDepartmentYieldComparison
} from '@/api/statistic'
import { getAllAreas } from '@/api/area'
import { getAllBreeds } from '@/api/breed'
import { getAllDepartments } from '@/api/department'
import * as echarts from 'echarts'

const chartDateRange = ref(null)
const chartDateRange2 = ref(null)
const chartDateRange3 = ref(null)
const chartDateRange4 = ref(null)

const planStats = ref({
  totalCount: 0,
  completedCount: 0,
  inProgressCount: 0,
  pendingCount: 0,
  completionRate: 0
})

// 区域、品种和部门列表（用于ID到名称的映射）
const areaList = ref([])
const breedList = ref([])
const departmentList = ref([])


const monthlyTrendChartRef = ref(null)
const breedRatioChartRef = ref(null)
const areaComparisonChartRef = ref(null)
const departmentComparisonChartRef = ref(null)
let monthlyTrendChart = null
let breedRatioChart = null
let areaComparisonChart = null
let departmentComparisonChart = null


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
      
      // 收集所有月份
      const allMonthsSet = new Set()
      data.forEach(item => {
        if (item.monthlyData) {
          Object.keys(item.monthlyData).forEach(month => allMonthsSet.add(month))
        }
      })
      const months = Array.from(allMonthsSet).sort()
      
      // 构建每个部门的数据系列
      const series = []
      const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#606266']
      
      data.forEach((item, index) => {
        const departmentId = item.departmentId
        const departmentName = getDepartmentName(departmentId)
        const monthlyData = item.monthlyData || {}
        
        // 为每个月份构建数据，如果该部门在某个月没有数据，则为0
        const yields = months.map(month => monthlyData[month] || 0)
        
        series.push({
          name: departmentName,
          type: 'line',
          smooth: true,
          data: yields,
          itemStyle: { color: colors[index % colors.length] }
        })
      })
      
      const option = {
        title: { text: '月度产量趋势（按部门）', left: 'center' },
        tooltip: { 
          trigger: 'axis',
          axisPointer: { type: 'cross' }
        },
        legend: {
          data: data.map(item => getDepartmentName(item.departmentId)),
          bottom: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: { 
          type: 'category', 
          boundaryGap: false,
          data: months 
        },
        yAxis: { type: 'value', name: '产量(公斤)' },
        series: series
      }
      
      await nextTick()
      // 确保 DOM 已渲染
      if (!monthlyTrendChartRef.value) {
        console.warn('月度趋势图表容器未找到，延迟重试')
        setTimeout(() => loadMonthlyTrend(), 200)
        return
      }
      // 如果图表已存在，先销毁再重新初始化
      if (monthlyTrendChart) {
        monthlyTrendChart.dispose()
        monthlyTrendChart = null
      }
      try {
        monthlyTrendChart = echarts.init(monthlyTrendChartRef.value)
        monthlyTrendChart.setOption(option)
      } catch (error) {
        console.error('初始化月度趋势图表失败', error)
      }
    }
  } catch (error) {
    console.error('加载月度趋势失败', error)
  }
}

// 根据ID获取品种名称
const getBreedName = (breedId) => {
  if (!breedId) return '-'
  const breed = breedList.value.find(b => b.breedId === breedId)
  return breed ? breed.breedName : `品种${breedId}`
}

// 根据ID获取区域名称
const getAreaName = (areaId) => {
  if (!areaId) return '-'
  const area = areaList.value.find(a => a.areaId === areaId)
  return area ? area.areaName : `区域${areaId}`
}

// 根据ID获取部门名称
const getDepartmentName = (departmentId) => {
  if (!departmentId) return '-'
  const department = departmentList.value.find(d => d.departmentId === departmentId)
  return department ? department.departmentName : `部门${departmentId}`
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
        name: getBreedName(item.breedId)
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
      // 确保 DOM 已渲染
      if (!breedRatioChartRef.value) {
        console.warn('品种占比图表容器未找到，延迟重试')
        setTimeout(() => loadBreedRatio(), 200)
        return
      }
      // 如果图表已存在，先销毁再重新初始化
      if (breedRatioChart) {
        breedRatioChart.dispose()
        breedRatioChart = null
      }
      try {
        breedRatioChart = echarts.init(breedRatioChartRef.value)
        breedRatioChart.setOption(option)
      } catch (error) {
        console.error('初始化品种占比图表失败', error)
      }
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
      
      // 按产量降序排序，便于查看
      const sortedData = [...data].sort((a, b) => {
        const yieldA = parseFloat(a.yield) || 0
        const yieldB = parseFloat(b.yield) || 0
        return yieldB - yieldA
      })
      
      const areas = sortedData.map(item => getAreaName(item.areaId))
      const yields = sortedData.map(item => item.yield)
      
      const option = {
        title: { text: '区域产量对比', left: 'center' },
        tooltip: { 
          trigger: 'axis',
          formatter: function(params) {
            const param = params[0]
            return `${param.name}<br/>产量: ${param.value}公斤`
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: { 
          type: 'category', 
          data: areas,
          axisLabel: {
            rotate: 45, // 如果区域名称太长，旋转45度
            interval: 0 // 显示所有标签
          }
        },
        yAxis: { type: 'value', name: '产量(公斤)' },
        series: [{
          data: yields,
          type: 'bar',
          itemStyle: { color: '#409eff' },
          label: {
            show: true,
            position: 'top',
            formatter: '{c}公斤'
          }
        }]
      }
      
      await nextTick()
      // 确保 DOM 已渲染
      if (!areaComparisonChartRef.value) {
        console.warn('区域对比图表容器未找到，延迟重试')
        setTimeout(() => loadAreaComparison(), 200)
        return
      }
      // 如果图表已存在，先销毁再重新初始化
      if (areaComparisonChart) {
        areaComparisonChart.dispose()
        areaComparisonChart = null
      }
      try {
        areaComparisonChart = echarts.init(areaComparisonChartRef.value)
        areaComparisonChart.setOption(option)
      } catch (error) {
        console.error('初始化区域对比图表失败', error)
      }
    }
  } catch (error) {
    console.error('加载区域对比失败', error)
  }
}

// 加载部门产量对比
const loadDepartmentComparison = async () => {
  try {
    const params = {}
    if (chartDateRange4.value && chartDateRange4.value.length === 2) {
      params.startDate = chartDateRange4.value[0]
      params.endDate = chartDateRange4.value[1]
    }
    const res = await getDepartmentYieldComparison(params)
    if (res.code === 200) {
      const data = res.data || []
      const departments = data.map(item => getDepartmentName(item.departmentId))
      const yields = data.map(item => item.yield)
      
      const option = {
        title: { text: '部门产量对比', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: departments },
        yAxis: { type: 'value', name: '产量(公斤)' },
        series: [{
          data: yields,
          type: 'bar',
          itemStyle: { color: '#67c23a' }
        }]
      }
      
      await nextTick()
      // 确保 DOM 已渲染
      if (!departmentComparisonChartRef.value) {
        console.warn('部门对比图表容器未找到，延迟重试')
        setTimeout(() => loadDepartmentComparison(), 200)
        return
      }
      // 如果图表已存在，先销毁再重新初始化
      if (departmentComparisonChart) {
        departmentComparisonChart.dispose()
        departmentComparisonChart = null
      }
      try {
        departmentComparisonChart = echarts.init(departmentComparisonChartRef.value)
        departmentComparisonChart.setOption(option)
      } catch (error) {
        console.error('初始化部门对比图表失败', error)
      }
    }
  } catch (error) {
    console.error('加载部门对比失败', error)
  }
}


// 加载区域列表
const loadAreaList = async () => {
  try {
    const res = await getAllAreas()
    if (res.code === 200) {
      areaList.value = res.data || []
    }
  } catch (error) {
    console.error('加载区域列表失败', error)
  }
}

// 加载品种列表
const loadBreedList = async () => {
  try {
    const res = await getAllBreeds()
    if (res.code === 200) {
      breedList.value = res.data || []
    }
  } catch (error) {
    console.error('加载品种列表失败', error)
  }
}

// 加载部门列表
const loadDepartmentList = async () => {
  try {
    const res = await getAllDepartments()
    if (res.code === 200) {
      departmentList.value = res.data || []
    }
  } catch (error) {
    console.error('加载部门列表失败', error)
  }
}

onMounted(() => {
  loadPlanStats()
  loadAreaList()
  loadBreedList()
  loadDepartmentList()
  // 延迟加载图表，确保 DOM 已渲染
  nextTick(() => {
    setTimeout(() => {
      loadMonthlyTrend()
      loadBreedRatio()
      loadAreaComparison()
      loadDepartmentComparison()
    }, 100)
  })
})

// 组件卸载时清理图表实例
onBeforeUnmount(() => {
  if (monthlyTrendChart) {
    monthlyTrendChart.dispose()
    monthlyTrendChart = null
  }
  if (breedRatioChart) {
    breedRatioChart.dispose()
    breedRatioChart = null
  }
  if (areaComparisonChart) {
    areaComparisonChart.dispose()
    areaComparisonChart = null
  }
  if (departmentComparisonChart) {
    departmentComparisonChart.dispose()
    departmentComparisonChart = null
  }
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
</style>

