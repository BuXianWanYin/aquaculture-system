package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 养殖品种实体类
 */
@Data
@TableName("base_breed")
public class BaseBreed {
    
    @TableId(type = IdType.AUTO)
    private Long breedId;
    
    private String breedName;
    
    private String category;
    
    private Integer growthCycle;
    
    private String unit;
    
    private BigDecimal suitableTempMin;
    
    private BigDecimal suitableTempMax;
    
    private String description;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

