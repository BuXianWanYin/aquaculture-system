package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.SysMessage;
import com.server.aquacultureserver.domain.SysOperLog;
import com.server.aquacultureserver.domain.SysPermission;
import com.server.aquacultureserver.domain.SysRole;
import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.dto.LoginDTO;
import com.server.aquacultureserver.dto.UserDTO;
import com.server.aquacultureserver.mapper.SysRoleMapper;
import com.server.aquacultureserver.service.BaseAreaService;
import com.server.aquacultureserver.service.SysMessageService;
import com.server.aquacultureserver.service.SysOperLogService;
import com.server.aquacultureserver.service.SysPermissionService;
import com.server.aquacultureserver.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

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
    
    @Autowired
    private SysMessageService messageService;
    
    @Autowired
    private SysPermissionService permissionService;
    
    @Autowired
    private SysRoleMapper roleMapper;
    
    @Autowired
    private BaseAreaService areaService;
    
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
    @RequiresPermission({"system:user:view"}) // 需要用户查看权限
    public Result<Page<SysUser>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Long roleId) {
        Page<SysUser> page = userService.getPage(current, size, username, roleId);
        return Result.success(page);
    }
    
    /**
     * 获取用户列表（用于下拉选择，普通操作员可用）
     * 普通操作员只能看到自己区域的用户
     */
    @GetMapping("/list")
    public Result<List<SysUser>> getUserList(
            @RequestParam(required = false) Long areaId) {
        List<SysUser> users = userService.getUserListForSelect(areaId);
        return Result.success(users);
    }
    
    /**
     * 新增用户
     */
    @PostMapping
    @RequiresPermission({"system:user:add"}) // 需要用户新增权限
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
    @RequiresPermission({"system:user:edit"}) // 需要用户编辑权限
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
    @RequiresPermission({"system:user:delete"}) // 需要用户删除权限
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
    @RequiresPermission({"system:user:reset"}) // 需要用户密码重置权限
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
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody SysUser user) {
        try {
            // 注册时只能选择普通操作员或决策层角色
            if (user.getRoleId() == null) {
                return Result.error("请选择角色");
            }
            if (user.getRoleId() == 1 || user.getRoleId() == 2) {
                return Result.error("不允许注册管理员角色");
            }
            
            // 注册时不设置部门，由管理员审核时分配
            user.setFarmId(null);
            // status会在saveUser中设置为2（待审核）
            
            boolean success = userService.saveUser(user);
            
            if (success) {
                // 发送消息通知给所有管理员
                List<Long> adminUserIds = userService.getAllAdminUserIds();
                String roleName = user.getRoleId() == 3 ? "普通操作员" : "决策层";
                
                // 获取注册的用户ID（插入后自动生成）
                SysUser registeredUser = userService.getByUsername(user.getUsername());
                
                for (Long adminId : adminUserIds) {
                    SysMessage message = new SysMessage();
                    message.setReceiverId(adminId);
                    message.setSenderId(0L); // 0表示系统
                    message.setMessageTitle("新用户注册待审核");
                    message.setMessageContent(String.format("用户【%s】（%s）已注册，等待审核。用户名：%s，真实姓名：%s，联系方式：%s", 
                        user.getRealName() != null && !user.getRealName().isEmpty() ? user.getRealName() : user.getUsername(),
                        roleName,
                        user.getUsername(),
                        user.getRealName() != null && !user.getRealName().isEmpty() ? user.getRealName() : "未填写",
                        user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "未填写"));
                    message.setMessageType("通知");
                    message.setBusinessType("user");
                    message.setBusinessId(registeredUser != null ? registeredUser.getUserId() : null);
                    message.setStatus(0); // 未读
                    messageService.sendMessage(message);
                }
            }
            
            return Result.success("注册成功！您选择的角色已自动分配对应权限，请等待管理员审核并分配所属区域后即可登录", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新个人信息（用户自己修改）
     */
    @PutMapping("/profile")
    public Result<Boolean> updateProfile(@RequestBody SysUser user, HttpServletRequest request) {
        try {
            // 从请求中获取当前用户ID
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("未获取到用户信息");
            }
            Long currentUserId = Long.valueOf(userIdObj.toString());
            
            // 只能修改自己的信息
            if (!currentUserId.equals(user.getUserId())) {
                return Result.error("只能修改自己的信息");
            }
            
            // 不允许修改用户名、角色、密码（密码单独接口修改）
            SysUser existingUser = userService.getById(currentUserId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            // 只允许修改：真实姓名、所属养殖区域、联系方式、家庭地址、头像
            existingUser.setRealName(user.getRealName());
            // 支持areaId（优先）和farmId（兼容旧数据）
            if (user.getAreaId() != null) {
                existingUser.setAreaId(user.getAreaId());
            } else if (user.getFarmId() != null) {
                // 兼容：如果没有areaId但有farmId，将farmId作为areaId使用
                existingUser.setAreaId(user.getFarmId());
            } else {
                // 如果传入了null，清空区域
                existingUser.setAreaId(null);
            }
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            if (user.getAvatar() != null) {
                existingUser.setAvatar(user.getAvatar());
            }
            
            boolean success = userService.updateUser(existingUser);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public Result<UserDTO> getCurrentUser(HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("未获取到用户信息");
            }
            Long userId = Long.valueOf(userIdObj.toString());
            SysUser user = userService.getById(userId);
            
            // 转换为UserDTO并添加权限信息
            UserDTO userDTO = new UserDTO();
            org.springframework.beans.BeanUtils.copyProperties(user, userDTO);
            
            // 查询角色信息
            SysRole role = roleMapper.selectById(user.getRoleId());
            if (role != null) {
                userDTO.setRoleName(role.getRoleName());
            }
            
            // 查询用户权限列表
            List<SysPermission> permissions = permissionService.getPermissionsByRoleId(user.getRoleId());
            List<String> permissionCodes = permissions.stream()
                .map(SysPermission::getPermissionCode)
                .collect(java.util.stream.Collectors.toList());
            userDTO.setPermissions(permissionCodes);
            
            // 设置区域ID（兼容farmId）
            userDTO.setAreaId(user.getAreaId() != null ? user.getAreaId() : user.getFarmId());
            
            return Result.success(userDTO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审核用户（通过或拒绝）
     */
    @PostMapping("/approve")
    @RequiresPermission({"system:user:approve"}) // 需要用户审核权限
    public Result<Boolean> approveUser(
            @RequestParam Long userId,
            @RequestParam Integer status, // 1-通过，0-拒绝
            @RequestParam(required = false) Long farmId, // 兼容旧参数名，实际是areaId
            @RequestParam(required = false) Long areaId, // 新参数名
            @RequestParam(required = false) String remark,
            HttpServletRequest request) {
        try {
            Object currentUserIdObj = request.getAttribute("userId");
            Long currentUserId = currentUserIdObj != null ? Long.valueOf(currentUserIdObj.toString()) : null;
            
            // 优先使用areaId，如果没有则使用farmId（兼容）
            Long finalAreaId = areaId != null ? areaId : farmId;
            
            boolean success = userService.approveUser(userId, status, finalAreaId, remark);
            
            if (success) {
                // 获取区域名称（如果有分配区域）
                String areaName = null;
                if (finalAreaId != null) {
                    com.server.aquacultureserver.domain.BaseArea area = areaService.getById(finalAreaId);
                    if (area != null) {
                        areaName = area.getAreaName();
                    }
                }
                
                // 发送消息通知给被审核的用户
                SysMessage message = new SysMessage();
                message.setReceiverId(userId);
                message.setSenderId(currentUserId != null ? currentUserId : 1L);
                message.setMessageTitle(status == 1 ? "账号审核通过" : "账号审核未通过");
                message.setMessageContent(status == 1 
                    ? String.format("您的账号已通过审核，可以正常登录使用。%s%s", 
                        areaName != null ? "已分配区域：" + areaName + "。" : "",
                        remark != null && !remark.isEmpty() ? "备注：" + remark : "")
                    : String.format("很抱歉，您的账号审核未通过。%s", 
                        remark != null && !remark.isEmpty() ? "原因：" + remark : ""));
                message.setMessageType("通知");
                message.setBusinessType("user");
                message.setBusinessId(userId);
                message.setStatus(0); // 未读
                messageService.sendMessage(message);
            }
            
            return Result.success(status == 1 ? "审核通过" : "审核拒绝", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询待审核用户列表
     */
    @GetMapping("/pending")
    @RequiresPermission({"system:user:view"}) // 需要用户查看权限
    public Result<List<SysUser>> getPendingUsers() {
        try {
            List<SysUser> users = userService.getPendingUsers();
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

