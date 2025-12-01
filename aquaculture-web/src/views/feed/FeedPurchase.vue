<template>
  <div class="feed-purchase">
    <div class="card-header">
      <span>饲料采购管理</span>
      <el-button v-if="hasPermission('feed:purchase:add')" type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增采购
      </el-button>
    </div>
    
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="饲料名称">
        <el-input v-model="searchForm.feedName" placeholder="请输入饲料名称" clearable style="width: 180px;" />
      </el-form-item>
      <el-form-item label="饲料类型">
        <el-select v-model="searchForm.feedType" placeholder="请选择类型" clearable style="width: 150px;">
          <el-option label="对虾专用饲料" value="对虾专用饲料" />
          <el-option label="淡水鱼饲料" value="淡水鱼饲料" />
          <el-option label="通用饲料" value="通用饲料" />
        </el-select>
      </el-form-item>
      <el-form-item label="供应商">
        <el-input v-model="searchForm.supplier" placeholder="请输入供应商" clearable style="width: 180px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column label="图片" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.imageUrl"
            :src="getImageUrl(row.imageUrl)"
            :preview-src-list="[getImageUrl(row.imageUrl)]"
            fit="cover"
            style="width: 60px; height: 60px; border-radius: 4px;"
            :preview-teleported="true"
          />
          <span v-else style="color: #909399;">暂无图片</span>
        </template>
      </el-table-column>
      <el-table-column prop="feedName" label="饲料名称" min-width="180" />
      <el-table-column prop="feedType" label="饲料类型" width="140" />
      <el-table-column prop="supplier" label="供应商" min-width="150" />
      <el-table-column prop="purchaseAmount" label="采购数量(kg)" width="140" />
      <el-table-column prop="unitPrice" label="单价(元/kg)" width="120" />
      <el-table-column prop="totalPrice" label="总价(元)" width="120" />
      <el-table-column prop="purchaseDate" label="采购日期" width="120">
        <template #default="{ row }">
          {{ formatDate(row.purchaseDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="batchNumber" label="批次号" width="140" />
      <el-table-column prop="expiryDate" label="保质期至" width="120">
        <template #default="{ row }">
          {{ formatDate(row.expiryDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="hasPermission('feed:purchase:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="hasPermission('feed:purchase:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    
    <!-- 采购表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="purchaseFormRef"
        :model="purchaseForm"
        :rules="getPurchaseRules()"
        label-width="120px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="采购模式" v-if="!isEdit">
          <el-radio-group v-model="purchaseMode" @change="handlePurchaseModeChange">
            <el-radio label="new">新采购</el-radio>
            <el-radio label="existing">已有采购</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item 
          v-if="purchaseMode === 'existing' && !isEdit" 
          label="选择已有采购" 
          prop="existingPurchaseId"
        >
          <el-select 
            v-model="purchaseForm.existingPurchaseId" 
            placeholder="请选择已有采购" 
            style="width: 100%;" 
            filterable
            @change="handleExistingPurchaseChange"
          >
            <el-option 
              v-for="purchase in existingPurchaseList" 
              :key="purchase.purchaseId" 
              :label="`${purchase.feedName} - ${purchase.supplier} - ${purchase.purchaseDate}`" 
              :value="purchase.purchaseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="饲料名称" prop="feedName">
          <el-input v-model="purchaseForm.feedName" placeholder="请输入饲料名称" :disabled="purchaseMode === 'existing' && !isEdit" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="饲料类型" prop="feedType">
              <el-select 
                v-model="purchaseForm.feedType" 
                placeholder="请选择类型" 
                style="width: 100%;"
                :disabled="purchaseMode === 'existing' && !isEdit"
              >
                <el-option label="对虾专用饲料" value="对虾专用饲料" />
                <el-option label="淡水鱼饲料" value="淡水鱼饲料" />
                <el-option label="通用饲料" value="通用饲料" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplier">
              <el-input 
                v-model="purchaseForm.supplier" 
                placeholder="请输入供应商" 
                :disabled="purchaseMode === 'existing' && !isEdit"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购数量(kg)" prop="purchaseAmount">
              <el-input-number v-model="purchaseForm.purchaseAmount" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单价(元/kg)" prop="unitPrice">
              <el-input-number v-model="purchaseForm.unitPrice" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购日期" prop="purchaseDate">
              <el-date-picker 
                v-model="purchaseForm.purchaseDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保质期至">
              <el-date-picker 
                v-model="purchaseForm.expiryDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="批次号">
          <el-input v-model="purchaseForm.batchNumber" placeholder="请输入批次号" />
        </el-form-item>
        <el-form-item label="图片">
          <div style="display: flex; flex-direction: column; gap: 10px; align-items: flex-start;">
            <el-image
              v-if="purchaseForm.imageUrl"
              :src="getImageUrl(purchaseForm.imageUrl)"
              fit="cover"
              style="width: 150px; height: 150px; border-radius: 4px; border: 1px solid #dcdfe6;"
              :preview-src-list="[getImageUrl(purchaseForm.imageUrl)]"
              :preview-teleported="true"
            />
            <div style="display: flex; gap: 10px;">
              <el-upload
                :action="uploadAction"
                :headers="uploadHeaders"
                :data="{ module: 'feed' }"
                :show-file-list="false"
                :on-success="handleImageSuccess"
                :before-upload="beforeImageUpload"
                accept="image/*"
              >
                <el-button type="primary" size="small">上传图片</el-button>
              </el-upload>
              <el-button
                v-if="purchaseForm.imageUrl"
                type="danger"
                size="small"
                @click="handleRemoveImage"
              >
                删除图片
              </el-button>
            </div>
          </div>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getFeedPurchaseList, saveFeedPurchase, updateFeedPurchase, deleteFeedPurchase, getAllFeedPurchases } from '@/api/feedPurchase'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增采购')
const isEdit = ref(false)
const purchaseFormRef = ref(null)
const purchaseMode = ref('new') // 'new' 新采购, 'existing' 已有采购
const existingPurchaseList = ref([])

const searchForm = reactive({
  feedName: '',
  feedType: '',
  supplier: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const purchaseForm = reactive({
  purchaseId: null,
  existingPurchaseId: null,
  feedName: '',
  feedType: '',
  supplier: '',
  purchaseAmount: null,
  unitPrice: null,
  totalPrice: null,
  purchaseDate: null,
  batchNumber: '',
  imageUrl: '',
  expiryDate: null,
  status: 1,
  creatorId: null
})

const uploadAction = '/api/upload/image'
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return {
    Authorization: token ? `Bearer ${token}` : ''
  }
})

const getImageUrl = (imageUrl) => {
  if (!imageUrl) return ''
  if (imageUrl.startsWith('http')) return imageUrl
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseUrl}${imageUrl}`
}

const handleImageSuccess = (response) => {
  if (response.code === 200 && response.data) {
    purchaseForm.imageUrl = response.data.filePath
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

const handleRemoveImage = () => {
  purchaseForm.imageUrl = ''
}

const getPurchaseRules = () => {
  const rules = {
    feedName: [
      { required: true, message: '请输入饲料名称', trigger: 'blur' }
    ],
    feedType: [
      { required: true, message: '请选择饲料类型', trigger: 'change' }
    ],
    purchaseAmount: [
      { required: true, message: '请输入采购数量', trigger: 'blur' }
    ],
    unitPrice: [
      { required: true, message: '请输入单价', trigger: 'blur' }
    ],
    purchaseDate: [
      { required: true, message: '请选择采购日期', trigger: 'change' }
    ]
  }
  
  if (purchaseMode.value === 'existing' && !isEdit.value) {
    rules.existingPurchaseId = [
      { required: true, message: '请选择已有采购', trigger: 'change' }
    ]
  }
  
  return rules
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFeedPurchaseList({
      current: pagination.current,
      size: pagination.size,
      feedName: searchForm.feedName || undefined,
      feedType: searchForm.feedType || undefined,
      supplier: searchForm.supplier || undefined
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
  searchForm.feedName = ''
  searchForm.feedType = ''
  searchForm.supplier = ''
  handleSearch()
}

const loadExistingPurchaseList = async () => {
  try {
    const res = await getAllFeedPurchases()
    if (res.code === 200) {
      existingPurchaseList.value = res.data || []
    }
  } catch (error) {
    console.error('加载已有采购列表失败', error)
  }
}


// 处理采购模式变化
const handlePurchaseModeChange = (mode) => {
  if (mode === 'existing') {
    purchaseForm.existingPurchaseId = null
    purchaseForm.feedName = ''
    purchaseForm.feedType = ''
    purchaseForm.supplier = ''
    purchaseForm.unitPrice = null
    purchaseForm.batchNumber = ''
    purchaseForm.expiryDate = null
  } else {
    purchaseForm.existingPurchaseId = null
  }
}

// 处理已有采购选择变化
const handleExistingPurchaseChange = (purchaseId) => {
  if (purchaseId) {
    const purchase = existingPurchaseList.value.find(p => p.purchaseId === purchaseId)
    if (purchase) {
      purchaseForm.feedName = purchase.feedName
      purchaseForm.feedType = purchase.feedType
      purchaseForm.supplier = purchase.supplier
      purchaseForm.unitPrice = purchase.unitPrice
      purchaseForm.batchNumber = purchase.batchNumber || ''
      purchaseForm.imageUrl = purchase.imageUrl || ''
      purchaseForm.expiryDate = purchase.expiryDate
    }
  }
}

const handleAdd = () => {
  isEdit.value = false
  purchaseMode.value = 'new'
  dialogTitle.value = '新增采购'
  resetForm()
  dialogVisible.value = true
  // 延迟清除验证，确保表单已渲染
  setTimeout(() => {
    purchaseFormRef.value?.clearValidate()
  }, 100)
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑采购'
  Object.assign(purchaseForm, {
    purchaseId: row.purchaseId,
    feedName: row.feedName,
    feedType: row.feedType,
    supplier: row.supplier,
    purchaseAmount: row.purchaseAmount,
    unitPrice: row.unitPrice,
    totalPrice: row.totalPrice,
    purchaseDate: row.purchaseDate,
    batchNumber: row.batchNumber || '',
    imageUrl: row.imageUrl || '',
    expiryDate: row.expiryDate,
    status: row.status,
    creatorId: row.creatorId
  })
  dialogVisible.value = true
  // 延迟清除验证，确保表单已渲染
  setTimeout(() => {
    purchaseFormRef.value?.clearValidate()
  }, 100)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该采购记录吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteFeedPurchase(row.purchaseId)
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
  if (!purchaseFormRef.value) return
  await purchaseFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateFeedPurchase(purchaseForm)
        } else {
          res = await saveFeedPurchase(purchaseForm)
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
  Object.assign(purchaseForm, {
    purchaseId: null,
    existingPurchaseId: null,
    feedName: '',
    feedType: '',
    supplier: '',
    purchaseAmount: null,
    unitPrice: null,
    totalPrice: null,
    purchaseDate: null,
    batchNumber: '',
    imageUrl: '',
    expiryDate: null,
    status: 1,
    creatorId: null
  })
  purchaseFormRef.value?.clearValidate()
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
  loadExistingPurchaseList()
})
</script>

<style scoped>
.feed-purchase {
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

