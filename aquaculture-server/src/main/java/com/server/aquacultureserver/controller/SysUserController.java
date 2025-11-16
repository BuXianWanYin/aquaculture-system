package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresRole;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.dto.LoginDTO;
import com.server.aquacultureserver.dto.UserDTO;
import com.server.aquacultureserver.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class SysUserController {
    
    @Autowired
    private SysUserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            UserDTO userDTO = userService.login(loginDTO);
            return Result.success("登录成功", userDTO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID查询用户
     */
    @GetMapping("/{userId}")
    public Result<SysUser> getById(@PathVariable Long userId) {
        SysUser user = userService.getById(userId);
        return Result.success(user);
    }
    
    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    @RequiresRole({1, 2}) // 系统管理员、部门管理员
    public Result<Page<SysUser>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Long roleId) {
        Page<SysUser> page = userService.getPage(current, size, username, roleId);
        return Result.success(page);
    }
    
    /**
     * 新增用户
     */
    @PostMapping
    @RequiresRole({1, 2}) // 系统管理员、部门管理员
    public Result<Boolean> saveUser(@RequestBody SysUser user) {
        try {
            boolean success = userService.saveUser(user);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户
     */
    @PutMapping
    @RequiresRole({1, 2}) // 系统管理员、部门管理员
    public Result<Boolean> updateUser(@RequestBody SysUser user) {
        try {
            boolean success = userService.updateUser(user);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @RequiresRole({1}) // 仅系统管理员
    public Result<Boolean> deleteUser(@PathVariable Long userId) {
        try {
            boolean success = userService.deleteUser(userId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/changePassword")
    public Result<Boolean> changePassword(
            @RequestParam Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            boolean success = userService.changePassword(userId, oldPassword, newPassword);
            return Result.success("密码修改成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/resetPassword")
    @RequiresRole({1, 2}) // 系统管理员、部门管理员
    public Result<Boolean> resetPassword(
            @RequestParam Long userId,
            @RequestParam String newPassword) {
        try {
            boolean success = userService.resetPassword(userId, newPassword);
            return Result.success("密码重置成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

