import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

/**
 * 角色判断 Composable
 * 基于用户信息中的角色名称进行判断，避免硬编码角色ID
 */
export function useRole() {
  const userStore = useUserStore()
  
  // 获取当前用户的角色信息
  const currentRole = computed(() => {
    const userInfo = userStore.userInfo
    if (!userInfo) return null
    
    return {
      roleId: userInfo.roleId,
      roleName: userInfo.roleName
    }
  })
  
  // 判断是否为系统管理员
  const isAdmin = computed(() => {
    const role = currentRole.value
    return role?.roleName === '系统管理员' || role?.roleName === '管理员'
  })
  
  // 判断是否为普通操作员
  const isOperator = computed(() => {
    const role = currentRole.value
    return role?.roleName === '普通操作员' || role?.roleName === '操作员'
  })
  
  // 判断是否为决策层
  const isDecisionMaker = computed(() => {
    const role = currentRole.value
    return role?.roleName === '决策层' || role?.roleName === '决策者'
  })
  
  // 判断是否为部门管理员
  const isDepartmentManager = computed(() => {
    const role = currentRole.value
    return role?.roleName === '部门管理员'
  })
  
  // 判断是否为管理员
  const isAdminOrManager = computed(() => {
    return isAdmin.value || isDepartmentManager.value
  })
  
  // 注意：以下方法已废弃，请使用 usePermission() 中的权限判断方法
  // 判断是否具有编辑权限（基于权限代码）
  const canEdit = computed(() => {
    // 此方法已废弃，请使用 usePermission().canEdit
    return false
  })
  
  // 判断是否具有删除权限（基于权限代码）
  const canDelete = computed(() => {
    // 此方法已废弃，请使用 usePermission().canDelete
    return false
  })
  
  // 判断是否具有审核权限（基于权限代码）
  const canAudit = computed(() => {
    // 此方法已废弃，请使用 usePermission().canAudit
    return false
  })
  
  return {
    currentRole,
    isAdmin,
    isOperator,
    isDecisionMaker,
    isDepartmentManager,
    isAdminOrManager,
    canEdit,
    canDelete,
    canAudit
  }
}

