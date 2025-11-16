package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    
    /**
     * 饲料已使用金额（临时字段，不存储到数据库）
     */
    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal feedUsedAmount;
}

