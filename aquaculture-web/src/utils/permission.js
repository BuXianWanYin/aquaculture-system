/**
 * 权限工具函数
 */

/**
 * 检查用户是否有权限
 * @param {string} permission - 权限标识
 * @param {object} userInfo - 用户信息
 * @returns {boolean}
 */
export function hasPermission(permission, userInfo) {
  if (!userInfo || !userInfo.roleId) {
    return false
  }
  
  // 系统管理员拥有所有权限
  if (userInfo.roleId === 1) {
    return true
  }
  
  // 根据角色ID判断权限
  // 这里简化处理，实际应该从后端获取用户的权限列表
  const rolePermissions = {
    1: ['*'], // 系统管理员 - 所有权限
    2: [ // 部门管理员
      'user:view',
      'user:edit',
      'plan:view',
      'plan:approve',
      'yield:view',
      'yield:audit',
      'base:view',
      'base:edit'
    ],
    3: [ // 普通操作员
      'plan:view',
      'plan:create',
      'plan:edit',
      'yield:view',
      'yield:create',
      'base:view'
    ],
    4: [ // 决策层
      'plan:view',
      'yield:view',
      'report:view'
    ]
  }
  
  const permissions = rolePermissions[userInfo.roleId] || []
  return permissions.includes('*') || permissions.includes(permission)
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

