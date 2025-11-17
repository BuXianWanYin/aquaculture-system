<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h1>用户注册</h1>
        <p>User Registration</p>
      </div>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-position="top"
      >
        <el-row :gutter="15">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                prefix-icon="User"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input
                v-model="registerForm.realName"
                placeholder="请输入真实姓名"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码（至少6位）"
                prefix-icon="Lock"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                prefix-icon="Lock"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="12">
            <el-form-item label="所属部门" prop="departmentId">
              <el-select
                v-model="registerForm.departmentId"
                placeholder="请选择所属部门"
                style="width: 100%;"
                filterable
                @change="handleDepartmentChange"
              >
                <el-option
                  v-for="dept in departmentList"
                  :key="dept.departmentId"
                  :label="dept.departmentName"
                  :value="dept.departmentId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="roleId">
              <el-select
                v-model="registerForm.roleId"
                placeholder="请选择角色"
                style="width: 100%;"
              >
                <el-option label="普通操作员" :value="3" />
                <el-option label="部门管理员" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item v-if="registerForm.roleId" style="margin-top: -10px; margin-bottom: 15px;">
          <div style="color: #909399; font-size: 12px; line-height: 1.4;">
            <span v-if="registerForm.roleId === 3">普通操作员：需要由该部门的部门管理员或系统管理员审核</span>
            <span v-else-if="registerForm.roleId === 5">部门管理员：只能由系统管理员审核</span>
          </div>
        </el-form-item>
        <el-row :gutter="15">
          <el-col :span="12">
            <el-form-item label="联系方式" prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入联系方式"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家庭地址">
              <el-input
                v-model="registerForm.address"
                placeholder="请输入家庭地址（选填）"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-alert
          title="提示"
          type="info"
          :closable="false"
          style="margin-bottom: 15px;"
        >
          <template #default>
            <div style="font-size: 12px; line-height: 1.5;">
              <div>注册后需要等待管理员审核，审核通过后即可登录使用。</div>
              <div style="margin-top: 4px;" v-if="registerForm.roleId === 3">
                选择"普通操作员"角色，管理员审核时会为您分配所属养殖区域。
              </div>
              <div style="margin-top: 4px;" v-else-if="registerForm.roleId === 5">
                选择"部门管理员"角色，系统管理员审核通过后即可使用。
              </div>
            </div>
          </template>
        </el-alert>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleRegister"
            class="register-button"
          >
            注册
          </el-button>
        </el-form-item>
        <el-form-item style="margin-bottom: 0;">
          <div style="text-align: center; margin-top: 5px;">
            <span style="color: #909399; font-size: 13px;">已有账号？</span>
            <el-button type="text" @click="handleLogin" style="padding: 0; margin-left: 5px; font-size: 13px;">
              立即登录
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'
import { getAllDepartments } from '@/api/department'
import { getAllAreas } from '@/api/area'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)
const departmentList = ref([])
const areaList = ref([])

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  departmentId: null,
  roleId: null,
  phone: '',
  address: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const { confirmPassword, ...formData } = registerForm
        const res = await register(formData)
        if (res.code === 200) {
          ElMessage.success(res.message || '注册成功！您选择的角色已自动分配对应权限，请等待管理员审核并分配所属区域后即可登录')
          router.push('/login')
        }
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleLogin = () => {
  router.push('/login')
}

// 部门变化处理
const handleDepartmentChange = (departmentId) => {
  // 可以在这里根据部门筛选区域（如果需要）
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
  loadDepartmentList()
})
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 50%, #90caf9 100%);
  position: relative;
}

.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg width="100" height="100" xmlns="http://www.w3.org/2000/svg"><defs><pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse"><path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(33, 150, 243, 0.1)" stroke-width="1"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
}

.register-box {
  width: 600px;
  padding: 35px 35px 30px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(33, 150, 243, 0.15);
  position: relative;
  z-index: 1;
  border: 1px solid rgba(33, 150, 243, 0.1);
}

.register-header {
  text-align: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e3f2fd;
}

.register-header h1 {
  font-size: 24px;
  color: #1976d2;
  margin-bottom: 8px;
  font-weight: 600;
  letter-spacing: 1px;
}

.register-header p {
  font-size: 12px;
  color: #64b5f6;
  font-weight: 400;
}

.register-form {
  margin-top: 15px;
}

.register-button {
  width: 100%;
  margin-top: 10px;
  height: 40px;
  font-size: 15px;
  font-weight: 500;
  background: #1976d2;
  border: none;
}

.register-button:hover {
  background: #1565c0;
}

:deep(.el-form-item) {
  margin-bottom: 15px;
}

:deep(.el-form-item__label) {
  color: #424242;
  font-weight: 500;
  font-size: 13px;
  margin-bottom: 6px;
  padding-bottom: 0;
}

:deep(.el-input__wrapper) {
  border-radius: 4px;
  border: 1px solid #e0e0e0;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  border-color: #90caf9;
}

:deep(.el-input.is-focus .el-input__wrapper) {
  border-color: #1976d2;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.1);
}

:deep(.el-input__inner) {
  color: #424242;
}

:deep(.el-input__inner::placeholder) {
  color: #bdbdbd;
}
</style>

