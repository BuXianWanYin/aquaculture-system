package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog {
    
    @TableId(type = IdType.AUTO)
    private Long logId;
    
    private Long userId;
    
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String realName;
    
    private String module;
    
    private String operType;
    
    private String operContent;
    
    private LocalDateTime operTime;
    
    private String ipAddress;
    
    private Integer operResult;
    
    private String errorMsg;
    
    private String remark;
}

