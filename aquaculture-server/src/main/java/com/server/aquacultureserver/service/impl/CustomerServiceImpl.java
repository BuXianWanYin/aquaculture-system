package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.Customer;
import com.server.aquacultureserver.mapper.CustomerMapper;
import com.server.aquacultureserver.service.CustomerService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户信息服务实现类
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerMapper customerMapper;
    
    /**
     * 查询所有有效的客户信息
     * @return 客户信息列表
     */
    @Override
    public List<Customer> getAllCustomers() {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        // 只查询状态为正常（1）的记录
        wrapper.eq(Customer::getStatus, 1);
        // 按创建时间倒序排列
        wrapper.orderByDesc(Customer::getCreateTime);
        return customerMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID查询客户信息
     * @param customerId 客户ID
     * @return 客户信息
     */
    @Override
    public Customer getById(Long customerId) {
        return customerMapper.selectById(customerId);
    }
    
    /**
     * 分页查询客户信息
     * @param current 当前页码
     * @param size 每页大小
     * @param customerName 客户名称（模糊查询）
     * @param customerType 客户类型（精确查询）
     * @param status 状态（1-正常，0-已删除）
     * @return 分页结果
     */
    @Override
    public Page<Customer> getPage(Integer current, Integer size, String customerName, String customerType, Integer status) {
        Page<Customer> page = new Page<>(current, size);
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        
        // 客户名称模糊查询
        if (customerName != null && !customerName.isEmpty()) {
            wrapper.like(Customer::getCustomerName, customerName);
        }
        // 客户类型精确查询
        if (customerType != null && !customerType.isEmpty()) {
            wrapper.eq(Customer::getCustomerType, customerType);
        }
        // 状态查询，如果未指定则默认查询正常状态
        if (status != null) {
            wrapper.eq(Customer::getStatus, status);
        } else {
            wrapper.eq(Customer::getStatus, 1);
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(Customer::getCreateTime);
        return customerMapper.selectPage(page, wrapper);
    }
    
    /**
     * 新增客户信息
     * @param customer 客户信息
     * @return 是否成功
     */
    @Override
    public boolean saveCustomer(Customer customer) {
        // 新记录默认状态为正常（1）
        if (customer.getStatus() == null) {
            customer.setStatus(1);
        }
        
        // 设置创建时间和创建人
        if (customer.getCreateTime() == null) {
            customer.setCreateTime(LocalDateTime.now());
        }
        if (customer.getCreatorId() == null) {
            customer.setCreatorId(UserContext.getCurrentUserId());
        }
        
        return customerMapper.insert(customer) > 0;
    }
    
    /**
     * 更新客户信息
     * @param customer 客户信息
     * @return 是否成功
     */
    @Override
    public boolean updateCustomer(Customer customer) {
        return customerMapper.updateById(customer) > 0;
    }
    
    /**
     * 删除客户信息（软删除）
     * @param customerId 客户ID
     * @return 是否成功
     */
    @Override
    public boolean deleteCustomer(Long customerId) {
        // 软删除，将状态设置为0
        Customer customer = getById(customerId);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }
        customer.setStatus(0);
        return customerMapper.updateById(customer) > 0;
    }
    
    /**
     * 统计有效的客户数量
     * @return 客户数量
     */
    @Override
    public long count() {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getStatus, 1);
        return customerMapper.selectCount(wrapper);
    }
}

