import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const loginAction = async (loginData) => {
    const res = await login(loginData)
    if (res.code === 200) {
      // 使用后端返回的JWT token
      if (res.data && res.data.token) {
        setToken(res.data.token)
        // 保存用户信息（不包含token）
        const userInfo = { ...res.data }
        delete userInfo.token
        setUserInfo(userInfo)
      } else {
        throw new Error('登录失败：未获取到token')
      }
      return true
    }
    throw new Error(res.message || '登录失败')
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    loginAction,
    logout
  }
})

