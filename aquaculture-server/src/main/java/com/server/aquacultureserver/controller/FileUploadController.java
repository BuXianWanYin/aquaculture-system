package com.server.aquacultureserver.controller;

import com.server.aquacultureserver.annotation.RequiresRole;
import com.server.aquacultureserver.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class FileUploadController {
    
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;
    
    /**
     * 上传产量凭证文件
     */
    @PostMapping("/yieldEvidence")
    @RequiresRole({1, 2, 3}) // 系统管理员、部门管理员、普通操作员
    public Result<Map<String, Object>> uploadYieldEvidence(
            @RequestParam("file") MultipartFile file,
            @RequestParam("yieldId") Long yieldId) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 获取文件原始名称
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return Result.error("文件名不能为空");
            }
            
            // 获取文件扩展名
            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex);
            }
            
            // 生成新的文件名（UUID + 扩展名）
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // 按日期创建目录结构：uploads/yield/YYYY/MM/
            LocalDate now = LocalDate.now();
            String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String directory = uploadPath + "/yield/" + datePath;
            
            // 创建目录
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 保存文件
            String filePath = directory + "/" + newFilename;
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());
            
            // 返回文件信息
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("filePath", "/uploads/yield/" + datePath + "/" + newFilename);
            fileInfo.put("fileType", file.getContentType());
            fileInfo.put("fileSize", file.getSize());
            fileInfo.put("originalFilename", originalFilename);
            fileInfo.put("yieldId", yieldId);
            
            return Result.success("上传成功", fileInfo);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}

