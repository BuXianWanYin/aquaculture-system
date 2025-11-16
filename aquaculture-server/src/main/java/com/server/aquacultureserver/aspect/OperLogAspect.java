package com.server.aquacultureserver.aspect;

import com.server.aquacultureserver.domain.SysOperLog;
import com.server.aquacultureserver.service.SysOperLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 * 自动记录用户的操作日志
 */
@Aspect
@Component
public class OperLogAspect {
    
    @Autowired
    private SysOperLogService operLogService;
    
    @Around("execution(* com.server.aquacultureserver.controller..*.*(..))")
    public Object recordOperLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        
        HttpServletRequest request = attributes.getRequest();
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // 排除登录接口和操作日志查询接口（避免日志记录日志）
        if (requestURI.contains("/login") || requestURI.contains("/operLog")) {
            return joinPoint.proceed();
        }
        
        // 不记录查看操作（GET请求）
        if ("GET".equals(method)) {
            return joinPoint.proceed();
        }
        
        // 从request中获取用户信息（由拦截器设置）
        Object userIdObj = request.getAttribute("userId");
        Long userId = userIdObj != null ? Long.valueOf(userIdObj.toString()) : null;
        
        // 如果没有用户信息，可能是未登录，不记录日志
        if (userId == null) {
            return joinPoint.proceed();
        }
        
        // 获取IP地址
        String ipAddress = getIpAddress(request);
        
        // 获取模块名称和操作类型
        String module = getModuleName(requestURI);
        String operType = getOperType(method, requestURI);
        
        // 获取操作内容
        String operContent = getOperContent(joinPoint, method, requestURI);
        
        // 创建日志对象
        SysOperLog log = new SysOperLog();
        log.setUserId(userId);
        log.setModule(module);
        log.setOperType(operType);
        log.setOperContent(operContent);
        log.setIpAddress(ipAddress);
        log.setOperResult(1); // 默认成功，如果异常会在catch中设置为失败
        log.setOperTime(LocalDateTime.now());
        
        Object result = null;
        try {
            // 执行方法
            result = joinPoint.proceed();
            
            // 判断操作是否成功（根据返回结果）
            if (result instanceof com.server.aquacultureserver.common.Result) {
                com.server.aquacultureserver.common.Result<?> res = (com.server.aquacultureserver.common.Result<?>) result;
                if (res.getCode() != 200) {
                    log.setOperResult(0);
                    log.setErrorMsg(res.getMessage());
                }
            }
            
        } catch (Exception e) {
            // 操作失败
            log.setOperResult(0);
            log.setErrorMsg(e.getMessage());
            throw e; // 重新抛出异常
        } finally {
            // 异步保存日志（避免影响主流程）
            try {
                operLogService.saveLog(log);
            } catch (Exception e) {
                // 日志记录失败不影响主流程，只打印错误
                System.err.println("操作日志记录失败: " + e.getMessage());
            }
        }
        
        return result;
    }
    
    /**
     * 获取IP地址（优先获取IPv4地址）
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = null;
        
        // 优先从HTTP头获取IP
        ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 处理X-Forwarded-For可能包含多个IP的情况，取第一个
            if (ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            // 如果是IPv4，直接返回
            if (isIPv4(ip)) {
                return ip;
            }
        }
        
        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip) && isIPv4(ip)) {
            return ip;
        }
        
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip) && isIPv4(ip)) {
            return ip;
        }
        
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip) && isIPv4(ip)) {
            return ip;
        }
        
        // 从RemoteAddr获取
        ip = request.getRemoteAddr();
        
        // 如果是IPv6的localhost，转换为IPv4
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        
        // 如果是其他IPv6地址，尝试获取IPv4地址
        if (!isIPv4(ip)) {
            // 如果是IPv6地址，尝试从其他来源获取IPv4
            // 如果无法获取IPv4，返回127.0.0.1作为默认值
            return "127.0.0.1";
        }
        
        return ip;
    }
    
    /**
     * 判断是否为IPv4地址
     */
    private boolean isIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        // IPv4地址格式：xxx.xxx.xxx.xxx
        // 简单判断：包含点且不包含冒号
        return ip.contains(".") && !ip.contains(":");
    }
    
    /**
     * 获取模块名称
     */
    private String getModuleName(String requestURI) {
        if (requestURI.contains("/user")) {
            return "用户管理";
        } else if (requestURI.contains("/role")) {
            return "角色管理";
        } else if (requestURI.contains("/breed")) {
            return "养殖品种管理";
        } else if (requestURI.contains("/area")) {
            return "养殖区域管理";
        } else if (requestURI.contains("/equipment")) {
            return "设备管理";
        } else if (requestURI.contains("/plan")) {
            return "养殖计划管理";
        } else if (requestURI.contains("/yield")) {
            return "产量统计管理";
        } else if (requestURI.contains("/statistic")) {
            return "数据报表与分析";
        } else if (requestURI.contains("/dashboard")) {
            return "首页";
        }
        return "系统管理";
    }
    
    /**
     * 获取操作类型
     */
    private String getOperType(String method, String requestURI) {
        switch (method) {
            case "POST":
                if (requestURI.contains("/login")) {
                    return "登录";
                } else if (requestURI.contains("/changePassword") || requestURI.contains("/resetPassword")) {
                    return "修改";
                } else {
                    return "新增";
                }
            case "PUT":
                return "修改";
            case "DELETE":
                return "删除";
            case "GET":
                if (requestURI.contains("/page") || requestURI.contains("/list")) {
                    return "查询";
                } else {
                    return "查看";
                }
            default:
                return "其他";
        }
    }
    
    /**
     * 获取操作内容
     */
    private String getOperContent(ProceedingJoinPoint joinPoint, String method, String requestURI) {
        StringBuilder content = new StringBuilder();
        
        // 根据URI和方法构建操作内容
        if (requestURI.contains("/user")) {
            if ("POST".equals(method)) {
                content.append("新增用户");
            } else if ("PUT".equals(method)) {
                content.append("修改用户信息");
            } else if ("DELETE".equals(method)) {
                content.append("删除用户");
            } else if ("GET".equals(method)) {
                content.append("查询用户列表");
            }
        } else if (requestURI.contains("/role")) {
            if ("POST".equals(method)) {
                content.append("新增角色");
            } else if ("PUT".equals(method)) {
                content.append("修改角色");
            } else if ("DELETE".equals(method)) {
                content.append("删除角色");
            } else if ("GET".equals(method)) {
                content.append("查询角色列表");
            }
        } else if (requestURI.contains("/breed")) {
            if ("POST".equals(method)) {
                content.append("新增养殖品种");
            } else if ("PUT".equals(method)) {
                content.append("修改养殖品种");
            } else if ("DELETE".equals(method)) {
                content.append("删除养殖品种");
            } else if ("GET".equals(method)) {
                content.append("查询养殖品种列表");
            }
        } else if (requestURI.contains("/area")) {
            if ("POST".equals(method)) {
                content.append("新增养殖区域");
            } else if ("PUT".equals(method)) {
                content.append("修改养殖区域");
            } else if ("DELETE".equals(method)) {
                content.append("删除养殖区域");
            } else if ("GET".equals(method)) {
                content.append("查询养殖区域列表");
            }
        } else if (requestURI.contains("/equipment")) {
            if ("POST".equals(method)) {
                content.append("新增设备");
            } else if ("PUT".equals(method)) {
                content.append("修改设备信息");
            } else if ("DELETE".equals(method)) {
                content.append("删除设备");
            } else if ("GET".equals(method)) {
                content.append("查询设备列表");
            }
        } else if (requestURI.contains("/plan")) {
            if ("POST".equals(method)) {
                content.append("新增养殖计划");
            } else if ("PUT".equals(method)) {
                content.append("修改养殖计划");
            } else if ("DELETE".equals(method)) {
                content.append("删除养殖计划");
            } else if (requestURI.contains("/approve")) {
                content.append("审批养殖计划");
            } else if ("GET".equals(method)) {
                content.append("查询养殖计划列表");
            }
        } else if (requestURI.contains("/yield")) {
            if ("POST".equals(method)) {
                content.append("新增产量统计");
            } else if ("PUT".equals(method)) {
                content.append("修改产量统计");
            } else if ("DELETE".equals(method)) {
                content.append("删除产量统计");
            } else if (requestURI.contains("/audit")) {
                content.append("审核产量统计");
            } else if ("GET".equals(method)) {
                content.append("查询产量统计列表");
            }
        } else if (requestURI.contains("/statistic")) {
            if ("POST".equals(method)) {
                content.append("新增统计数据");
            } else if ("PUT".equals(method)) {
                content.append("修改统计数据");
            } else if ("DELETE".equals(method)) {
                content.append("删除统计数据");
            } else if ("GET".equals(method)) {
                content.append("查询统计数据");
            }
        } else if (requestURI.contains("/dashboard")) {
            content.append("查看首页统计");
        } else {
            content.append("执行操作: ").append(requestURI);
        }
        
        // 尝试获取参数信息
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // 获取第一个参数（通常是实体对象）
                Object firstArg = args[0];
                if (firstArg != null) {
                    String argStr = firstArg.toString();
                    // 如果是实体对象，尝试获取关键字段
                    if (firstArg instanceof com.server.aquacultureserver.domain.SysUser) {
                        com.server.aquacultureserver.domain.SysUser user = (com.server.aquacultureserver.domain.SysUser) firstArg;
                        if (user.getUsername() != null) {
                            content.append("：").append(user.getUsername());
                        }
                    } else if (argStr.length() < 100) {
                        // 参数内容不太长时，添加到操作内容中
                        content.append("：").append(argStr);
                    }
                }
            }
        } catch (Exception e) {
            // 忽略参数解析错误
        }
        
        return content.toString();
    }
}

