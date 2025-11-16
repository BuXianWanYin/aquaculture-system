import request from '@/utils/request'

export const login = (data) => {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export const getUserList = (params) => {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

export const getUserById = (userId) => {
  return request({
    url: `/user/${userId}`,
    method: 'get'
  })
}

export const saveUser = (data) => {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

export const updateUser = (data) => {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

export const deleteUser = (userId) => {
  return request({
    url: `/user/${userId}`,
    method: 'delete'
  })
}

export const changePassword = (data) => {
  return request({
    url: '/user/changePassword',
    method: 'post',
    params: data
  })
}

export const resetPassword = (data) => {
  return request({
    url: '/user/resetPassword',
    method: 'post',
    params: data
  })
}

