package com.server.aquacultureserver.utils;

import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具类
 * 用于获取当前登录用户信息
 */
@Component
public class UserContext {
    
    private static SysUserMapper userMapper;
    
    @Autowired
    public void setUserMapper(SysUserMapper userMapper) {
        UserContext.userMapper = userMapper;
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
        // 优先使用areaId，如果没有则使用farmId（兼容）
        return user.getAreaId() != null ? user.getAreaId() : user.getFarmId();
    }
    
    /**
     * 判断当前用户是否为系统管理员
     */
    public static boolean isAdmin() {
        Long roleId = getCurrentUserRoleId();
        return roleId != null && roleId == 1;
    }
    
    /**
     * 判断当前用户是否为普通操作员
     */
    public static boolean isOperator() {
        Long roleId = getCurrentUserRoleId();
        return roleId != null && roleId == 3;
    }
    
    /**
     * 判断当前用户是否为决策层
     */
    public static boolean isDecisionMaker() {
        Long roleId = getCurrentUserRoleId();
        return roleId != null && roleId == 4;
    }
}

