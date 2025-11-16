<template>
  <div class="profile-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      
      <el-row :gutter="40">
        <el-col :span="8">
          <div class="avatar-section">
            <el-upload
              class="avatar-uploader"
              :action="uploadAction"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="profileForm.avatar" :src="getAvatarUrl(profileForm.avatar)" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="avatar-tip">点击上传头像</div>
          </div>
        </el-col>
        <el-col :span="16">
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="120px"
          >
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            <el-form-item label="角色">
              <el-input v-model="profileForm.roleName" disabled />
            </el-form-item>
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="联系方式" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入联系方式" />
            </el-form-item>
            <el-form-item label="家庭地址" prop="address">
              <el-input v-model="profileForm.address" placeholder="请输入家庭地址" />
            </el-form-item>
            <el-form-item 
              v-if="userStore.userInfo?.roleId !== 1" 
              label="所属养殖区域" 
              prop="areaId"
            >
              <el-select 
                v-model="profileForm.areaId" 
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
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave">保存</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
      
      <el-divider />
      
      <el-card shadow="never">
        <template #header>
          <span>修改密码</span>
        </template>
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="120px"
          style="max-width: 500px;"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码（至少6位）" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            <el-button @click="handleResetPassword">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCurrentUser, updateProfile, changePassword, uploadAvatar } from '@/api/user'
import { getAllAreas } from '@/api/area'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const loading = ref(false)
const areaList = ref([])

const profileForm = reactive({
  userId: null,
  username: '',
  roleName: '',
  realName: '',
  phone: '',
  address: '',
  areaId: null,
  avatar: ''
})

const originalProfile = ref({})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const uploadAction = '/api/upload/avatar'
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return {
    Authorization: `Bearer ${token}`
  }
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await getCurrentUser()
    if (res.code === 200) {
      Object.assign(profileForm, {
        userId: res.data.userId,
        username: res.data.username,
        roleName: res.data.roleName || '',
        realName: res.data.realName || '',
        phone: res.data.phone || '',
        address: res.data.address || '',
        areaId: res.data.areaId || res.data.farmId || null, // 兼容farmId字段
        avatar: res.data.avatar || ''
      })
      originalProfile.value = { ...profileForm }
    }
  } catch (error) {
    ElMessage.error('加载用户信息失败')
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

// 保存个人信息
const handleSave = async () => {
  if (!profileFormRef.value) return
  
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await updateProfile(profileForm)
        if (res.code === 200) {
          ElMessage.success('保存成功')
          // 更新store中的用户信息
          await loadUserInfo()
          // 更新store
          const updatedUser = await getCurrentUser()
          if (updatedUser.code === 200) {
            userStore.setUserInfo(updatedUser.data)
          }
        }
      } catch (error) {
        ElMessage.error(error.message || '保存失败')
      }
    }
  })
}

// 重置表单
const handleReset = () => {
  Object.assign(profileForm, originalProfile.value)
  profileFormRef.value?.clearValidate()
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await changePassword({
          userId: profileForm.userId,
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        if (res.code === 200) {
          ElMessage.success('密码修改成功，请重新登录')
          // 清空密码表单
          passwordForm.oldPassword = ''
          passwordForm.newPassword = ''
          passwordForm.confirmPassword = ''
          passwordFormRef.value?.clearValidate()
        }
      } catch (error) {
        ElMessage.error(error.message || '密码修改失败')
      }
    }
  })
}

// 重置密码表单
const handleResetPassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

// 头像上传成功
const handleAvatarSuccess = async (response) => {
  if (response.code === 200) {
    profileForm.avatar = response.data.filePath
    ElMessage.success('头像上传成功')
    // 自动保存
    await handleSave()
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB!')
    return false
  }
  return true
}

// 获取头像URL
const getAvatarUrl = (avatarPath) => {
  if (!avatarPath) return ''
  if (avatarPath.startsWith('http')) return avatarPath
  if (avatarPath.startsWith('/')) return avatarPath
  return `/uploads${avatarPath}`
}

onMounted(() => {
  loadUserInfo()
  // 非admin用户才加载区域列表
  if (userStore.userInfo?.roleId !== 1) {
    loadAreaList()
  }
})
</script>

<style scoped>
.profile-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.avatar-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}
</style>

