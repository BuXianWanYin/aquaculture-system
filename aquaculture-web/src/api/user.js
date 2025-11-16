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
 * @param {Object} data - 密码数据 {userId, oldPassword, newPassword}
 * @returns {Promise} 返回操作结果
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

