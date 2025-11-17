package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.BaseBreed;
import com.server.aquacultureserver.mapper.BaseBreedMapper;
import com.server.aquacultureserver.service.BaseBreedService;
import com.server.aquacultureserver.utils.UserContext;
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
    
    /**
     * 查询所有有效的养殖品种
     * @return 品种信息列表
     */
    @Override
    public List<BaseBreed> getAllBreeds() {
        return breedMapper.selectList(
            new LambdaQueryWrapper<BaseBreed>()
                .eq(BaseBreed::getStatus, 1)
                .orderByDesc(BaseBreed::getCreateTime)
        );
    }
    
    /**
     * 根据ID查询品种信息
     * @param breedId 品种ID
     * @return 品种信息
     */
    @Override
    public BaseBreed getById(Long breedId) {
        return breedMapper.selectById(breedId);
    }
    
    /**
     * 分页查询品种信息
     * @param current 当前页码
     * @param size 每页大小
     * @param breedName 品种名称（模糊查询）
     * @param category 品种类别（精确查询）
     * @return 分页结果
     */
    @Override
    public Page<BaseBreed> getPage(Integer current, Integer size, String breedName, String category) {
        Page<BaseBreed> page = new Page<>(current, size);
        LambdaQueryWrapper<BaseBreed> wrapper = new LambdaQueryWrapper<>();
        
        // 品种名称模糊查询
        if (breedName != null && !breedName.isEmpty()) {
            wrapper.like(BaseBreed::getBreedName, breedName);
        }
        // 品种类别精确查询
        if (category != null && !category.isEmpty()) {
            wrapper.eq(BaseBreed::getCategory, category);
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(BaseBreed::getCreateTime);
        return breedMapper.selectPage(page, wrapper);
    }
    
    /**
     * 新增品种信息
     * 普通操作员无权新增品种
     * @param breed 品种信息
     * @return 是否成功
     */
    @Override
    public boolean saveBreed(BaseBreed breed) {
        // 普通操作员不能新增品种
        if (UserContext.isOperator()) {
            throw new RuntimeException("普通操作员无权新增品种");
        }
        return breedMapper.insert(breed) > 0;
    }
    
    /**
     * 更新品种信息
     * 普通操作员无权修改品种
     * @param breed 品种信息
     * @return 是否成功
     */
    @Override
    public boolean updateBreed(BaseBreed breed) {
        // 普通操作员不能修改品种
        if (UserContext.isOperator()) {
            throw new RuntimeException("普通操作员无权修改品种");
        }
        return breedMapper.updateById(breed) > 0;
    }
    
    /**
     * 删除品种（物理删除）
     * 普通操作员无权删除品种
     * @param breedId 品种ID
     * @return 是否成功
     */
    @Override
    public boolean deleteBreed(Long breedId) {
        // 普通操作员不能删除品种
        if (UserContext.isOperator()) {
            throw new RuntimeException("普通操作员无权删除品种");
        }
        return breedMapper.deleteById(breedId) > 0;
    }
}

