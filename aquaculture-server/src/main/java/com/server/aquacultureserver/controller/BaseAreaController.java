package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.BaseArea;
import com.server.aquacultureserver.service.BaseAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 养殖区域控制器
 */
@RestController
@RequestMapping("/api/area")
@CrossOrigin
public class BaseAreaController {
    
    @Autowired
    private BaseAreaService areaService;
    
    /**
     * 查询所有区域
     */
    @GetMapping("/all")
    public Result<List<BaseArea>> getAllAreas() {
        List<BaseArea> areas = areaService.getAllAreas();
        return Result.success(areas);
    }
    
    /**
     * 分页查询区域列表
     */
    @GetMapping("/page")
    public Result<Page<BaseArea>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String areaName,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer status) {
        Page<BaseArea> page = areaService.getPage(current, size, areaName, departmentId, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询区域
     */
    @GetMapping("/{areaId}")
    public Result<BaseArea> getById(@PathVariable Long areaId) {
        BaseArea area = areaService.getById(areaId);
        return Result.success(area);
    }
    
    /**
     * 新增区域
     */
    @PostMapping
    public Result<Boolean> saveArea(@RequestBody BaseArea area) {
        try {
            boolean success = areaService.saveArea(area);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新区域
     */
    @PutMapping
    public Result<Boolean> updateArea(@RequestBody BaseArea area) {
        try {
            boolean success = areaService.updateArea(area);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除区域
     */
    @DeleteMapping("/{areaId}")
    public Result<Boolean> deleteArea(@PathVariable Long areaId) {
        try {
            boolean success = areaService.deleteArea(areaId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

