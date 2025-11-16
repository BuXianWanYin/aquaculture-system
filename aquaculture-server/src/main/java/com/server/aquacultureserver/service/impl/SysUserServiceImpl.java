package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysRole;
import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.dto.LoginDTO;
import com.server.aquacultureserver.dto.UserDTO;
import com.server.aquacultureserver.mapper.SysRoleMapper;
import com.server.aquacultureserver.mapper.SysUserMapper;
import com.server.aquacultureserver.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 用户服务实现类
 */
@Service
public class SysUserServiceImpl implements SysUserService {
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private SysRoleMapper roleMapper;
    
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
        
        String encryptedPassword = encryptPassword(loginDTO.getPassword());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }
        
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        
        // 查询角色信息
        SysRole role = roleMapper.selectById(user.getRoleId());
        if (role != null) {
            userDTO.setRoleName(role.getRoleName());
        }
        
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
        return userMapper.selectPage(page, wrapper);
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
}

