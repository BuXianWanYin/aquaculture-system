<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>水产养殖管理系统</h1>
        <p>Aquaculture Management System</p>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        label-position="top"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-button"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.loginAction(loginForm)
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 50%, #90caf9 100%);
  position: relative;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg width="100" height="100" xmlns="http://www.w3.org/2000/svg"><defs><pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse"><path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(33, 150, 243, 0.1)" stroke-width="1"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
}

.login-box {
  width: 420px;
  padding: 50px 40px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(33, 150, 243, 0.15);
  position: relative;
  z-index: 1;
  border: 1px solid rgba(33, 150, 243, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 45px;
  padding-bottom: 25px;
  border-bottom: 2px solid #e3f2fd;
}

.login-header h1 {
  font-size: 26px;
  color: #1976d2;
  margin-bottom: 10px;
  font-weight: 600;
  letter-spacing: 1px;
}

.login-header p {
  font-size: 13px;
  color: #64b5f6;
  font-weight: 400;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
  margin-top: 15px;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  background: #1976d2;
  border: none;
}

.login-button:hover {
  background: #1565c0;
}

:deep(.el-form-item__label) {
  color: #424242;
  font-weight: 500;
  font-size: 14px;
  margin-bottom: 8px;
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

