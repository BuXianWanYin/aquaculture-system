package com.server.aquacultureserver.controller;

import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.service.SysUserService;
import com.server.aquacultureserver.service.AquaculturePlanService;
import com.server.aquacultureserver.service.YieldStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页仪表盘控制器
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
    
    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 用户总数
        long userCount = userService.count();
        statistics.put("userCount", userCount);
        
        // 计划总数
        long planCount = planService.count();
        statistics.put("planCount", planCount);
        
        // 产量统计总数
        long yieldCount = yieldStatisticsService.count();
        statistics.put("yieldCount", yieldCount);
        
        // 待审批计划数
        long pendingPlanCount = planService.countPendingPlans();
        statistics.put("pendingPlanCount", pendingPlanCount);
        
        return Result.success(statistics);
    }
}

