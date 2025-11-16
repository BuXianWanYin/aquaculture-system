package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户信息实体类
 */
@Data
@TableName("customer")
public class Customer {
    
    @TableId(type = IdType.AUTO)
    private Long customerId;
    
    private String customerName;
    
    private String contactPerson;
    
    private String contactPhone;
    
    private String contactAddress;
    
    private String customerType;
    
    private String creditRating;
    
    private Integer status;
    
    private Long creatorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

