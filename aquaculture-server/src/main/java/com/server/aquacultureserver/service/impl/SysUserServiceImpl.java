package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysPermission;
import com.server.aquacultureserver.domain.SysRole;
import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.dto.LoginDTO;
import com.server.aquacultureserver.dto.UserDTO;
import com.server.aquacultureserver.mapper.SysRoleMapper;
import com.server.aquacultureserver.mapper.SysUserMapper;
import com.server.aquacultureserver.service.SysPermissionService;
import com.server.aquacultureserver.service.SysUserService;
import com.server.aquacultureserver.utils.JwtUtil;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class SysUserServiceImpl implements SysUserService {
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private SysRoleMapper roleMapper;
    
    @Autowired
    private SysPermissionService permissionService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 密码加密
     */
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
    }
    
    @Override
    public UserDTO login(LoginDTO loginDTO) {
        SysUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, loginDTO.getUsername())
        );
        
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 先检查用户状态，再验证密码，这样能给出更准确的提示
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }
        
        // 待审核用户不能登录，优先提示审核状态
        if (user.getStatus() == 2) {
            throw new RuntimeException("您的账号正在审核中，请等待管理员审核通过后再登录");
        }
        
        // 验证密码
        String encryptedPassword = encryptPassword(loginDTO.getPassword());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        
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
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRoleId());
        userDTO.setToken(token);
        
        return userDTO;
    }
    
    @Override
    public SysUser getById(Long userId) {
        return userMapper.selectById(userId);
    }
    
    @Override
    public SysUser getByUsername(String username) {
        return userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
        );
    }
    
    @Override
    public Page<SysUser> getPage(Integer current, Integer size, String username, Long roleId) {
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (roleId != null) {
            wrapper.eq(SysUser::getRoleId, roleId);
        }
        
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = userMapper.selectPage(page, wrapper);
        
        // 填充角色名称
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            for (SysUser user : result.getRecords()) {
                if (user.getRoleId() != null) {
                    SysRole role = roleMapper.selectById(user.getRoleId());
                    if (role != null) {
                        user.setRoleName(role.getRoleName());
                    }
                }
            }
        }
        
        return result;
    }
    
    @Override
    public boolean saveUser(SysUser user) {
        // 检查用户名是否已存在
        SysUser existUser = getByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(encryptPassword(user.getPassword()));
        
        // 如果是注册（不是管理员创建），设置状态为待审核（2）
        // 管理员创建用户时，status应该已经设置好了（1-启用）
        if (user.getStatus() == null) {
            user.setStatus(2); // 2-待审核
            // 注册时不设置部门，由管理员审核时分配
            user.setFarmId(null);
        }
        
        return userMapper.insert(user) > 0;
    }
    
    @Override
    public boolean updateUser(SysUser user) {
        // 如果更新了用户名，检查是否重复
        if (user.getUsername() != null) {
            SysUser existUser = getByUsername(user.getUsername());
            if (existUser != null && !existUser.getUserId().equals(user.getUserId())) {
                throw new RuntimeException("用户名已存在");
            }
        }
        
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    public boolean deleteUser(Long userId) {
        return userMapper.deleteById(userId) > 0;
    }
    
    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        String encryptedOldPassword = encryptPassword(oldPassword);
        if (!encryptedOldPassword.equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(encryptPassword(newPassword));
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setPassword(encryptPassword(newPassword));
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    public long count() {
        return userMapper.selectCount(null);
    }
    
    @Override
    public boolean approveUser(Long userId, Integer status, Long farmId, String remark) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (user.getStatus() != 2) {
            throw new RuntimeException("该用户不是待审核状态");
        }
        
        // 更新用户状态和区域
        user.setStatus(status); // 1-通过，0-拒绝
        if (farmId != null) {
            // 将farmId作为areaId使用
            user.setAreaId(farmId);
            // 同时更新farmId以保持兼容
            user.setFarmId(farmId);
        }
        
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    public List<Long> getAllAdminUserIds() {
        List<SysUser> adminUsers = userMapper.selectList(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRoleId, 1) // 系统管理员
                .eq(SysUser::getStatus, 1) // 已启用
        );
        return adminUsers.stream()
            .map(SysUser::getUserId)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<SysUser> getPendingUsers() {
        List<SysUser> users = userMapper.selectList(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStatus, 2) // 待审核
                .orderByDesc(SysUser::getCreateTime)
        );
        
        // 填充角色名称
        for (SysUser user : users) {
            if (user.getRoleId() != null) {
                SysRole role = roleMapper.selectById(user.getRoleId());
                if (role != null) {
                    user.setRoleName(role.getRoleName());
                }
            }
        }
        
        return users;
    }
    
    @Override
    public List<SysUser> getUserListForSelect(Long areaId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        // 只返回已启用的用户
        wrapper.eq(SysUser::getStatus, 1);
        
        // 普通操作员只能看到自己区域的用户
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(SysUser::getAreaId, userAreaId);
            } else {
                return Collections.emptyList();
            }
        } else if (areaId != null) {
            // 管理员可以指定区域
            wrapper.eq(SysUser::getAreaId, areaId);
        }
        
        wrapper.select(SysUser::getUserId, SysUser::getUsername, SysUser::getRealName, SysUser::getAreaId)
               .orderByDesc(SysUser::getCreateTime);
        return userMapper.selectList(wrapper);
    }
}

