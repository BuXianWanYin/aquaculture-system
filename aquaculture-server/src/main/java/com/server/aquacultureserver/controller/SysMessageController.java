package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresRole;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.SysMessage;
import com.server.aquacultureserver.service.SysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 消息通知控制器
 */
@RestController
@RequestMapping("/api/message")
@CrossOrigin
public class SysMessageController {
    
    @Autowired
    private SysMessageService messageService;
    
    /**
     * 分页查询消息列表（按接收人）
     */
    @GetMapping("/page")
    public Result<Page<SysMessage>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long receiverId,
            @RequestParam(required = false) String messageType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            Page<SysMessage> page = messageService.getPageByReceiver(current, size, receiverId, messageType, status, keyword, startTime, endTime);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID查询消息详情
     */
    @GetMapping("/{messageId}")
    public Result<SysMessage> getById(@PathVariable Long messageId) {
        try {
            SysMessage message = messageService.getById(messageId);
            return Result.success(message);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 发送消息
     */
    @PostMapping
    @RequiresRole({1}) // 系统管理员可以发送消息
    public Result<Boolean> sendMessage(@RequestBody SysMessage message) {
        try {
            boolean success = messageService.sendMessage(message);
            return Result.success("发送成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 标记消息为已读
     */
    @PutMapping("/{messageId}/read")
    public Result<Boolean> markAsRead(
            @PathVariable Long messageId,
            @RequestParam Long receiverId) {
        try {
            boolean success = messageService.markAsRead(messageId, receiverId);
            return Result.success("标记成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量标记为已读
     */
    @PutMapping("/batch/read")
    public Result<Boolean> markBatchAsRead(
            @RequestBody Long[] messageIds,
            @RequestParam Long receiverId) {
        try {
            boolean success = messageService.markBatchAsRead(messageIds, receiverId);
            return Result.success("批量标记成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 标记全部为已读
     */
    @PutMapping("/all/read")
    public Result<Boolean> markAllAsRead(@RequestParam Long receiverId) {
        try {
            boolean success = messageService.markAllAsRead(receiverId);
            return Result.success("全部标记成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public Result<Boolean> deleteMessage(
            @PathVariable Long messageId,
            @RequestParam Long receiverId) {
        try {
            boolean success = messageService.deleteMessage(messageId, receiverId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除消息
     */
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(
            @RequestBody Long[] messageIds,
            @RequestParam Long receiverId) {
        try {
            boolean success = messageService.deleteBatch(messageIds, receiverId);
            return Result.success("批量删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    public Result<Long> getUnreadCount(@RequestParam Long receiverId) {
        try {
            long count = messageService.getUnreadCount(receiverId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取未读消息列表
     */
    @GetMapping("/unread")
    public Result<Page<SysMessage>> getUnreadMessages(
            @RequestParam Long receiverId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<SysMessage> page = messageService.getUnreadMessages(receiverId, current, size);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

