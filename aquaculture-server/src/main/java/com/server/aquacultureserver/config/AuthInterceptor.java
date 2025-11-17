package com.server.aquacultureserver.config;

import com.alibaba.fastjson.JSON;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.SysRole;
import com.server.aquacultureserver.domain.SysPermission;
import com.server.aquacultureserver.mapper.SysRoleMapper;
import com.server.aquacultureserver.service.SysPermissionService;
import com.server.aquacultureserver.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT权限拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private SysRoleMapper roleMapper;
    
    @Autowired
    private SysPermissionService permissionService;
    
    // 不需要认证的路径（但这些路径如果提供了token，也会解析用户信息）
    private static final String[] WHITE_LIST = {
        "/api/user/login",
        "/api/user/register",
        "/api/department/all",  // 注册页面需要加载部门列表，不需要token；但登录后需要根据角色过滤
        "/error"
    };
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 从请求头获取token
        String authHeader = request.getHeader("Authorization");
        String token = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        
        // 检查是否是白名单路径
        boolean isWhiteListPath = false;
        for (String whitePath : WHITE_LIST) {
            if (requestURI.startsWith(whitePath)) {
                isWhiteListPath = true;
                break;
            }
        }
        
        // 如果是白名单路径且没有token，直接放行（用于注册等场景）
        if (isWhiteListPath && (token == null || token.isEmpty())) {
            return true;
        }
        
        // 如果是白名单路径但有token，解析token并设置用户信息（用于登录后根据角色过滤）
        if (isWhiteListPath && token != null && !token.isEmpty()) {
            // 验证token有效性
            if (jwtUtil.validateToken(token)) {
                // 将用户信息存储到request中，供后续使用
                Long userId = jwtUtil.getUserIdFromToken(token);
                Long roleId = jwtUtil.getRoleIdFromToken(token);
                request.setAttribute("userId", userId);
                request.setAttribute("roleId", roleId);
                request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
                
                // 查询角色名称并设置到request中（用于角色权限判断，避免硬编码角色ID）
                if (roleId != null) {
                    SysRole role = roleMapper.selectById(roleId);
                    if (role != null) {
                        request.setAttribute("roleName", role.getRoleName());
                        
                        // 查询用户权限列表并设置到request中（用于权限控制）
                        List<SysPermission> permissions = permissionService.getPermissionsByRoleId(roleId);
                        List<String> permissionCodes = permissions.stream()
                            .map(SysPermission::getPermissionCode)
                            .collect(Collectors.toList());
                        request.setAttribute("permissions", permissionCodes);
                    }
                }
            }
            // 即使token无效，也放行（允许未登录访问）
            return true;
        }
        
        // 非白名单路径必须验证token
        if (token == null || token.isEmpty()) {
            writeResponse(response, Result.error(401, "未授权，请先登录"));
            return false;
        }
        
        // 验证token有效性
        if (!jwtUtil.validateToken(token)) {
            writeResponse(response, Result.error(401, "Token无效或已过期，请重新登录"));
            return false;
        }
        
        // 将用户信息存储到request中，供后续使用
        Long userId = jwtUtil.getUserIdFromToken(token);
        Long roleId = jwtUtil.getRoleIdFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("roleId", roleId);
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        
        // 查询角色名称并设置到request中（用于角色权限判断，避免硬编码角色ID）
        if (roleId != null) {
            SysRole role = roleMapper.selectById(roleId);
            if (role != null) {
                request.setAttribute("roleName", role.getRoleName());
                
                // 查询用户权限列表并设置到request中（用于权限控制）
                List<SysPermission> permissions = permissionService.getPermissionsByRoleId(roleId);
                List<String> permissionCodes = permissions.stream()
                    .map(SysPermission::getPermissionCode)
                    .collect(Collectors.toList());
                request.setAttribute("permissions", permissionCodes);
            }
        }
        
        return true;
    }
    
    private void writeResponse(HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JSON.toJSONString(result));
    }
}

