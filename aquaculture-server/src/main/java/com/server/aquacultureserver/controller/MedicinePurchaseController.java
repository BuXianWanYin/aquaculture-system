package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.MedicinePurchase;
import com.server.aquacultureserver.service.MedicinePurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 药品采购控制器
 */
@RestController
@RequestMapping("/api/medicine/purchase")
@CrossOrigin
public class MedicinePurchaseController {
    
    @Autowired
    private MedicinePurchaseService purchaseService;
    
    /**
     * 查询所有采购记录
     */
    @GetMapping("/all")
    public Result<List<MedicinePurchase>> getAllPurchases() {
        List<MedicinePurchase> purchases = purchaseService.getAllPurchases();
        return Result.success(purchases);
    }
    
    /**
     * 分页查询采购记录列表
     */
    @GetMapping("/page")
    @RequiresPermission({"medicine:purchase:list"})
    public Result<Page<MedicinePurchase>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String medicineName,
            @RequestParam(required = false) String medicineType,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) Integer status) {
        Page<MedicinePurchase> page = purchaseService.getPage(current, size, medicineName, medicineType, supplier, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询采购记录
     */
    @GetMapping("/{purchaseId}")
    @RequiresPermission({"medicine:purchase:detail"})
    public Result<MedicinePurchase> getById(@PathVariable Long purchaseId) {
        MedicinePurchase purchase = purchaseService.getById(purchaseId);
        return Result.success(purchase);
    }
    
    /**
     * 新增采购记录
     */
    @PostMapping
    @RequiresPermission({"medicine:purchase:add"})
    public Result<Boolean> savePurchase(@RequestBody MedicinePurchase purchase) {
        try {
            boolean success = purchaseService.savePurchase(purchase);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新采购记录
     */
    @PutMapping
    @RequiresPermission({"medicine:purchase:edit"})
    public Result<Boolean> updatePurchase(@RequestBody MedicinePurchase purchase) {
        try {
            boolean success = purchaseService.updatePurchase(purchase);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除采购记录
     */
    @DeleteMapping("/{purchaseId}")
    @RequiresPermission({"medicine:purchase:delete"})
    public Result<Boolean> deletePurchase(@PathVariable Long purchaseId) {
        try {
            boolean success = purchaseService.deletePurchase(purchaseId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据药品名称和类型查询采购记录
     */
    @GetMapping("/byMedicine")
    public Result<List<MedicinePurchase>> getByMedicineNameAndType(
            @RequestParam String medicineName,
            @RequestParam String medicineType) {
        try {
            List<MedicinePurchase> purchases = purchaseService.getByMedicineNameAndType(medicineName, medicineType);
            return Result.success(purchases);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

