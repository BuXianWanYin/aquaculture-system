package com.server.aquacultureserver.utils;

import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.domain.BaseArea;
import com.server.aquacultureserver.constants.RoleConstants;
import com.server.aquacultureserver.mapper.SysUserMapper;
import com.server.aquacultureserver.mapper.BaseAreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户上下文工具类
 * 用于获取当前登录用户信息
 */
@Component
public class UserContext {
    
    private static SysUserMapper userMapper;
    private static BaseAreaMapper areaMapper;
    
    @Autowired
    public void setUserMapper(SysUserMapper userMapper) {
        UserContext.userMapper = userMapper;
    }
    
    @Autowired
    public void setAreaMapper(BaseAreaMapper areaMapper) {
        UserContext.areaMapper = areaMapper;
    }
    
    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            HttpServletRequest request = attributes.getRequest();
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return null;
            }
            return Long.valueOf(userIdObj.toString());
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取当前登录用户信息
     */
    public static SysUser getCurrentUser() {
        Long userId = getCurrentUserId();
        if (userId == null || userMapper == null) {
            return null;
        }
        return userMapper.selectById(userId);
    }
    
    /**
     * 获取当前用户的角色ID
     */
    public static Long getCurrentUserRoleId() {
        SysUser user = getCurrentUser();
        return user != null ? user.getRoleId() : null;
    }
    
    /**
     * 获取当前用户的区域ID
     */
    public static Long getCurrentUserAreaId() {
        SysUser user = getCurrentUser();
        if (user == null) {
            return null;
        }
        return user.getAreaId();
    }
    
    /**
     * 获取当前用户的角色名称（从request中获取，由AuthInterceptor设置）
     */
    private static String getCurrentUserRoleName() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            HttpServletRequest request = attributes.getRequest();
            Object roleNameObj = request.getAttribute("roleName");
            if (roleNameObj != null) {
                return roleNameObj.toString();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 判断当前用户是否为系统管理员（通过角色名称判断，避免硬编码角色ID）
     */
    public static boolean isAdmin() {
        String roleName = getCurrentUserRoleName();
        return RoleConstants.isAdmin(roleName);
    }
    
    /**
     * 判断当前用户是否为普通操作员（通过角色名称判断，避免硬编码角色ID）
     */
    public static boolean isOperator() {
        String roleName = getCurrentUserRoleName();
        return RoleConstants.isOperator(roleName);
    }
    
    /**
     * 判断当前用户是否为决策层（通过角色名称判断，避免硬编码角色ID）
     */
    public static boolean isDecisionMaker() {
        String roleName = getCurrentUserRoleName();
        return RoleConstants.isDecisionMaker(roleName);
    }
    
    /**
     * 判断当前用户是否为部门管理员（通过角色名称判断，避免硬编码角色ID）
     */
    public static boolean isDepartmentManager() {
        String roleName = getCurrentUserRoleName();
        return RoleConstants.isDepartmentManager(roleName);
    }
    
    /**
     * 获取部门管理员管理的所有区域ID列表
     * 部门管理员直接通过departmentId获取该部门下的所有区域
     */
    public static List<Long> getDepartmentManagerAreaIds() {
        if (!isDepartmentManager()) {
            return null;
        }
        
        SysUser user = getCurrentUser();
        if (user == null || user.getDepartmentId() == null || areaMapper == null) {
            return null;
        }
        
        // 直接通过用户的departmentId查询该部门下的所有区域
        List<BaseArea> areas = areaMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BaseArea>()
                .eq(BaseArea::getDepartmentId, user.getDepartmentId())
                .eq(BaseArea::getStatus, 1)
        );
        
        return areas.stream()
            .map(BaseArea::getAreaId)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取部门管理员管理的所有用户ID列表
     * 部门管理员直接通过departmentId获取该部门下所有区域的用户（普通操作员）
     * 同时包含部门管理员自己创建的记录
     */
    public static List<Long> getDepartmentManagerUserIds() {
        if (!isDepartmentManager()) {
            return null;
        }
        
        SysUser currentUser = getCurrentUser();
        if (currentUser == null || currentUser.getDepartmentId() == null || userMapper == null) {
            return null;
        }
        
        // 先获取该部门下的所有区域ID
        List<Long> areaIds = getDepartmentManagerAreaIds();
        if (areaIds == null || areaIds.isEmpty()) {
            // 如果没有区域，只返回部门管理员自己
            return java.util.Collections.singletonList(currentUser.getUserId());
        }
        
        // 查询该部门下所有区域的用户（普通操作员等）
        List<SysUser> users = userMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                .in(SysUser::getAreaId, areaIds)
                .eq(SysUser::getStatus, 1)
        );
        
        List<Long> userIds = users.stream()
            .map(SysUser::getUserId)
            .collect(Collectors.toList());
        
        // 添加部门管理员自己的ID（如果还没有包含）
        Long currentUserId = currentUser.getUserId();
        if (!userIds.contains(currentUserId)) {
            userIds.add(currentUserId);
        }
        
        return userIds;
    }
}

