package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.aquacultureserver.domain.YieldEvidence;
import com.server.aquacultureserver.mapper.YieldEvidenceMapper;
import com.server.aquacultureserver.service.YieldEvidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产量凭证服务实现类
 */
@Service
public class YieldEvidenceServiceImpl implements YieldEvidenceService {
    
    @Autowired
    private YieldEvidenceMapper evidenceMapper;
    
    @Override
    public List<YieldEvidence> getByYieldId(Long yieldId) {
        return evidenceMapper.selectList(
            new LambdaQueryWrapper<YieldEvidence>()
                .eq(YieldEvidence::getYieldId, yieldId)
                .orderByDesc(YieldEvidence::getUploadTime)
        );
    }
    
    @Override
    public boolean saveEvidence(YieldEvidence evidence) {
        return evidenceMapper.insert(evidence) > 0;
    }
    
    @Override
    public boolean deleteEvidence(Long evidenceId) {
        return evidenceMapper.deleteById(evidenceId) > 0;
    }
    
    @Override
    public boolean deleteByYieldId(Long yieldId) {
        return evidenceMapper.delete(
            new LambdaQueryWrapper<YieldEvidence>()
                .eq(YieldEvidence::getYieldId, yieldId)
        ) > 0;
    }
}

