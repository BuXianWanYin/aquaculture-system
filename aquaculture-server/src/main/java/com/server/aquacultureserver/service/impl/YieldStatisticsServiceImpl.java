package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.YieldStatistics;
import com.server.aquacultureserver.mapper.YieldStatisticsMapper;
import com.server.aquacultureserver.service.AquaculturePlanService;
import com.server.aquacultureserver.service.YieldStatisticsService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 产量统计服务实现类
 */
@Service
public class YieldStatisticsServiceImpl implements YieldStatisticsService {
    
    @Autowired
    private YieldStatisticsMapper statisticsMapper;
    
    @Autowired
    private AquaculturePlanService planService;
    
    @Override
    public List<YieldStatistics> getAllStatistics() {
        LambdaQueryWrapper<YieldStatistics> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的产量
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(YieldStatistics::getAreaId, areaId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员只能查看其部门下所有区域的产量
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(YieldStatistics::getAreaId, areaIds);
            } else {
                return Collections.emptyList();
            }
        }
        
        wrapper.orderByDesc(YieldStatistics::getCreateTime);
        return statisticsMapper.selectList(wrapper);
    }
    
    @Override
    public YieldStatistics getById(Long yieldId) {
        return statisticsMapper.selectById(yieldId);
    }
    
    @Override
    public Page<YieldStatistics> getPage(Integer current, Integer size, Long planId, Long areaId, Long breedId, Integer status) {
        Page<YieldStatistics> page = new Page<>(current, size);
        LambdaQueryWrapper<YieldStatistics> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的产量
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(YieldStatistics::getAreaId, userAreaId);
            } else {
                return page;
            }
        }
        // 部门管理员只能查看其部门下所有区域的产量
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(YieldStatistics::getAreaId, areaIds);
            } else {
                return page;
            }
        }
        
        if (planId != null) {
            wrapper.eq(YieldStatistics::getPlanId, planId);
        }
        if (areaId != null) {
            wrapper.eq(YieldStatistics::getAreaId, areaId);
        }
        if (breedId != null) {
            wrapper.eq(YieldStatistics::getBreedId, breedId);
        }
        if (status != null) {
            wrapper.eq(YieldStatistics::getStatus, status);
        }
        
        wrapper.orderByDesc(YieldStatistics::getCreateTime);
        return statisticsMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean saveStatistics(YieldStatistics statistics) {
        // 普通操作员只能创建自己区域的产量
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null) {
                throw new RuntimeException("您尚未分配区域，无法创建产量记录");
            }
            statistics.setAreaId(userAreaId);
        }
        // 部门管理员只能创建其部门下区域的产量
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty()) {
                throw new RuntimeException("您尚未分配部门，无法创建产量记录");
            }
            if (statistics.getAreaId() == null || !areaIds.contains(statistics.getAreaId())) {
                throw new RuntimeException("您只能创建本部门下区域的产量记录");
            }
        }
        
        // 新记录默认状态为待审核
        if (statistics.getStatus() == null) {
            statistics.setStatus(0);
        }
        
        // 设置创建时间和创建人
        if (statistics.getCreateTime() == null) {
            statistics.setCreateTime(LocalDateTime.now());
        }
        if (statistics.getCreatorId() == null) {
            statistics.setCreatorId(UserContext.getCurrentUserId());
        }
        
        return statisticsMapper.insert(statistics) > 0;
    }
    
    @Override
    public boolean updateStatistics(YieldStatistics statistics) {
        // 普通操作员不能编辑产量统计
        if (UserContext.isOperator()) {
            throw new RuntimeException("普通操作员无权编辑产量统计");
        }
        // 部门管理员只能编辑其部门下区域的产量统计
        if (UserContext.isDepartmentManager()) {
            YieldStatistics existing = getById(statistics.getYieldId());
            if (existing == null) {
                throw new RuntimeException("产量统计记录不存在");
            }
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || !areaIds.contains(existing.getAreaId())) {
                throw new RuntimeException("您只能编辑本部门下区域的产量统计");
            }
            // 不允许修改到其他部门
            if (statistics.getAreaId() != null && !areaIds.contains(statistics.getAreaId())) {
                throw new RuntimeException("您只能将产量统计修改为本部门下的区域");
            }
        }
        return statisticsMapper.updateById(statistics) > 0;
    }
    
    @Override
    public boolean deleteStatistics(Long yieldId) {
        // 普通操作员不能删除产量统计
        if (UserContext.isOperator()) {
            throw new RuntimeException("普通操作员无权删除产量统计");
        }
        // 部门管理员只能删除其部门下区域的产量统计
        if (UserContext.isDepartmentManager()) {
            YieldStatistics statistics = getById(yieldId);
            if (statistics == null) {
                throw new RuntimeException("产量统计记录不存在");
            }
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || !areaIds.contains(statistics.getAreaId())) {
                throw new RuntimeException("您只能删除本部门下区域的产量统计");
            }
        }
        return statisticsMapper.deleteById(yieldId) > 0;
    }
    
    @Override
    public boolean auditStatistics(Long yieldId, Long auditorId, String auditOpinion, Integer status) {
        YieldStatistics statistics = getById(yieldId);
        if (statistics == null) {
            throw new RuntimeException("产量统计记录不存在");
        }
        
        statistics.setAuditorId(auditorId);
        statistics.setAuditOpinion(auditOpinion);
        statistics.setAuditTime(LocalDateTime.now());
        statistics.setStatus(status);
        
        boolean result = statisticsMapper.updateById(statistics) > 0;
        
        // 如果审核通过（status=1）且有关联的计划，检查并更新计划状态
        if (result && status == 1 && statistics.getPlanId() != null) {
            try {
                planService.checkAndUpdatePlanStatus(statistics.getPlanId());
            } catch (Exception e) {
                // 计划状态更新失败不影响产量审核结果
                System.err.println("更新计划状态失败: " + e.getMessage());
            }
        }
        
        return result;
    }
    
    @Override
    public long count() {
        return statisticsMapper.selectCount(null);
    }
}

