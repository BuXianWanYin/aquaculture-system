package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.FeedingRecord;
import com.server.aquacultureserver.mapper.FeedingRecordMapper;
import com.server.aquacultureserver.service.FeedingRecordService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 投喂记录服务实现类
 */
@Service
public class FeedingRecordServiceImpl implements FeedingRecordService {
    
    @Autowired
    private FeedingRecordMapper recordMapper;
    
    /**
     * 查询所有有效的投喂记录
     * 根据用户角色进行权限过滤
     * @return 投喂记录列表
     */
    @Override
    public List<FeedingRecord> getAllRecords() {
        LambdaQueryWrapper<FeedingRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的投喂记录
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(FeedingRecord::getAreaId, areaId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员只能查看其部门下所有区域的投喂记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(FeedingRecord::getAreaId, areaIds);
            } else {
                return Collections.emptyList();
            }
        }
        
        wrapper.eq(FeedingRecord::getStatus, 1);
        wrapper.orderByDesc(FeedingRecord::getCreateTime);
        return recordMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID查询投喂记录
     * @param recordId 投喂记录ID
     * @return 投喂记录
     */
    @Override
    public FeedingRecord getById(Long recordId) {
        return recordMapper.selectById(recordId);
    }
    
    /**
     * 分页查询投喂记录
     * 根据用户角色进行权限过滤
     * @param current 当前页码
     * @param size 每页大小
     * @param planId 养殖计划ID
     * @param areaId 区域ID
     * @param feedName 饲料名称（模糊查询）
     * @param status 状态（1-正常，0-已删除）
     * @return 分页结果
     */
    @Override
    public Page<FeedingRecord> getPage(Integer current, Integer size, Long planId, Long areaId, String feedName, Integer status) {
        Page<FeedingRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<FeedingRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的投喂记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(FeedingRecord::getAreaId, userAreaId);
            } else {
                return page;
            }
        }
        // 部门管理员只能查看其部门下所有区域的投喂记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(FeedingRecord::getAreaId, areaIds);
            } else {
                return page;
            }
        }
        
        if (planId != null) {
            wrapper.eq(FeedingRecord::getPlanId, planId);
        }
        if (areaId != null) {
            wrapper.eq(FeedingRecord::getAreaId, areaId);
        }
        if (feedName != null && !feedName.isEmpty()) {
            wrapper.like(FeedingRecord::getFeedName, feedName);
        }
        if (status != null) {
            wrapper.eq(FeedingRecord::getStatus, status);
        } else {
            wrapper.eq(FeedingRecord::getStatus, 1);
        }
        
        wrapper.orderByDesc(FeedingRecord::getCreateTime);
        return recordMapper.selectPage(page, wrapper);
    }
    
    /**
     * 新增投喂记录
     * 根据用户角色进行权限控制
     * @param record 投喂记录
     * @return 是否成功
     */
    @Override
    public boolean saveRecord(FeedingRecord record) {
        // 普通操作员只能创建自己区域的投喂记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null) {
                throw new RuntimeException("您尚未分配区域，无法创建投喂记录");
            }
            record.setAreaId(userAreaId);
        }
        // 部门管理员只能创建其部门下区域的投喂记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty()) {
                throw new RuntimeException("您尚未分配部门，无法创建投喂记录");
            }
            if (record.getAreaId() == null || !areaIds.contains(record.getAreaId())) {
                throw new RuntimeException("您只能创建本部门下区域的投喂记录");
            }
        }
        
        // 新记录默认状态为正常
        if (record.getStatus() == null) {
            record.setStatus(1);
        }
        
        // 设置创建时间和创建人
        if (record.getCreateTime() == null) {
            record.setCreateTime(LocalDateTime.now());
        }
        if (record.getCreatorId() == null) {
            record.setCreatorId(UserContext.getCurrentUserId());
        }
        
        return recordMapper.insert(record) > 0;
    }
    
    /**
     * 更新投喂记录
     * @param record 投喂记录
     * @return 是否成功
     */
    @Override
    public boolean updateRecord(FeedingRecord record) {
        return recordMapper.updateById(record) > 0;
    }
    
    /**
     * 删除投喂记录（软删除）
     * @param recordId 投喂记录ID
     * @return 是否成功
     */
    @Override
    public boolean deleteRecord(Long recordId) {
        // 软删除，将状态设置为0
        FeedingRecord record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("投喂记录不存在");
        }
        record.setStatus(0);
        return recordMapper.updateById(record) > 0;
    }
    
    /**
     * 统计有效的投喂记录数量
     * @return 记录数量
     */
    @Override
    public long count() {
        LambdaQueryWrapper<FeedingRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedingRecord::getStatus, 1);
        return recordMapper.selectCount(wrapper);
    }
}

