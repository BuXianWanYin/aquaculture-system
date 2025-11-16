package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysMessage;

import java.time.LocalDateTime;

/**
 * 消息服务接口
 */
public interface SysMessageService {
    
    /**
     * 分页查询消息列表（按接收人）
     */
    Page<SysMessage> getPageByReceiver(Integer current, Integer size, 
                                      Long receiverId, 
                                      String messageType, 
                                      Integer status,
                                      String keyword,
                                      LocalDateTime startTime, 
                                      LocalDateTime endTime);
    
    /**
     * 根据ID查询消息详情
     */
    SysMessage getById(Long messageId);
    
    /**
     * 发送消息
     */
    boolean sendMessage(SysMessage message);
    
    /**
     * 标记消息为已读
     */
    boolean markAsRead(Long messageId, Long receiverId);
    
    /**
     * 批量标记为已读
     */
    boolean markBatchAsRead(Long[] messageIds, Long receiverId);
    
    /**
     * 标记全部为已读
     */
    boolean markAllAsRead(Long receiverId);
    
    /**
     * 删除消息
     */
    boolean deleteMessage(Long messageId, Long receiverId);
    
    /**
     * 批量删除消息
     */
    boolean deleteBatch(Long[] messageIds, Long receiverId);
    
    /**
     * 获取未读消息数量
     */
    long getUnreadCount(Long receiverId);
    
    /**
     * 获取未读消息列表
     */
    Page<SysMessage> getUnreadMessages(Long receiverId, Integer current, Integer size);
}

