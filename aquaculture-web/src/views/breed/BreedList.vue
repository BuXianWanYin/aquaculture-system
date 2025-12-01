<template>
  <div class="breed-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>养殖品种管理</span>
          <el-button v-if="hasPermission('breed:add')" type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增品种
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="品种名称">
          <el-input v-model="searchForm.breedName" placeholder="请输入品种名称" clearable />
        </el-form-item>
        <el-form-item label="品类">
          <el-select v-model="searchForm.category" placeholder="请选择品类" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="鱼类" value="鱼类" />
            <el-option label="甲壳类" value="甲壳类" />
          </el-select>
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
        <el-table-column prop="breedName" label="品种名称" min-width="150" />
        <el-table-column prop="category" label="品类" min-width="100" />
        <el-table-column prop="growthCycle" label="生长周期(天)" min-width="120" />
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column label="适宜水温" min-width="150">
          <template #default="{ row }">
            <span v-if="row.suitableTempMin && row.suitableTempMax">
              {{ row.suitableTempMin }}℃ - {{ row.suitableTempMax }}℃
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPermission('breed:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="hasPermission('breed:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            <span v-if="!hasPermission('breed:edit') && !hasPermission('breed:delete')" style="color: #909399;">无操作权限</span>
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
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="breedFormRef"
        :model="breedForm"
        :rules="breedRules"
        label-width="120px"
      >
        <el-form-item label="品种名称" prop="breedName">
          <el-input v-model="breedForm.breedName" />
        </el-form-item>
        <el-form-item label="品类" prop="category">
          <el-select v-model="breedForm.category" placeholder="请选择品类" style="width: 100%;">
            <el-option label="鱼类" value="鱼类" />
            <el-option label="甲壳类" value="甲壳类" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="生长周期(天)" prop="growthCycle">
          <el-input-number v-model="breedForm.growthCycle" :min="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="breedForm.unit" />
        </el-form-item>
        <el-form-item label="适宜最低水温(℃)">
          <el-input-number v-model="breedForm.suitableTempMin" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="适宜最高水温(℃)">
          <el-input-number v-model="breedForm.suitableTempMax" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="breedForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="图片">
          <div style="display: flex; flex-direction: column; gap: 10px; align-items: flex-start;">
            <el-image
              v-if="breedForm.imageUrl"
              :src="getImageUrl(breedForm.imageUrl)"
              fit="cover"
              style="width: 150px; height: 150px; border-radius: 4px; border: 1px solid #dcdfe6;"
              :preview-src-list="[getImageUrl(breedForm.imageUrl)]"
              :preview-teleported="true"
            />
            <div style="display: flex; gap: 10px; align-items: center;">
              <el-upload
                :action="uploadAction"
                :headers="uploadHeaders"
                :data="{ module: 'breed' }"
                :show-file-list="false"
                :on-success="handleImageSuccess"
                :before-upload="beforeImageUpload"
                accept="image/*"
              >
                <el-button type="primary" size="small">上传图片</el-button>
              </el-upload>
              <el-button
                v-if="breedForm.imageUrl"
                type="danger"
                size="small"
                @click="handleRemoveImage"
              >
                删除图片
              </el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="breedForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
import { getBreedList, saveBreed, updateBreed, deleteBreed } from '@/api/breed'
import { uploadImage } from '@/api/upload'
import { formatDateTime } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增品种')
const isEdit = ref(false)
const breedFormRef = ref(null)

const searchForm = reactive({
  breedName: '',
  category: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const breedForm = reactive({
  breedId: null,
  breedName: '',
  category: '',
  growthCycle: null,
  unit: '公斤',
  suitableTempMin: null,
  suitableTempMax: null,
  description: '',
  imageUrl: '',
  status: 1
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
    breedForm.imageUrl = response.data.filePath
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
  breedForm.imageUrl = ''
}

const breedRules = {
  breedName: [
    { required: true, message: '请输入品种名称', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择品类', trigger: 'change' }
  ],
  growthCycle: [
    { required: true, message: '请输入生长周期', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请输入单位', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBreedList({
      current: pagination.current,
      size: pagination.size,
      breedName: searchForm.breedName || undefined,
      category: searchForm.category || undefined
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
  searchForm.breedName = ''
  searchForm.category = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增品种'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑品种'
  Object.assign(breedForm, {
    breedId: row.breedId,
    breedName: row.breedName,
    category: row.category,
    growthCycle: row.growthCycle,
    unit: row.unit,
    suitableTempMin: row.suitableTempMin,
    suitableTempMax: row.suitableTempMax,
    description: row.description || '',
    imageUrl: row.imageUrl || '',
    status: row.status
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该品种吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteBreed(row.breedId)
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
  if (!breedFormRef.value) return
  await breedFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateBreed(breedForm)
        } else {
          res = await saveBreed(breedForm)
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
  Object.assign(breedForm, {
    breedId: null,
    breedName: '',
    category: '',
    growthCycle: null,
    unit: '公斤',
    suitableTempMin: null,
    suitableTempMax: null,
    description: '',
    imageUrl: '',
    status: 1
  })
  breedFormRef.value?.clearValidate()
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
.breed-list {
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

