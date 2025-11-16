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
 * 用药记录实体类
 */
@Data
@TableName("medicine_usage")
public class MedicineUsage {
    
    @TableId(type = IdType.AUTO)
    private Long usageId;
    
    private Long recordId;
    
    private Long preventionId;
    
    private Long planId;
    
    private Long areaId;
    
    private String medicineName;
    
    private String medicineType;
    
    private BigDecimal dosage;
    
    private String unit;
    
    private BigDecimal unitPrice;
    
    private BigDecimal totalCost;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate usageDate;
    
    private String usageMethod;
    
    private String batchNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    
    private String remarks;
    
    private Integer status;
    
    private Long creatorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

