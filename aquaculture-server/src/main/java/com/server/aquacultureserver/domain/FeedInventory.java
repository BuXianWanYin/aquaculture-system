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
 * 饲料库存实体类
 */
@Data
@TableName("feed_inventory")
public class FeedInventory {
    
    @TableId(type = IdType.AUTO)
    private Long inventoryId;
    
    private String feedName;
    
    private String feedType;
    
    private BigDecimal currentStock;
    
    private BigDecimal unitPrice;
    
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

