package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.YieldStatistics;
import com.server.aquacultureserver.mapper.YieldStatisticsMapper;
import com.server.aquacultureserver.service.YieldStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 产量统计服务实现类
 */
@Service
public class YieldStatisticsServiceImpl implements YieldStatisticsService {
    
    @Autowired
    private YieldStatisticsMapper statisticsMapper;
    
    @Override
    public List<YieldStatistics> getAllStatistics() {
        return statisticsMapper.selectList(
            new LambdaQueryWrapper<YieldStatistics>()
                .orderByDesc(YieldStatistics::getCreateTime)
        );
    }
    
    @Override
    public YieldStatistics getById(Long yieldId) {
        return statisticsMapper.selectById(yieldId);
    }
    
    @Override
    public Page<YieldStatistics> getPage(Integer current, Integer size, Long planId, Long areaId, Long breedId, Integer status) {
        Page<YieldStatistics> page = new Page<>(current, size);
        LambdaQueryWrapper<YieldStatistics> wrapper = new LambdaQueryWrapper<>();
        
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
        // 新记录默认状态为待审核
        if (statistics.getStatus() == null) {
            statistics.setStatus(0);
        }
        return statisticsMapper.insert(statistics) > 0;
    }
    
    @Override
    public boolean updateStatistics(YieldStatistics statistics) {
        return statisticsMapper.updateById(statistics) > 0;
    }
    
    @Override
    public boolean deleteStatistics(Long yieldId) {
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
        
        return statisticsMapper.updateById(statistics) > 0;
    }
    
    @Override
    public long count() {
        return statisticsMapper.selectCount(null);
    }
}

