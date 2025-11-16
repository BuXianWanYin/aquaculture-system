<template>
  <div class="customer">
    <div class="card-header">
      <span>客户管理</span>
      <el-button v-if="hasPermission('sales:customer:add')" type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增客户
      </el-button>
    </div>
    
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="客户名称">
        <el-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable style="width: 180px;" />
      </el-form-item>
      <el-form-item label="客户类型">
        <el-select v-model="searchForm.customerType" placeholder="请选择类型" clearable style="width: 150px;">
          <el-option label="批发商" value="批发商" />
          <el-option label="零售商" value="零售商" />
          <el-option label="加工企业" value="加工企业" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="customerName" label="客户名称" min-width="180" />
      <el-table-column prop="contactPerson" label="联系人" width="120" />
      <el-table-column prop="contactPhone" label="联系电话" width="140" />
      <el-table-column prop="contactAddress" label="联系地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="customerType" label="客户类型" width="120" />
      <el-table-column prop="creditRating" label="信用等级" width="120">
        <template #default="{ row }">
          <el-tag :type="getCreditTagType(row.creditRating)">
            {{ row.creditRating }}
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
          <el-button v-if="hasPermission('sales:customer:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="hasPermission('sales:customer:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    
    <!-- 客户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="customerFormRef"
        :model="customerForm"
        :rules="customerRules"
        label-width="120px"
      >
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="customerForm.customerName" placeholder="请输入客户名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人">
              <el-input v-model="customerForm.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="customerForm.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="联系地址">
          <el-input v-model="customerForm.contactAddress" placeholder="请输入联系地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户类型">
              <el-select v-model="customerForm.customerType" placeholder="请选择类型" style="width: 100%;">
                <el-option label="批发商" value="批发商" />
                <el-option label="零售商" value="零售商" />
                <el-option label="加工企业" value="加工企业" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信用等级">
              <el-select v-model="customerForm.creditRating" placeholder="请选择信用等级" style="width: 100%;">
                <el-option label="优" value="优" />
                <el-option label="良" value="良" />
                <el-option label="中" value="中" />
                <el-option label="差" value="差" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
import { getCustomerList, saveCustomer, updateCustomer, deleteCustomer } from '@/api/customer'
import { formatDateTime } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增客户')
const isEdit = ref(false)
const customerFormRef = ref(null)

const searchForm = reactive({
  customerName: '',
  customerType: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const customerForm = reactive({
  customerId: null,
  customerName: '',
  contactPerson: '',
  contactPhone: '',
  contactAddress: '',
  customerType: '',
  creditRating: '',
  status: 1,
  creatorId: null
})

const customerRules = {
  customerName: [
    { required: true, message: '请输入客户名称', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCustomerList({
      current: pagination.current,
      size: pagination.size,
      customerName: searchForm.customerName || undefined,
      customerType: searchForm.customerType || undefined
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

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.customerName = ''
  searchForm.customerType = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增客户'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑客户'
  Object.assign(customerForm, {
    customerId: row.customerId,
    customerName: row.customerName,
    contactPerson: row.contactPerson || '',
    contactPhone: row.contactPhone || '',
    contactAddress: row.contactAddress || '',
    customerType: row.customerType || '',
    creditRating: row.creditRating || '',
    status: row.status,
    creatorId: row.creatorId
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该客户吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteCustomer(row.customerId)
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
  if (!customerFormRef.value) return
  await customerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateCustomer(customerForm)
        } else {
          res = await saveCustomer(customerForm)
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

const getCreditTagType = (rating) => {
  if (rating === '优') return 'success'
  if (rating === '良') return ''
  if (rating === '中') return 'warning'
  return 'danger'
}

const resetForm = () => {
  Object.assign(customerForm, {
    customerId: null,
    customerName: '',
    contactPerson: '',
    contactPhone: '',
    contactAddress: '',
    customerType: '',
    creditRating: '',
    status: 1,
    creatorId: null
  })
  customerFormRef.value?.clearValidate()
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
})
</script>

<style scoped>
.customer {
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

