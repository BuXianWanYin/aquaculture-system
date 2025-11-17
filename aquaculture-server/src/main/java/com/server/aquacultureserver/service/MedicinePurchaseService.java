package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.MedicinePurchase;

import java.util.List;

/**
 * 药品采购服务接口
 */
public interface MedicinePurchaseService {
    
    /**
     * 查询所有采购记录
     */
    List<MedicinePurchase> getAllPurchases();
    
    /**
     * 根据ID查询采购记录
     */
    MedicinePurchase getById(Long purchaseId);
    
    /**
     * 分页查询采购记录列表
     */
    Page<MedicinePurchase> getPage(Integer current, Integer size, String medicineName, String medicineType, String supplier, Integer status);
    
    /**
     * 新增采购记录
     */
    boolean savePurchase(MedicinePurchase purchase);
    
    /**
     * 更新采购记录
     */
    boolean updatePurchase(MedicinePurchase purchase);
    
    /**
     * 删除采购记录
     */
    boolean deletePurchase(Long purchaseId);
    
    /**
     * 统计采购记录总数
     */
    long count();
    
    /**
     * 根据药品名称和类型查询采购记录
     */
    List<MedicinePurchase> getByMedicineNameAndType(String medicineName, String medicineType);
}

