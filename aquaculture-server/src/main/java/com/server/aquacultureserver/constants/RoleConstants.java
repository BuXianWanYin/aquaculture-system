package com.server.aquacultureserver.constants;

/**
 * 角色常量类
 * 用于避免硬编码角色ID，通过角色名称进行判断
 */
public class RoleConstants {
    
    /**
     * 管理员角色名称
     */
    public static final String ROLE_ADMIN = "管理员";
    
    /**
     * 普通操作员角色名称
     */
    public static final String ROLE_OPERATOR = "普通操作员";
    
    /**
     * 决策层角色名称
     */
    public static final String ROLE_DECISION_MAKER = "决策层";
    
    /**
     * 部门管理员角色名称
     */
    public static final String ROLE_DEPARTMENT_MANAGER = "部门管理员";
    
    /**
     * 根据角色名称判断是否为管理员
     */
    public static boolean isAdmin(String roleName) {
        return ROLE_ADMIN.equals(roleName) || "系统管理员".equals(roleName);
    }
    
    /**
     * 根据角色名称判断是否为普通操作员
     */
    public static boolean isOperator(String roleName) {
        return ROLE_OPERATOR.equals(roleName) || "操作员".equals(roleName);
    }
    
    /**
     * 根据角色名称判断是否为决策层
     */
    public static boolean isDecisionMaker(String roleName) {
        return ROLE_DECISION_MAKER.equals(roleName) || "决策者".equals(roleName);
    }
    
    /**
     * 根据角色名称判断是否为部门管理员
     */
    public static boolean isDepartmentManager(String roleName) {
        return ROLE_DEPARTMENT_MANAGER.equals(roleName);
    }
}

