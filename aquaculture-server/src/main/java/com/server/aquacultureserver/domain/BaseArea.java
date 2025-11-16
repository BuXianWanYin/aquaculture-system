package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 养殖区域实体类
 */
@Data
@TableName("base_area")
public class BaseArea {
    
    @TableId(type = IdType.AUTO)
    private Long areaId;
    
    private String areaCode;
    
    private String areaName;
    
    private BigDecimal areaSize;
    
    private Long departmentId;
    
    private String location;
    
    private String waterQuality;
    
    private Integer status;
    
    private Long managerId;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

