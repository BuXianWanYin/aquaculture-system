package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.dto.LoginDTO;
import com.server.aquacultureserver.dto.UserDTO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface SysUserService {
    
    /**
     * 用户登录
     */
    UserDTO login(LoginDTO loginDTO);
    
    /**
     * 根据ID查询用户
     */
    SysUser getById(Long userId);
    
    /**
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);
    
    /**
     * 分页查询用户列表
     */
    Page<SysUser> getPage(Integer current, Integer size, String username, Long roleId);
    
    /**
     * 新增用户
     */
    boolean saveUser(SysUser user);
    
    /**
     * 更新用户
     */
    boolean updateUser(SysUser user);
    
    /**
     * 删除用户
     */
    boolean deleteUser(Long userId);
    
    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    boolean resetPassword(Long userId, String newPassword);
    
    /**
     * 统计用户总数
     */
    long count();
}

