<template>
  <div class="role-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增角色
          </el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 角色表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="roleDesc" label="角色描述" min-width="200" />
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
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link size="small" @click="handleAssignPermission(row)">分配权限</el-button>
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
    
    <!-- 角色表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" />
        </el-form-item>
        <el-form-item label="角色描述" prop="roleDesc">
          <el-input v-model="roleForm.roleDesc" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
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
    
    <!-- 权限分配对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      :title="`为角色「${currentRole?.roleName}」分配权限`"
      width="600px"
      @close="handlePermissionDialogClose"
    >
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTreeData"
        :props="{ children: 'children', label: 'permissionName' }"
        show-checkbox
        node-key="permissionId"
        :default-expand-all="true"
        @check="handlePermissionCheck"
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePermissionSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getRoleList, saveRole, updateRole, deleteRole } from '@/api/role'
import { getAllPermissions, getPermissionsByRoleId, assignPermissionsToRole } from '@/api/permission'
import { formatDateTime } from '@/utils/date'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const isEdit = ref(false)
const roleFormRef = ref(null)
const permissionDialogVisible = ref(false)
const permissionTreeRef = ref(null)
const currentRole = ref(null)
const permissionTreeData = ref([])
const checkedPermissionIds = ref([])

const searchForm = reactive({
  roleName: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const roleForm = reactive({
  roleId: null,
  roleName: '',
  roleDesc: '',
  status: 1
})

const roleRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoleList({
      current: pagination.current,
      size: pagination.size,
      roleName: searchForm.roleName || undefined
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

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.roleName = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  dialogVisible.value = true
  resetForm()
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  Object.assign(roleForm, {
    roleId: row.roleId,
    roleName: row.roleName,
    roleDesc: row.roleDesc || '',
    status: row.status
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteRole(row.roleId)
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
  if (!roleFormRef.value) return
  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateRole(roleForm)
        } else {
          res = await saveRole(roleForm)
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
  Object.assign(roleForm, {
    roleId: null,
    roleName: '',
    roleDesc: '',
    status: 1
  })
  roleFormRef.value?.clearValidate()
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

// 构建权限树形结构
const buildPermissionTree = (permissions) => {
  const tree = []
  const map = {}
  
  // 创建映射
  permissions.forEach(permission => {
    map[permission.permissionId] = { ...permission, children: [] }
  })
  
  // 构建树
  permissions.forEach(permission => {
    const node = map[permission.permissionId]
    if (permission.parentId && permission.parentId !== 0 && map[permission.parentId]) {
      map[permission.parentId].children.push(node)
    } else {
      tree.push(node)
    }
  })
  
  return tree
}

// 分配权限
const handleAssignPermission = async (row) => {
  currentRole.value = row
  permissionDialogVisible.value = true
  
  try {
    // 加载所有权限
    const allRes = await getAllPermissions()
    if (allRes.code === 200) {
      permissionTreeData.value = buildPermissionTree(allRes.data || [])
    }
    
    // 加载角色已分配的权限
    const roleRes = await getPermissionsByRoleId(row.roleId)
    if (roleRes.code === 200) {
      checkedPermissionIds.value = (roleRes.data || []).map(p => p.permissionId)
      
      // 等待DOM更新后设置选中状态，这样会自动触发父子级联
      await nextTick()
      if (permissionTreeRef.value && checkedPermissionIds.value.length > 0) {
        permissionTreeRef.value.setCheckedKeys(checkedPermissionIds.value)
      }
    }
  } catch (error) {
    ElMessage.error('加载权限数据失败')
  }
}

// 处理权限树节点选中事件
const handlePermissionCheck = (data, checkedInfo) => {
  // 当父节点被选中时，el-tree会自动处理子节点的选中
  // 这里可以添加额外的逻辑，比如记录操作日志等
  // 由于移除了check-strictly，父子节点会自动关联
}

// 提交权限分配
const handlePermissionSubmit = async () => {
  if (!permissionTreeRef.value) return
  
  // 获取所有选中的节点（包括父节点和子节点）
  const checkedKeys = permissionTreeRef.value.getCheckedKeys()
  // 获取半选中的节点（父节点被部分选中时）
  const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
  // 合并所有选中的权限ID
  const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]
  
  try {
    const res = await assignPermissionsToRole(currentRole.value.roleId, allCheckedKeys)
    if (res.code === 200) {
      ElMessage.success('权限分配成功')
      permissionDialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error(error.message || '权限分配失败')
  }
}

// 关闭权限分配对话框
const handlePermissionDialogClose = () => {
  currentRole.value = null
  permissionTreeData.value = []
  checkedPermissionIds.value = []
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.role-list {
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

