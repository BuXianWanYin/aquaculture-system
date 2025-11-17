package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SalesRecord;
import com.server.aquacultureserver.mapper.SalesRecordMapper;
import com.server.aquacultureserver.service.SalesRecordService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 销售记录服务实现类
 */
@Service
public class SalesRecordServiceImpl implements SalesRecordService {
    
    @Autowired
    private SalesRecordMapper recordMapper;
    
    /**
     * 查询所有有效的销售记录
     * 根据用户角色进行权限过滤
     * @return 销售记录列表
     */
    @Override
    public List<SalesRecord> getAllRecords() {
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的销售记录
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(SalesRecord::getAreaId, areaId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员只能查看其部门下所有区域的销售记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(SalesRecord::getAreaId, areaIds);
            } else {
                return Collections.emptyList();
            }
        }
        
        wrapper.eq(SalesRecord::getStatus, 1);
        wrapper.orderByDesc(SalesRecord::getCreateTime);
        return recordMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID查询销售记录
     * @param salesId 销售记录ID
     * @return 销售记录
     */
    @Override
    public SalesRecord getById(Long salesId) {
        return recordMapper.selectById(salesId);
    }
    
    /**
     * 分页查询销售记录
     * 根据用户角色进行权限过滤
     * @param current 当前页码
     * @param size 每页大小
     * @param planId 养殖计划ID
     * @param areaId 区域ID
     * @param breedId 品种ID
     * @param customerId 客户ID
     * @param paymentStatus 付款状态
     * @param status 状态（1-正常，0-已删除）
     * @return 分页结果
     */
    @Override
    public Page<SalesRecord> getPage(Integer current, Integer size, Long planId, Long areaId, Long breedId, Long customerId, Integer paymentStatus, Integer status) {
        Page<SalesRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的销售记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(SalesRecord::getAreaId, userAreaId);
            } else {
                return page;
            }
        }
        // 部门管理员只能查看其部门下所有区域的销售记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(SalesRecord::getAreaId, areaIds);
            } else {
                return page;
            }
        }
        
        if (planId != null) {
            wrapper.eq(SalesRecord::getPlanId, planId);
        }
        if (areaId != null) {
            wrapper.eq(SalesRecord::getAreaId, areaId);
        }
        if (breedId != null) {
            wrapper.eq(SalesRecord::getBreedId, breedId);
        }
        if (customerId != null) {
            wrapper.eq(SalesRecord::getCustomerId, customerId);
        }
        if (paymentStatus != null) {
            wrapper.eq(SalesRecord::getPaymentStatus, paymentStatus);
        }
        if (status != null) {
            wrapper.eq(SalesRecord::getStatus, status);
        } else {
            wrapper.eq(SalesRecord::getStatus, 1);
        }
        
        wrapper.orderByDesc(SalesRecord::getCreateTime);
        return recordMapper.selectPage(page, wrapper);
    }
    
    /**
     * 新增销售记录
     * 根据用户角色进行权限控制，自动计算总金额
     * @param record 销售记录
     * @return 是否成功
     */
    @Override
    public boolean saveRecord(SalesRecord record) {
        // 普通操作员只能创建自己区域的销售记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null) {
                throw new RuntimeException("您尚未分配区域，无法创建销售记录");
            }
            record.setAreaId(userAreaId);
        }
        // 部门管理员只能创建其部门下区域的销售记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty()) {
                throw new RuntimeException("您尚未分配部门，无法创建销售记录");
            }
            if (record.getAreaId() == null || !areaIds.contains(record.getAreaId())) {
                throw new RuntimeException("您只能创建本部门下区域的销售记录");
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
        
        // 计算总金额 = 销售数量 × 单价
        if (record.getSalesQuantity() != null && record.getUnitPrice() != null) {
            record.setTotalAmount(record.getSalesQuantity().multiply(record.getUnitPrice()));
        }
        
        return recordMapper.insert(record) > 0;
    }
    
    /**
     * 更新销售记录
     * 自动重新计算总金额
     * @param record 销售记录
     * @return 是否成功
     */
    @Override
    public boolean updateRecord(SalesRecord record) {
        // 计算总金额 = 销售数量 × 单价
        if (record.getSalesQuantity() != null && record.getUnitPrice() != null) {
            record.setTotalAmount(record.getSalesQuantity().multiply(record.getUnitPrice()));
        }
        return recordMapper.updateById(record) > 0;
    }
    
    /**
     * 删除销售记录（软删除）
     * @param salesId 销售记录ID
     * @return 是否成功
     */
    @Override
    public boolean deleteRecord(Long salesId) {
        // 软删除，将状态设置为0
        SalesRecord record = getById(salesId);
        if (record == null) {
            throw new RuntimeException("销售记录不存在");
        }
        record.setStatus(0);
        return recordMapper.updateById(record) > 0;
    }
    
    /**
     * 统计有效的销售记录数量
     * @return 记录数量
     */
    @Override
    public long count() {
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesRecord::getStatus, 1);
        return recordMapper.selectCount(wrapper);
    }
    
    /**
     * 获取销售统计数据
     * 根据用户角色进行权限过滤，统计总金额、总数量和记录数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param areaId 区域ID
     * @param breedId 品种ID
     * @return 统计数据（包含totalAmount、totalQuantity、recordCount）
     */
    @Override
    public Map<String, Object> getSalesStatistics(String startDate, String endDate, Long areaId, Long breedId) {
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesRecord::getStatus, 1);
        
        // 普通操作员只能统计自己区域的销售
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(SalesRecord::getAreaId, userAreaId);
            } else {
                return new HashMap<>();
            }
        }
        // 部门管理员只能统计其部门下所有区域的销售
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(SalesRecord::getAreaId, areaIds);
            } else {
                return new HashMap<>();
            }
        }
        
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(SalesRecord::getSalesDate, LocalDate.parse(startDate));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(SalesRecord::getSalesDate, LocalDate.parse(endDate));
        }
        if (areaId != null) {
            wrapper.eq(SalesRecord::getAreaId, areaId);
        }
        if (breedId != null) {
            wrapper.eq(SalesRecord::getBreedId, breedId);
        }
        
        List<SalesRecord> records = recordMapper.selectList(wrapper);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalQuantity = BigDecimal.ZERO;
        
        for (SalesRecord record : records) {
            if (record.getTotalAmount() != null) {
                totalAmount = totalAmount.add(record.getTotalAmount());
            }
            if (record.getSalesQuantity() != null) {
                totalQuantity = totalQuantity.add(record.getSalesQuantity());
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalAmount", totalAmount);
        result.put("totalQuantity", totalQuantity);
        result.put("recordCount", records.size());
        
        return result;
    }
}

