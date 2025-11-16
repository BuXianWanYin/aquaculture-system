package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 养殖计划实体类
 */
@Data
@TableName("aquaculture_plan")
public class AquaculturePlan {
    
    @TableId(type = IdType.AUTO)
    private Long planId;
    
    private String planName;
    
    private Long areaId;
    
    private Long breedId;
    
    private BigDecimal targetYield;
    
    private BigDecimal releaseAmount;
    
    private Integer cycleDays;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private BigDecimal feedBudget;
    
    private String preventionPlan;
    
    private Integer status;
    
    private Long creatorId;
    
    private Long approverId;
    
    private String approveOpinion;
    
    private LocalDateTime approveTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

