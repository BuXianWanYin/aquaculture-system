package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售记录实体类
 */
@Data
@TableName("sales_record")
public class SalesRecord {
    
    @TableId(type = IdType.AUTO)
    private Long salesId;
    
    private Long yieldId;
    
    private Long planId;
    
    private Long areaId;
    
    private Long breedId;
    
    private Long customerId;
    
    private BigDecimal salesQuantity;
    
    private BigDecimal unitPrice;
    
    private BigDecimal totalAmount;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate salesDate;
    
    private String salesChannel;
    
    private String paymentMethod;
    
    private Integer paymentStatus;
    
    private Integer invoiceStatus;
    
    private String remarks;
    
    private Integer status;
    
    private Long creatorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

