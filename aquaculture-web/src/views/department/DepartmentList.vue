<template>
  <div class="department-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>部门管理</span>
          <el-button v-if="hasPermission('department:add')" type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增部门
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="部门名称">
          <el-input v-model="searchForm.departmentName" placeholder="请输入部门名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px;">
            <el-option label="全部" value="" />
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="departmentCode" label="部门编号" min-width="120" />
        <el-table-column prop="departmentName" label="部门名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
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
            <el-button v-if="hasPermission('department:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="hasPermission('department:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            <span v-if="!hasPermission('department:edit') && !hasPermission('department:delete')" style="color: #909399;">无操作权限</span>
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
        ref="departmentFormRef"
        :model="departmentForm"
        :rules="departmentRules"
        label-width="120px"
      >
        <el-form-item label="部门编号" prop="departmentCode">
          <el-input v-model="departmentForm.departmentCode" />
        </el-form-item>
        <el-form-item label="部门名称" prop="departmentName">
          <el-input v-model="departmentForm.departmentName" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="departmentForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="departmentForm.status">
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getDepartmentList, saveDepartment, updateDepartment, deleteDepartment } from '@/api/department'
import { formatDateTime } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const isEdit = ref(false)
const departmentFormRef = ref(null)

const searchForm = reactive({
  departmentName: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const departmentForm = reactive({
  departmentId: null,
  departmentCode: '',
  departmentName: '',
  description: '',
  status: 1,
  managerId: null
})

const departmentRules = {
  departmentCode: [
    { required: true, message: '请输入部门编号', trigger: 'blur' }
  ],
  departmentName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDepartmentList({
      current: pagination.current,
      size: pagination.size,
      departmentName: searchForm.departmentName || undefined,
      status: searchForm.status
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
  searchForm.departmentName = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增部门'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑部门'
  Object.assign(departmentForm, {
    departmentId: row.departmentId,
    departmentCode: row.departmentCode,
    departmentName: row.departmentName,
    description: row.description || '',
    status: row.status,
    managerId: row.managerId
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该部门吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteDepartment(row.departmentId)
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
  if (!departmentFormRef.value) return
  await departmentFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateDepartment(departmentForm)
        } else {
          res = await saveDepartment(departmentForm)
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
  Object.assign(departmentForm, {
    departmentId: null,
    departmentCode: '',
    departmentName: '',
    description: '',
    status: 1,
    managerId: null
  })
  departmentFormRef.value?.clearValidate()
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
.department-list {
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

