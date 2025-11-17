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
    
    @Autowired
    private com.server.aquacultureserver.service.BaseDepartmentService departmentService;
    
    @Autowired
    private com.server.aquacultureserver.mapper.SysUserMapper userMapper;
    
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
     * 如果oldPassword为空，则不需要原密码验证（仅限修改自己的密码）
     */
    @PostMapping("/changePassword")
    public Result<Boolean> changePassword(
            @RequestParam Long userId,
            @RequestParam(required = false) String oldPassword,
            @RequestParam String newPassword,
            HttpServletRequest request) {
        try {
            // 如果原密码为空，则验证用户只能修改自己的密码
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                Object userIdObj = request.getAttribute("userId");
                if (userIdObj == null) {
                    return Result.error("未获取到用户信息");
                }
                Long currentUserId = Long.valueOf(userIdObj.toString());
                
                // 只能修改自己的密码
                if (!currentUserId.equals(userId)) {
                    return Result.error("只能修改自己的密码");
                }
            }
            
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
            // 注册时只能选择普通操作员（roleId=3）或部门管理员（roleId=5）
            if (user.getRoleId() == null) {
                return Result.error("请选择角色");
            }
            if (user.getRoleId() == 1 || user.getRoleId() == 2 || user.getRoleId() == 4) {
                return Result.error("不允许注册系统管理员、决策层或其他角色，只能选择普通操作员或部门管理员");
            }
            if (user.getRoleId() != 3 && user.getRoleId() != 5) {
                return Result.error("只能选择普通操作员或部门管理员角色");
            }
            
            // 根据角色验证必填字段
            if (user.getRoleId() == 5L) {
                // 部门管理员必须选择部门
                if (user.getDepartmentId() == null) {
                    return Result.error("部门管理员必须选择所属部门");
                }
            } else if (user.getRoleId() == 3L) {
                // 操作员必须选择部门（用于后续分配区域时确定部门）
                if (user.getDepartmentId() == null) {
                    return Result.error("操作员必须选择所属部门");
                }
            }
            
            // 注册时不设置区域，由管理员审核时分配
            user.setAreaId(null);
            // status会在saveUser中设置为2（待审核）
            
            boolean success = userService.saveUser(user);
            
            if (success) {
                // 发送消息通知
                // 如果是操作员，通知该部门的部门管理员和系统管理员
                // 如果是部门管理员，只通知系统管理员
                String roleName = user.getRoleId() == 3 ? "普通操作员" : "部门管理员";
                
                // 获取注册的用户ID（插入后自动生成）
                SysUser registeredUser = userService.getByUsername(user.getUsername());
                
                // 发送消息通知
                // 如果是操作员（roleId=3），通知该部门的部门管理员和系统管理员
                // 如果是部门管理员（roleId=5），只通知系统管理员
                
                // 通知系统管理员
                List<Long> adminUserIds = userService.getAllAdminUserIds();
                
                // 如果是操作员，还需要通知该部门的部门管理员
                java.util.Set<Long> notifyUserIds = new java.util.HashSet<>(adminUserIds);
                if (user.getRoleId() == 3L && user.getDepartmentId() != null) {
                    // 查询该部门下的部门管理员（roleId=5）
                    List<SysUser> deptManagers = userMapper.selectList(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getRoleId, 5L) // 部门管理员
                            .eq(SysUser::getDepartmentId, user.getDepartmentId())
                            .eq(SysUser::getStatus, 1) // 已启用
                    );
                    for (SysUser deptManager : deptManagers) {
                        notifyUserIds.add(deptManager.getUserId());
                    }
                }
                
                for (Long notifyUserId : notifyUserIds) {
                    SysMessage message = new SysMessage();
                    message.setReceiverId(notifyUserId);
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
            // 设置区域ID
            existingUser.setAreaId(user.getAreaId());
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
            
            // 设置区域ID和部门ID
            userDTO.setAreaId(user.getAreaId());
            userDTO.setDepartmentId(user.getDepartmentId());
            
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
            @RequestParam(required = false) Long departmentId, // 部门ID（部门管理员使用）
            @RequestParam(required = false) Long areaId, // 区域ID（操作员使用）
            @RequestParam(required = false) String remark,
            HttpServletRequest request) {
        try {
            Object currentUserIdObj = request.getAttribute("userId");
            Long currentUserId = currentUserIdObj != null ? Long.valueOf(currentUserIdObj.toString()) : null;
            
            // 参数顺序：userId, status, departmentId, areaId, remark
            boolean success = userService.approveUser(userId, status, departmentId, areaId, remark);
            
            if (success) {
                // 获取被审核用户信息
                SysUser approvedUser = userService.getById(userId);
                String notifyContent = "";
                
                if (status == 1) {
                    // 审核通过
                    if (approvedUser != null && approvedUser.getRoleId() != null) {
                        if (approvedUser.getRoleId() == 5L) {
                            // 部门管理员：显示部门信息
                            if (departmentId != null) {
                                com.server.aquacultureserver.domain.BaseDepartment dept = departmentService.getById(departmentId);
                                if (dept != null) {
                                    notifyContent = String.format("您的账号已通过审核，已分配部门：%s。", dept.getDepartmentName());
                                } else {
                                    notifyContent = "您的账号已通过审核，可以正常登录使用。";
                                }
                            } else {
                                notifyContent = "您的账号已通过审核，可以正常登录使用。";
                            }
                        } else if (approvedUser.getRoleId() == 3L) {
                            // 操作员：显示区域信息
                            if (areaId != null) {
                                com.server.aquacultureserver.domain.BaseArea area = areaService.getById(areaId);
                                if (area != null) {
                                    notifyContent = String.format("您的账号已通过审核，已分配区域：%s。", area.getAreaName());
                                } else {
                                    notifyContent = "您的账号已通过审核，可以正常登录使用。";
                                }
                            } else {
                                notifyContent = "您的账号已通过审核，可以正常登录使用。";
                            }
                        } else {
                            notifyContent = "您的账号已通过审核，可以正常登录使用。";
                        }
                    }
                    
                    if (remark != null && !remark.isEmpty()) {
                        notifyContent += "备注：" + remark;
                    }
                } else {
                    // 审核拒绝
                    notifyContent = String.format("很抱歉，您的账号审核未通过。%s", 
                        remark != null && !remark.isEmpty() ? "原因：" + remark : "");
                }
                
                // 发送消息通知给被审核的用户
                SysMessage message = new SysMessage();
                message.setReceiverId(userId);
                message.setSenderId(currentUserId != null ? currentUserId : 1L);
                message.setMessageTitle(status == 1 ? "账号审核通过" : "账号审核未通过");
                message.setMessageContent(notifyContent);
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

