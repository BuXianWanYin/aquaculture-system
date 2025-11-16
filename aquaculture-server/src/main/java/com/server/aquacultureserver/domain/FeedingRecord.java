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
 * 投喂记录实体类
 */
@Data
@TableName("feeding_record")
public class FeedingRecord {
    
    @TableId(type = IdType.AUTO)
    private Long recordId;
    
    private Long planId;
    
    private Long areaId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate feedingDate;
    
    private String feedingTime;
    
    private String feedName;
    
    private String feedType;
    
    private BigDecimal feedingAmount;
    
    private String feedingMethod;
    
    private BigDecimal waterTemperature;
    
    private String remarks;
    
    private Integer status;
    
    private Long creatorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

