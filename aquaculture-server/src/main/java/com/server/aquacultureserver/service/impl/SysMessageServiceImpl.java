package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysMessage;
import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.mapper.SysMessageMapper;
import com.server.aquacultureserver.mapper.SysUserMapper;
import com.server.aquacultureserver.service.SysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 消息服务实现类
 */
@Service
public class SysMessageServiceImpl implements SysMessageService {
    
    @Autowired
    private SysMessageMapper messageMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Override
    public Page<SysMessage> getPageByReceiver(Integer current, Integer size, 
                                               Long receiverId, 
                                               String messageType, 
                                               Integer status,
                                               String keyword,
                                               LocalDateTime startTime, 
                                               LocalDateTime endTime) {
        Page<SysMessage> page = new Page<>(current, size);
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        
        // 必须指定接收人
        if (receiverId == null) {
            throw new IllegalArgumentException("接收人ID不能为空");
        }
        wrapper.eq(SysMessage::getReceiverId, receiverId);
        
        if (messageType != null && !messageType.isEmpty()) {
            wrapper.eq(SysMessage::getMessageType, messageType);
        }
        if (status != null) {
            wrapper.eq(SysMessage::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysMessage::getMessageTitle, keyword)
                    .or().like(SysMessage::getMessageContent, keyword));
        }
        if (startTime != null) {
            wrapper.ge(SysMessage::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(SysMessage::getCreateTime, endTime);
        }
        
        wrapper.orderByDesc(SysMessage::getCreateTime);
        Page<SysMessage> result = messageMapper.selectPage(page, wrapper);
        
        // 填充发送人和接收人信息
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            for (SysMessage message : result.getRecords()) {
                // 填充发送人信息
                if (message.getSenderId() != null && message.getSenderId() > 0) {
                    SysUser sender = userMapper.selectById(message.getSenderId());
                    if (sender != null) {
                        message.setSenderName(sender.getRealName() != null ? sender.getRealName() : sender.getUsername());
                    }
                } else {
                    message.setSenderName("系统");
                }
                // 填充接收人信息
                if (message.getReceiverId() != null) {
                    SysUser receiver = userMapper.selectById(message.getReceiverId());
                    if (receiver != null) {
                        message.setReceiverName(receiver.getRealName() != null ? receiver.getRealName() : receiver.getUsername());
                    }
                }
            }
        }
        
        return result;
    }
    
    @Override
    public SysMessage getById(Long messageId) {
        SysMessage message = messageMapper.selectById(messageId);
        if (message != null) {
            // 填充发送人信息
            if (message.getSenderId() != null && message.getSenderId() > 0) {
                SysUser sender = userMapper.selectById(message.getSenderId());
                if (sender != null) {
                    message.setSenderName(sender.getRealName() != null ? sender.getRealName() : sender.getUsername());
                }
            } else {
                message.setSenderName("系统");
            }
            // 填充接收人信息
            if (message.getReceiverId() != null) {
                SysUser receiver = userMapper.selectById(message.getReceiverId());
                if (receiver != null) {
                    message.setReceiverName(receiver.getRealName() != null ? receiver.getRealName() : receiver.getUsername());
                }
            }
        }
        return message;
    }
    
    @Override
    public boolean sendMessage(SysMessage message) {
        if (message.getCreateTime() == null) {
            message.setCreateTime(LocalDateTime.now());
        }
        if (message.getStatus() == null) {
            message.setStatus(0); // 默认未读
        }
        return messageMapper.insert(message) > 0;
    }
    
    @Override
    public boolean markAsRead(Long messageId, Long receiverId) {
        SysMessage message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("消息不存在");
        }
        if (!message.getReceiverId().equals(receiverId)) {
            throw new IllegalArgumentException("无权操作此消息");
        }
        
        LambdaUpdateWrapper<SysMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysMessage::getMessageId, messageId)
                .eq(SysMessage::getReceiverId, receiverId)
                .set(SysMessage::getStatus, 1)
                .set(SysMessage::getReadTime, LocalDateTime.now());
        
        return messageMapper.update(null, wrapper) > 0;
    }
    
    @Override
    public boolean markBatchAsRead(Long[] messageIds, Long receiverId) {
        for (Long messageId : messageIds) {
            markAsRead(messageId, receiverId);
        }
        return true;
    }
    
    @Override
    public boolean markAllAsRead(Long receiverId) {
        LambdaUpdateWrapper<SysMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysMessage::getReceiverId, receiverId)
                .eq(SysMessage::getStatus, 0)
                .set(SysMessage::getStatus, 1)
                .set(SysMessage::getReadTime, LocalDateTime.now());
        
        return messageMapper.update(null, wrapper) > 0;
    }
    
    @Override
    public boolean deleteMessage(Long messageId, Long receiverId) {
        SysMessage message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("消息不存在");
        }
        if (!message.getReceiverId().equals(receiverId)) {
            throw new IllegalArgumentException("无权删除此消息");
        }
        return messageMapper.deleteById(messageId) > 0;
    }
    
    @Override
    public boolean deleteBatch(Long[] messageIds, Long receiverId) {
        for (Long messageId : messageIds) {
            deleteMessage(messageId, receiverId);
        }
        return true;
    }
    
    @Override
    public long getUnreadCount(Long receiverId) {
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getReceiverId, receiverId)
                .eq(SysMessage::getStatus, 0);
        return messageMapper.selectCount(wrapper);
    }
    
    @Override
    public Page<SysMessage> getUnreadMessages(Long receiverId, Integer current, Integer size) {
        Page<SysMessage> page = new Page<>(current, size);
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getReceiverId, receiverId)
                .eq(SysMessage::getStatus, 0)
                .orderByDesc(SysMessage::getCreateTime);
        
        Page<SysMessage> result = messageMapper.selectPage(page, wrapper);
        
        // 填充发送人信息
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            for (SysMessage message : result.getRecords()) {
                if (message.getSenderId() != null && message.getSenderId() > 0) {
                    SysUser sender = userMapper.selectById(message.getSenderId());
                    if (sender != null) {
                        message.setSenderName(sender.getRealName() != null ? sender.getRealName() : sender.getUsername());
                    }
                } else {
                    message.setSenderName("系统");
                }
            }
        }
        
        return result;
    }
}

