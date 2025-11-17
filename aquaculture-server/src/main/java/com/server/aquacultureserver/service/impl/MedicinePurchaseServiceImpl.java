package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.MedicinePurchase;
import com.server.aquacultureserver.domain.MedicineInventory;
import com.server.aquacultureserver.mapper.MedicinePurchaseMapper;
import com.server.aquacultureserver.service.MedicinePurchaseService;
import com.server.aquacultureserver.service.MedicineInventoryService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 药品采购服务实现类
 */
@Service
public class MedicinePurchaseServiceImpl implements MedicinePurchaseService {
    
    @Autowired
    private MedicinePurchaseMapper purchaseMapper;
    
    @Autowired
    private MedicineInventoryService inventoryService;
    
    /**
     * 查询所有有效的药品采购记录
     * @return 药品采购记录列表
     */
    @Override
    public List<MedicinePurchase> getAllPurchases() {
        LambdaQueryWrapper<MedicinePurchase> wrapper = new LambdaQueryWrapper<>();
        // 只查询状态为正常（1）的记录
        wrapper.eq(MedicinePurchase::getStatus, 1);
        // 按创建时间倒序排列
        wrapper.orderByDesc(MedicinePurchase::getCreateTime);
        return purchaseMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID查询药品采购记录
     * @param purchaseId 采购记录ID
     * @return 药品采购记录
     */
    @Override
    public MedicinePurchase getById(Long purchaseId) {
        return purchaseMapper.selectById(purchaseId);
    }
    
    /**
     * 分页查询药品采购记录
     * @param current 当前页码
     * @param size 每页大小
     * @param medicineName 药品名称（模糊查询）
     * @param medicineType 药品类型（精确查询）
     * @param supplier 供应商（模糊查询）
     * @param status 状态（1-正常，0-已删除）
     * @return 分页结果
     */
    @Override
    public Page<MedicinePurchase> getPage(Integer current, Integer size, String medicineName, String medicineType, String supplier, Integer status) {
        Page<MedicinePurchase> page = new Page<>(current, size);
        LambdaQueryWrapper<MedicinePurchase> wrapper = new LambdaQueryWrapper<>();
        
        // 药品名称模糊查询
        if (medicineName != null && !medicineName.isEmpty()) {
            wrapper.like(MedicinePurchase::getMedicineName, medicineName);
        }
        // 药品类型精确查询
        if (medicineType != null && !medicineType.isEmpty()) {
            wrapper.eq(MedicinePurchase::getMedicineType, medicineType);
        }
        // 供应商模糊查询
        if (supplier != null && !supplier.isEmpty()) {
            wrapper.like(MedicinePurchase::getSupplier, supplier);
        }
        // 状态查询，如果未指定则默认查询正常状态
        if (status != null) {
            wrapper.eq(MedicinePurchase::getStatus, status);
        } else {
            wrapper.eq(MedicinePurchase::getStatus, 1);
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(MedicinePurchase::getCreateTime);
        return purchaseMapper.selectPage(page, wrapper);
    }
    
    /**
     * 新增药品采购记录
     * 新增采购后会自动更新药品库存（如果为新药品则创建库存，如果为已有药品则累加库存）
     * @param purchase 药品采购记录
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean savePurchase(MedicinePurchase purchase) {
        // 设置默认状态为正常（1）
        if (purchase.getStatus() == null) {
            purchase.setStatus(1);
        }
        // 设置创建时间
        if (purchase.getCreateTime() == null) {
            purchase.setCreateTime(LocalDateTime.now());
        }
        // 设置创建人ID
        if (purchase.getCreatorId() == null) {
            purchase.setCreatorId(UserContext.getCurrentUserId());
        }
        
        // 计算总价 = 采购数量 × 单价
        if (purchase.getPurchaseAmount() != null && purchase.getUnitPrice() != null) {
            purchase.setTotalPrice(purchase.getPurchaseAmount().multiply(purchase.getUnitPrice()));
        }
        
        // 保存采购记录
        boolean result = purchaseMapper.insert(purchase) > 0;
        
        // 如果采购成功且状态为正常，则自动更新库存
        if (result && purchase.getStatus() == 1) {
            updateInventoryFromPurchase(purchase);
        }
        
        return result;
    }
    
    /**
     * 根据采购记录更新库存
     * 库存按药品名称+类型聚合，不按批次号区分
     * 如果该药品已有库存，则累加数量；如果为新药品，则创建新库存记录
     * @param purchase 药品采购记录
     */
    private void updateInventoryFromPurchase(MedicinePurchase purchase) {
        // 参数校验
        if (purchase.getMedicineName() == null || purchase.getPurchaseAmount() == null) {
            return;
        }
        
        // 查找是否有相同的药品名称和类型的库存（不按批次号）
        MedicineInventory inventory = inventoryService.getByMedicineNameAndType(
            purchase.getMedicineName(), 
            purchase.getMedicineType()
        );
        
        if (inventory != null) {
            // 如果存在，则累加库存数量
            inventory.setCurrentStock(inventory.getCurrentStock().add(purchase.getPurchaseAmount()));
            // 更新单价为最新采购单价
            inventory.setUnitPrice(purchase.getUnitPrice());
            inventory.setUpdateTime(LocalDateTime.now());
            inventoryService.updateInventory(inventory);
        } else {
            // 如果不存在，则创建新库存记录
            MedicineInventory newInventory = new MedicineInventory();
            newInventory.setMedicineName(purchase.getMedicineName());
            newInventory.setMedicineType(purchase.getMedicineType());
            newInventory.setCurrentStock(purchase.getPurchaseAmount());
            newInventory.setUnit(purchase.getUnit());
            newInventory.setUnitPrice(purchase.getUnitPrice());
            newInventory.setStatus(1);
            newInventory.setCreatorId(purchase.getCreatorId());
            newInventory.setCreateTime(LocalDateTime.now());
            inventoryService.saveInventory(newInventory);
        }
    }
    
    /**
     * 更新药品采购记录
     * 如果采购数量、药品名称、类型或状态发生变化，会自动更新库存
     * @param purchase 药品采购记录
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean updatePurchase(MedicinePurchase purchase) {
        // 获取原始采购记录
        MedicinePurchase oldPurchase = getById(purchase.getPurchaseId());
        if (oldPurchase == null) {
            throw new RuntimeException("采购记录不存在");
        }
        
        // 计算总价 = 采购数量 × 单价
        if (purchase.getPurchaseAmount() != null && purchase.getUnitPrice() != null) {
            purchase.setTotalPrice(purchase.getPurchaseAmount().multiply(purchase.getUnitPrice()));
        }
        
        // 判断是否需要更新库存（如果状态、数量、药品名称或类型发生变化，则需要更新库存）
        boolean needUpdateInventory = false;
        boolean oldStatus = (oldPurchase.getStatus() != null && oldPurchase.getStatus() == 1);
        boolean newStatus = (purchase.getStatus() != null && purchase.getStatus() == 1);
        
        // 状态变化需要更新库存
        if (oldStatus != newStatus) {
            needUpdateInventory = true;
        }
        
        // 采购数量变化需要更新库存
        if (oldPurchase.getPurchaseAmount() != null && purchase.getPurchaseAmount() != null) {
            if (oldPurchase.getPurchaseAmount().compareTo(purchase.getPurchaseAmount()) != 0) {
                needUpdateInventory = true;
            }
        }
        
        // 药品名称或类型变化需要更新库存
        String oldMedicineName = oldPurchase.getMedicineName() == null ? "" : oldPurchase.getMedicineName();
        String newMedicineName = purchase.getMedicineName() == null ? "" : purchase.getMedicineName();
        String oldMedicineType = oldPurchase.getMedicineType() == null ? "" : oldPurchase.getMedicineType();
        String newMedicineType = purchase.getMedicineType() == null ? "" : purchase.getMedicineType();
        
        if (!oldMedicineName.equals(newMedicineName) || !oldMedicineType.equals(newMedicineType)) {
            needUpdateInventory = true;
        }
        
        // 更新采购记录
        boolean result = purchaseMapper.updateById(purchase) > 0;
        
        if (result && needUpdateInventory) {
            // 如果原记录是正常状态，需要先恢复原库存
            if (oldStatus && oldPurchase.getMedicineName() != null && oldPurchase.getPurchaseAmount() != null) {
                MedicineInventory oldInventory = inventoryService.getByMedicineNameAndType(oldMedicineName, oldMedicineType);
                if (oldInventory != null) {
                    // 检查该药品是否还有其他采购记录（排除当前正在更新的这条）
                    LambdaQueryWrapper<MedicinePurchase> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(MedicinePurchase::getMedicineName, oldMedicineName);
                    wrapper.eq(MedicinePurchase::getMedicineType, oldMedicineType);
                    wrapper.eq(MedicinePurchase::getStatus, 1);
                    wrapper.ne(MedicinePurchase::getPurchaseId, purchase.getPurchaseId());
                    long remainingPurchaseCount = purchaseMapper.selectCount(wrapper);
                    
                    if (remainingPurchaseCount == 0) {
                        // 如果该药品没有其他采购记录了，删除库存记录
                        inventoryService.deleteInventory(oldInventory.getInventoryId());
                    } else {
                        // 如果还有其他采购记录，只更新库存（减少对应数量）
                        BigDecimal restoredStock = oldInventory.getCurrentStock().subtract(oldPurchase.getPurchaseAmount());
                        if (restoredStock.compareTo(BigDecimal.ZERO) < 0) {
                            restoredStock = BigDecimal.ZERO;
                        }
                        oldInventory.setCurrentStock(restoredStock);
                        oldInventory.setUpdateTime(LocalDateTime.now());
                        inventoryService.updateInventory(oldInventory);
                    }
                }
            }
            
            // 如果新记录是正常状态，需要增加新库存
            if (newStatus) {
                updateInventoryFromPurchase(purchase);
            }
        }
        
        return result;
    }
    
    /**
     * 删除药品采购记录（软删除）
     * 删除采购记录后会自动更新库存：
     * - 如果该药品还有其他采购记录，只减少对应库存数量
     * - 如果该药品的所有采购记录都被删除，则删除对应的库存记录
     * @param purchaseId 采购记录ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean deletePurchase(Long purchaseId) {
        MedicinePurchase purchase = getById(purchaseId);
        if (purchase == null) {
            throw new RuntimeException("采购记录不存在");
        }
        
        String medicineName = purchase.getMedicineName();
        String medicineType = purchase.getMedicineType();
        
        // 如果采购记录状态为正常，删除时需要减少库存
        if (purchase.getStatus() == 1 && medicineName != null && purchase.getPurchaseAmount() != null) {
            MedicineInventory inventory = inventoryService.getByMedicineNameAndType(medicineName, medicineType);
            if (inventory != null) {
                // 先软删除采购记录（状态设为0）
                purchase.setStatus(0);
                purchaseMapper.updateById(purchase);
                
                // 检查该药品是否还有其他采购记录
                LambdaQueryWrapper<MedicinePurchase> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(MedicinePurchase::getMedicineName, medicineName);
                wrapper.eq(MedicinePurchase::getMedicineType, medicineType);
                wrapper.eq(MedicinePurchase::getStatus, 1);
                long remainingPurchaseCount = purchaseMapper.selectCount(wrapper);
                
                if (remainingPurchaseCount == 0) {
                    // 如果该药品的所有采购记录都被删除了，删除库存记录
                    inventoryService.deleteInventory(inventory.getInventoryId());
                } else {
                    // 如果还有其他采购记录，只更新库存（减少对应数量）
                    BigDecimal newStock = inventory.getCurrentStock().subtract(purchase.getPurchaseAmount());
                    if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                        newStock = BigDecimal.ZERO;
                    }
                    inventory.setCurrentStock(newStock);
                    inventory.setUpdateTime(LocalDateTime.now());
                    inventoryService.updateInventory(inventory);
                }
                
                return true;
            }
        }
        
        // 如果库存不存在或采购记录状态不是正常，只软删除采购记录
        purchase.setStatus(0);
        return purchaseMapper.updateById(purchase) > 0;
    }
    
    /**
     * 统计有效的药品采购记录数量
     * @return 记录数量
     */
    @Override
    public long count() {
        LambdaQueryWrapper<MedicinePurchase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicinePurchase::getStatus, 1);
        return purchaseMapper.selectCount(wrapper);
    }
    
    /**
     * 根据药品名称和类型查询采购记录
     * 用于用药记录中选择批次号时获取该药品的所有采购记录
     * @param medicineName 药品名称
     * @param medicineType 药品类型
     * @return 采购记录列表（按采购日期倒序）
     */
    @Override
    public List<MedicinePurchase> getByMedicineNameAndType(String medicineName, String medicineType) {
        LambdaQueryWrapper<MedicinePurchase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicinePurchase::getMedicineName, medicineName);
        wrapper.eq(MedicinePurchase::getMedicineType, medicineType);
        wrapper.eq(MedicinePurchase::getStatus, 1);
        // 按采购日期倒序排列
        wrapper.orderByDesc(MedicinePurchase::getPurchaseDate);
        return purchaseMapper.selectList(wrapper);
    }
}

