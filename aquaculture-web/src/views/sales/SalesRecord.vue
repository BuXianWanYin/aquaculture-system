<template>
  <div class="sales-record">
    <div class="card-header">
      <span>销售记录管理</span>
      <el-button v-if="hasPermission('sales:record:add')" type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增销售记录
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
      <el-form-item label="客户">
        <el-select v-model="searchForm.customerId" placeholder="请选择客户" clearable filterable style="width: 180px;">
          <el-option 
            v-for="customer in customerList" 
            :key="customer.customerId" 
            :label="customer.customerName" 
            :value="customer.customerId" 
          />
        </el-select>
      </el-form-item>
      <el-form-item label="付款状态">
        <el-select v-model="searchForm.paymentStatus" placeholder="请选择状态" clearable style="width: 150px;">
          <el-option label="全部" value="" />
          <el-option label="未付款" :value="0" />
          <el-option label="已付款" :value="1" />
          <el-option label="部分付款" :value="2" />
        </el-select>
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
      <el-table-column prop="customerId" label="客户" min-width="150">
        <template #default="{ row }">
          {{ getCustomerName(row.customerId) }}
        </template>
      </el-table-column>
      <el-table-column prop="salesQuantity" label="销售数量(kg)" width="140" />
      <el-table-column prop="unitPrice" label="单价(元/kg)" width="120" />
      <el-table-column prop="totalAmount" label="总金额(元)" width="120" />
      <el-table-column prop="salesDate" label="销售日期" width="120">
        <template #default="{ row }">
          {{ formatDate(row.salesDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="salesChannel" label="销售渠道" width="120" />
      <el-table-column prop="paymentStatus" label="付款状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getPaymentTagType(row.paymentStatus)">
            {{ getPaymentStatusText(row.paymentStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="invoiceStatus" label="发票状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.invoiceStatus === 1 ? 'success' : 'info'">
            {{ row.invoiceStatus === 1 ? '已开票' : '未开票' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="hasPermission('sales:record:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="hasPermission('sales:record:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    
    <!-- 销售记录表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="recordFormRef"
        :model="recordForm"
        :rules="recordRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产量统计">
              <el-select v-model="recordForm.yieldId" placeholder="请选择产量统计" style="width: 100%;" filterable @change="handleYieldChange" clearable>
                <el-option 
                  v-for="item in yieldList" 
                  :key="item.yieldId" 
                  :label="`${formatDate(item.statisticsDate)} - ${item.actualYield}kg`" 
                  :value="item.yieldId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="养殖计划" prop="planId">
              <el-select v-model="recordForm.planId" placeholder="请选择计划" style="width: 100%;" filterable @change="handlePlanChange" :disabled="!!recordForm.yieldId">
                <el-option 
                  v-for="plan in planList" 
                  :key="plan.planId" 
                  :label="plan.planName" 
                  :value="plan.planId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属区域" prop="areaId">
              <el-select v-model="recordForm.areaId" placeholder="请选择区域" style="width: 100%;" filterable :disabled="!!recordForm.planId || !!recordForm.yieldId">
                <el-option 
                  v-for="area in areaList" 
                  :key="area.areaId" 
                  :label="area.areaName" 
                  :value="area.areaId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="recordForm.customerId" placeholder="请选择客户" style="width: 100%;" filterable>
                <el-option 
                  v-for="customer in customerList" 
                  :key="customer.customerId" 
                  :label="customer.customerName" 
                  :value="customer.customerId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="销售数量(kg)" prop="salesQuantity">
              <el-input-number v-model="recordForm.salesQuantity" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单价(元/kg)" prop="unitPrice">
              <el-input-number v-model="recordForm.unitPrice" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="销售日期" prop="salesDate">
              <el-date-picker 
                v-model="recordForm.salesDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售渠道">
              <el-select v-model="recordForm.salesChannel" placeholder="请选择渠道" style="width: 100%;">
                <el-option label="批发" value="批发" />
                <el-option label="零售" value="零售" />
                <el-option label="加工" value="加工" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="付款方式">
              <el-select v-model="recordForm.paymentMethod" placeholder="请选择付款方式" style="width: 100%;">
                <el-option label="现金" value="现金" />
                <el-option label="转账" value="转账" />
                <el-option label="赊账" value="赊账" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="付款状态">
              <el-select v-model="recordForm.paymentStatus" placeholder="请选择状态" style="width: 100%;">
                <el-option label="未付款" :value="0" />
                <el-option label="已付款" :value="1" />
                <el-option label="部分付款" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发票状态">
              <el-select v-model="recordForm.invoiceStatus" placeholder="请选择状态" style="width: 100%;">
                <el-option label="未开票" :value="0" />
                <el-option label="已开票" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="recordForm.remarks" type="textarea" :rows="2" placeholder="请输入备注" />
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
import { getSalesRecordList, saveSalesRecord, updateSalesRecord, deleteSalesRecord } from '@/api/salesRecord'
import { getAllCustomers } from '@/api/customer'
import { getAllPlans } from '@/api/plan'
import { getAllAreas } from '@/api/area'
import { getAllStatistics } from '@/api/yield'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增销售记录')
const isEdit = ref(false)
const recordFormRef = ref(null)
const planList = ref([])
const areaList = ref([])
const customerList = ref([])
const yieldList = ref([])

const searchForm = reactive({
  planId: null,
  areaId: null,
  customerId: null,
  paymentStatus: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const recordForm = reactive({
  salesId: null,
  yieldId: null,
  planId: null,
  areaId: null,
  breedId: null,
  customerId: null,
  salesQuantity: null,
  unitPrice: null,
  totalAmount: null,
  salesDate: null,
  salesChannel: '',
  paymentMethod: '',
  paymentStatus: 0,
  invoiceStatus: 0,
  remarks: '',
  status: 1,
  creatorId: null
})

const recordRules = {
  planId: [
    { required: true, message: '请选择养殖计划', trigger: 'change' }
  ],
  customerId: [
    { required: true, message: '请选择客户', trigger: 'change' }
  ],
  salesQuantity: [
    { required: true, message: '请输入销售数量', trigger: 'blur' }
  ],
  unitPrice: [
    { required: true, message: '请输入单价', trigger: 'blur' }
  ],
  salesDate: [
    { required: true, message: '请选择销售日期', trigger: 'change' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSalesRecordList({
      current: pagination.current,
      size: pagination.size,
      planId: searchForm.planId || undefined,
      areaId: searchForm.areaId || undefined,
      customerId: searchForm.customerId || undefined,
      paymentStatus: searchForm.paymentStatus
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

const loadPlanList = async () => {
  try {
    const res = await getAllPlans()
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

const loadCustomerList = async () => {
  try {
    const res = await getAllCustomers()
    if (res.code === 200) {
      customerList.value = res.data || []
    }
  } catch (error) {
    console.error('加载客户列表失败', error)
  }
}

const loadYieldList = async () => {
  try {
    const res = await getAllStatistics()
    if (res.code === 200) {
      yieldList.value = res.data || []
    }
  } catch (error) {
    console.error('加载产量统计列表失败', error)
  }
}

const handleYieldChange = (yieldId) => {
  if (yieldId) {
    const yieldItem = yieldList.value.find(y => y.yieldId === yieldId)
    if (yieldItem) {
      recordForm.planId = yieldItem.planId
      recordForm.areaId = yieldItem.areaId
      recordForm.breedId = yieldItem.breedId
    }
  }
}

const handlePlanChange = (planId) => {
  if (planId) {
    const plan = planList.value.find(p => p.planId === planId)
    if (plan) {
      recordForm.areaId = plan.areaId
      recordForm.breedId = plan.breedId
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

const getCustomerName = (customerId) => {
  if (!customerId) return '-'
  const customer = customerList.value.find(c => c.customerId === customerId)
  return customer ? customer.customerName : `客户${customerId}`
}

const getPaymentStatusText = (status) => {
  if (status === 0) return '未付款'
  if (status === 1) return '已付款'
  if (status === 2) return '部分付款'
  return '-'
}

const getPaymentTagType = (status) => {
  if (status === 1) return 'success'
  if (status === 2) return 'warning'
  return 'danger'
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.planId = null
  searchForm.areaId = null
  searchForm.customerId = null
  searchForm.paymentStatus = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增销售记录'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑销售记录'
  Object.assign(recordForm, {
    salesId: row.salesId,
    yieldId: row.yieldId,
    planId: row.planId,
    areaId: row.areaId,
    breedId: row.breedId,
    customerId: row.customerId,
    salesQuantity: row.salesQuantity,
    unitPrice: row.unitPrice,
    totalAmount: row.totalAmount,
    salesDate: row.salesDate,
    salesChannel: row.salesChannel || '',
    paymentMethod: row.paymentMethod || '',
    paymentStatus: row.paymentStatus !== undefined ? row.paymentStatus : 0,
    invoiceStatus: row.invoiceStatus !== undefined ? row.invoiceStatus : 0,
    remarks: row.remarks || '',
    status: row.status,
    creatorId: row.creatorId
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该销售记录吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteSalesRecord(row.salesId)
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
  if (!recordFormRef.value) return
  await recordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateSalesRecord(recordForm)
        } else {
          res = await saveSalesRecord(recordForm)
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

const resetForm = () => {
  Object.assign(recordForm, {
    salesId: null,
    yieldId: null,
    planId: null,
    areaId: null,
    breedId: null,
    customerId: null,
    salesQuantity: null,
    unitPrice: null,
    totalAmount: null,
    salesDate: null,
    salesChannel: '',
    paymentMethod: '',
    paymentStatus: 0,
    invoiceStatus: 0,
    remarks: '',
    status: 1,
    creatorId: null
  })
  recordFormRef.value?.clearValidate()
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
  loadCustomerList()
  loadYieldList()
})
</script>


<style scoped>
.sales-record {
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

