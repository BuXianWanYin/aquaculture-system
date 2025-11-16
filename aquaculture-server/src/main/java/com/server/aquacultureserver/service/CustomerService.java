package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.Customer;

import java.util.List;

/**
 * 客户信息服务接口
 */
public interface CustomerService {
    
    /**
     * 查询所有客户
     */
    List<Customer> getAllCustomers();
    
    /**
     * 根据ID查询客户
     */
    Customer getById(Long customerId);
    
    /**
     * 分页查询客户列表
     */
    Page<Customer> getPage(Integer current, Integer size, String customerName, String customerType, Integer status);
    
    /**
     * 新增客户
     */
    boolean saveCustomer(Customer customer);
    
    /**
     * 更新客户信息
     */
    boolean updateCustomer(Customer customer);
    
    /**
     * 删除客户
     */
    boolean deleteCustomer(Long customerId);
    
    /**
     * 统计客户总数
     */
    long count();
}

