package com.server.aquacultureserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web MVC配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    /**
     * 配置静态资源访问
     * 允许访问上传的文件
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将相对路径转换为绝对路径
        String absolutePath = uploadPath;
        try {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.isAbsolute()) {
                // 如果是相对路径，转换为绝对路径
                String projectPath = System.getProperty("user.dir");
                absolutePath = new File(projectPath, uploadPath).getAbsolutePath();
            }
            
            // 确保路径以 / 或 \ 结尾（Windows使用\，Linux使用/）
            if (!absolutePath.endsWith(File.separator)) {
                absolutePath = absolutePath + File.separator;
            }
            
            // 将Windows路径分隔符统一为 /
            absolutePath = absolutePath.replace("\\", "/");
            
            // 将 /uploads/** 映射到实际的文件路径
            // 注意：当请求 /uploads/yield/xxx.jpg 时，Spring会去掉 /uploads 前缀，查找 yield/xxx.jpg
            // 所以我们需要将 /uploads/** 映射到 uploads/ 目录
            String resourceLocation = "file:" + absolutePath;
            System.out.println("静态资源路径配置: /uploads/** -> " + resourceLocation);
            
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(resourceLocation)
                    .setCachePeriod(0); // 不缓存，方便调试
        } catch (Exception e) {
            System.err.println("配置静态资源路径失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

