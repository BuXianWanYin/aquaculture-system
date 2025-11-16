package com.server.aquacultureserver.aspect;

import com.alibaba.fastjson.JSON;
import com.server.aquacultureserver.annotation.RequiresRole;
import com.server.aquacultureserver.common.Result;
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
        
        // 从request中获取角色ID（由拦截器设置）
        Object roleIdObj = request.getAttribute("roleId");
        if (roleIdObj == null) {
            return writeErrorResponse(attributes.getResponse(), "未获取到用户角色信息");
        }
        
        Long roleId = Long.valueOf(roleIdObj.toString());
        
        // 获取方法上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresRole requiresRole = method.getAnnotation(RequiresRole.class);
        
        if (requiresRole != null) {
            int[] allowedRoles = requiresRole.value();
            boolean hasPermission = false;
            
            // 检查用户角色是否在允许的角色列表中
            for (int allowedRole : allowedRoles) {
                if (roleId == allowedRole) {
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
    
    private Object writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JSON.toJSONString(Result.error(403, message)));
        return null;
    }
}

