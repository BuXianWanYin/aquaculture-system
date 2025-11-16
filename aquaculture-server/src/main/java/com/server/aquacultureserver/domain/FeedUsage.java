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
 * 饲料使用记录实体类
 */
@Data
@TableName("feed_usage")
public class FeedUsage {
    
    @TableId(type = IdType.AUTO)
    private Long usageId;
    
    private Long planId;
    
    private Long areaId;
    
    private String feedName;
    
    private String feedType;
    
    private BigDecimal usageAmount;
    
    private BigDecimal unitPrice;
    
    private BigDecimal totalCost;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate usageDate;
    
    private String batchNumber;
    
    private String feedingTime;
    
    private String remarks;
    
    private Integer status;
    
    private Long creatorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

