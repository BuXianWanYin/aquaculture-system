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
  
  // 判断是否为管理员（已删除部门管理员角色，只保留管理员）
  const isAdminOrManager = computed(() => {
    return isAdmin.value
  })
  
  // 判断是否具有编辑权限（仅管理员）
  const canEdit = computed(() => {
    return isAdmin.value
  })
  
  // 判断是否具有删除权限（仅管理员）
  const canDelete = computed(() => {
    return isAdmin.value
  })
  
  // 判断是否具有审核权限（仅管理员）
  const canAudit = computed(() => {
    return isAdmin.value
  })
  
  return {
    currentRole,
    isAdmin,
    isOperator,
    isDecisionMaker,
    isAdminOrManager,
    canEdit,
    canDelete,
    canAudit
  }
}

