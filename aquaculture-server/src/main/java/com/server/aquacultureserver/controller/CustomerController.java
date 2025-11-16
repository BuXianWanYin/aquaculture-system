package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.Customer;
import com.server.aquacultureserver.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户信息控制器
 */
@RestController
@RequestMapping("/api/sales/customer")
@CrossOrigin
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    /**
     * 查询所有客户
     */
    @GetMapping("/all")
    public Result<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return Result.success(customers);
    }
    
    /**
     * 分页查询客户列表
     */
    @GetMapping("/page")
    @RequiresPermission({"sales:customer:list"})
    public Result<Page<Customer>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) Integer status) {
        Page<Customer> page = customerService.getPage(current, size, customerName, customerType, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询客户
     */
    @GetMapping("/{customerId}")
    @RequiresPermission({"sales:customer:detail"})
    public Result<Customer> getById(@PathVariable Long customerId) {
        Customer customer = customerService.getById(customerId);
        return Result.success(customer);
    }
    
    /**
     * 新增客户
     */
    @PostMapping
    @RequiresPermission({"sales:customer:add"})
    public Result<Boolean> saveCustomer(@RequestBody Customer customer) {
        try {
            boolean success = customerService.saveCustomer(customer);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新客户信息
     */
    @PutMapping
    @RequiresPermission({"sales:customer:edit"})
    public Result<Boolean> updateCustomer(@RequestBody Customer customer) {
        try {
            boolean success = customerService.updateCustomer(customer);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除客户
     */
    @DeleteMapping("/{customerId}")
    @RequiresPermission({"sales:customer:delete"})
    public Result<Boolean> deleteCustomer(@PathVariable Long customerId) {
        try {
            boolean success = customerService.deleteCustomer(customerId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

