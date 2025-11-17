package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.BaseDepartment;
import com.server.aquacultureserver.service.BaseDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 */
@RestController
@RequestMapping("/api/department")
@CrossOrigin
public class BaseDepartmentController {
    
    @Autowired
    private BaseDepartmentService departmentService;
    
    /**
     * 查询所有部门（需要登录，根据角色过滤）
     */
    @GetMapping("/all")
    public Result<List<BaseDepartment>> getAllDepartments() {
        List<BaseDepartment> departments = departmentService.getAllDepartments();
        return Result.success(departments);
    }
    
    
    /**
     * 分页查询部门列表
     */
    @GetMapping("/page")
    @RequiresPermission({"department:view"}) // 需要部门查看权限
    public Result<Page<BaseDepartment>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) Integer status) {
        Page<BaseDepartment> page = departmentService.getPage(current, size, departmentName, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询部门
     */
    @GetMapping("/{departmentId}")
    public Result<BaseDepartment> getById(@PathVariable Long departmentId) {
        BaseDepartment department = departmentService.getById(departmentId);
        return Result.success(department);
    }
    
    /**
     * 新增部门
     */
    @PostMapping
    @RequiresPermission({"department:add"}) // 需要部门新增权限
    public Result<Boolean> saveDepartment(@RequestBody BaseDepartment department) {
        try {
            boolean success = departmentService.saveDepartment(department);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新部门
     */
    @PutMapping
    @RequiresPermission({"department:edit"}) // 需要部门编辑权限
    public Result<Boolean> updateDepartment(@RequestBody BaseDepartment department) {
        try {
            boolean success = departmentService.updateDepartment(department);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除部门
     */
    @DeleteMapping("/{departmentId}")
    @RequiresPermission({"department:delete"}) // 需要部门删除权限
    public Result<Boolean> deleteDepartment(@PathVariable Long departmentId) {
        try {
            boolean success = departmentService.deleteDepartment(departmentId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

