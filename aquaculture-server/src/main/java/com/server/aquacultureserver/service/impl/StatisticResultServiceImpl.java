package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.AquaculturePlan;
import com.server.aquacultureserver.domain.BaseArea;
import com.server.aquacultureserver.domain.StatisticResult;
import com.server.aquacultureserver.domain.YieldStatistics;
import com.server.aquacultureserver.mapper.AquaculturePlanMapper;
import com.server.aquacultureserver.mapper.BaseAreaMapper;
import com.server.aquacultureserver.mapper.StatisticResultMapper;
import com.server.aquacultureserver.mapper.YieldStatisticsMapper;
import com.server.aquacultureserver.service.StatisticResultService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 统计结果服务实现类
 * 负责统计数据的查询、保存、更新和删除，以及各种统计报表的生成
 */
@Service
public class StatisticResultServiceImpl implements StatisticResultService {
    
    @Autowired
    private StatisticResultMapper statisticMapper;
    
    @Autowired
    private YieldStatisticsMapper yieldStatisticsMapper;
    
    @Autowired
    private AquaculturePlanMapper planMapper;
    
    @Autowired
    private BaseAreaMapper areaMapper;
    
    /**
     * 分页查询统计结果
     * @param current 当前页码
     * @param size 每页大小
     * @param statisticName 统计名称（模糊查询）
     * @param statisticDimension 统计维度（精确查询）
     * @param dimensionValue 维度值（精确查询）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页结果（按计算时间降序）
     */
    @Override
    public Page<StatisticResult> getPage(Integer current, Integer size, 
                                         String statisticName, String statisticDimension,
                                         String dimensionValue, LocalDate startDate, LocalDate endDate) {
        Page<StatisticResult> page = new Page<>(current, size);
        LambdaQueryWrapper<StatisticResult> wrapper = new LambdaQueryWrapper<>();
        
        if (statisticName != null && !statisticName.isEmpty()) {
            wrapper.like(StatisticResult::getStatisticName, statisticName);
        }
        if (statisticDimension != null && !statisticDimension.isEmpty()) {
            wrapper.eq(StatisticResult::getStatisticDimension, statisticDimension);
        }
        if (dimensionValue != null && !dimensionValue.isEmpty()) {
            wrapper.eq(StatisticResult::getDimensionValue, dimensionValue);
        }
        if (startDate != null) {
            wrapper.ge(StatisticResult::getStartDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(StatisticResult::getEndDate, endDate);
        }
        
        wrapper.orderByDesc(StatisticResult::getCalculateTime);
        return statisticMapper.selectPage(page, wrapper);
    }
    
    /**
     * 根据ID查询统计结果
     * @param statisticId 统计结果ID
     * @return 统计结果信息
     */
    @Override
    public StatisticResult getById(Long statisticId) {
        return statisticMapper.selectById(statisticId);
    }
    
    /**
     * 新增统计结果
     * 如果未设置计算时间，自动设置为当前时间
     * @param statistic 统计结果信息
     * @return 是否成功
     */
    @Override
    public boolean saveStatistic(StatisticResult statistic) {
        if (statistic.getCalculateTime() == null) {
            statistic.setCalculateTime(LocalDateTime.now());
        }
        return statisticMapper.insert(statistic) > 0;
    }
    
    /**
     * 更新统计结果
     * 自动更新修改时间为当前时间
     * @param statistic 统计结果信息
     * @return 是否成功
     */
    @Override
    public boolean updateStatistic(StatisticResult statistic) {
        statistic.setUpdateTime(LocalDateTime.now());
        return statisticMapper.updateById(statistic) > 0;
    }
    
    /**
     * 删除统计结果（物理删除）
     * @param statisticId 统计结果ID
     * @return 是否成功
     */
    @Override
    public boolean deleteStatistic(Long statisticId) {
        return statisticMapper.deleteById(statisticId) > 0;
    }
    
    /**
     * 批量删除统计结果（物理删除）
     * @param statisticIds 统计结果ID数组
     * @return 是否成功
     */
    @Override
    public boolean deleteBatch(Long[] statisticIds) {
        for (Long statisticId : statisticIds) {
            statisticMapper.deleteById(statisticId);
        }
        return true;
    }
    
    /**
     * 获取月度产量趋势统计
     * 按部门和月份分组统计产量数据，用于趋势分析
     * 根据用户角色进行权限过滤（部门管理员只能查看本部门数据）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 月度产量趋势数据列表（每个部门一条记录，包含所有月份的数据）
     */
    @Override
    public List<Map<String, Object>> getMonthlyYieldTrend(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 查询产量统计数据（只查询已通过审核的）
        LambdaQueryWrapper<YieldStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YieldStatistics::getStatus, 1); // 已通过审核的产量
        
        // 部门管理员只能查看其部门下的产量数据
        if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(YieldStatistics::getAreaId, areaIds);
            } else {
                return result; // 如果没有管理的区域，返回空列表
            }
        }
        
        // 按日期范围过滤
        if (startDate != null) {
            wrapper.ge(YieldStatistics::getStatisticsDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(YieldStatistics::getStatisticsDate, endDate);
        }
        
        List<YieldStatistics> statistics = yieldStatisticsMapper.selectList(wrapper);
        
        // 查询所有区域，建立区域ID到部门ID的映射
        List<BaseArea> areas = areaMapper.selectList(null);
        Map<Long, Long> areaToDepartmentMap = new HashMap<>();
        for (BaseArea area : areas) {
            if (area.getDepartmentId() != null) {
                areaToDepartmentMap.put(area.getAreaId(), area.getDepartmentId());
            }
        }
        
        // 按部门和月份分组统计
        // Map<部门ID, Map<月份, 产量>>
        Map<Long, Map<String, BigDecimal>> departmentMonthlyData = new HashMap<>();
        Set<String> allMonths = new HashSet<>();
        
        for (YieldStatistics stat : statistics) {
            if (stat.getStatisticsDate() != null && stat.getActualYield() != null && stat.getAreaId() != null) {
                Long departmentId = areaToDepartmentMap.get(stat.getAreaId());
                if (departmentId != null) {
                    // 提取月份（YYYY-MM格式）
                    String monthKey = stat.getStatisticsDate().toString().substring(0, 7);
                    allMonths.add(monthKey);
                    
                    // 累加该部门该月份的产量
                    departmentMonthlyData.putIfAbsent(departmentId, new HashMap<>());
                    Map<String, BigDecimal> monthlyData = departmentMonthlyData.get(departmentId);
                    monthlyData.put(monthKey, 
                        monthlyData.getOrDefault(monthKey, BigDecimal.ZERO).add(stat.getActualYield()));
                }
            }
        }
        
        // 转换为列表格式：每个部门一条记录，包含所有月份的数据
        for (Map.Entry<Long, Map<String, BigDecimal>> deptEntry : departmentMonthlyData.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("departmentId", deptEntry.getKey());
            item.put("monthlyData", deptEntry.getValue());
            result.add(item);
        }
        
        return result;
    }
    
    /**
     * 获取品种产量占比统计
     * 按品种分组统计产量，并计算各品种占总产量的百分比
     * 根据用户角色进行权限过滤（部门管理员只能查看本部门数据）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 品种产量占比数据列表（包含品种ID、产量、占比）
     */
    @Override
    public List<Map<String, Object>> getBreedYieldRatio(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 查询产量统计数据（只查询已通过审核的）
        LambdaQueryWrapper<YieldStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YieldStatistics::getStatus, 1); // 已通过审核的产量
        
        // 部门管理员只能查看其部门下的产量数据
        if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(YieldStatistics::getAreaId, areaIds);
            } else {
                return result; // 如果没有管理的区域，返回空列表
            }
        }
        
        // 按日期范围过滤
        if (startDate != null) {
            wrapper.ge(YieldStatistics::getStatisticsDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(YieldStatistics::getStatisticsDate, endDate);
        }
        
        List<YieldStatistics> statistics = yieldStatisticsMapper.selectList(wrapper);
        
        // 按品种分组统计产量
        Map<Long, BigDecimal> breedData = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        
        for (YieldStatistics stat : statistics) {
            if (stat.getBreedId() != null && stat.getActualYield() != null) {
                // 累加各品种的产量
                breedData.put(stat.getBreedId(), 
                    breedData.getOrDefault(stat.getBreedId(), BigDecimal.ZERO).add(stat.getActualYield()));
                total = total.add(stat.getActualYield());
            }
        }
        
        // 转换为列表格式，计算各品种占比
        for (Map.Entry<Long, BigDecimal> entry : breedData.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("breedId", entry.getKey());
            item.put("yield", entry.getValue());
            // 计算占比（百分比，保留4位小数）
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                item.put("ratio", entry.getValue().divide(total, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100)).doubleValue());
            } else {
                item.put("ratio", 0.0);
            }
            result.add(item);
        }
        
        return result;
    }
    
    /**
     * 获取区域产量对比统计
     * 按区域分组统计产量，用于区域间的产量对比分析
     * 根据用户角色进行权限过滤（部门管理员只能查看本部门数据）
     * 所有区域都会显示，没有产量的区域显示为0
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 区域产量对比数据列表（包含区域ID、产量）
     */
    @Override
    public List<Map<String, Object>> getAreaYieldComparison(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 先查询所有区域（只查询启用状态的区域）
        LambdaQueryWrapper<BaseArea> areaWrapper = new LambdaQueryWrapper<>();
        areaWrapper.eq(BaseArea::getStatus, 1); // 只查询启用状态的区域
        
        // 部门管理员只能查看其部门下的区域
        if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                areaWrapper.in(BaseArea::getAreaId, areaIds);
            } else {
                return result; // 如果没有管理的区域，返回空列表
            }
        }
        
        List<BaseArea> allAreas = areaMapper.selectList(areaWrapper);
        
        // 查询产量统计数据（只查询已通过审核的）
        LambdaQueryWrapper<YieldStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YieldStatistics::getStatus, 1); // 已通过审核的产量
        
        // 部门管理员只能查看其部门下的产量数据
        if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(YieldStatistics::getAreaId, areaIds);
            } else {
                return result; // 如果没有管理的区域，返回空列表
            }
        }
        
        // 按日期范围过滤
        if (startDate != null) {
            wrapper.ge(YieldStatistics::getStatisticsDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(YieldStatistics::getStatisticsDate, endDate);
        }
        
        List<YieldStatistics> statistics = yieldStatisticsMapper.selectList(wrapper);
        
        // 按区域分组统计产量
        Map<Long, BigDecimal> areaData = new HashMap<>();
        
        for (YieldStatistics stat : statistics) {
            if (stat.getAreaId() != null && stat.getActualYield() != null) {
                // 累加各区域的产量
                areaData.put(stat.getAreaId(), 
                    areaData.getOrDefault(stat.getAreaId(), BigDecimal.ZERO).add(stat.getActualYield()));
            }
        }
        
        // 为所有区域创建数据，没有产量的区域显示为0
        for (BaseArea area : allAreas) {
            Map<String, Object> item = new HashMap<>();
            item.put("areaId", area.getAreaId());
            item.put("yield", areaData.getOrDefault(area.getAreaId(), BigDecimal.ZERO));
            result.add(item);
        }
        
        return result;
    }
    
    /**
     * 获取计划完成情况统计
     * 统计所有计划的总数、已完成数、执行中数、待审批数，并计算完成率
     * 根据用户角色进行权限过滤（部门管理员只能查看本部门数据）
     * @return 计划完成情况统计数据（包含总数、已完成数、执行中数、待审批数、完成率）
     */
    @Override
    public Map<String, Object> getPlanCompletionStats() {
        Map<String, Object> result = new HashMap<>();
        
        // 查询所有计划
        LambdaQueryWrapper<AquaculturePlan> planWrapper = new LambdaQueryWrapper<>();
        
        // 部门管理员只能查看其部门下的计划
        if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                planWrapper.in(AquaculturePlan::getAreaId, areaIds);
            } else {
                // 如果没有管理的区域，返回空统计
                result.put("totalCount", 0);
                result.put("completedCount", 0);
                result.put("inProgressCount", 0);
                result.put("pendingCount", 0);
                result.put("completionRate", 0.0);
                return result;
            }
        }
        
        List<AquaculturePlan> allPlans = planMapper.selectList(planWrapper);
        
        // 统计各状态计划数量
        long totalCount = allPlans.size();
        long completedCount = 0;  // 已完成（status=4）
        long inProgressCount = 0;  // 执行中（status=3）
        long pendingCount = 0;     // 待审批（status=0）
        
        for (AquaculturePlan plan : allPlans) {
            if (plan.getStatus() != null) {
                if (plan.getStatus() == 4) { // 已完成
                    completedCount++;
                } else if (plan.getStatus() == 3) { // 执行中
                    inProgressCount++;
                } else if (plan.getStatus() == 0) { // 待审批
                    pendingCount++;
                }
            }
        }
        
        result.put("totalCount", totalCount);
        result.put("completedCount", completedCount);
        result.put("inProgressCount", inProgressCount);
        result.put("pendingCount", pendingCount);
        
        // 计算完成率（百分比）
        if (totalCount > 0) {
            result.put("completionRate", (double) completedCount / totalCount * 100);
        } else {
            result.put("completionRate", 0.0);
        }
        
        return result;
    }
    
    /**
     * 获取部门产量对比统计
     * 按部门分组统计产量，用于部门间的产量对比分析
     * 根据用户角色进行权限过滤（部门管理员只能查看本部门数据）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 部门产量对比数据列表（包含部门ID、产量）
     */
    @Override
    public List<Map<String, Object>> getDepartmentYieldComparison(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 部门管理员只能查看自己部门的数据
        Long userDepartmentId = null;
        if (UserContext.isDepartmentManager()) {
            com.server.aquacultureserver.domain.SysUser user = UserContext.getCurrentUser();
            if (user != null && user.getDepartmentId() != null) {
                userDepartmentId = user.getDepartmentId();
            } else {
                return result; // 如果没有部门，返回空列表
            }
        }
        
        // 查询产量统计数据（只查询已通过审核的）
        LambdaQueryWrapper<YieldStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YieldStatistics::getStatus, 1); // 已通过审核的产量
        
        // 部门管理员只查看其部门下的产量数据
        if (userDepartmentId != null) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(YieldStatistics::getAreaId, areaIds);
            } else {
                return result; // 如果没有管理的区域，返回空列表
            }
        }
        
        // 按日期范围过滤
        if (startDate != null) {
            wrapper.ge(YieldStatistics::getStatisticsDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(YieldStatistics::getStatisticsDate, endDate);
        }
        
        List<YieldStatistics> statistics = yieldStatisticsMapper.selectList(wrapper);
        
        // 查询所有区域，建立区域ID到部门ID的映射
        List<BaseArea> areas = areaMapper.selectList(null);
        Map<Long, Long> areaToDepartmentMap = new HashMap<>();
        for (BaseArea area : areas) {
            if (area.getDepartmentId() != null) {
                areaToDepartmentMap.put(area.getAreaId(), area.getDepartmentId());
            }
        }
        
        // 按部门分组统计产量
        Map<Long, BigDecimal> departmentData = new HashMap<>();
        
        for (YieldStatistics stat : statistics) {
            if (stat.getAreaId() != null && stat.getActualYield() != null) {
                Long departmentId = areaToDepartmentMap.get(stat.getAreaId());
                if (departmentId != null) {
                    // 如果是部门管理员，只统计自己部门的数据
                    if (userDepartmentId != null && !userDepartmentId.equals(departmentId)) {
                        continue;
                    }
                    // 累加各部门的产量
                    departmentData.put(departmentId, 
                        departmentData.getOrDefault(departmentId, BigDecimal.ZERO).add(stat.getActualYield()));
                }
            }
        }
        
        // 转换为列表格式
        for (Map.Entry<Long, BigDecimal> entry : departmentData.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("departmentId", entry.getKey());
            item.put("yield", entry.getValue());
            result.add(item);
        }
        
        return result;
    }
}

