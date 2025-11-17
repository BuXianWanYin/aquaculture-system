import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - 登录数据 {username, password}
 * @returns {Promise} 返回用户信息和token
 */
export const login = (data) => {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/**
 * 分页查询用户列表
 * @param {Object} params - 查询参数 {current, size, username, roleId}
 * @returns {Promise} 返回分页用户列表
 */
export const getUserList = (params) => {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

/**
 * 获取用户列表（用于下拉选择，普通操作员可用）
 * @param {Object} params - 查询参数 {areaId}
 * @returns {Promise} 返回用户列表
 */
export const getUserListForSelect = (params) => {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

/**
 * 根据用户ID查询用户详情
 * @param {Number} userId - 用户ID
 * @returns {Promise} 返回用户信息
 */
export const getUserById = (userId) => {
  return request({
    url: `/user/${userId}`,
    method: 'get'
  })
}

/**
 * 新增用户
 * @param {Object} data - 用户数据
 * @returns {Promise} 返回操作结果
 */
export const saveUser = (data) => {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

/**
 * 更新用户信息
 * @param {Object} data - 用户数据
 * @returns {Promise} 返回操作结果
 */
export const updateUser = (data) => {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

/**
 * 删除用户
 * @param {Number} userId - 用户ID
 * @returns {Promise} 返回操作结果
 */
export const deleteUser = (userId) => {
  return request({
    url: `/user/${userId}`,
    method: 'delete'
  })
}

/**
 * 修改密码
 * @param {Object} data - 密码数据 {userId, newPassword} 或 {userId, oldPassword, newPassword}
 * @returns {Promise} 返回操作结果
 * @description 在个人信息页面修改密码时不需要oldPassword，修改其他用户密码时需要oldPassword（需要原密码验证）
 */
export const changePassword = (data) => {
  return request({
    url: '/user/changePassword',
    method: 'post',
    params: data
  })
}

/**
 * 重置用户密码（管理员操作）
 * @param {Object} data - 密码数据 {userId, newPassword}
 * @returns {Promise} 返回操作结果
 */
export const resetPassword = (data) => {
  return request({
    url: '/user/resetPassword',
    method: 'post',
    params: data
  })
}

/**
 * 用户注册
 * @param {Object} data - 注册数据 {username, password, realName, roleId, phone, address, departmentId}
 * @returns {Promise} 返回操作结果
 */
export const register = (data) => {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

/**
 * 获取当前登录用户信息
 * @returns {Promise} 返回当前用户信息
 */
export const getCurrentUser = () => {
  return request({
    url: '/user/current',
    method: 'get'
  })
}

/**
 * 更新个人信息
 * @param {Object} data - 用户数据 {userId, realName, phone, address, areaId, avatar}
 * @returns {Promise} 返回操作结果
 */
export const updateProfile = (data) => {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

/**
 * 上传头像
 * @param {FormData} formData - 包含file的FormData
 * @returns {Promise} 返回文件信息
 */
export const uploadAvatar = (formData) => {
  return request({
    url: '/upload/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 查询待审核用户列表
 * @returns {Promise} 返回待审核用户列表
 */
export const getPendingUsers = () => {
  return request({
    url: '/user/pending',
    method: 'get'
  })
}

/**
 * 审核用户（通过或拒绝）
 * @param {Object} params - 审核参数 {userId, status, departmentId, areaId, remark}
 * @returns {Promise} 返回操作结果
 */
export const approveUser = (params) => {
  return request({
    url: '/user/approve',
    method: 'post',
    params
  })
}

