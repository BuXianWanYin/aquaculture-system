package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 计划调整实体类
 */
@Data
@TableName("plan_adjust")
public class PlanAdjust {
    
    @TableId(type = IdType.AUTO)
    private Long adjustId;
    
    private Long planId;
    
    private String adjustReason;
    
    private String oldParams;
    
    private String newParams;
    
    private String adjustType;
    
    private Integer approveStatus;
    
    private Long approverId;
    
    private String approveOpinion;
    
    private LocalDateTime approveTime;
    
    private Long creatorId;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

