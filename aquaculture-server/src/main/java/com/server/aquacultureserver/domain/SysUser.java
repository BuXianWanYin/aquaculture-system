package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("sys_user")
public class SysUser {
    
    @TableId(type = IdType.AUTO)
    private Long userId;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private Long roleId;
    
    @TableField(exist = false)
    private String roleName;
    
    private Long areaId;
    
    private Long departmentId; // 所属部门ID（部门管理员使用）
    
    private String phone;
    
    private String address;
    
    private String avatar;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

