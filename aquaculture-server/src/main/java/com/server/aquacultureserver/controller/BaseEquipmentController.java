package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.BaseEquipment;
import com.server.aquacultureserver.service.BaseEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备信息控制器
 */
@RestController
@RequestMapping("/api/equipment")
@CrossOrigin
public class BaseEquipmentController {
    
    @Autowired
    private BaseEquipmentService equipmentService;
    
    /**
     * 查询所有设备
     */
    @GetMapping("/all")
    public Result<List<BaseEquipment>> getAllEquipments() {
        List<BaseEquipment> equipments = equipmentService.getAllEquipments();
        return Result.success(equipments);
    }
    
    /**
     * 分页查询设备列表
     */
    @GetMapping("/page")
    public Result<Page<BaseEquipment>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String equipmentName,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) String equipmentType) {
        Page<BaseEquipment> page = equipmentService.getPage(current, size, equipmentName, areaId, equipmentType);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询设备
     */
    @GetMapping("/{equipmentId}")
    public Result<BaseEquipment> getById(@PathVariable Long equipmentId) {
        BaseEquipment equipment = equipmentService.getById(equipmentId);
        return Result.success(equipment);
    }
    
    /**
     * 新增设备
     */
    @PostMapping
    public Result<Boolean> saveEquipment(@RequestBody BaseEquipment equipment) {
        try {
            boolean success = equipmentService.saveEquipment(equipment);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新设备
     */
    @PutMapping
    public Result<Boolean> updateEquipment(@RequestBody BaseEquipment equipment) {
        try {
            boolean success = equipmentService.updateEquipment(equipment);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除设备
     */
    @DeleteMapping("/{equipmentId}")
    public Result<Boolean> deleteEquipment(@PathVariable Long equipmentId) {
        try {
            boolean success = equipmentService.deleteEquipment(equipmentId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

