<template>
  <div class="permission-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增权限
          </el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="模块">
          <el-select v-model="searchForm.module" placeholder="请选择模块" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="系统管理" value="system" />
            <el-option label="养殖计划" value="plan" />
            <el-option label="区域管理" value="area" />
            <el-option label="品种管理" value="breed" />
            <el-option label="设备管理" value="equipment" />
            <el-option label="产量管理" value="yield" />
            <el-option label="统计报表" value="statistics" />
            <el-option label="仪表盘" value="dashboard" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="请输入权限名称或标识" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 权限表格 -->
      <el-table 
        :data="tableData" 
        v-loading="loading" 
        border 
        stripe 
        style="width: 100%"
        row-key="permissionId"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        default-expand-all
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="permissionName" label="权限名称" min-width="200" />
        <el-table-column prop="permissionCode" label="权限标识" min-width="200" />
        <el-table-column prop="module" label="所属模块" min-width="120" />
        <el-table-column prop="parentId" label="父权限ID" width="120">
          <template #default="{ row }">
            <span v-if="row.parentId && row.parentId !== 0">{{ row.parentId }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
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
    
    <!-- 权限表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="permissionFormRef"
        :model="permissionForm"
        :rules="permissionRules"
        label-width="120px"
      >
        <el-form-item label="权限标识" prop="permissionCode">
          <el-input v-model="permissionForm.permissionCode" placeholder="如: system:user:view" />
        </el-form-item>
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="permissionForm.permissionName" placeholder="如: 用户查看" />
        </el-form-item>
        <el-form-item label="所属模块" prop="module">
          <el-select v-model="permissionForm.module" placeholder="请选择模块" style="width: 100%;">
            <el-option label="系统管理" value="system" />
            <el-option label="养殖计划" value="plan" />
            <el-option label="区域管理" value="area" />
            <el-option label="品种管理" value="breed" />
            <el-option label="设备管理" value="equipment" />
            <el-option label="产量管理" value="yield" />
            <el-option label="统计报表" value="statistics" />
            <el-option label="仪表盘" value="dashboard" />
          </el-select>
        </el-form-item>
        <el-form-item label="父权限" prop="parentId">
          <el-select 
            v-model="permissionForm.parentId" 
            placeholder="请选择父权限（可选）" 
            clearable
            filterable
            style="width: 100%;"
          >
            <el-option
              v-for="item in parentPermissionOptions"
              :key="item.permissionId"
              :label="item.permissionName"
              :value="item.permissionId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="permissionForm.sortOrder" :min="0" style="width: 100%;" />
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
import { 
  getPermissionList, 
  getAllPermissions,
  savePermission, 
  updatePermission, 
  deletePermission 
} from '@/api/permission'
import { formatDateTime } from '@/utils/date'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增权限')
const isEdit = ref(false)
const permissionFormRef = ref(null)
const parentPermissionOptions = ref([])

const searchForm = reactive({
  module: '',
  keyword: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const permissionForm = reactive({
  permissionId: null,
  permissionCode: '',
  permissionName: '',
  module: '',
  parentId: null,
  sortOrder: 0
})

const permissionRules = {
  permissionCode: [
    { required: true, message: '请输入权限标识', trigger: 'blur' }
  ],
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  module: [
    { required: true, message: '请选择所属模块', trigger: 'change' }
  ]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getPermissionList({
      current: pagination.current,
      size: pagination.size,
      module: searchForm.module || undefined,
      keyword: searchForm.keyword || undefined
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

// 加载父权限选项
const loadParentPermissions = async () => {
  try {
    const res = await getAllPermissions()
    if (res.code === 200) {
      parentPermissionOptions.value = res.data || []
    }
  } catch (error) {
    console.error('加载父权限失败', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.module = ''
  searchForm.keyword = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增权限'
  dialogVisible.value = true
  resetForm()
  loadParentPermissions()
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑权限'
  Object.assign(permissionForm, {
    permissionId: row.permissionId,
    permissionCode: row.permissionCode,
    permissionName: row.permissionName,
    module: row.module,
    parentId: row.parentId || null,
    sortOrder: row.sortOrder || 0
  })
  dialogVisible.value = true
  loadParentPermissions()
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该权限吗？删除后无法恢复！', '提示', {
      type: 'warning'
    })
    const res = await deletePermission(row.permissionId)
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
  if (!permissionFormRef.value) return
  await permissionFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updatePermission(permissionForm)
        } else {
          res = await savePermission(permissionForm)
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
  Object.assign(permissionForm, {
    permissionId: null,
    permissionCode: '',
    permissionName: '',
    module: '',
    parentId: null,
    sortOrder: 0
  })
  permissionFormRef.value?.clearValidate()
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
})
</script>

<style scoped>
.permission-list {
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

