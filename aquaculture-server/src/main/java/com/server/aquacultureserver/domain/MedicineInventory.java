package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 药品库存实体类
 */
@Data
@TableName("medicine_inventory")
public class MedicineInventory {
    
    @TableId(type = IdType.AUTO)
    private Long inventoryId;
    
    private String medicineName;
    
    private String medicineType;
    
    private BigDecimal currentStock;
    
    private String unit;
    
    private BigDecimal unitPrice;
    
    private Integer status;
    
    private Long creatorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

