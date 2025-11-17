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
    
    /**
     * 查询所有有效的用药记录
     * 根据用户角色进行权限过滤：
     * - 普通操作员：只能查看自己区域的用药记录
     * - 部门管理员：只能查看其部门下所有区域的用药记录
     * - 系统管理员：可以查看所有用药记录
     * @return 用药记录列表
     */
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
        
        // 只查询状态为正常（1）的记录
        wrapper.eq(MedicineUsage::getStatus, 1);
        // 按创建时间倒序排列
        wrapper.orderByDesc(MedicineUsage::getCreateTime);
        return usageMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID查询用药记录
     * @param usageId 用药记录ID
     * @return 用药记录
     */
    @Override
    public MedicineUsage getById(Long usageId) {
        return usageMapper.selectById(usageId);
    }
    
    /**
     * 分页查询用药记录
     * 根据用户角色进行权限过滤
     * @param current 当前页码
     * @param size 每页大小
     * @param recordId 病害记录ID
     * @param preventionId 病害预防ID
     * @param planId 养殖计划ID
     * @param areaId 区域ID
     * @param medicineName 药品名称（模糊查询）
     * @param status 状态（1-正常，0-已删除）
     * @return 分页结果
     */
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
    
    /**
     * 根据病害记录ID查询用药记录
     * @param recordId 病害记录ID
     * @return 用药记录列表
     */
    @Override
    public List<MedicineUsage> getByRecordId(Long recordId) {
        LambdaQueryWrapper<MedicineUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineUsage::getRecordId, recordId);
        wrapper.eq(MedicineUsage::getStatus, 1);
        wrapper.orderByDesc(MedicineUsage::getCreateTime);
        return usageMapper.selectList(wrapper);
    }
    
    /**
     * 根据病害预防ID查询用药记录
     * @param preventionId 病害预防ID
     * @return 用药记录列表
     */
    @Override
    public List<MedicineUsage> getByPreventionId(Long preventionId) {
        LambdaQueryWrapper<MedicineUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineUsage::getPreventionId, preventionId);
        wrapper.eq(MedicineUsage::getStatus, 1);
        wrapper.orderByDesc(MedicineUsage::getCreateTime);
        return usageMapper.selectList(wrapper);
    }
    
    /**
     * 新增用药记录
     * 新增用药后会自动从药品库存中扣减对应数量
     * 根据用户角色进行权限控制：
     * - 普通操作员：只能创建自己区域的用药记录
     * - 部门管理员：只能创建其部门下区域的用药记录
     * @param usage 用药记录
     * @return 是否成功
     */
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
        
        // 计算总成本 = 用量 × 单价
        if (usage.getDosage() != null && usage.getUnitPrice() != null) {
            usage.setTotalCost(usage.getDosage().multiply(usage.getUnitPrice()));
        }
        
        // 根据药品名称和类型查找库存并自动扣减库存
        if (usage.getMedicineName() != null && usage.getMedicineType() != null && usage.getDosage() != null) {
            MedicineInventory inventory = inventoryService.getByMedicineNameAndType(
                usage.getMedicineName(), 
                usage.getMedicineType()
            );
            if (inventory != null) {
                // 计算新库存 = 当前库存 - 使用数量
                BigDecimal newStock = inventory.getCurrentStock().subtract(usage.getDosage());
                // 检查库存是否充足
                if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("库存不足，当前库存：" + inventory.getCurrentStock() + inventory.getUnit());
                }
                // 更新库存
                inventory.setCurrentStock(newStock);
                inventory.setUpdateTime(LocalDateTime.now());
                inventoryService.updateInventory(inventory);
            } else {
                throw new RuntimeException("未找到对应的库存记录，请检查药品名称和类型");
            }
        }
        
        // 保存用药记录
        return usageMapper.insert(usage) > 0;
    }
    
    /**
     * 更新用药记录
     * 如果使用数量、药品名称或类型发生变化，会自动更新库存：
     * - 先恢复旧记录的库存数量
     * - 再扣减新记录的库存数量
     * @param usage 用药记录
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean updateUsage(MedicineUsage usage) {
        // 获取原始使用记录
        MedicineUsage oldUsage = getById(usage.getUsageId());
        if (oldUsage == null) {
            throw new RuntimeException("用药记录不存在");
        }
        
        // 计算总成本 = 用量 × 单价
        if (usage.getDosage() != null && usage.getUnitPrice() != null) {
            usage.setTotalCost(usage.getDosage().multiply(usage.getUnitPrice()));
        }
        
        // 判断是否需要更新库存（如果使用数量、药品名称或类型发生变化，则需要更新库存）
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
            // 先恢复旧记录的库存（按药品名称+类型）
            if (oldUsage.getMedicineName() != null && oldUsage.getMedicineType() != null && oldUsage.getDosage() != null) {
                MedicineInventory oldInventory = inventoryService.getByMedicineNameAndType(
                    oldUsage.getMedicineName(), 
                    oldUsage.getMedicineType()
                );
                if (oldInventory != null) {
                    // 恢复库存 = 当前库存 + 旧使用数量
                    BigDecimal restoredStock = oldInventory.getCurrentStock().add(oldAmount);
                    oldInventory.setCurrentStock(restoredStock);
                    oldInventory.setUpdateTime(LocalDateTime.now());
                    inventoryService.updateInventory(oldInventory);
                }
            }
            
            // 再扣减新记录的库存（按药品名称+类型）
            if (usage.getMedicineName() != null && usage.getMedicineType() != null && usage.getDosage() != null) {
                MedicineInventory newInventory = inventoryService.getByMedicineNameAndType(
                    usage.getMedicineName(), 
                    usage.getMedicineType()
                );
                if (newInventory != null) {
                    // 计算新库存 = 当前库存 - 新使用数量
                    BigDecimal newStock = newInventory.getCurrentStock().subtract(newAmount);
                    // 检查库存是否充足
                    if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                        throw new RuntimeException("库存不足，当前库存：" + newInventory.getCurrentStock() + newInventory.getUnit());
                    }
                    // 更新库存
                    newInventory.setCurrentStock(newStock);
                    newInventory.setUpdateTime(LocalDateTime.now());
                    inventoryService.updateInventory(newInventory);
                } else {
                    throw new RuntimeException("未找到对应的库存记录，请检查药品名称和类型");
                }
            }
        }
        
        // 更新用药记录
        usage.setUpdateTime(LocalDateTime.now());
        return usageMapper.updateById(usage) > 0;
    }
    
    /**
     * 删除用药记录（软删除）
     * 删除用药记录后会自动恢复对应药品的库存数量
     * @param usageId 用药记录ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean deleteUsage(Long usageId) {
        // 获取用药记录
        MedicineUsage usage = getById(usageId);
        if (usage == null) {
            throw new RuntimeException("用药记录不存在");
        }
        
        // 恢复库存（按药品名称+类型），只有正常状态的记录才需要恢复库存
        if (usage.getMedicineName() != null && usage.getMedicineType() != null && usage.getDosage() != null && usage.getStatus() == 1) {
            MedicineInventory inventory = inventoryService.getByMedicineNameAndType(
                usage.getMedicineName(), 
                usage.getMedicineType()
            );
            if (inventory != null) {
                // 恢复库存 = 当前库存 + 使用数量
                BigDecimal restoredStock = inventory.getCurrentStock().add(usage.getDosage());
                inventory.setCurrentStock(restoredStock);
                inventory.setUpdateTime(LocalDateTime.now());
                inventoryService.updateInventory(inventory);
            }
        }
        
        // 软删除，将状态设为0
        usage.setStatus(0);
        usage.setUpdateTime(LocalDateTime.now());
        return usageMapper.updateById(usage) > 0;
    }
    
    /**
     * 统计有效的用药记录数量
     * @return 记录数量
     */
    @Override
    public long count() {
        LambdaQueryWrapper<MedicineUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineUsage::getStatus, 1);
        return usageMapper.selectCount(wrapper);
    }
}

