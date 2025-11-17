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
    
    /**
     * 查询所有计划调整记录
     * @return 计划调整记录列表（按创建时间倒序）
     */
    @Override
    public List<PlanAdjust> getAllAdjusts() {
        return adjustMapper.selectList(
            new LambdaQueryWrapper<PlanAdjust>()
                .orderByDesc(PlanAdjust::getCreateTime)
        );
    }
    
    /**
     * 根据ID查询计划调整记录
     * @param adjustId 调整记录ID
     * @return 计划调整记录
     */
    @Override
    public PlanAdjust getById(Long adjustId) {
        return adjustMapper.selectById(adjustId);
    }
    
    /**
     * 根据养殖计划ID查询计划调整记录
     * @param planId 养殖计划ID
     * @return 计划调整记录列表（按创建时间倒序）
     */
    @Override
    public List<PlanAdjust> getByPlanId(Long planId) {
        return adjustMapper.selectList(
            new LambdaQueryWrapper<PlanAdjust>()
                .eq(PlanAdjust::getPlanId, planId)
                .orderByDesc(PlanAdjust::getCreateTime)
        );
    }
    
    /**
     * 分页查询计划调整记录
     * @param current 当前页码
     * @param size 每页大小
     * @param planId 养殖计划ID（精确查询）
     * @param approveStatus 审批状态（0-待审批，1-已通过，2-已驳回）
     * @return 分页结果
     */
    @Override
    public Page<PlanAdjust> getPage(Integer current, Integer size, Long planId, Integer approveStatus) {
        Page<PlanAdjust> page = new Page<>(current, size);
        LambdaQueryWrapper<PlanAdjust> wrapper = new LambdaQueryWrapper<>();
        
        // 养殖计划ID精确查询
        if (planId != null) {
            wrapper.eq(PlanAdjust::getPlanId, planId);
        }
        // 审批状态精确查询
        if (approveStatus != null) {
            wrapper.eq(PlanAdjust::getApproveStatus, approveStatus);
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(PlanAdjust::getCreateTime);
        return adjustMapper.selectPage(page, wrapper);
    }
    
    /**
     * 新增计划调整记录
     * 新调整申请默认状态为待审批（0）
     * @param adjust 计划调整记录
     * @return 是否成功
     */
    @Override
    public boolean saveAdjust(PlanAdjust adjust) {
        // 新调整申请默认状态为待审批（0）
        if (adjust.getApproveStatus() == null) {
            adjust.setApproveStatus(0);
        }
        return adjustMapper.insert(adjust) > 0;
    }
    
    /**
     * 更新计划调整记录
     * @param adjust 计划调整记录
     * @return 是否成功
     */
    @Override
    public boolean updateAdjust(PlanAdjust adjust) {
        return adjustMapper.updateById(adjust) > 0;
    }
    
    /**
     * 审批计划调整记录
     * @param adjustId 调整记录ID
     * @param approverId 审批人ID
     * @param approveOpinion 审批意见
     * @param approveStatus 审批状态（1-已通过，2-已驳回）
     * @return 是否成功
     */
    @Override
    public boolean approveAdjust(Long adjustId, Long approverId, String approveOpinion, Integer approveStatus) {
        PlanAdjust adjust = getById(adjustId);
        if (adjust == null) {
            throw new RuntimeException("调整记录不存在");
        }
        
        // 设置审批信息
        adjust.setApproverId(approverId);
        adjust.setApproveOpinion(approveOpinion);
        adjust.setApproveTime(LocalDateTime.now());
        adjust.setApproveStatus(approveStatus);
        
        return adjustMapper.updateById(adjust) > 0;
    }
}

