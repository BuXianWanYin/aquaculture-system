package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.PlanAdjust;
import com.server.aquacultureserver.mapper.PlanAdjustMapper;
import com.server.aquacultureserver.service.PlanAdjustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 计划调整服务实现类
 */
@Service
public class PlanAdjustServiceImpl implements PlanAdjustService {
    
    @Autowired
    private PlanAdjustMapper adjustMapper;
    
    @Override
    public List<PlanAdjust> getAllAdjusts() {
        return adjustMapper.selectList(
            new LambdaQueryWrapper<PlanAdjust>()
                .orderByDesc(PlanAdjust::getCreateTime)
        );
    }
    
    @Override
    public PlanAdjust getById(Long adjustId) {
        return adjustMapper.selectById(adjustId);
    }
    
    @Override
    public List<PlanAdjust> getByPlanId(Long planId) {
        return adjustMapper.selectList(
            new LambdaQueryWrapper<PlanAdjust>()
                .eq(PlanAdjust::getPlanId, planId)
                .orderByDesc(PlanAdjust::getCreateTime)
        );
    }
    
    @Override
    public Page<PlanAdjust> getPage(Integer current, Integer size, Long planId, Integer approveStatus) {
        Page<PlanAdjust> page = new Page<>(current, size);
        LambdaQueryWrapper<PlanAdjust> wrapper = new LambdaQueryWrapper<>();
        
        if (planId != null) {
            wrapper.eq(PlanAdjust::getPlanId, planId);
        }
        if (approveStatus != null) {
            wrapper.eq(PlanAdjust::getApproveStatus, approveStatus);
        }
        
        wrapper.orderByDesc(PlanAdjust::getCreateTime);
        return adjustMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean saveAdjust(PlanAdjust adjust) {
        // 新调整申请默认状态为待审批
        if (adjust.getApproveStatus() == null) {
            adjust.setApproveStatus(0);
        }
        return adjustMapper.insert(adjust) > 0;
    }
    
    @Override
    public boolean updateAdjust(PlanAdjust adjust) {
        return adjustMapper.updateById(adjust) > 0;
    }
    
    @Override
    public boolean approveAdjust(Long adjustId, Long approverId, String approveOpinion, Integer approveStatus) {
        PlanAdjust adjust = getById(adjustId);
        if (adjust == null) {
            throw new RuntimeException("调整记录不存在");
        }
        
        adjust.setApproverId(approverId);
        adjust.setApproveOpinion(approveOpinion);
        adjust.setApproveTime(LocalDateTime.now());
        adjust.setApproveStatus(approveStatus);
        
        return adjustMapper.updateById(adjust) > 0;
    }
}

