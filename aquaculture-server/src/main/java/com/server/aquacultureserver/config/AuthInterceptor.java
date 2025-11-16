package com.server.aquacultureserver.config;

import com.alibaba.fastjson.JSON;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT权限拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // 不需要认证的路径
    private static final String[] WHITE_LIST = {
        "/api/user/login",
        "/api/user/register",
        "/error"
    };
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 白名单路径直接放行
        for (String whitePath : WHITE_LIST) {
            if (requestURI.startsWith(whitePath)) {
                return true;
            }
        }
        
        // 从请求头获取token
        String authHeader = request.getHeader("Authorization");
        String token = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        
        // 如果没有token，返回未授权
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
        
        return true;
    }
    
    private void writeResponse(HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JSON.toJSONString(result));
    }
}

