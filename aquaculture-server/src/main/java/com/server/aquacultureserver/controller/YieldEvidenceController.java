package com.server.aquacultureserver.controller;

import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.YieldEvidence;
import com.server.aquacultureserver.service.YieldEvidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产量凭证控制器
 */
@RestController
@RequestMapping("/api/yieldEvidence")
@CrossOrigin
public class YieldEvidenceController {
    
    @Autowired
    private YieldEvidenceService evidenceService;
    
    /**
     * 根据产量ID查询凭证列表
     */
    @GetMapping("/yield/{yieldId}")
    public Result<List<YieldEvidence>> getByYieldId(@PathVariable Long yieldId) {
        List<YieldEvidence> evidenceList = evidenceService.getByYieldId(yieldId);
        return Result.success(evidenceList);
    }
    
    /**
     * 新增凭证
     */
    @PostMapping
    @RequiresPermission({"yield:evidence:add"}) // 需要产量凭证新增权限
    public Result<Boolean> saveEvidence(@RequestBody YieldEvidence evidence) {
        try {
            boolean success = evidenceService.saveEvidence(evidence);
            return Result.success("上传成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除凭证
     */
    @DeleteMapping("/{evidenceId}")
    @RequiresPermission({"yield:evidence:delete"}) // 需要产量凭证删除权限
    public Result<Boolean> deleteEvidence(@PathVariable Long evidenceId) {
        try {
            boolean success = evidenceService.deleteEvidence(evidenceId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

