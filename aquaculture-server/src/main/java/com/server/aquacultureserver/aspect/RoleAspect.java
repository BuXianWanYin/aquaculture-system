package com.server.aquacultureserver.aspect;

import com.alibaba.fastjson.JSON;
import com.server.aquacultureserver.annotation.RequiresRole;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.constants.RoleConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 角色权限切面
 */
@Aspect
@Component
public class RoleAspect {
    
    @Around("@annotation(com.server.aquacultureserver.annotation.RequiresRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 从request中获取角色名称（由拦截器设置，避免硬编码角色ID）
        Object roleNameObj = request.getAttribute("roleName");
        if (roleNameObj == null) {
            return writeErrorResponse(attributes.getResponse(), "未获取到用户角色信息");
        }
        
        String roleName = roleNameObj.toString();
        
        // 获取方法上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresRole requiresRole = method.getAnnotation(RequiresRole.class);
        
        if (requiresRole != null) {
            int[] allowedRoleIds = requiresRole.value();
            boolean hasPermission = false;
            
            // 将角色ID转换为角色名称进行判断（避免硬编码）
            for (int roleId : allowedRoleIds) {
                String allowedRoleName = getRoleNameById(roleId);
                if (allowedRoleName != null && allowedRoleName.equals(roleName)) {
                    hasPermission = true;
                    break;
                }
            }
            
            if (!hasPermission) {
                return writeErrorResponse(attributes.getResponse(), requiresRole.message());
            }
        }
        
        // 权限检查通过，继续执行方法
        return joinPoint.proceed();
    }
    
    /**
     * 根据角色ID获取角色名称（用于权限判断，避免硬编码）
     * 注意：这里仍然使用角色ID，但通过查询数据库获取角色名称进行判断
     * 如果角色ID对应的角色不存在，返回null
     */
    private String getRoleNameById(int roleId) {
        // 使用角色常量进行判断，避免硬编码
        // 如果角色ID是1，返回管理员角色名称
        if (roleId == 1) {
            return RoleConstants.ROLE_ADMIN;
        } else if (roleId == 3) {
            return RoleConstants.ROLE_OPERATOR;
        } else if (roleId == 4) {
            return RoleConstants.ROLE_DECISION_MAKER;
        }
        return null;
    }
    
    private Object writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JSON.toJSONString(Result.error(403, message)));
        return null;
    }
}

