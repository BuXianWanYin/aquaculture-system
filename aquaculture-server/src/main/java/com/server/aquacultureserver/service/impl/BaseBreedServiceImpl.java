package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.BaseBreed;
import com.server.aquacultureserver.mapper.BaseBreedMapper;
import com.server.aquacultureserver.service.BaseBreedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 养殖品种服务实现类
 */
@Service
public class BaseBreedServiceImpl implements BaseBreedService {
    
    @Autowired
    private BaseBreedMapper breedMapper;
    
    @Override
    public List<BaseBreed> getAllBreeds() {
        return breedMapper.selectList(
            new LambdaQueryWrapper<BaseBreed>()
                .eq(BaseBreed::getStatus, 1)
                .orderByDesc(BaseBreed::getCreateTime)
        );
    }
    
    @Override
    public BaseBreed getById(Long breedId) {
        return breedMapper.selectById(breedId);
    }
    
    @Override
    public Page<BaseBreed> getPage(Integer current, Integer size, String breedName, String category) {
        Page<BaseBreed> page = new Page<>(current, size);
        LambdaQueryWrapper<BaseBreed> wrapper = new LambdaQueryWrapper<>();
        
        if (breedName != null && !breedName.isEmpty()) {
            wrapper.like(BaseBreed::getBreedName, breedName);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(BaseBreed::getCategory, category);
        }
        
        wrapper.orderByDesc(BaseBreed::getCreateTime);
        return breedMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean saveBreed(BaseBreed breed) {
        return breedMapper.insert(breed) > 0;
    }
    
    @Override
    public boolean updateBreed(BaseBreed breed) {
        return breedMapper.updateById(breed) > 0;
    }
    
    @Override
    public boolean deleteBreed(Long breedId) {
        return breedMapper.deleteById(breedId) > 0;
    }
}

