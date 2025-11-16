<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div>
            <el-badge :value="pendingCount" :hidden="pendingCount === 0" class="pending-badge">
              <el-button type="warning" @click="activeTab = 'pending'" v-if="userStore.userInfo?.roleId === 1">
                待审核用户
              </el-button>
            </el-badge>
            <el-button type="primary" @click="handleAdd" style="margin-left: 10px;">
              <el-icon><Plus /></el-icon>
              新增用户
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="用户列表" name="list">
          <!-- 搜索表单 -->
          <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.roleId" placeholder="请选择角色" clearable style="width: 200px;">
            <el-option label="全部" value="" />
            <el-option 
              v-for="role in roleList" 
              :key="role.roleId" 
              :label="role.roleName" 
              :value="role.roleId" 
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 用户表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="真实姓名" min-width="120" />
        <el-table-column prop="roleName" label="角色" min-width="120" />
        <el-table-column prop="phone" label="联系方式" min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else-if="row.status === 0" type="danger">禁用</el-tag>
            <el-tag v-else-if="row.status === 2" type="warning">待审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 2 && userStore.userInfo?.roleId === 1" type="success" link size="small" @click="handleApprove(row)">
              审核
            </el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link size="small" @click="handleResetPassword(row)">重置密码</el-button>
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
        </el-tab-pane>
        
        <!-- 待审核用户标签页 -->
        <el-tab-pane label="待审核用户" name="pending" v-if="userStore.userInfo?.roleId === 1">
          <el-table :data="pendingUsers" v-loading="pendingLoading" border stripe style="width: 100%">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="username" label="用户名" min-width="120" />
            <el-table-column prop="realName" label="真实姓名" min-width="120" />
            <el-table-column prop="roleName" label="角色" min-width="120" />
            <el-table-column prop="phone" label="联系方式" min-width="150" />
            <el-table-column prop="address" label="地址" min-width="200" />
            <el-table-column prop="createTime" label="注册时间" min-width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="success" link size="small" @click="handleApprove(row)">
                  审核
                </el-button>
                <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="userForm.password" type="password" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="userForm.roleId" placeholder="请选择角色" style="width: 100%;">
            <el-option 
              v-for="role in roleList" 
              :key="role.roleId" 
              :label="role.roleName" 
              :value="role.roleId" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="联系方式" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="userForm.address" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
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
    
    <!-- 审核用户对话框 -->
    <el-dialog
      v-model="approveDialogVisible"
      title="审核用户"
      width="600px"
      @close="handleApproveDialogClose"
    >
      <el-form
        ref="approveFormRef"
        :model="approveForm"
        :rules="approveRules"
        label-width="120px"
      >
        <el-form-item label="用户名">
          <el-input v-model="approveForm.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="approveForm.realName" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="approveForm.roleName" disabled />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="approveForm.phone" disabled />
        </el-form-item>
        <el-form-item label="分配区域" prop="areaId">
          <el-select 
            v-model="approveForm.areaId" 
            placeholder="请选择养殖区域" 
            style="width: 100%;"
            clearable
          >
            <el-option
              v-for="area in areaList"
              :key="area.areaId"
              :label="area.areaName"
              :value="area.areaId"
            />
          </el-select>
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            审核通过后，将为用户分配该区域
          </div>
        </el-form-item>
        <el-form-item label="审核结果" prop="status">
          <el-radio-group v-model="approveForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="0">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="approveForm.remark" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入审核备注（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleApproveSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, saveUser, updateUser, deleteUser, resetPassword, getPendingUsers, approveUser } from '@/api/user'
import { getAllRoles } from '@/api/role'
import { getAllAreas } from '@/api/area'
import { useUserStore } from '@/stores/user'
import { formatDateTime } from '@/utils/date'

const userStore = useUserStore()
const loading = ref(false)
const pendingLoading = ref(false)
const tableData = ref([])
const pendingUsers = ref([])
const roleList = ref([])
const areaList = ref([])
const dialogVisible = ref(false)
const approveDialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const isEdit = ref(false)
const activeTab = ref('list')
const userFormRef = ref(null)
const approveFormRef = ref(null)

const searchForm = reactive({
  username: '',
  roleId: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const userForm = reactive({
  userId: null,
  username: '',
  password: '',
  realName: '',
  roleId: null,
  phone: '',
  address: '',
  status: 1
})

const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const approveForm = reactive({
  userId: null,
  username: '',
  realName: '',
  roleName: '',
  phone: '',
  areaId: null,
  status: 1, // 1-通过，0-拒绝
  remark: ''
})

const approveRules = {
  status: [
    { required: true, message: '请选择审核结果', trigger: 'change' }
  ]
}

// 待审核用户数量
const pendingCount = computed(() => {
  return pendingUsers.value.length
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      current: pagination.current,
      size: pagination.size,
      username: searchForm.username || undefined,
      roleId: searchForm.roleId || undefined
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
  searchForm.username = ''
  searchForm.roleId = null
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  dialogVisible.value = true
  resetForm()
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  Object.assign(userForm, {
    userId: row.userId,
    username: row.username,
    password: '',
    realName: row.realName,
    roleId: row.roleId,
    phone: row.phone || '',
    address: row.address || '',
    status: row.status
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteUser(row.userId)
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

// 重置密码
const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm('确定要重置该用户的密码吗？', '提示', {
      type: 'warning',
      inputPlaceholder: '请输入新密码',
      inputType: 'password'
    })
    const res = await resetPassword({
      userId: row.userId,
      newPassword: '123456'
    })
    if (res.code === 200) {
      ElMessage.success('密码重置成功，新密码为：123456')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置密码失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateUser(userForm)
        } else {
          res = await saveUser(userForm)
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
  Object.assign(userForm, {
    userId: null,
    username: '',
    password: '',
    realName: '',
    roleId: null,
    phone: '',
    address: '',
    status: 1
  })
  userFormRef.value?.clearValidate()
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

// 加载待审核用户列表
const loadPendingUsers = async () => {
  if (userStore.userInfo?.roleId !== 1) return // 只有管理员可以查看
  
  pendingLoading.value = true
  try {
    const res = await getPendingUsers()
    if (res.code === 200) {
      pendingUsers.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载待审核用户失败')
  } finally {
    pendingLoading.value = false
  }
}

// 标签页切换
const handleTabChange = (tabName) => {
  if (tabName === 'pending') {
    loadPendingUsers()
  } else if (tabName === 'list') {
    loadData()
  }
}

// 审核用户
const handleApprove = (row) => {
  Object.assign(approveForm, {
    userId: row.userId,
    username: row.username,
    realName: row.realName || '',
    roleName: row.roleName || '',
    phone: row.phone || '',
    areaId: row.areaId || row.farmId || null,
    status: 1, // 默认通过
    remark: ''
  })
  approveDialogVisible.value = true
}

// 提交审核
const handleApproveSubmit = async () => {
  if (!approveFormRef.value) return
  
  await approveFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 如果审核通过但没有选择区域，给出提示
        if (approveForm.status === 1 && !approveForm.areaId) {
          await ElMessageBox.confirm(
            '审核通过但未分配区域，用户将无法正常使用系统功能。是否继续？',
            '提示',
            {
              type: 'warning',
              confirmButtonText: '继续',
              cancelButtonText: '取消'
            }
          )
        }
        
        const res = await approveUser({
          userId: approveForm.userId,
          status: approveForm.status,
          farmId: approveForm.areaId, // 后端接口参数名是farmId，但实际是areaId
          remark: approveForm.remark || undefined
        })
        
        if (res.code === 200) {
          ElMessage.success(approveForm.status === 1 ? '审核通过' : '审核拒绝')
          approveDialogVisible.value = false
          // 刷新列表
          if (activeTab.value === 'pending') {
            loadPendingUsers()
          } else {
            loadData()
          }
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.message || '审核失败')
        }
      }
    }
  })
}

// 关闭审核对话框
const handleApproveDialogClose = () => {
  Object.assign(approveForm, {
    userId: null,
    username: '',
    realName: '',
    roleName: '',
    phone: '',
    areaId: null,
    status: 1,
    remark: ''
  })
  approveFormRef.value?.clearValidate()
}

// 加载角色列表
const loadRoleList = async () => {
  try {
    const res = await getAllRoles()
    if (res.code === 200) {
      roleList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载角色列表失败')
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

onMounted(() => {
  loadData()
  loadRoleList()
  if (userStore.userInfo?.roleId === 1) {
    loadAreaList()
    loadPendingUsers()
  }
})
</script>

<style scoped>
.user-list {
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

.pending-badge {
  margin-right: 10px;
}
</style>

