package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Data
@TableName("sys_message")
public class SysMessage {
    
    @TableId(type = IdType.AUTO)
    private Long messageId;
    
    private Long receiverId;
    
    @TableField(exist = false)
    private String receiverName;
    
    private Long senderId;
    
    @TableField(exist = false)
    private String senderName;
    
    private String messageTitle;
    
    private String messageContent;
    
    private String messageType; // 通知/提醒/公告
    
    private String businessType; // 关联业务类型
    
    private Long businessId; // 关联业务ID
    
    private Integer status; // 0-未读，1-已读
    
    private LocalDateTime createTime;
    
    private LocalDateTime readTime;
}

