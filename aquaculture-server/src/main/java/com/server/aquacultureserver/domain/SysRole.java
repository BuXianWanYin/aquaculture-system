package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体类
 */
@Data
@TableName("sys_role")
public class SysRole {
    
    @TableId(type = IdType.AUTO)
    private Long roleId;
    
    private String roleName;
    
    private String roleDesc;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

