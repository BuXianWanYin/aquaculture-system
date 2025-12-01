package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 病害防治记录实体类
 */
@Data
@TableName("disease_prevention")
public class DiseasePrevention {
    
    @TableId(type = IdType.AUTO)
    private Long preventionId;
    
    private Long recordId;
    
    private Long planId;
    
    private Long areaId;
    
    private String preventionMethod;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate preventionDate;
    
    private String preventionResult;
    
    private String effectDescription;
    
    private String operator;
    
    private String remarks;
    
    private String imageUrl;
    
    private Integer status;
    
    private Long creatorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

