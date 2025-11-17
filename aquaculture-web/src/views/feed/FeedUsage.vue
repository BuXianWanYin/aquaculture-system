<template>
  <div class="feed-usage">
    <div class="card-header">
      <span>饲料使用记录管理</span>
      <el-button v-if="hasPermission('feed:usage:add')" type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增使用记录
      </el-button>
    </div>
    
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="养殖计划">
        <el-select v-model="searchForm.planId" placeholder="请选择计划" clearable filterable style="width: 180px;">
          <el-option 
            v-for="plan in planList" 
            :key="plan.planId" 
            :label="plan.planName" 
            :value="plan.planId" 
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属区域">
        <el-select v-model="searchForm.areaId" placeholder="请选择区域" clearable filterable style="width: 180px;">
          <el-option 
            v-for="area in areaList" 
            :key="area.areaId" 
            :label="area.areaName" 
            :value="area.areaId" 
          />
        </el-select>
      </el-form-item>
      <el-form-item label="饲料名称">
        <el-input v-model="searchForm.feedName" placeholder="请输入饲料名称" clearable style="width: 180px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="planId" label="养殖计划" min-width="150">
        <template #default="{ row }">
          {{ getPlanName(row.planId) }}
        </template>
      </el-table-column>
      <el-table-column prop="areaId" label="所属区域" min-width="120">
        <template #default="{ row }">
          {{ getAreaName(row.areaId) }}
        </template>
      </el-table-column>
      <el-table-column prop="feedName" label="饲料名称" min-width="150" />
      <el-table-column prop="feedType" label="饲料类型" width="140" />
      <el-table-column prop="usageAmount" label="使用数量(kg)" width="140" />
      <el-table-column prop="unitPrice" label="单价(元/kg)" width="120" />
      <el-table-column prop="totalCost" label="总成本(元)" width="120" />
      <el-table-column prop="usageDate" label="使用日期" width="120">
        <template #default="{ row }">
          {{ formatDate(row.usageDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="feedingTime" label="投喂时间" width="120" />
      <el-table-column prop="batchNumber" label="批次号" width="140" />
      <el-table-column prop="createTime" label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="hasPermission('feed:usage:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="hasPermission('feed:usage:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
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
    
    <!-- 使用记录表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="usageFormRef"
        :model="usageForm"
        :rules="usageRules"
        label-width="120px"
        :validate-on-rule-change="false"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养殖计划" prop="planId">
              <el-select v-model="usageForm.planId" placeholder="请选择计划" style="width: 100%;" filterable @change="handlePlanChange">
                <el-option 
                  v-for="plan in planList" 
                  :key="plan.planId" 
                  :label="plan.planName" 
                  :value="plan.planId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属区域" prop="areaId">
              <el-select v-model="usageForm.areaId" placeholder="请选择区域" style="width: 100%;" filterable :disabled="!!usageForm.planId">
                <el-option 
                  v-for="area in areaList" 
                  :key="area.areaId" 
                  :label="area.areaName" 
                  :value="area.areaId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="饲料名称" prop="inventoryId">
          <el-select 
            v-model="usageForm.inventoryId" 
            placeholder="请选择饲料名称" 
            style="width: 100%;" 
            filterable
            @change="handleInventoryChange"
          >
            <el-option 
              v-for="inventory in inventoryList" 
              :key="inventory.inventoryId" 
              :label="inventory.feedName" 
              :value="inventory.inventoryId"
              :disabled="inventory.currentStock <= 0"
            >
              <span>{{ inventory.feedName }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px;">库存: {{ inventory.currentStock }}kg</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="饲料类型" prop="feedType">
              <el-input v-model="usageForm.feedType" disabled placeholder="自动绑定" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用数量(kg)" prop="usageAmount">
              <el-input-number v-model="usageForm.usageAmount" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单价(元/kg)">
              <el-input-number v-model="usageForm.unitPrice" :min="0" :precision="2" style="width: 100%;" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用日期" prop="usageDate">
              <el-date-picker 
                v-model="usageForm.usageDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="投喂时间">
              <el-select v-model="usageForm.feedingTime" placeholder="请选择投喂时间" style="width: 100%;">
                <el-option label="早" value="早" />
                <el-option label="中" value="中" />
                <el-option label="晚" value="晚" />
                <el-option label="早晚" value="早晚" />
                <el-option label="早中晚" value="早中晚" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批次号" prop="batchNumber">
              <!-- 如果有采购记录，显示下拉框 -->
              <el-select 
                v-if="purchaseList.length > 0"
                v-model="usageForm.batchNumber" 
                placeholder="请选择批次号" 
                style="width: 100%;"
              >
                <el-option
                  v-for="purchase in purchaseList"
                  :key="purchase.purchaseId"
                  :label="getBatchOptionLabel(purchase)"
                  :value="purchase.batchNumber || ''"
                />
              </el-select>
              <!-- 如果没有采购记录，显示提示 -->
              <el-input 
                v-else
                v-model="usageForm.batchNumber" 
                placeholder="该饲料暂无采购记录，请先进行采购" 
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="usageForm.remarks" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getFeedUsageList, saveFeedUsage, updateFeedUsage, deleteFeedUsage } from '@/api/feedUsage'
import { getApprovedPlans } from '@/api/plan'
import { getAllAreas } from '@/api/area'
import { getAllFeedInventories } from '@/api/feedInventory'
import { getFeedPurchasesByFeed } from '@/api/feedPurchase'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增使用记录')
const isEdit = ref(false)
const usageFormRef = ref(null)
const planList = ref([])
const areaList = ref([])
const inventoryList = ref([])
const purchaseList = ref([])

const searchForm = reactive({
  planId: null,
  areaId: null,
  feedName: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const usageForm = reactive({
  usageId: null,
  planId: null,
  areaId: null,
  inventoryId: null,
  feedName: '',
  feedType: '',
  usageAmount: null,
  unitPrice: null,
  totalCost: null,
  usageDate: null,
  batchNumber: '',
  feedingTime: '',
  remarks: '',
  status: 1,
  creatorId: null
})

const usageRules = {
  planId: [
    { required: true, message: '请选择养殖计划', trigger: 'change' }
  ],
  inventoryId: [
    { required: true, message: '请选择饲料名称', trigger: 'change' }
  ],
  feedType: [
    { required: true, message: '请选择饲料类型', trigger: 'change' }
  ],
  usageAmount: [
    { required: true, message: '请输入使用数量', trigger: 'blur' }
  ],
  batchNumber: [
    { required: true, message: '请选择批次号', trigger: 'change' }
  ],
  usageDate: [
    { required: true, message: '请选择使用日期', trigger: 'change' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFeedUsageList({
      current: pagination.current,
      size: pagination.size,
      planId: searchForm.planId || undefined,
      areaId: searchForm.areaId || undefined,
      feedName: searchForm.feedName || undefined
    })
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

// 加载计划列表（只加载已审核通过的计划）
const loadPlanList = async () => {
  try {
    const res = await getApprovedPlans()
    if (res.code === 200) {
      planList.value = res.data || []
    }
  } catch (error) {
    console.error('加载计划列表失败', error)
  }
}

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

const loadInventoryList = async () => {
  try {
    const res = await getAllFeedInventories()
    if (res.code === 200) {
      inventoryList.value = res.data || []
    }
  } catch (error) {
    console.error('加载库存列表失败', error)
  }
}

// 处理库存选择变化
const handleInventoryChange = async (inventoryId) => {
  if (inventoryId) {
    const inventory = inventoryList.value.find(inv => inv.inventoryId === inventoryId)
    if (inventory) {
      usageForm.feedName = inventory.feedName
      usageForm.feedType = inventory.feedType
      usageForm.unitPrice = inventory.unitPrice
      
      // 查询该饲料的采购记录
      try {
        const res = await getFeedPurchasesByFeed(inventory.feedName, inventory.feedType)
        if (res.code === 200) {
          purchaseList.value = res.data || []
          
          // 如果有采购记录，默认选择第一条（如果只有一条会自动选中）
          if (purchaseList.value.length > 0) {
            usageForm.batchNumber = purchaseList.value[0].batchNumber || ''
          } else {
            // 如果没有采购记录，清空批次号
            usageForm.batchNumber = ''
          }
        } else {
          purchaseList.value = []
          usageForm.batchNumber = ''
        }
      } catch (error) {
        console.error('加载采购记录失败', error)
        purchaseList.value = []
        usageForm.batchNumber = ''
      }
    }
  } else {
    usageForm.feedName = ''
    usageForm.feedType = ''
    usageForm.unitPrice = null
    usageForm.batchNumber = ''
    purchaseList.value = []
  }
}

// 获取过期日期文本
const getExpiryDateText = (purchase) => {
  if (!purchase || !purchase.expiryDate) {
    return ''
  }
  return `过期日期: ${formatDate(purchase.expiryDate)}`
}

// 获取批次号选项标签（批次号 + 过期日期）
const getBatchOptionLabel = (purchase) => {
  const batchNumber = purchase.batchNumber || '无批次号'
  if (purchase.expiryDate) {
    return `${batchNumber} (过期日期: ${formatDate(purchase.expiryDate)})`
  }
  return batchNumber
}

const handlePlanChange = (planId) => {
  if (planId) {
    const plan = planList.value.find(p => p.planId === planId)
    if (plan) {
      usageForm.areaId = plan.areaId
    }
  }
}

const getPlanName = (planId) => {
  if (!planId) return '-'
  const plan = planList.value.find(p => p.planId === planId)
  return plan ? plan.planName : `计划${planId}`
}

const getAreaName = (areaId) => {
  if (!areaId) return '-'
  const area = areaList.value.find(a => a.areaId === areaId)
  return area ? area.areaName : `区域${areaId}`
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.planId = null
  searchForm.areaId = null
  searchForm.feedName = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增使用记录'
  resetForm()
  purchaseList.value = []
  dialogVisible.value = true
  // 延迟清除验证，确保表单已渲染
  setTimeout(() => {
    usageFormRef.value?.clearValidate()
  }, 100)
}

const handleEdit = async (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑使用记录'
  // 查找对应的库存ID（按饲料名称和类型）
  const inventory = inventoryList.value.find(inv => inv.feedName === row.feedName && inv.feedType === row.feedType)
  Object.assign(usageForm, {
    usageId: row.usageId,
    planId: row.planId,
    areaId: row.areaId,
    inventoryId: inventory ? inventory.inventoryId : null,
    feedName: row.feedName,
    feedType: row.feedType,
    usageAmount: row.usageAmount,
    unitPrice: row.unitPrice,
    totalCost: row.totalCost,
    usageDate: row.usageDate,
    batchNumber: row.batchNumber || '',
    feedingTime: row.feedingTime || '',
    remarks: row.remarks || '',
    status: row.status,
    creatorId: row.creatorId
  })
  
  // 加载该饲料的采购记录
  if (row.feedName && row.feedType) {
    try {
      const res = await getFeedPurchasesByFeed(row.feedName, row.feedType)
      if (res.code === 200) {
        purchaseList.value = res.data || []
      } else {
        purchaseList.value = []
      }
    } catch (error) {
      console.error('加载采购记录失败', error)
      purchaseList.value = []
    }
  } else {
    purchaseList.value = []
  }
  
  dialogVisible.value = true
  // 延迟清除验证，确保表单已渲染
  setTimeout(() => {
    usageFormRef.value?.clearValidate()
  }, 100)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该使用记录吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteFeedUsage(row.usageId)
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

const handleSubmit = async () => {
  if (!usageFormRef.value) return
  await usageFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateFeedUsage(usageForm)
        } else {
          res = await saveFeedUsage(usageForm)
        }
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
          dialogVisible.value = false
          loadData()
          // 刷新库存页面
          window.dispatchEvent(new CustomEvent('refresh-inventory'))
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const resetForm = () => {
  Object.assign(usageForm, {
    usageId: null,
    planId: null,
    areaId: null,
    inventoryId: null,
    feedName: '',
    feedType: '',
    usageAmount: null,
    unitPrice: null,
    totalCost: null,
    usageDate: null,
    batchNumber: '',
    feedingTime: '',
    remarks: '',
    status: 1,
    creatorId: null
  })
  purchaseList.value = []
  usageFormRef.value?.clearValidate()
}

const handleDialogClose = () => {
  resetForm()
}

const handleSizeChange = () => {
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

onMounted(() => {
  loadData()
  loadPlanList()
  loadAreaList()
  loadInventoryList()
})
</script>

<style scoped>
.feed-usage {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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

