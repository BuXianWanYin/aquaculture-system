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
      setToken('mock-token-' + Date.now())
      setUserInfo(res.data)
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

