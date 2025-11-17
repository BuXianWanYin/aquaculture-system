package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.MedicineUsage;
import com.server.aquacultureserver.domain.MedicineInventory;
import com.server.aquacultureserver.mapper.MedicineUsageMapper;
import com.server.aquacultureserver.service.MedicineUsageService;
import com.server.aquacultureserver.service.MedicineInventoryService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 用药记录服务实现类
 */
@Service
public class MedicineUsageServiceImpl implements MedicineUsageService {
    
    @Autowired
    private MedicineUsageMapper usageMapper;
    
    @Autowired
    private MedicineInventoryService inventoryService;
    
    @Override
    public List<MedicineUsage> getAllUsages() {
        LambdaQueryWrapper<MedicineUsage> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的用药记录
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(MedicineUsage::getAreaId, areaId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员只能查看其部门下所有区域的用药记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(MedicineUsage::getAreaId, areaIds);
            } else {
                return Collections.emptyList();
            }
        }
        
        wrapper.eq(MedicineUsage::getStatus, 1);
        wrapper.orderByDesc(MedicineUsage::getCreateTime);
        return usageMapper.selectList(wrapper);
    }
    
    @Override
    public MedicineUsage getById(Long usageId) {
        return usageMapper.selectById(usageId);
    }
    
    @Override
    public Page<MedicineUsage> getPage(Integer current, Integer size, Long recordId, Long preventionId, Long planId, Long areaId, String medicineName, Integer status) {
        Page<MedicineUsage> page = new Page<>(current, size);
        LambdaQueryWrapper<MedicineUsage> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的用药记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(MedicineUsage::getAreaId, userAreaId);
            } else {
                return page;
            }
        }
        // 部门管理员只能查看其部门下所有区域的用药记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(MedicineUsage::getAreaId, areaIds);
            } else {
                return page;
            }
        }
        
        if (recordId != null) {
            wrapper.eq(MedicineUsage::getRecordId, recordId);
        }
        if (preventionId != null) {
            wrapper.eq(MedicineUsage::getPreventionId, preventionId);
        }
        if (planId != null) {
            wrapper.eq(MedicineUsage::getPlanId, planId);
        }
        if (areaId != null) {
            wrapper.eq(MedicineUsage::getAreaId, areaId);
        }
        if (medicineName != null && !medicineName.isEmpty()) {
            wrapper.like(MedicineUsage::getMedicineName, medicineName);
        }
        if (status != null) {
            wrapper.eq(MedicineUsage::getStatus, status);
        } else {
            wrapper.eq(MedicineUsage::getStatus, 1);
        }
        
        wrapper.orderByDesc(MedicineUsage::getCreateTime);
        return usageMapper.selectPage(page, wrapper);
    }
    
    @Override
    public List<MedicineUsage> getByRecordId(Long recordId) {
        LambdaQueryWrapper<MedicineUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineUsage::getRecordId, recordId);
        wrapper.eq(MedicineUsage::getStatus, 1);
        wrapper.orderByDesc(MedicineUsage::getCreateTime);
        return usageMapper.selectList(wrapper);
    }
    
    @Override
    public List<MedicineUsage> getByPreventionId(Long preventionId) {
        LambdaQueryWrapper<MedicineUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineUsage::getPreventionId, preventionId);
        wrapper.eq(MedicineUsage::getStatus, 1);
        wrapper.orderByDesc(MedicineUsage::getCreateTime);
        return usageMapper.selectList(wrapper);
    }
    
    @Override
    @Transactional
    public boolean saveUsage(MedicineUsage usage) {
        // 普通操作员只能创建自己区域的用药记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null) {
                throw new RuntimeException("您尚未分配区域，无法创建用药记录");
            }
            usage.setAreaId(userAreaId);
        }
        // 部门管理员只能创建其部门下区域的用药记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty()) {
                throw new RuntimeException("您尚未分配部门，无法创建用药记录");
            }
            if (usage.getAreaId() == null || !areaIds.contains(usage.getAreaId())) {
                throw new RuntimeException("您只能创建本部门下区域的用药记录");
            }
        }
        
        // 新记录默认状态为正常
        if (usage.getStatus() == null) {
            usage.setStatus(1);
        }
        
        // 设置创建时间和创建人
        if (usage.getCreateTime() == null) {
            usage.setCreateTime(LocalDateTime.now());
        }
        if (usage.getCreatorId() == null) {
            usage.setCreatorId(UserContext.getCurrentUserId());
        }
        
        // 计算总成本
        if (usage.getDosage() != null && usage.getUnitPrice() != null) {
            usage.setTotalCost(usage.getDosage().multiply(usage.getUnitPrice()));
        }
        
        // 根据药品名称和类型查找库存并减少库存
        if (usage.getMedicineName() != null && usage.getMedicineType() != null && usage.getDosage() != null) {
            MedicineInventory inventory = inventoryService.getByMedicineNameAndType(
                usage.getMedicineName(), 
                usage.getMedicineType()
            );
            if (inventory != null) {
                BigDecimal newStock = inventory.getCurrentStock().subtract(usage.getDosage());
                if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("库存不足，当前库存：" + inventory.getCurrentStock() + inventory.getUnit());
                }
                inventory.setCurrentStock(newStock);
                inventory.setUpdateTime(LocalDateTime.now());
                inventoryService.updateInventory(inventory);
            } else {
                throw new RuntimeException("未找到对应的库存记录，请检查药品名称和类型");
            }
        }
        
        return usageMapper.insert(usage) > 0;
    }
    
    @Override
    @Transactional
    public boolean updateUsage(MedicineUsage usage) {
        // 获取原始使用记录
        MedicineUsage oldUsage = getById(usage.getUsageId());
        if (oldUsage == null) {
            throw new RuntimeException("用药记录不存在");
        }
        
        // 计算总成本
        if (usage.getDosage() != null && usage.getUnitPrice() != null) {
            usage.setTotalCost(usage.getDosage().multiply(usage.getUnitPrice()));
        }
        
        // 如果使用数量、药品名称或类型发生变化，需要更新库存
        boolean needUpdateInventory = false;
        BigDecimal oldAmount = oldUsage.getDosage() != null ? oldUsage.getDosage() : BigDecimal.ZERO;
        BigDecimal newAmount = usage.getDosage() != null ? usage.getDosage() : BigDecimal.ZERO;
        
        String oldMedicineName = oldUsage.getMedicineName() == null ? "" : oldUsage.getMedicineName();
        String newMedicineName = usage.getMedicineName() == null ? "" : usage.getMedicineName();
        String oldMedicineType = oldUsage.getMedicineType() == null ? "" : oldUsage.getMedicineType();
        String newMedicineType = usage.getMedicineType() == null ? "" : usage.getMedicineType();
        
        if (!oldAmount.equals(newAmount) || 
            !oldMedicineName.equals(newMedicineName) ||
            !oldMedicineType.equals(newMedicineType)) {
            needUpdateInventory = true;
        }
        
        if (needUpdateInventory) {
            // 恢复旧记录的库存（按药品名称+类型）
            if (oldUsage.getMedicineName() != null && oldUsage.getMedicineType() != null && oldUsage.getDosage() != null) {
                MedicineInventory oldInventory = inventoryService.getByMedicineNameAndType(
                    oldUsage.getMedicineName(), 
                    oldUsage.getMedicineType()
                );
                if (oldInventory != null) {
                    BigDecimal restoredStock = oldInventory.getCurrentStock().add(oldAmount);
                    oldInventory.setCurrentStock(restoredStock);
                    oldInventory.setUpdateTime(LocalDateTime.now());
                    inventoryService.updateInventory(oldInventory);
                }
            }
            
            // 减少新记录的库存（按药品名称+类型）
            if (usage.getMedicineName() != null && usage.getMedicineType() != null && usage.getDosage() != null) {
                MedicineInventory newInventory = inventoryService.getByMedicineNameAndType(
                    usage.getMedicineName(), 
                    usage.getMedicineType()
                );
                if (newInventory != null) {
                    BigDecimal newStock = newInventory.getCurrentStock().subtract(newAmount);
                    if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                        throw new RuntimeException("库存不足，当前库存：" + newInventory.getCurrentStock() + newInventory.getUnit());
                    }
                    newInventory.setCurrentStock(newStock);
                    newInventory.setUpdateTime(LocalDateTime.now());
                    inventoryService.updateInventory(newInventory);
                } else {
                    throw new RuntimeException("未找到对应的库存记录，请检查药品名称和类型");
                }
            }
        }
        
        usage.setUpdateTime(LocalDateTime.now());
        return usageMapper.updateById(usage) > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteUsage(Long usageId) {
        // 软删除，将状态设置为0，并恢复库存
        MedicineUsage usage = getById(usageId);
        if (usage == null) {
            throw new RuntimeException("用药记录不存在");
        }
        
        // 恢复库存（按药品名称+类型）
        if (usage.getMedicineName() != null && usage.getMedicineType() != null && usage.getDosage() != null && usage.getStatus() == 1) {
            MedicineInventory inventory = inventoryService.getByMedicineNameAndType(
                usage.getMedicineName(), 
                usage.getMedicineType()
            );
            if (inventory != null) {
                BigDecimal restoredStock = inventory.getCurrentStock().add(usage.getDosage());
                inventory.setCurrentStock(restoredStock);
                inventory.setUpdateTime(LocalDateTime.now());
                inventoryService.updateInventory(inventory);
            }
        }
        
        usage.setStatus(0);
        usage.setUpdateTime(LocalDateTime.now());
        return usageMapper.updateById(usage) > 0;
    }
    
    @Override
    public long count() {
        LambdaQueryWrapper<MedicineUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineUsage::getStatus, 1);
        return usageMapper.selectCount(wrapper);
    }
}

