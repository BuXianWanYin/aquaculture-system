import request from '@/utils/request'

/**
 * 分页查询角色列表
 * @param {Object} params - 查询参数 {current, size, roleName}
 * @returns {Promise} 返回分页角色列表
 */
export const getRoleList = (params) => {
  return request({
    url: '/role/page',
    method: 'get',
    params
  })
}

/**
 * 根据角色ID查询角色详情
 * @param {Number} roleId - 角色ID
 * @returns {Promise} 返回角色信息
 */
export const getRoleById = (roleId) => {
  return request({
    url: `/role/${roleId}`,
    method: 'get'
  })
}

/**
 * 新增角色
 * @param {Object} data - 角色数据
 * @returns {Promise} 返回操作结果
 */
export const saveRole = (data) => {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

/**
 * 更新角色信息
 * @param {Object} data - 角色数据
 * @returns {Promise} 返回操作结果
 */
export const updateRole = (data) => {
  return request({
    url: '/role',
    method: 'put',
    data
  })
}

/**
 * 删除角色
 * @param {Number} roleId - 角色ID
 * @returns {Promise} 返回操作结果
 */
export const deleteRole = (roleId) => {
  return request({
    url: `/role/${roleId}`,
    method: 'delete'
  })
}

/**
 * 查询所有角色（用于下拉选择）
 * @returns {Promise} 返回所有角色列表
 */
export const getAllRoles = () => {
  return request({
    url: '/role/all',
    method: 'get'
  })
}

