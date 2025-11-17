<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div>
            <el-badge :value="pendingCount" :hidden="pendingCount === 0" class="pending-badge">
              <el-button type="warning" @click="activeTab = 'pending'" v-if="hasPermission('system:user:view')">
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
            <el-button v-if="row.status === 2 && hasPermission('system:user:approve')" type="success" link size="small" @click="handleApprove(row)">
              审核
            </el-button>
            <el-button 
              type="primary" 
              link 
              size="small" 
              @click="handleEdit(row)"
              :disabled="isUserAdmin(row.roleName)"
              :title="isUserAdmin(row.roleName) ? '系统管理员不允许编辑' : ''"
            >
              编辑
            </el-button>
            <el-button type="warning" link size="small" @click="handleResetPassword(row)">重置密码</el-button>
            <el-button 
              type="danger" 
              link 
              size="small" 
              @click="handleDelete(row)"
              :disabled="isUserAdmin(row.roleName)"
              :title="isUserAdmin(row.roleName) ? '系统管理员不允许删除' : ''"
            >
              删除
            </el-button>
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
        <el-tab-pane label="待审核用户" name="pending" v-if="hasPermission('system:user:view')">
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
                <el-button 
                  type="danger" 
                  link 
                  size="small" 
                  @click="handleDelete(row)"
                  :disabled="isUserAdmin(row.roleName)"
                  :title="isUserAdmin(row.roleName) ? '系统管理员不允许删除' : ''"
                >
                  删除
                </el-button>
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
          <el-select 
            v-model="userForm.roleId" 
            placeholder="请选择角色" 
            style="width: 100%;" 
            @change="handleRoleChange"
            :disabled="isEditingAdmin"
          >
            <el-option 
              v-for="role in availableRolesForAdd" 
              :key="role.roleId" 
              :label="role.roleName" 
              :value="role.roleId" 
            />
          </el-select>
        </el-form-item>
        <!-- 根据角色显示不同的绑定选项 -->
        <el-form-item 
          label="所属部门" 
          prop="departmentId" 
          v-if="isUserDepartmentManager"
        >
          <el-select v-model="userForm.departmentId" placeholder="请选择部门" style="width: 100%;" filterable>
            <el-option
              v-for="dept in departmentList"
              :key="dept.departmentId"
              :label="dept.departmentName"
              :value="dept.departmentId"
            />
          </el-select>
        </el-form-item>
        <el-form-item 
          label="所属区域" 
          prop="areaId" 
          v-if="isUserOperator"
        >
          <el-select 
            v-model="userForm.areaId" 
            placeholder="请选择区域" 
            style="width: 100%;" 
            filterable
            :loading="!areaList || areaList.length === 0"
          >
            <el-option
              v-for="area in areaList"
              :key="area.areaId"
              :label="area.areaName"
              :value="area.areaId"
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
        <!-- 根据被审批用户的角色显示不同的绑定选项 -->
        <el-form-item 
          label="分配部门" 
          prop="departmentId" 
          v-if="isApproveUserDepartmentManager"
        >
          <el-select 
            v-model="approveForm.departmentId" 
            placeholder="请选择部门" 
            style="width: 100%;"
            clearable
            filterable
          >
            <el-option
              v-for="dept in departmentList"
              :key="dept.departmentId"
              :label="dept.departmentName"
              :value="dept.departmentId"
            />
          </el-select>
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            审核通过后，将为部门管理员分配该部门
          </div>
        </el-form-item>
        <el-form-item 
          label="分配区域" 
          prop="areaId" 
          v-if="isApproveUserOperator"
        >
          <el-select 
            v-model="approveForm.areaId" 
            placeholder="请选择养殖区域" 
            style="width: 100%;"
            clearable
            filterable
            :loading="!filteredAreaList || filteredAreaList.length === 0"
          >
            <el-option
              v-for="area in filteredAreaList"
              :key="area.areaId"
              :label="area.areaName"
              :value="area.areaId"
            />
          </el-select>
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            审核通过后，将为操作员分配该区域
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
import { getAllDepartments } from '@/api/department'
import { useUserStore } from '@/stores/user'
import { usePermission } from '@/composables/usePermission'
import { formatDateTime } from '@/utils/date'
import { isDepartmentManager, isOperator, isAdmin } from '@/constants/role'

const userStore = useUserStore()
const { hasPermission } = usePermission()
const loading = ref(false)
const pendingLoading = ref(false)
const tableData = ref([])
const pendingUsers = ref([])
const roleList = ref([])
const areaList = ref([])
const departmentList = ref([])
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
  roleName: '', // 角色名称
  departmentId: null, // 部门ID（部门管理员使用）
  areaId: null, // 区域ID（操作员使用）
  phone: '',
  address: '',
  status: 1
})

// 根据角色ID获取角色名称
const getUserRoleName = (roleId) => {
  if (!roleId || !roleList.value || roleList.value.length === 0) return ''
  const role = roleList.value.find(r => r.roleId === roleId)
  return role ? role.roleName : ''
}

// 判断是否为部门管理员角色（基于角色名称）
const isUserDepartmentManager = computed(() => {
  const roleName = userForm.roleName || getUserRoleName(userForm.roleId)
  return isDepartmentManager(roleName)
})

// 判断是否为操作员角色（基于角色名称）
const isUserOperator = computed(() => {
  const roleName = userForm.roleName || getUserRoleName(userForm.roleId)
  return isOperator(roleName)
})

// 判断用户是否为系统管理员（基于角色名称）
const isUserAdmin = (roleName) => {
  return isAdmin(roleName)
}

// 获取可选择的角色列表（新增用户时排除系统管理员，编辑系统管理员时显示当前角色但禁用）
const availableRolesForAdd = computed(() => {
  // 如果是编辑模式，且当前编辑的用户是系统管理员，显示当前角色但会被禁用
  if (isEdit.value && isUserAdmin(userForm.roleName)) {
    // 如果正在编辑系统管理员，显示当前角色（下拉框会被禁用）
    const currentRole = roleList.value.find(role => role.roleId === userForm.roleId)
    return currentRole ? [currentRole] : []
  }
  if (isEdit.value) {
    // 编辑非系统管理员时显示所有角色
    return roleList.value
  }
  // 新增时排除系统管理员
  return roleList.value.filter(role => !isAdmin(role.roleName))
})

// 判断编辑的用户是否为系统管理员
const isEditingAdmin = computed(() => {
  return isEdit.value && isUserAdmin(userForm.roleName)
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
  roleId: null, // 被审批用户的角色ID
  phone: '',
  departmentId: null, // 部门ID（部门管理员使用）
  areaId: null, // 区域ID（操作员使用）
  status: 1, // 1-通过，0-拒绝
  remark: ''
})

// 判断被审批用户是否为部门管理员角色（基于角色名称）
const isApproveUserDepartmentManager = computed(() => {
  return isDepartmentManager(approveForm.roleName)
})

// 判断被审批用户是否为操作员角色（基于角色名称）
const isApproveUserOperator = computed(() => {
  return isOperator(approveForm.roleName)
})

// 根据被审批用户的部门筛选区域列表（如果是操作员）
const filteredAreaList = computed(() => {
  if (!isApproveUserOperator.value || !approveForm.departmentId) {
    return areaList.value
  }
  // 只显示该部门下的区域
  return areaList.value.filter(area => area.departmentId === approveForm.departmentId)
})

const approveRules = {
  status: [
    { required: true, message: '请选择审核结果', trigger: 'change' }
  ],
  departmentId: [
    { 
      validator: (rule, value, callback) => {
        if (approveForm.status === 1 && isApproveUserDepartmentManager.value && !value) {
          callback(new Error('部门管理员审核通过时必须分配部门'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ],
  areaId: [
    { 
      validator: (rule, value, callback) => {
        if (approveForm.status === 1 && isApproveUserOperator.value && !value) {
          callback(new Error('操作员审核通过时必须分配区域'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
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

// 角色变化处理
const handleRoleChange = (roleId) => {
  // 更新角色名称
  userForm.roleName = getUserRoleName(roleId)
  
  // 根据角色名称清除之前的选择
  const roleName = userForm.roleName
  if (!isDepartmentManager(roleName)) {
    userForm.departmentId = null
  }
  if (!isOperator(roleName)) {
    userForm.areaId = null
  }
}

// 编辑
const handleEdit = async (row) => {
  // 检查是否为系统管理员
  const roleName = row.roleName || getUserRoleName(row.roleId)
  if (isUserAdmin(roleName)) {
    ElMessage.warning('系统管理员不允许编辑')
    return
  }
  
  // 确保区域列表已加载（如果是操作员）
  if (isOperator(roleName) && (!areaList.value || areaList.value.length === 0)) {
    await loadAreaList()
  }
  
  // 确保部门列表已加载（如果是部门管理员）
  if (isDepartmentManager(roleName) && (!departmentList.value || departmentList.value.length === 0)) {
    await loadDepartmentList()
  }
  
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  Object.assign(userForm, {
    userId: row.userId,
    username: row.username,
    password: '',
    realName: row.realName,
    roleId: row.roleId,
    roleName: roleName,
    departmentId: row.departmentId || null,
    areaId: row.areaId || null,
    phone: row.phone || '',
    address: row.address || '',
    status: row.status
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  // 检查是否为系统管理员
  const roleName = row.roleName || getUserRoleName(row.roleId)
  if (isUserAdmin(roleName)) {
    ElMessage.warning('系统管理员不允许删除')
    return
  }
  
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
        // 新增用户时，检查密码是否为空
        if (!isEdit.value) {
          if (!userForm.password || userForm.password.trim() === '') {
            ElMessage.error('请输入密码')
            return
          }
        }
        
        let res
        if (isEdit.value) {
          res = await updateUser(userForm)
        } else {
          // 新增用户时，确保传递密码字段
          const submitData = { ...userForm }
          res = await saveUser(submitData)
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
    roleName: '',
    departmentId: null,
    areaId: null,
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
  if (!hasPermission('system:user:view')) return // 需要用户查看权限
  
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
const handleApprove = async (row) => {
  const roleName = row.roleName || getUserRoleName(row.roleId)
  
  // 确保区域列表已加载（如果是操作员）
  if (isOperator(roleName)) {
    if (!areaList.value || areaList.value.length === 0) {
      await loadAreaList()
    }
    // 如果区域列表已加载但被审核用户有部门ID，需要加载部门列表以便筛选区域
    if (row.departmentId && (!departmentList.value || departmentList.value.length === 0)) {
      await loadDepartmentList()
    }
  }
  
  // 确保部门列表已加载（如果是部门管理员）
  if (isDepartmentManager(roleName) && (!departmentList.value || departmentList.value.length === 0)) {
    await loadDepartmentList()
  }
  
  Object.assign(approveForm, {
    userId: row.userId,
    username: row.username,
    realName: row.realName || '',
    roleName: roleName,
    roleId: row.roleId || null,
    phone: row.phone || '',
    departmentId: row.departmentId || null,
    areaId: row.areaId || null,
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
        // 根据角色验证必填字段
        if (approveForm.status === 1) {
          if (isApproveUserDepartmentManager.value && !approveForm.departmentId) {
            ElMessage.warning('部门管理员审核通过时必须分配部门')
            return
          }
          if (isApproveUserOperator.value && !approveForm.areaId) {
            ElMessage.warning('操作员审核通过时必须分配区域')
            return
          }
        }
        
        const res = await approveUser({
          userId: approveForm.userId,
          status: approveForm.status,
          departmentId: approveForm.departmentId || undefined,
          areaId: approveForm.areaId || undefined,
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
    roleId: null,
    phone: '',
    departmentId: null,
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

// 加载部门列表
const loadDepartmentList = async () => {
  try {
    const res = await getAllDepartments()
    if (res.code === 200) {
      departmentList.value = res.data || []
    }
  } catch (error) {
    console.error('加载部门列表失败', error)
  }
}

onMounted(() => {
  loadData()
  loadRoleList()
  if (hasPermission('system:user:view')) {
    loadAreaList()
    loadDepartmentList()
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

