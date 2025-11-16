package com.server.aquacultureserver.service;

import com.server.aquacultureserver.domain.YieldEvidence;

import java.util.List;

/**
 * 产量凭证服务接口
 */
public interface YieldEvidenceService {
    
    /**
     * 根据产量ID查询凭证列表
     */
    List<YieldEvidence> getByYieldId(Long yieldId);
    
    /**
     * 新增凭证
     */
    boolean saveEvidence(YieldEvidence evidence);
    
    /**
     * 删除凭证
     */
    boolean deleteEvidence(Long evidenceId);
    
    /**
     * 根据产量ID删除所有凭证
     */
    boolean deleteByYieldId(Long yieldId);
}

