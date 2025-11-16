package com.server.aquacultureserver.controller;

import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.AquaculturePlan;
import com.server.aquacultureserver.domain.SysMessage;
import com.server.aquacultureserver.domain.SysOperLog;
import com.server.aquacultureserver.service.SysUserService;
import com.server.aquacultureserver.service.AquaculturePlanService;
import com.server.aquacultureserver.service.YieldStatisticsService;
import com.server.aquacultureserver.service.BaseAreaService;
import com.server.aquacultureserver.service.BaseBreedService;
import com.server.aquacultureserver.service.FeedInventoryService;
import com.server.aquacultureserver.service.SalesRecordService;
import com.server.aquacultureserver.service.SysMessageService;
import com.server.aquacultureserver.service.SysOperLogService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {
    
    @Autowired
    private SysUserService userService;
    
    @Autowired
    private AquaculturePlanService planService;
    
    @Autowired
    private YieldStatisticsService yieldStatisticsService;
    
    @Autowired
    private BaseAreaService areaService;
    
    @Autowired
    private BaseBreedService breedService;
    
    @Autowired
    private FeedInventoryService feedInventoryService;
    
    @Autowired
    private SalesRecordService salesRecordService;
    
    @Autowired
    private SysMessageService messageService;
    
    @Autowired
    private SysOperLogService operLogService;
    
    /**
     * 获取统计数据（根据角色返回不同数据）
     * 方案四：完全角色定制化仪表盘
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        boolean isAdmin = UserContext.isAdmin();
        boolean isDepartmentManager = UserContext.isDepartmentManager();
        boolean isOperator = UserContext.isOperator();
        boolean isDecisionMaker = UserContext.isDecisionMaker();
        
        if (isAdmin) {
            // 系统管理员：全系统统计
            statistics = getAdminStatistics();
        } else if (isDepartmentManager) {
            // 部门管理员：部门统计
            statistics = getDepartmentManagerStatistics();
        } else if (isOperator) {
            // 普通操作员：我的数据
            statistics = getOperatorStatistics();
        } else if (isDecisionMaker) {
            // 决策层：汇总报表
            statistics = getDecisionMakerStatistics();
        } else {
            // 默认返回基础信息
            statistics = getBasicStatistics();
        }
        
        // 未读消息数（所有角色都有）
        try {
            Long currentUserId = UserContext.getCurrentUserId();
            if (currentUserId != null) {
                long unreadCount = messageService.getUnreadCount(currentUserId);
                statistics.put("unreadMessageCount", unreadCount);
            } else {
                statistics.put("unreadMessageCount", 0);
            }
        } catch (Exception e) {
            statistics.put("unreadMessageCount", 0);
        }
        
        return Result.success(statistics);
    }
    
    /**
     * 系统管理员统计数据：全系统统计、用户管理、系统监控
     */
    private Map<String, Object> getAdminStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 全系统统计
        try {
            stats.put("planCount", planService.count());
            stats.put("yieldCount", yieldStatisticsService.count());
            stats.put("userCount", userService.count());
            stats.put("areaCount", areaService.getAllAreas().size());
            stats.put("breedCount", breedService.getAllBreeds().size());
            stats.put("feedInventoryCount", feedInventoryService.count());
            stats.put("salesCount", salesRecordService.count());
            stats.put("pendingPlanCount", planService.countPendingPlans());
            stats.put("ongoingPlanCount", planService.getPage(1, 1, null, null, null, 3).getTotal());
            stats.put("completedPlanCount", planService.getPage(1, 1, null, null, null, 4).getTotal());
        } catch (Exception e) {
            // 设置默认值
            stats.put("planCount", 0);
            stats.put("yieldCount", 0);
            stats.put("userCount", 0);
            stats.put("areaCount", 0);
            stats.put("breedCount", 0);
            stats.put("feedInventoryCount", 0);
            stats.put("salesCount", 0);
            stats.put("pendingPlanCount", 0);
            stats.put("ongoingPlanCount", 0);
            stats.put("completedPlanCount", 0);
        }
        
        return stats;
    }
    
    /**
     * 部门管理员统计数据：部门统计、部门计划、部门人员
     */
    private Map<String, Object> getDepartmentManagerStatistics() {
        Map<String, Object> stats = new HashMap<>();
        java.util.List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
        
        try {
            // 部门区域数
            stats.put("areaCount", areaIds != null ? areaIds.size() : 0);
            
            // 部门计划数（通过区域过滤，简化计算）
            if (areaIds != null && !areaIds.isEmpty()) {
                // 使用getAllPlans方法会应用权限过滤，这里需要统计部门计划数
                try {
                    java.util.List<AquaculturePlan> allPlans = planService.getAllPlans();
                    long deptPlanCount = allPlans.stream()
                        .filter(p -> areaIds.contains(p.getAreaId()))
                        .count();
                    stats.put("planCount", deptPlanCount);
                } catch (Exception e) {
                    stats.put("planCount", 0);
                }
            } else {
                stats.put("planCount", 0);
            }
            
            // 部门产量统计数
            stats.put("yieldCount", yieldStatisticsService.count());
            
            // 部门进行中计划
            stats.put("ongoingPlanCount", planService.getPage(1, 1, null, null, null, 3).getTotal());
            
            // 部门已完成计划
            stats.put("completedPlanCount", planService.getPage(1, 1, null, null, null, 4).getTotal());
            
            // 部门待审批计划
            stats.put("pendingPlanCount", planService.countPendingPlans());
            
            // 部门人员数（通过区域查询用户，使用UserContext的方法）
            try {
                java.util.List<Long> userIds = UserContext.getDepartmentManagerUserIds();
                stats.put("userCount", userIds != null ? userIds.size() : 0);
            } catch (Exception e) {
                stats.put("userCount", 0);
            }
            
            stats.put("breedCount", breedService.getAllBreeds().size());
            stats.put("feedInventoryCount", feedInventoryService.count());
            stats.put("salesCount", salesRecordService.count());
        } catch (Exception e) {
            stats.put("planCount", 0);
            stats.put("yieldCount", 0);
            stats.put("areaCount", 0);
            stats.put("userCount", 0);
            stats.put("breedCount", 0);
            stats.put("feedInventoryCount", 0);
            stats.put("salesCount", 0);
            stats.put("pendingPlanCount", 0);
            stats.put("ongoingPlanCount", 0);
            stats.put("completedPlanCount", 0);
        }
        
        return stats;
    }
    
    /**
     * 普通操作员统计数据：我的计划、我的任务、我的消息
     */
    private Map<String, Object> getOperatorStatistics() {
        Map<String, Object> stats = new HashMap<>();
        Long areaId = UserContext.getCurrentUserAreaId();
        
        try {
            // 我的计划数（我的区域）
            if (areaId != null) {
                stats.put("planCount", planService.getPage(1, 1, null, areaId, null, null).getTotal());
                stats.put("ongoingPlanCount", planService.getPage(1, 1, null, areaId, null, 3).getTotal());
                stats.put("completedPlanCount", planService.getPage(1, 1, null, areaId, null, 4).getTotal());
            } else {
                stats.put("planCount", 0);
                stats.put("ongoingPlanCount", 0);
                stats.put("completedPlanCount", 0);
            }
            
            // 我的产量统计
            stats.put("yieldCount", yieldStatisticsService.count());
            
            // 我的待办任务（待审批计划）
            stats.put("pendingPlanCount", planService.countPendingPlans());
            
            stats.put("areaCount", areaId != null ? 1 : 0);
            stats.put("userCount", 0); // 操作员不显示用户数
            stats.put("breedCount", 0);
            stats.put("feedInventoryCount", 0);
            stats.put("salesCount", 0);
        } catch (Exception e) {
            stats.put("planCount", 0);
            stats.put("yieldCount", 0);
            stats.put("areaCount", 0);
            stats.put("pendingPlanCount", 0);
            stats.put("ongoingPlanCount", 0);
            stats.put("completedPlanCount", 0);
        }
        
        return stats;
    }
    
    /**
     * 决策层统计数据：汇总报表、趋势分析、审批事项
     */
    private Map<String, Object> getDecisionMakerStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 汇总报表数据
            stats.put("planCount", planService.count());
            stats.put("yieldCount", yieldStatisticsService.count());
            stats.put("salesCount", salesRecordService.count());
            stats.put("areaCount", areaService.getAllAreas().size());
            
            // 审批事项
            stats.put("pendingPlanCount", planService.countPendingPlans());
            
            // 趋势分析数据
            stats.put("ongoingPlanCount", planService.getPage(1, 1, null, null, null, 3).getTotal());
            stats.put("completedPlanCount", planService.getPage(1, 1, null, null, null, 4).getTotal());
            
            // 不显示敏感信息
            stats.put("userCount", 0);
            stats.put("breedCount", 0);
            stats.put("feedInventoryCount", 0);
        } catch (Exception e) {
            stats.put("planCount", 0);
            stats.put("yieldCount", 0);
            stats.put("salesCount", 0);
            stats.put("areaCount", 0);
            stats.put("pendingPlanCount", 0);
            stats.put("ongoingPlanCount", 0);
            stats.put("completedPlanCount", 0);
        }
        
        return stats;
    }
    
    /**
     * 基础统计数据（默认）
     */
    private Map<String, Object> getBasicStatistics() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("planCount", planService.count());
            stats.put("yieldCount", yieldStatisticsService.count());
            stats.put("areaCount", areaService.getAllAreas().size());
            stats.put("pendingPlanCount", planService.countPendingPlans());
        } catch (Exception e) {
            stats.put("planCount", 0);
            stats.put("yieldCount", 0);
            stats.put("areaCount", 0);
            stats.put("pendingPlanCount", 0);
        }
        return stats;
    }
    
    /**
     * 获取最近的计划列表
     */
    @GetMapping("/recent/plans")
    public Result<List<AquaculturePlan>> getRecentPlans(@RequestParam(defaultValue = "5") Integer size) {
        try {
            // 获取最近的计划，按创建时间倒序
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<AquaculturePlan> page = 
                planService.getPage(1, size, null, null, null, null);
            return Result.success(page.getRecords());
        } catch (Exception e) {
            return Result.error("获取最近计划失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取最近的操作日志
     */
    @GetMapping("/recent/logs")
    public Result<List<SysOperLog>> getRecentLogs(@RequestParam(defaultValue = "10") Integer size) {
        try {
            Long currentUserId = UserContext.getCurrentUserId();
            // 获取当前用户最近的操作日志
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysOperLog> page = 
                operLogService.getPage(1, size, currentUserId, null, null, null, null, null);
            return Result.success(page.getRecords());
        } catch (Exception e) {
            return Result.error("获取最近操作日志失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取未读消息列表
     */
    @GetMapping("/recent/messages")
    public Result<List<SysMessage>> getRecentMessages(@RequestParam(defaultValue = "5") Integer size) {
        try {
            Long currentUserId = UserContext.getCurrentUserId();
            if (currentUserId == null) {
                return Result.error("用户未登录");
            }
            // 获取未读消息
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysMessage> page = 
                messageService.getUnreadMessages(currentUserId, 1, size);
            return Result.success(page.getRecords());
        } catch (Exception e) {
            return Result.error("获取未读消息失败：" + e.getMessage());
        }
    }
}

