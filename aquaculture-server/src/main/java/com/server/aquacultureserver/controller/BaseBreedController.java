package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.BaseBreed;
import com.server.aquacultureserver.service.BaseBreedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 养殖品种控制器
 */
@RestController
@RequestMapping("/api/breed")
@CrossOrigin
public class BaseBreedController {
    
    @Autowired
    private BaseBreedService breedService;
    
    /**
     * 查询所有品种
     */
    @GetMapping("/all")
    public Result<List<BaseBreed>> getAllBreeds() {
        List<BaseBreed> breeds = breedService.getAllBreeds();
        return Result.success(breeds);
    }
    
    /**
     * 分页查询品种列表
     */
    @GetMapping("/page")
    public Result<Page<BaseBreed>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String breedName,
            @RequestParam(required = false) String category) {
        Page<BaseBreed> page = breedService.getPage(current, size, breedName, category);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询品种
     */
    @GetMapping("/{breedId}")
    public Result<BaseBreed> getById(@PathVariable Long breedId) {
        BaseBreed breed = breedService.getById(breedId);
        return Result.success(breed);
    }
    
    /**
     * 新增品种
     */
    @PostMapping
    public Result<Boolean> saveBreed(@RequestBody BaseBreed breed) {
        try {
            boolean success = breedService.saveBreed(breed);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新品种
     */
    @PutMapping
    public Result<Boolean> updateBreed(@RequestBody BaseBreed breed) {
        try {
            boolean success = breedService.updateBreed(breed);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除品种
     */
    @DeleteMapping("/{breedId}")
    public Result<Boolean> deleteBreed(@PathVariable Long breedId) {
        try {
            boolean success = breedService.deleteBreed(breedId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

