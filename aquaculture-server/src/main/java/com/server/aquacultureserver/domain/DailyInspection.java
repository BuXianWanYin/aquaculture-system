package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日常检查记录实体类
 */
@Data
@TableName("daily_inspection")
public class DailyInspection {
    
    @TableId(type = IdType.AUTO)
    private Long inspectionId;
    
    private Long planId;
    
    private Long areaId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate inspectionDate;
    
    private String inspectionTime;
    
    private String inspectionType;
    
    private String inspectionItem;
    
    private String inspectionResult;
    
    private String problemDescription;
    
    private String handlingMethod;
    
    private String handler;
    
    private Integer status;
    
    private Long creatorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

