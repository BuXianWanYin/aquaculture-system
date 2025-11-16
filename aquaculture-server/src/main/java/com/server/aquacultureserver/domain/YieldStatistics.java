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
 * 产量统计实体类
 */
@Data
@TableName("yield_statistics")
public class YieldStatistics {
    
    @TableId(type = IdType.AUTO)
    private Long yieldId;
    
    private Long planId;
    
    private Long areaId;
    
    private Long breedId;
    
    private BigDecimal actualYield;
    
    private String specification;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate statisticsDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime catchTime;
    
    private Long managerId;
    
    private Integer status; // 0-待审核，1-已通过，2-已驳回
    
    private Long auditorId;
    
    private String auditOpinion;
    
    private LocalDateTime auditTime;
    
    private Long creatorId;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

