package com.server.aquacultureserver.utils;

import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.domain.BaseArea;
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
    
    /**
     * 判断当前用户是否为部门管理员
     */
    public static boolean isDepartmentManager() {
        Long roleId = getCurrentUserRoleId();
        return roleId != null && roleId == 5;
    }
    
    /**
     * 获取部门管理员管理的所有区域ID列表
     * 通过用户的areaId找到对应的departmentId，然后获取该部门下的所有区域
     */
    public static List<Long> getDepartmentManagerAreaIds() {
        if (!isDepartmentManager()) {
            return null;
        }
        
        Long userAreaId = getCurrentUserAreaId();
        if (userAreaId == null || areaMapper == null) {
            return null;
        }
        
        // 获取用户绑定的区域
        BaseArea userArea = areaMapper.selectById(userAreaId);
        if (userArea == null || userArea.getDepartmentId() == null) {
            return null;
        }
        
        // 查询该部门下的所有区域
        List<BaseArea> areas = areaMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BaseArea>()
                .eq(BaseArea::getDepartmentId, userArea.getDepartmentId())
                .eq(BaseArea::getStatus, 1)
        );
        
        return areas.stream()
            .map(BaseArea::getAreaId)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取部门管理员管理的所有用户ID列表
     * 通过用户的areaId找到对应的departmentId，然后获取该部门下所有区域的用户
     */
    public static List<Long> getDepartmentManagerUserIds() {
        if (!isDepartmentManager()) {
            return null;
        }
        
        List<Long> areaIds = getDepartmentManagerAreaIds();
        if (areaIds == null || areaIds.isEmpty() || userMapper == null) {
            return null;
        }
        
        // 查询该部门下所有区域的用户
        List<SysUser> users = userMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                .in(SysUser::getAreaId, areaIds)
                .eq(SysUser::getStatus, 1)
        );
        
        return users.stream()
            .map(SysUser::getUserId)
            .collect(Collectors.toList());
    }
}

