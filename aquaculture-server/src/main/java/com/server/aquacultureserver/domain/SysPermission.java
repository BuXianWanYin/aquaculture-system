package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限实体类
 */
@Data
@TableName("sys_permission")
public class SysPermission {
    
    @TableId(type = IdType.AUTO)
    private Long permissionId;
    
    private String permissionCode;
    
    private String permissionName;
    
    private String module;
    
    private Long parentId;
    
    private Integer sortOrder;
    
    private LocalDateTime createTime;
}

