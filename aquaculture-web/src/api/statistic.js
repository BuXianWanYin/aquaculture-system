import request from '@/utils/request'

/**
 * 分页查询统计结果列表
 * @param {Object} params - 查询参数 {current, size, statisticName, statisticDimension, dimensionValue, startDate, endDate}
 * @returns {Promise} 返回分页统计结果列表
 */
export const getStatisticList = (params) => {
  return request({
    url: '/statistic/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询统计结果详情
 * @param {Number} statisticId - 统计ID
 * @returns {Promise} 返回统计结果信息
 */
export const getStatisticById = (statisticId) => {
  return request({
    url: `/statistic/${statisticId}`,
    method: 'get'
  })
}

/**
 * 新增统计结果
 * @param {Object} data - 统计结果数据
 * @returns {Promise} 返回操作结果
 */
export const saveStatistic = (data) => {
  return request({
    url: '/statistic',
    method: 'post',
    data
  })
}

/**
 * 更新统计结果
 * @param {Object} data - 统计结果数据
 * @returns {Promise} 返回操作结果
 */
export const updateStatistic = (data) => {
  return request({
    url: '/statistic',
    method: 'put',
    data
  })
}

/**
 * 删除统计结果
 * @param {Number} statisticId - 统计ID
 * @returns {Promise} 返回操作结果
 */
export const deleteStatistic = (statisticId) => {
  return request({
    url: `/statistic/${statisticId}`,
    method: 'delete'
  })
}

/**
 * 批量删除统计结果
 * @param {Array} statisticIds - 统计ID数组
 * @returns {Promise} 返回操作结果
 */
export const deleteBatchStatistic = (statisticIds) => {
  return request({
    url: '/statistic/batch',
    method: 'delete',
    data: statisticIds
  })
}

/**
 * 获取月度产量趋势数据
 * @param {Object} params - 查询参数 {startDate, endDate}
 * @returns {Promise} 返回月度产量趋势数据
 */
export const getMonthlyYieldTrend = (params) => {
  return request({
    url: '/statistic/monthlyYieldTrend',
    method: 'get',
    params
  })
}

/**
 * 获取品种产量占比数据
 * @param {Object} params - 查询参数 {startDate, endDate}
 * @returns {Promise} 返回品种产量占比数据
 */
export const getBreedYieldRatio = (params) => {
  return request({
    url: '/statistic/breedYieldRatio',
    method: 'get',
    params
  })
}

/**
 * 获取区域产量对比数据
 * @param {Object} params - 查询参数 {startDate, endDate}
 * @returns {Promise} 返回区域产量对比数据
 */
export const getAreaYieldComparison = (params) => {
  return request({
    url: '/statistic/areaYieldComparison',
    method: 'get',
    params
  })
}

/**
 * 获取计划完成情况统计
 * @returns {Promise} 返回计划完成情况统计数据
 */
export const getPlanCompletionStats = () => {
  return request({
    url: '/statistic/planCompletionStats',
    method: 'get'
  })
}

/**
 * 获取部门产量对比数据
 * @param {Object} params - 查询参数 {startDate, endDate}
 * @returns {Promise} 返回部门产量对比数据
 */
export const getDepartmentYieldComparison = (params) => {
  return request({
    url: '/statistic/departmentYieldComparison',
    method: 'get',
    params
  })
}

