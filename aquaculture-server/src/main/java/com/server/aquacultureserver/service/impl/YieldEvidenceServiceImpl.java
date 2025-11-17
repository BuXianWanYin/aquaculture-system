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
    
    /**
     * 根据产量统计ID查询产量凭证列表
     * @param yieldId 产量统计ID
     * @return 产量凭证列表（按上传时间倒序）
     */
    @Override
    public List<YieldEvidence> getByYieldId(Long yieldId) {
        return evidenceMapper.selectList(
            new LambdaQueryWrapper<YieldEvidence>()
                .eq(YieldEvidence::getYieldId, yieldId)
                .orderByDesc(YieldEvidence::getUploadTime)
        );
    }
    
    /**
     * 新增产量凭证
     * @param evidence 产量凭证
     * @return 是否成功
     */
    @Override
    public boolean saveEvidence(YieldEvidence evidence) {
        return evidenceMapper.insert(evidence) > 0;
    }
    
    /**
     * 删除产量凭证（物理删除）
     * @param evidenceId 凭证ID
     * @return 是否成功
     */
    @Override
    public boolean deleteEvidence(Long evidenceId) {
        return evidenceMapper.deleteById(evidenceId) > 0;
    }
    
    /**
     * 根据产量统计ID删除所有相关凭证（物理删除）
     * @param yieldId 产量统计ID
     * @return 是否成功
     */
    @Override
    public boolean deleteByYieldId(Long yieldId) {
        return evidenceMapper.delete(
            new LambdaQueryWrapper<YieldEvidence>()
                .eq(YieldEvidence::getYieldId, yieldId)
        ) > 0;
    }
}

