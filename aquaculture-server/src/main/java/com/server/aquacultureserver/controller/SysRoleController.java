package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresRole;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.SysRole;
import com.server.aquacultureserver.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/api/role")
@CrossOrigin
public class SysRoleController {
    
    @Autowired
    private SysRoleService roleService;
    
    /**
     * 查询所有角色
     */
    @GetMapping("/all")
    public Result<List<SysRole>> getAllRoles() {
        List<SysRole> roles = roleService.getAllRoles();
        return Result.success(roles);
    }
    
    /**
     * 分页查询角色列表
     */
    @GetMapping("/page")
    @RequiresRole({1}) // 仅系统管理员
    public Result<Page<SysRole>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleName) {
        Page<SysRole> page = roleService.getPage(current, size, roleName);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询角色
     */
    @GetMapping("/{roleId}")
    public Result<SysRole> getById(@PathVariable Long roleId) {
        SysRole role = roleService.getById(roleId);
        return Result.success(role);
    }
    
    /**
     * 新增角色
     */
    @PostMapping
    @RequiresRole({1}) // 仅系统管理员
    public Result<Boolean> saveRole(@RequestBody SysRole role) {
        try {
            boolean success = roleService.saveRole(role);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新角色
     */
    @PutMapping
    @RequiresRole({1}) // 仅系统管理员
    public Result<Boolean> updateRole(@RequestBody SysRole role) {
        try {
            boolean success = roleService.updateRole(role);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除角色
     */
    @DeleteMapping("/{roleId}")
    @RequiresRole({1}) // 仅系统管理员
    public Result<Boolean> deleteRole(@PathVariable Long roleId) {
        try {
            boolean success = roleService.deleteRole(roleId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

