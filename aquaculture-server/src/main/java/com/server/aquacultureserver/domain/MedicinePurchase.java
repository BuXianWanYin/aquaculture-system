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
 * 药品采购实体类
 */
@Data
@TableName("medicine_purchase")
public class MedicinePurchase {
    
    @TableId(type = IdType.AUTO)
    private Long purchaseId;
    
    private String medicineName;
    
    private String medicineType;
    
    private String supplier;
    
    private BigDecimal purchaseAmount;
    
    private String unit;
    
    private BigDecimal unitPrice;
    
    private BigDecimal totalPrice;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;
    
    private String batchNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    
    private Integer status;
    
    private Long creatorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

