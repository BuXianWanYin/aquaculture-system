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
    
    /**
     * 分页查询接收人的消息列表
     * 查询结果会自动填充发送人和接收人信息
     * @param current 当前页码
     * @param size 每页大小
     * @param receiverId 接收人ID（必填）
     * @param messageType 消息类型
     * @param status 状态（0-未读，1-已读）
     * @param keyword 关键词（模糊查询标题或内容）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果（包含发送人和接收人信息）
     */
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
    
    /**
     * 根据ID查询消息
     * 查询结果会自动填充发送人和接收人信息
     * @param messageId 消息ID
     * @return 消息（包含发送人和接收人信息）
     */
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
    
    /**
     * 发送消息
     * @param message 消息
     * @return 是否成功
     */
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
    
    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @param receiverId 接收人ID
     * @return 是否成功
     */
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
    
    /**
     * 批量标记消息为已读
     * @param messageIds 消息ID数组
     * @param receiverId 接收人ID
     * @return 是否成功
     */
    @Override
    public boolean markBatchAsRead(Long[] messageIds, Long receiverId) {
        for (Long messageId : messageIds) {
            markAsRead(messageId, receiverId);
        }
        return true;
    }
    
    /**
     * 标记所有消息为已读
     * @param receiverId 接收人ID
     * @return 是否成功
     */
    @Override
    public boolean markAllAsRead(Long receiverId) {
        LambdaUpdateWrapper<SysMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysMessage::getReceiverId, receiverId)
                .eq(SysMessage::getStatus, 0)
                .set(SysMessage::getStatus, 1)
                .set(SysMessage::getReadTime, LocalDateTime.now());
        
        return messageMapper.update(null, wrapper) > 0;
    }
    
    /**
     * 删除消息（物理删除）
     * 只有接收人可以删除自己的消息
     * @param messageId 消息ID
     * @param receiverId 接收人ID
     * @return 是否成功
     */
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
    
    /**
     * 批量删除消息（物理删除）
     * @param messageIds 消息ID数组
     * @param receiverId 接收人ID
     * @return 是否成功
     */
    @Override
    public boolean deleteBatch(Long[] messageIds, Long receiverId) {
        for (Long messageId : messageIds) {
            deleteMessage(messageId, receiverId);
        }
        return true;
    }
    
    /**
     * 获取未读消息数量
     * @param receiverId 接收人ID
     * @return 未读消息数量
     */
    @Override
    public long getUnreadCount(Long receiverId) {
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getReceiverId, receiverId)
                .eq(SysMessage::getStatus, 0);
        return messageMapper.selectCount(wrapper);
    }
    
    /**
     * 分页查询未读消息
     * 查询结果会自动填充发送人信息
     * @param receiverId 接收人ID
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页结果（包含发送人信息）
     */
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

