package com.server.aquacultureserver.aspect;

import com.alibaba.fastjson.JSON;
import com.server.aquacultureserver.annotation.RequiresPermission;
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
import java.util.List;

/**
 * 权限切面
 * 基于权限代码进行权限控制
 */
@Aspect
@Component
public class PermissionAspect {
    
    @Around("@annotation(com.server.aquacultureserver.annotation.RequiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 从request中获取用户权限列表（由拦截器设置）
        @SuppressWarnings("unchecked")
        List<String> userPermissions = (List<String>) request.getAttribute("permissions");
        if (userPermissions == null || userPermissions.isEmpty()) {
            return writeErrorResponse(attributes.getResponse(), "未获取到用户权限信息");
        }
        
        // 获取方法上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresPermission requiresPermission = method.getAnnotation(RequiresPermission.class);
        
        if (requiresPermission != null) {
            String[] requiredPermissions = requiresPermission.value();
            boolean hasPermission = false;
            
            // 检查用户是否拥有任一所需权限
            for (String requiredPermission : requiredPermissions) {
                if (userPermissions.contains(requiredPermission)) {
                    hasPermission = true;
                    break;
                }
            }
            
            if (!hasPermission) {
                return writeErrorResponse(attributes.getResponse(), requiresPermission.message());
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

