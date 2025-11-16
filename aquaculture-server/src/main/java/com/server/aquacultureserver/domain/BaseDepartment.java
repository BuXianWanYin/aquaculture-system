package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门实体类
 */
@Data
@TableName("base_department")
public class BaseDepartment {
    
    @TableId(type = IdType.AUTO)
    private Long departmentId;
    
    private String departmentCode;
    
    private String departmentName;
    
    private String description;
    
    private Integer status;
    
    private Long managerId;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

