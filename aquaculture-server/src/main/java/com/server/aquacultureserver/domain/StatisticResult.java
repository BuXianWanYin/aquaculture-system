package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 统计结果实体类
 */
@Data
@TableName("statistic_result")
public class StatisticResult {
    
    @TableId(type = IdType.AUTO)
    private Long statisticId;
    
    private String statisticName;
    
    private String statisticDimension;
    
    private String dimensionValue;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private BigDecimal statisticValue;
    
    private String statisticUnit;
    
    private LocalDateTime calculateTime;
    
    private LocalDateTime updateTime;
}

