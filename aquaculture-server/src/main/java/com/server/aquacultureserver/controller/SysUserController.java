package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresRole;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.SysOperLog;
import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.dto.LoginDTO;
import com.server.aquacultureserver.dto.UserDTO;
import com.server.aquacultureserver.service.SysOperLogService;
import com.server.aquacultureserver.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class SysUserController {
    
    @Autowired
    private SysUserService userService;
    
    @Autowired
    private SysOperLogService operLogService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserDTO> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            UserDTO userDTO = userService.login(loginDTO);
            
            // 记录登录日志
            SysOperLog log = new SysOperLog();
            log.setUserId(userDTO.getUserId());
            log.setModule("用户管理");
            log.setOperType("登录");
            log.setOperContent("用户登录系统：" + loginDTO.getUsername());
            log.setIpAddress(getIpAddress(request));
            log.setOperResult(1);
            log.setOperTime(LocalDateTime.now());
            operLogService.saveLog(log);
            
            return Result.success("登录成功", userDTO);
        } catch (Exception e) {
            // 记录登录失败日志
            try {
                SysOperLog log = new SysOperLog();
                log.setModule("用户管理");
                log.setOperType("登录");
                log.setOperContent("用户登录失败：" + loginDTO.getUsername());
                log.setIpAddress(getIpAddress(request));
                log.setOperResult(0);
                log.setErrorMsg(e.getMessage());
                log.setOperTime(LocalDateTime.now());
                operLogService.saveLog(log);
            } catch (Exception ex) {
                // 忽略日志记录失败
            }
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取IP地址（优先获取IPv4地址）
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = null;
        
        // 优先从HTTP头获取IP
        ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 处理X-Forwarded-For可能包含多个IP的情况，取第一个
            if (ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            // 如果是IPv4，直接返回
            if (isIPv4(ip)) {
                return ip;
            }
        }
        
        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip) && isIPv4(ip)) {
            return ip;
        }
        
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip) && isIPv4(ip)) {
            return ip;
        }
        
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip) && isIPv4(ip)) {
            return ip;
        }
        
        // 从RemoteAddr获取
        ip = request.getRemoteAddr();
        
        // 如果是IPv6的localhost，转换为IPv4
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        
        // 如果是其他IPv6地址，尝试获取IPv4地址
        if (!isIPv4(ip)) {
            // 如果是IPv6地址，尝试从其他来源获取IPv4
            // 如果无法获取IPv4，返回127.0.0.1作为默认值
            return "127.0.0.1";
        }
        
        return ip;
    }
    
    /**
     * 判断是否为IPv4地址
     */
    private boolean isIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        // IPv4地址格式：xxx.xxx.xxx.xxx
        // 简单判断：包含点且不包含冒号
        return ip.contains(".") && !ip.contains(":");
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

