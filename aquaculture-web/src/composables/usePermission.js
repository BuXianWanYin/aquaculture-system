import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

/**
 * 权限判断 Composable
 * 基于后端返回的权限列表进行判断，而不是硬编码角色
 */
export function usePermission() {
  const userStore = useUserStore()
  
  // 获取当前用户的权限列表
  const permissions = computed(() => {
    const userInfo = userStore.userInfo
    return userInfo?.permissions || []
  })
  
  // 检查是否具有指定权限
  const hasPermission = (permission) => {
    const perms = permissions.value
    if (!perms || perms.length === 0) return false
    
    // 如果包含通配符，表示拥有所有权限
    if (perms.includes('*')) return true
    
    return perms.includes(permission)
  }
  
  // 检查是否具有任意一个权限
  const hasAnyPermission = (permissionList) => {
    if (!Array.isArray(permissionList)) return false
    return permissionList.some(perm => hasPermission(perm))
  }
  
  // 检查是否具有所有权限
  const hasAllPermissions = (permissionList) => {
    if (!Array.isArray(permissionList)) return false
    return permissionList.every(perm => hasPermission(perm))
  }
  
  // 判断是否具有编辑权限
  const canEdit = computed(() => {
    return hasAnyPermission([
      'breed:edit',
      'area:edit',
      'equipment:edit',
      'plan:edit',
      'yield:edit'
    ])
  })
  
  // 判断是否具有删除权限
  const canDelete = computed(() => {
    return hasAnyPermission([
      'breed:delete',
      'area:delete',
      'equipment:delete',
      'plan:delete',
      'yield:delete'
    ])
  })
  
  // 判断是否具有新增权限
  const canCreate = computed(() => {
    return hasAnyPermission([
      'breed:add',
      'area:add',
      'equipment:add',
      'plan:add',
      'yield:add'
    ])
  })
  
  // 判断是否具有审核权限
  const canAudit = computed(() => {
    return hasAnyPermission([
      'plan:approve',
      'yield:audit'
    ])
  })
  
  // 判断是否为普通操作员（通过权限判断，而不是角色名称）
  const isOperator = computed(() => {
    // 普通操作员通常只有查看和新增权限，没有编辑和删除权限
    return hasPermission('plan:add') && 
           hasPermission('yield:add') && 
           !hasPermission('breed:edit') && 
           !hasPermission('area:edit')
  })
  
  // 判断是否为管理员（有编辑和删除权限）
  const isAdminOrManager = computed(() => {
    return canEdit.value && canDelete.value
  })
  
  return {
    permissions,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    canEdit,
    canDelete,
    canCreate,
    canAudit,
    isOperator,
    isAdminOrManager
  }
}

