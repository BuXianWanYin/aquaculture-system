<template>
  <div class="equipment-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备管理</span>
          <el-button v-if="hasPermission('equipment:add')" type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增设备
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="设备名称">
          <el-input v-model="searchForm.equipmentName" placeholder="请输入设备名称" clearable />
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="searchForm.equipmentType" placeholder="请选择设备类型" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="增氧机" value="增氧机" />
            <el-option label="投饵机" value="投饵机" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="equipmentName" label="设备名称" min-width="150" />
        <el-table-column prop="equipmentModel" label="设备型号" min-width="120" />
        <el-table-column prop="equipmentType" label="设备类型" min-width="100" />
        <el-table-column prop="installLocation" label="安装位置" min-width="200" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === 0" type="danger">故障</el-tag>
            <el-tag v-else type="warning">维修中</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastMaintainTime" label="最后维护时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.lastMaintainTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPermission('equipment:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="hasPermission('equipment:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            <span v-if="!hasPermission('equipment:edit') && !hasPermission('equipment:delete')" style="color: #909399;">无操作权限</span>
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
        ref="equipmentFormRef"
        :model="equipmentForm"
        :rules="equipmentRules"
        label-width="120px"
      >
        <el-form-item label="设备名称" prop="equipmentName">
          <el-input v-model="equipmentForm.equipmentName" />
        </el-form-item>
        <el-form-item label="设备型号">
          <el-input v-model="equipmentForm.equipmentModel" />
        </el-form-item>
        <el-form-item label="设备类型" prop="equipmentType">
          <el-select v-model="equipmentForm.equipmentType" placeholder="请选择设备类型" style="width: 100%;">
            <el-option label="增氧机" value="增氧机" />
            <el-option label="投饵机" value="投饵机" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属区域" prop="areaId">
          <el-select v-model="equipmentForm.areaId" placeholder="请选择区域" style="width: 100%;" filterable>
            <el-option 
              v-for="area in areaList" 
              :key="area.areaId" 
              :label="area.areaName" 
              :value="area.areaId" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="安装位置">
          <el-input v-model="equipmentForm.installLocation" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="equipmentForm.quantity" :min="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="维护记录">
          <el-input v-model="equipmentForm.maintainRecord" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="equipmentForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">故障</el-radio>
            <el-radio :label="2">维修中</el-radio>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getEquipmentList, saveEquipment, updateEquipment, deleteEquipment } from '@/api/equipment'
import { getAllAreas } from '@/api/area'
import { formatDateTime } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增设备')
const isEdit = ref(false)
const equipmentFormRef = ref(null)
const areaList = ref([])

const searchForm = reactive({
  equipmentName: '',
  equipmentType: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const equipmentForm = reactive({
  equipmentId: null,
  equipmentName: '',
  equipmentModel: '',
  equipmentType: '',
  areaId: null,
  installLocation: '',
  quantity: 1,
  lastMaintainTime: null,
  maintainRecord: '',
  status: 1
})

const equipmentRules = {
  equipmentName: [
    { required: true, message: '请输入设备名称', trigger: 'blur' }
  ],
  equipmentType: [
    { required: true, message: '请选择设备类型', trigger: 'change' }
  ],
  areaId: [
    { required: true, message: '请选择所属区域', trigger: 'change' }
  ],
  quantity: [
    { required: true, message: '请输入数量', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getEquipmentList({
      current: pagination.current,
      size: pagination.size,
      equipmentName: searchForm.equipmentName || undefined,
      equipmentType: searchForm.equipmentType || undefined
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
  searchForm.equipmentName = ''
  searchForm.equipmentType = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增设备'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑设备'
  Object.assign(equipmentForm, {
    equipmentId: row.equipmentId,
    equipmentName: row.equipmentName,
    equipmentModel: row.equipmentModel || '',
    equipmentType: row.equipmentType,
    areaId: row.areaId,
    installLocation: row.installLocation || '',
    quantity: row.quantity,
    lastMaintainTime: row.lastMaintainTime,
    maintainRecord: row.maintainRecord || '',
    status: row.status
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该设备吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteEquipment(row.equipmentId)
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
  if (!equipmentFormRef.value) return
  await equipmentFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateEquipment(equipmentForm)
        } else {
          res = await saveEquipment(equipmentForm)
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
  Object.assign(equipmentForm, {
    equipmentId: null,
    equipmentName: '',
    equipmentModel: '',
    equipmentType: '',
    areaId: null,
    installLocation: '',
    quantity: 1,
    lastMaintainTime: null,
    maintainRecord: '',
    status: 1
  })
  equipmentFormRef.value?.clearValidate()
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

onMounted(() => {
  loadData()
  loadAreaList()
})
</script>

<style scoped>
.equipment-list {
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

