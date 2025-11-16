package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备信息实体类
 */
@Data
@TableName("base_equipment")
public class BaseEquipment {
    
    @TableId(type = IdType.AUTO)
    private Long equipmentId;
    
    private String equipmentName;
    
    private String equipmentModel;
    
    private String equipmentType;
    
    private Long areaId;
    
    private String installLocation;
    
    private Integer quantity;
    
    private LocalDateTime lastMaintainTime;
    
    private String maintainRecord;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

