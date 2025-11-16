import request from '@/utils/request'

/**
 * 查询所有权限
 * @returns {Promise} 返回所有权限列表
 */
export const getAllPermissions = () => {
  return request({
    url: '/permission/all',
    method: 'get'
  })
}

/**
 * 分页查询权限列表
 * @param {Object} params - 查询参数 {current, size, module, keyword}
 * @returns {Promise} 返回分页权限列表
 */
export const getPermissionList = (params) => {
  return request({
    url: '/permission/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询权限详情
 * @param {Number} permissionId - 权限ID
 * @returns {Promise} 返回权限信息
 */
export const getPermissionById = (permissionId) => {
  return request({
    url: `/permission/${permissionId}`,
    method: 'get'
  })
}

/**
 * 根据模块查询权限列表
 * @param {String} module - 模块名称
 * @returns {Promise} 返回权限列表
 */
export const getPermissionsByModule = (module) => {
  return request({
    url: `/permission/module/${module}`,
    method: 'get'
  })
}

/**
 * 根据父权限ID查询子权限列表
 * @param {Number} parentId - 父权限ID
 * @returns {Promise} 返回子权限列表
 */
export const getPermissionsByParentId = (parentId) => {
  return request({
    url: `/permission/parent/${parentId}`,
    method: 'get'
  })
}

/**
 * 根据角色ID查询权限列表
 * @param {Number} roleId - 角色ID
 * @returns {Promise} 返回权限列表
 */
export const getPermissionsByRoleId = (roleId) => {
  return request({
    url: `/permission/role/${roleId}`,
    method: 'get'
  })
}

/**
 * 新增权限
 * @param {Object} data - 权限数据
 * @returns {Promise} 返回操作结果
 */
export const savePermission = (data) => {
  return request({
    url: '/permission',
    method: 'post',
    data
  })
}

/**
 * 更新权限信息
 * @param {Object} data - 权限数据
 * @returns {Promise} 返回操作结果
 */
export const updatePermission = (data) => {
  return request({
    url: '/permission',
    method: 'put',
    data
  })
}

/**
 * 删除权限
 * @param {Number} permissionId - 权限ID
 * @returns {Promise} 返回操作结果
 */
export const deletePermission = (permissionId) => {
  return request({
    url: `/permission/${permissionId}`,
    method: 'delete'
  })
}

/**
 * 为角色分配权限
 * @param {Number} roleId - 角色ID
 * @param {Array} permissionIds - 权限ID数组
 * @returns {Promise} 返回操作结果
 */
export const assignPermissionsToRole = (roleId, permissionIds) => {
  return request({
    url: '/permission/assign',
    method: 'post',
    params: {
      roleId: roleId,
      permissionIds: permissionIds
    }
  })
}

/**
 * 移除角色的所有权限
 * @param {Number} roleId - 角色ID
 * @returns {Promise} 返回操作结果
 */
export const removeAllPermissionsFromRole = (roleId) => {
  return request({
    url: `/permission/role/${roleId}`,
    method: 'delete'
  })
}

