import request from '@/utils/request'

export const getRoleList = (params) => {
  return request({
    url: '/role/page',
    method: 'get',
    params
  })
}

export const getRoleById = (roleId) => {
  return request({
    url: `/role/${roleId}`,
    method: 'get'
  })
}

export const saveRole = (data) => {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

export const updateRole = (data) => {
  return request({
    url: '/role',
    method: 'put',
    data
  })
}

export const deleteRole = (roleId) => {
  return request({
    url: `/role/${roleId}`,
    method: 'delete'
  })
}

export const getAllRoles = () => {
  return request({
    url: '/role/all',
    method: 'get'
  })
}

