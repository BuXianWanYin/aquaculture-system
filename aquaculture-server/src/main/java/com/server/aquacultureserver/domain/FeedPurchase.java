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
 * 饲料采购实体类
 */
@Data
@TableName("feed_purchase")
public class FeedPurchase {
    
    @TableId(type = IdType.AUTO)
    private Long purchaseId;
    
    private String feedName;
    
    private String feedType;
    
    private String supplier;
    
    private BigDecimal purchaseAmount;
    
    private BigDecimal unitPrice;
    
    private BigDecimal totalPrice;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;
    
    private String batchNumber;
    
    private String imageUrl;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    
    private Integer status;
    
    private Long creatorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

