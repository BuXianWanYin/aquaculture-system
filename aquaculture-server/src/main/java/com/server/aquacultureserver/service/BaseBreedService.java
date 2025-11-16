package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.BaseBreed;

import java.util.List;

/**
 * 养殖品种服务接口
 */
public interface BaseBreedService {
    
    /**
     * 查询所有品种
     */
    List<BaseBreed> getAllBreeds();
    
    /**
     * 根据ID查询品种
     */
    BaseBreed getById(Long breedId);
    
    /**
     * 分页查询品种列表
     */
    Page<BaseBreed> getPage(Integer current, Integer size, String breedName, String category);
    
    /**
     * 新增品种
     */
    boolean saveBreed(BaseBreed breed);
    
    /**
     * 更新品种
     */
    boolean updateBreed(BaseBreed breed);
    
    /**
     * 删除品种
     */
    boolean deleteBreed(Long breedId);
}

