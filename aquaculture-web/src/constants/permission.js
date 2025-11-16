/**
 * 权限代码常量
 * 统一管理所有权限代码，避免硬编码
 * 与后端 sys_permission.permission_code 保持一致
 */

// 系统管理权限
export const SYSTEM_PERMISSIONS = {
  USER_VIEW: 'system:user:view',
  USER_ADD: 'system:user:add',
  USER_EDIT: 'system:user:edit',
  USER_DELETE: 'system:user:delete',
  USER_RESET: 'system:user:reset',
  USER_APPROVE: 'system:user:approve',
  ROLE_VIEW: 'system:role:view',
  ROLE_ADD: 'system:role:add',
  ROLE_EDIT: 'system:role:edit',
  ROLE_DELETE: 'system:role:delete',
  PERMISSION_VIEW: 'system:permission:view',
  PERMISSION_ADD: 'system:permission:add',
  PERMISSION_EDIT: 'system:permission:edit',
  PERMISSION_DELETE: 'system:permission:delete'
}

// 部门管理权限
export const DEPARTMENT_PERMISSIONS = {
  VIEW: 'department:view',
  ADD: 'department:add',
  EDIT: 'department:edit',
  DELETE: 'department:delete'
}

// 计划管理权限
export const PLAN_PERMISSIONS = {
  VIEW: 'plan:view',
  ADD: 'plan:add',
  EDIT: 'plan:edit',
  DELETE: 'plan:delete',
  APPROVE: 'plan:approve',
  ADJUST_VIEW: 'plan:adjust:view',
  ADJUST_ADD: 'plan:adjust:add',
  ADJUST_EDIT: 'plan:adjust:edit',
  ADJUST_DELETE: 'plan:adjust:delete',
  ADJUST_APPROVE: 'plan:adjust:approve'
}

// 产量管理权限
export const YIELD_PERMISSIONS = {
  VIEW: 'yield:view',
  ADD: 'yield:add',
  EDIT: 'yield:edit',
  DELETE: 'yield:delete',
  AUDIT: 'yield:audit',
  EVIDENCE_VIEW: 'yield:evidence:view',
  EVIDENCE_ADD: 'yield:evidence:add',
  EVIDENCE_DELETE: 'yield:evidence:delete'
}

// 其他权限
export const OTHER_PERMISSIONS = {
  LOG_VIEW: 'log:view',
  STATISTICS_VIEW: 'statistics:view',
  MESSAGE_SEND: 'message:send'
}

// 所有权限代码（用于类型检查等）
export const ALL_PERMISSIONS = {
  ...SYSTEM_PERMISSIONS,
  ...DEPARTMENT_PERMISSIONS,
  ...PLAN_PERMISSIONS,
  ...YIELD_PERMISSIONS,
  ...OTHER_PERMISSIONS
}

