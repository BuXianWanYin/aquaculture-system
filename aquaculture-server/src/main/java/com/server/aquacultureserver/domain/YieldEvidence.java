package com.server.aquacultureserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 产量凭证实体类
 */
@Data
@TableName("yield_evidence")
public class YieldEvidence {
    
    @TableId(type = IdType.AUTO)
    private Long evidenceId;
    
    private Long yieldId;
    
    private String filePath;
    
    private String fileType;
    
    private Long fileSize;
    
    private LocalDateTime uploadTime;
    
    private Long uploaderId;
}

