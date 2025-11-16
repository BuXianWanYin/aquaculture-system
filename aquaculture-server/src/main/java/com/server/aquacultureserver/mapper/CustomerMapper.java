package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户信息Mapper接口
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}

