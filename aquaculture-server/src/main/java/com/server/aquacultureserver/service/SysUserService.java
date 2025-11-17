package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.dto.LoginDTO;
import com.server.aquacultureserver.dto.UserDTO;


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
     * @param userId 用户ID
     * @param oldPassword 原密码（可为空，为空时跳过原密码验证，仅限修改自己的密码）
     * @param newPassword 新密码
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
    
    /**
     * 审核用户（通过或拒绝）
     * @param userId 用户ID
     * @param status 审核状态（1-通过，0-拒绝）
     * @param departmentId 部门ID（部门管理员使用）
     * @param areaId 区域ID（操作员使用）
     * @param remark 备注
     */
    boolean approveUser(Long userId, Integer status, Long departmentId, Long areaId, String remark);
    
    /**
     * 获取所有管理员用户ID列表
     */
    java.util.List<Long> getAllAdminUserIds();
    
    /**
     * 查询待审核用户列表
     */
    java.util.List<SysUser> getPendingUsers();
    
    /**
     * 获取用户列表（用于下拉选择）
     * 普通操作员只能看到自己区域的用户
     */
    java.util.List<SysUser> getUserListForSelect(Long areaId);
}

