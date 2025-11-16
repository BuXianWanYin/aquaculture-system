/**
 * 权限工具函数
 */

/**
 * 检查用户是否有权限
 * @param {string} permission - 权限标识
 * @param {object} userInfo - 用户信息（包含permissions数组）
 * @returns {boolean}
 */
export function hasPermission(permission, userInfo) {
  if (!userInfo || !userInfo.permissions) {
    return false
  }
  
  const permissions = userInfo.permissions
  
  // 如果包含通配符，表示拥有所有权限
  if (permissions.includes('*')) {
    return true
  }
  
  return permissions.includes(permission)
}

/**
 * 检查用户是否有角色
 * @param {number|number[]} roleIds - 角色ID或角色ID数组
 * @param {object} userInfo - 用户信息
 * @returns {boolean}
 */
export function hasRole(roleIds, userInfo) {
  if (!userInfo || !userInfo.roleId) {
    return false
  }
  
  if (Array.isArray(roleIds)) {
    return roleIds.includes(userInfo.roleId)
  }
  
  return userInfo.roleId === roleIds
}

