package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresRole;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.SysPermission;
import com.server.aquacultureserver.dto.AssignPermissionDTO;
import com.server.aquacultureserver.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器
 */
@RestController
@RequestMapping("/api/permission")
@CrossOrigin
public class SysPermissionController {
    
    @Autowired
    private SysPermissionService permissionService;
    
    /**
     * 查询所有权限
     */
    @GetMapping("/all")
    public Result<List<SysPermission>> getAllPermissions() {
        List<SysPermission> permissions = permissionService.getAllPermissions();
        return Result.success(permissions);
    }
    
    /**
     * 分页查询权限列表
     */
    @GetMapping("/page")
    @RequiresRole({1}) // 仅系统管理员
    public Result<Page<SysPermission>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String keyword) {
        Page<SysPermission> page = permissionService.getPage(current, size, module, keyword);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询权限
     */
    @GetMapping("/{permissionId}")
    public Result<SysPermission> getById(@PathVariable Long permissionId) {
        SysPermission permission = permissionService.getById(permissionId);
        return Result.success(permission);
    }
    
    /**
     * 根据模块查询权限列表
     */
    @GetMapping("/module/{module}")
    public Result<List<SysPermission>> getByModule(@PathVariable String module) {
        List<SysPermission> permissions = permissionService.getByModule(module);
        return Result.success(permissions);
    }
    
    /**
     * 根据父权限ID查询子权限列表
     */
    @GetMapping("/parent/{parentId}")
    public Result<List<SysPermission>> getByParentId(@PathVariable Long parentId) {
        List<SysPermission> permissions = permissionService.getByParentId(parentId);
        return Result.success(permissions);
    }
    
    /**
     * 根据角色ID查询权限列表
     */
    @GetMapping("/role/{roleId}")
    public Result<List<SysPermission>> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<SysPermission> permissions = permissionService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }
    
    /**
     * 新增权限
     */
    @PostMapping
    @RequiresRole({1}) // 仅系统管理员
    public Result<Boolean> savePermission(@RequestBody SysPermission permission) {
        try {
            boolean success = permissionService.savePermission(permission);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新权限
     */
    @PutMapping
    @RequiresRole({1}) // 仅系统管理员
    public Result<Boolean> updatePermission(@RequestBody SysPermission permission) {
        try {
            boolean success = permissionService.updatePermission(permission);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除权限
     */
    @DeleteMapping("/{permissionId}")
    @RequiresRole({1}) // 仅系统管理员
    public Result<Boolean> deletePermission(@PathVariable Long permissionId) {
        try {
            boolean success = permissionService.deletePermission(permissionId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 为角色分配权限
     */
    @PostMapping("/assign")
    @RequiresRole({1}) // 仅系统管理员
    public Result<Boolean> assignPermissionsToRole(@RequestBody AssignPermissionDTO dto) {
        try {
            if (dto == null || dto.getRoleId() == null) {
                return Result.error("角色ID不能为空");
            }
            
            Long[] permissionIds = null;
            if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
                permissionIds = dto.getPermissionIds().toArray(new Long[0]);
            } else {
                // 如果没有传递权限ID，设置为空数组（清空所有权限）
                permissionIds = new Long[0];
            }
            
            boolean success = permissionService.assignPermissionsToRole(dto.getRoleId(), permissionIds);
            return Result.success("分配成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 移除角色的所有权限
     */
    @DeleteMapping("/role/{roleId}")
    @RequiresRole({1}) // 仅系统管理员
    public Result<Boolean> removeAllPermissionsFromRole(@PathVariable Long roleId) {
        try {
            boolean success = permissionService.removeAllPermissionsFromRole(roleId);
            return Result.success("移除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

