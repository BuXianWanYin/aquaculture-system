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
 * 病害记录实体类
 */
@Data
@TableName("disease_record")
public class DiseaseRecord {
    
    @TableId(type = IdType.AUTO)
    private Long recordId;
    
    private Long planId;
    
    private Long areaId;
    
    private Long breedId;
    
    private String diseaseName;
    
    private String diseaseType;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate occurrenceDate;
    
    private BigDecimal affectedArea;
    
    private BigDecimal affectedQuantity;
    
    private String severityLevel;
    
    private String symptoms;
    
    private Integer status;
    
    private Long creatorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

