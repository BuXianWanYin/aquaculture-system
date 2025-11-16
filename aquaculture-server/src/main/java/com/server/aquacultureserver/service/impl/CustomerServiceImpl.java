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
    
    @Override
    public List<Customer> getAllCustomers() {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getStatus, 1);
        wrapper.orderByDesc(Customer::getCreateTime);
        return customerMapper.selectList(wrapper);
    }
    
    @Override
    public Customer getById(Long customerId) {
        return customerMapper.selectById(customerId);
    }
    
    @Override
    public Page<Customer> getPage(Integer current, Integer size, String customerName, String customerType, Integer status) {
        Page<Customer> page = new Page<>(current, size);
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        
        if (customerName != null && !customerName.isEmpty()) {
            wrapper.like(Customer::getCustomerName, customerName);
        }
        if (customerType != null && !customerType.isEmpty()) {
            wrapper.eq(Customer::getCustomerType, customerType);
        }
        if (status != null) {
            wrapper.eq(Customer::getStatus, status);
        } else {
            wrapper.eq(Customer::getStatus, 1);
        }
        
        wrapper.orderByDesc(Customer::getCreateTime);
        return customerMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean saveCustomer(Customer customer) {
        // 新记录默认状态为正常
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
    
    @Override
    public boolean updateCustomer(Customer customer) {
        return customerMapper.updateById(customer) > 0;
    }
    
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
    
    @Override
    public long count() {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getStatus, 1);
        return customerMapper.selectCount(wrapper);
    }
}

