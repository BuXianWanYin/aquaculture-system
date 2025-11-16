/**
 * 角色常量
 * 用于避免硬编码角色ID，通过角色名称进行判断
 */

export const ROLE_NAMES = {
  ADMIN: '管理员',
  OPERATOR: '普通操作员',
  DECISION_MAKER: '决策层'
}

/**
 * 根据角色名称判断是否为管理员
 */
export function isAdmin(roleName) {
  return roleName === ROLE_NAMES.ADMIN || roleName === '系统管理员'
}

/**
 * 根据角色名称判断是否为普通操作员
 */
export function isOperator(roleName) {
  return roleName === ROLE_NAMES.OPERATOR || roleName === '操作员'
}

/**
 * 根据角色名称判断是否为决策层
 */
export function isDecisionMaker(roleName) {
  return roleName === ROLE_NAMES.DECISION_MAKER || roleName === '决策者'
}

/**
 * 检查用户角色是否在允许的角色列表中（基于角色名称）
 */
export function hasRole(roleNames, userRoleName) {
  if (!userRoleName || !roleNames || roleNames.length === 0) {
    return false
  }
  if (Array.isArray(roleNames)) {
    return roleNames.some(roleName => {
      if (roleName === ROLE_NAMES.ADMIN) {
        return isAdmin(userRoleName)
      } else if (roleName === ROLE_NAMES.OPERATOR) {
        return isOperator(userRoleName)
      } else if (roleName === ROLE_NAMES.DECISION_MAKER) {
        return isDecisionMaker(userRoleName)
      }
      return roleName === userRoleName
    })
  }
  return roleNames === userRoleName
}

