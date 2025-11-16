package com.server.aquacultureserver.aspect;

import com.server.aquacultureserver.domain.*;
import com.server.aquacultureserver.service.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 操作日志切面
 * 自动记录用户的操作日志
 */
@Aspect
@Component
public class OperLogAspect {
    
    @Autowired
    private SysOperLogService operLogService;
    
    @Autowired
    private SysUserService userService;
    
    @Autowired
    private SysRoleService roleService;
    
    @Autowired
    private BaseAreaService areaService;
    
    @Autowired
    private BaseBreedService breedService;
    
    @Autowired
    private BaseEquipmentService equipmentService;
    
    @Autowired
    private AquaculturePlanService planService;
    
    @Autowired
    private YieldStatisticsService yieldStatisticsService;
    
    @Autowired
    private BaseDepartmentService departmentService;
    
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
        } else if (requestURI.contains("/department")) {
            return "部门管理";
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
        } else if (requestURI.contains("/feed")) {
            return "饲料管理";
        } else if (requestURI.contains("/disease")) {
            return "病害防控管理";
        } else if (requestURI.contains("/production")) {
            return "日常生产记录";
        } else if (requestURI.contains("/sales")) {
            return "销售管理";
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
     * 获取操作内容（将ID转换为名称）
     */
    private String getOperContent(ProceedingJoinPoint joinPoint, String method, String requestURI) {
        StringBuilder content = new StringBuilder();
        
        // 根据URI和方法构建操作内容
        if (requestURI.contains("/user")) {
            if ("POST".equals(method)) {
                content.append("新增用户");
                appendUserName(content, joinPoint);
            } else if ("PUT".equals(method)) {
                content.append("修改用户信息");
                appendUserName(content, joinPoint);
            } else if ("DELETE".equals(method)) {
                content.append("删除用户");
                appendUserIdFromPath(content, requestURI, "user");
            } else if ("GET".equals(method)) {
                content.append("查询用户列表");
            }
        } else if (requestURI.contains("/role")) {
            if ("POST".equals(method)) {
                content.append("新增角色");
                appendRoleName(content, joinPoint);
            } else if ("PUT".equals(method)) {
                content.append("修改角色");
                appendRoleName(content, joinPoint);
            } else if ("DELETE".equals(method)) {
                content.append("删除角色");
                appendRoleIdFromPath(content, requestURI);
            } else if ("GET".equals(method)) {
                content.append("查询角色列表");
            }
        } else if (requestURI.contains("/department")) {
            if ("POST".equals(method)) {
                content.append("新增部门");
                appendDepartmentName(content, joinPoint);
            } else if ("PUT".equals(method)) {
                content.append("修改部门");
                appendDepartmentName(content, joinPoint);
            } else if ("DELETE".equals(method)) {
                content.append("删除部门");
                appendDepartmentIdFromPath(content, requestURI);
            } else if ("GET".equals(method)) {
                content.append("查询部门列表");
            }
        } else if (requestURI.contains("/breed")) {
            if ("POST".equals(method)) {
                content.append("新增养殖品种");
                appendBreedName(content, joinPoint);
            } else if ("PUT".equals(method)) {
                content.append("修改养殖品种");
                appendBreedName(content, joinPoint);
            } else if ("DELETE".equals(method)) {
                content.append("删除养殖品种");
                appendBreedIdFromPath(content, requestURI);
            } else if ("GET".equals(method)) {
                content.append("查询养殖品种列表");
            }
        } else if (requestURI.contains("/area")) {
            if ("POST".equals(method)) {
                content.append("新增养殖区域");
                appendAreaName(content, joinPoint);
            } else if ("PUT".equals(method)) {
                content.append("修改养殖区域");
                appendAreaName(content, joinPoint);
            } else if ("DELETE".equals(method)) {
                content.append("删除养殖区域");
                appendAreaIdFromPath(content, requestURI);
            } else if ("GET".equals(method)) {
                content.append("查询养殖区域列表");
            }
        } else if (requestURI.contains("/equipment")) {
            if ("POST".equals(method)) {
                content.append("新增设备");
                appendEquipmentName(content, joinPoint);
            } else if ("PUT".equals(method)) {
                content.append("修改设备信息");
                appendEquipmentName(content, joinPoint);
            } else if ("DELETE".equals(method)) {
                content.append("删除设备");
                appendEquipmentIdFromPath(content, requestURI);
            } else if ("GET".equals(method)) {
                content.append("查询设备列表");
            }
        } else if (requestURI.contains("/plan")) {
            if ("POST".equals(method)) {
                content.append("新增养殖计划");
                appendPlanName(content, joinPoint);
            } else if ("PUT".equals(method)) {
                content.append("修改养殖计划");
                appendPlanName(content, joinPoint);
            } else if ("DELETE".equals(method)) {
                content.append("删除养殖计划");
                appendPlanIdFromPath(content, requestURI);
            } else if (requestURI.contains("/approve")) {
                content.append("审批养殖计划");
                appendPlanNameFromParams(content, joinPoint);
            } else if ("GET".equals(method)) {
                content.append("查询养殖计划列表");
            }
        } else if (requestURI.contains("/yield")) {
            if ("POST".equals(method)) {
                content.append("新增产量统计");
                appendYieldInfo(content, joinPoint);
            } else if ("PUT".equals(method)) {
                content.append("修改产量统计");
                appendYieldInfo(content, joinPoint);
            } else if ("DELETE".equals(method)) {
                content.append("删除产量统计");
                appendYieldIdFromPath(content, requestURI);
            } else if (requestURI.contains("/audit")) {
                content.append("审核产量统计");
                appendYieldInfoFromParams(content, joinPoint);
            } else if ("GET".equals(method)) {
                content.append("查询产量统计列表");
            }
        } else if (requestURI.contains("/feed")) {
            if (requestURI.contains("/purchase")) {
                if ("POST".equals(method)) {
                    content.append("新增饲料采购记录");
                } else if ("PUT".equals(method)) {
                    content.append("修改饲料采购记录");
                } else if ("DELETE".equals(method)) {
                    content.append("删除饲料采购记录");
                }
            } else if (requestURI.contains("/inventory")) {
                if ("POST".equals(method)) {
                    content.append("新增饲料库存记录");
                } else if ("PUT".equals(method)) {
                    content.append("修改饲料库存记录");
                } else if ("DELETE".equals(method)) {
                    content.append("删除饲料库存记录");
                }
            } else if (requestURI.contains("/usage")) {
                if ("POST".equals(method)) {
                    content.append("新增饲料使用记录");
                } else if ("PUT".equals(method)) {
                    content.append("修改饲料使用记录");
                } else if ("DELETE".equals(method)) {
                    content.append("删除饲料使用记录");
                }
            }
        } else if (requestURI.contains("/disease")) {
            if (requestURI.contains("/record")) {
                if ("POST".equals(method)) {
                    content.append("新增病害记录");
                } else if ("PUT".equals(method)) {
                    content.append("修改病害记录");
                } else if ("DELETE".equals(method)) {
                    content.append("删除病害记录");
                }
            } else if (requestURI.contains("/prevention")) {
                if ("POST".equals(method)) {
                    content.append("新增病害防治记录");
                } else if ("PUT".equals(method)) {
                    content.append("修改病害防治记录");
                } else if ("DELETE".equals(method)) {
                    content.append("删除病害防治记录");
                }
            } else if (requestURI.contains("/medicine")) {
                if ("POST".equals(method)) {
                    content.append("新增用药记录");
                } else if ("PUT".equals(method)) {
                    content.append("修改用药记录");
                } else if ("DELETE".equals(method)) {
                    content.append("删除用药记录");
                }
            }
        } else if (requestURI.contains("/production")) {
            if (requestURI.contains("/feeding")) {
                if ("POST".equals(method)) {
                    content.append("新增投喂记录");
                } else if ("PUT".equals(method)) {
                    content.append("修改投喂记录");
                } else if ("DELETE".equals(method)) {
                    content.append("删除投喂记录");
                }
            } else if (requestURI.contains("/inspection")) {
                if ("POST".equals(method)) {
                    content.append("新增日常检查记录");
                } else if ("PUT".equals(method)) {
                    content.append("修改日常检查记录");
                } else if ("DELETE".equals(method)) {
                    content.append("删除日常检查记录");
                }
            }
        } else if (requestURI.contains("/sales")) {
            if (requestURI.contains("/customer")) {
                if ("POST".equals(method)) {
                    content.append("新增客户信息");
                } else if ("PUT".equals(method)) {
                    content.append("修改客户信息");
                } else if ("DELETE".equals(method)) {
                    content.append("删除客户信息");
                }
            } else if (requestURI.contains("/record")) {
                if ("POST".equals(method)) {
                    content.append("新增销售记录");
                } else if ("PUT".equals(method)) {
                    content.append("修改销售记录");
                } else if ("DELETE".equals(method)) {
                    content.append("删除销售记录");
                }
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
        
        return content.toString();
    }
    
    /**
     * 从路径中提取用户ID并转换为用户名
     */
    private void appendUserIdFromPath(StringBuilder content, String requestURI, String prefix) {
        try {
            Pattern pattern = Pattern.compile("/" + prefix + "/(\\d+)");
            Matcher matcher = pattern.matcher(requestURI);
            if (matcher.find()) {
                Long userId = Long.parseLong(matcher.group(1));
                SysUser user = userService.getById(userId);
                if (user != null) {
                    content.append(": ").append(user.getRealName() != null ? user.getRealName() : user.getUsername());
                } else {
                    content.append(": ").append(userId);
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从实体对象中提取用户名
     */
    private void appendUserName(StringBuilder content, ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof SysUser) {
                SysUser user = (SysUser) args[0];
                if (user.getUserId() != null) {
                    // 如果是更新操作，从数据库获取完整信息
                    SysUser dbUser = userService.getById(user.getUserId());
                    if (dbUser != null) {
                        content.append(": ").append(dbUser.getRealName() != null ? dbUser.getRealName() : dbUser.getUsername());
                    } else if (user.getUsername() != null) {
                        content.append(": ").append(user.getUsername());
                    }
                } else if (user.getUsername() != null) {
                    content.append(": ").append(user.getUsername());
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从路径中提取角色ID并转换为角色名
     */
    private void appendRoleIdFromPath(StringBuilder content, String requestURI) {
        try {
            Pattern pattern = Pattern.compile("/role/(\\d+)");
            Matcher matcher = pattern.matcher(requestURI);
            if (matcher.find()) {
                Long roleId = Long.parseLong(matcher.group(1));
                SysRole role = roleService.getById(roleId);
                if (role != null) {
                    content.append(": ").append(role.getRoleName());
                } else {
                    content.append(": ").append(roleId);
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从实体对象中提取角色名
     */
    private void appendRoleName(StringBuilder content, ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof SysRole) {
                SysRole role = (SysRole) args[0];
                if (role.getRoleName() != null) {
                    content.append(": ").append(role.getRoleName());
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从路径中提取区域ID并转换为区域名
     */
    private void appendAreaIdFromPath(StringBuilder content, String requestURI) {
        try {
            Pattern pattern = Pattern.compile("/area/(\\d+)");
            Matcher matcher = pattern.matcher(requestURI);
            if (matcher.find()) {
                Long areaId = Long.parseLong(matcher.group(1));
                BaseArea area = areaService.getById(areaId);
                if (area != null) {
                    content.append(": ").append(area.getAreaName());
                } else {
                    content.append(": ").append(areaId);
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从实体对象中提取区域名
     */
    private void appendAreaName(StringBuilder content, ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof BaseArea) {
                BaseArea area = (BaseArea) args[0];
                if (area.getAreaId() != null && area.getAreaName() == null) {
                    BaseArea dbArea = areaService.getById(area.getAreaId());
                    if (dbArea != null) {
                        content.append(": ").append(dbArea.getAreaName());
                    }
                } else if (area.getAreaName() != null) {
                    content.append(": ").append(area.getAreaName());
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从路径中提取品种ID并转换为品种名
     */
    private void appendBreedIdFromPath(StringBuilder content, String requestURI) {
        try {
            Pattern pattern = Pattern.compile("/breed/(\\d+)");
            Matcher matcher = pattern.matcher(requestURI);
            if (matcher.find()) {
                Long breedId = Long.parseLong(matcher.group(1));
                BaseBreed breed = breedService.getById(breedId);
                if (breed != null) {
                    content.append(": ").append(breed.getBreedName());
                } else {
                    content.append(": ").append(breedId);
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从实体对象中提取品种名
     */
    private void appendBreedName(StringBuilder content, ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof BaseBreed) {
                BaseBreed breed = (BaseBreed) args[0];
                if (breed.getBreedId() != null && breed.getBreedName() == null) {
                    BaseBreed dbBreed = breedService.getById(breed.getBreedId());
                    if (dbBreed != null) {
                        content.append(": ").append(dbBreed.getBreedName());
                    }
                } else if (breed.getBreedName() != null) {
                    content.append(": ").append(breed.getBreedName());
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从路径中提取设备ID并转换为设备名
     */
    private void appendEquipmentIdFromPath(StringBuilder content, String requestURI) {
        try {
            Pattern pattern = Pattern.compile("/equipment/(\\d+)");
            Matcher matcher = pattern.matcher(requestURI);
            if (matcher.find()) {
                Long equipmentId = Long.parseLong(matcher.group(1));
                BaseEquipment equipment = equipmentService.getById(equipmentId);
                if (equipment != null) {
                    content.append(": ").append(equipment.getEquipmentName());
                } else {
                    content.append(": ").append(equipmentId);
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从实体对象中提取设备名
     */
    private void appendEquipmentName(StringBuilder content, ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof BaseEquipment) {
                BaseEquipment equipment = (BaseEquipment) args[0];
                if (equipment.getEquipmentId() != null && equipment.getEquipmentName() == null) {
                    BaseEquipment dbEquipment = equipmentService.getById(equipment.getEquipmentId());
                    if (dbEquipment != null) {
                        content.append(": ").append(dbEquipment.getEquipmentName());
                    }
                } else if (equipment.getEquipmentName() != null) {
                    content.append(": ").append(equipment.getEquipmentName());
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从路径中提取计划ID并转换为计划名
     */
    private void appendPlanIdFromPath(StringBuilder content, String requestURI) {
        try {
            Pattern pattern = Pattern.compile("/plan/(\\d+)");
            Matcher matcher = pattern.matcher(requestURI);
            if (matcher.find()) {
                Long planId = Long.parseLong(matcher.group(1));
                AquaculturePlan plan = planService.getById(planId);
                if (plan != null) {
                    content.append(": ").append(plan.getPlanName());
                } else {
                    content.append(": ").append(planId);
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从实体对象中提取计划名
     */
    private void appendPlanName(StringBuilder content, ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof AquaculturePlan) {
                AquaculturePlan plan = (AquaculturePlan) args[0];
                if (plan.getPlanId() != null && plan.getPlanName() == null) {
                    AquaculturePlan dbPlan = planService.getById(plan.getPlanId());
                    if (dbPlan != null) {
                        content.append(": ").append(dbPlan.getPlanName());
                    }
                } else if (plan.getPlanName() != null) {
                    content.append(": ").append(plan.getPlanName());
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从参数中提取计划名（用于审批操作）
     */
    private void appendPlanNameFromParams(StringBuilder content, ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> params = (Map<String, Object>) args[0];
                Object planIdObj = params.get("planId");
                if (planIdObj != null) {
                    Long planId = Long.parseLong(planIdObj.toString());
                    AquaculturePlan plan = planService.getById(planId);
                    if (plan != null) {
                        content.append(": ").append(plan.getPlanName());
                    }
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从路径中提取产量统计ID并转换为信息
     */
    private void appendYieldIdFromPath(StringBuilder content, String requestURI) {
        try {
            Pattern pattern = Pattern.compile("/yield/(\\d+)");
            Matcher matcher = pattern.matcher(requestURI);
            if (matcher.find()) {
                Long yieldId = Long.parseLong(matcher.group(1));
                YieldStatistics yield = yieldStatisticsService.getById(yieldId);
                if (yield != null) {
                    // 尝试获取区域和品种名称
                    String areaName = yield.getAreaId() != null ? getAreaNameById(yield.getAreaId()) : null;
                    String breedName = yield.getBreedId() != null ? getBreedNameById(yield.getBreedId()) : null;
                    if (areaName != null || breedName != null) {
                        content.append(": ").append(areaName != null ? areaName : "").append(breedName != null ? " " + breedName : "");
                    } else {
                        content.append(": ").append(yieldId);
                    }
                } else {
                    content.append(": ").append(yieldId);
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从实体对象中提取产量统计信息
     */
    private void appendYieldInfo(StringBuilder content, ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof YieldStatistics) {
                YieldStatistics yield = (YieldStatistics) args[0];
                String areaName = yield.getAreaId() != null ? getAreaNameById(yield.getAreaId()) : null;
                String breedName = yield.getBreedId() != null ? getBreedNameById(yield.getBreedId()) : null;
                if (areaName != null || breedName != null) {
                    content.append(": ").append(areaName != null ? areaName : "").append(breedName != null ? " " + breedName : "");
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从参数中提取产量统计信息（用于审核操作）
     */
    private void appendYieldInfoFromParams(StringBuilder content, ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> params = (Map<String, Object>) args[0];
                Object yieldIdObj = params.get("yieldId");
                if (yieldIdObj != null) {
                    Long yieldId = Long.parseLong(yieldIdObj.toString());
                    YieldStatistics yield = yieldStatisticsService.getById(yieldId);
                    if (yield != null) {
                        String areaName = yield.getAreaId() != null ? getAreaNameById(yield.getAreaId()) : null;
                        String breedName = yield.getBreedId() != null ? getBreedNameById(yield.getBreedId()) : null;
                        if (areaName != null || breedName != null) {
                            content.append(": ").append(areaName != null ? areaName : "").append(breedName != null ? " " + breedName : "");
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 根据区域ID获取区域名称
     */
    private String getAreaNameById(Long areaId) {
        try {
            BaseArea area = areaService.getById(areaId);
            return area != null ? area.getAreaName() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 根据品种ID获取品种名称
     */
    private String getBreedNameById(Long breedId) {
        try {
            BaseBreed breed = breedService.getById(breedId);
            return breed != null ? breed.getBreedName() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 从路径中提取部门ID并转换为部门名
     */
    private void appendDepartmentIdFromPath(StringBuilder content, String requestURI) {
        try {
            Pattern pattern = Pattern.compile("/department/(\\d+)");
            Matcher matcher = pattern.matcher(requestURI);
            if (matcher.find()) {
                Long departmentId = Long.parseLong(matcher.group(1));
                BaseDepartment department = departmentService.getById(departmentId);
                if (department != null) {
                    content.append(": ").append(department.getDepartmentName());
                } else {
                    content.append(": ").append(departmentId);
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 从实体对象中提取部门名
     */
    private void appendDepartmentName(StringBuilder content, ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof BaseDepartment) {
                BaseDepartment department = (BaseDepartment) args[0];
                if (department.getDepartmentId() != null && department.getDepartmentName() == null) {
                    BaseDepartment dbDepartment = departmentService.getById(department.getDepartmentId());
                    if (dbDepartment != null) {
                        content.append(": ").append(dbDepartment.getDepartmentName());
                    }
                } else if (department.getDepartmentName() != null) {
                    content.append(": ").append(department.getDepartmentName());
                }
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }
}

