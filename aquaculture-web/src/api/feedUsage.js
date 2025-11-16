import request from '@/utils/request'

/**
 * 分页查询饲料使用记录列表
 * @param {Object} params - 查询参数 {current, size, planId, areaId, feedName, feedType, status}
 * @returns {Promise} 返回分页使用记录列表
 */
export const getFeedUsageList = (params) => {
  return request({
    url: '/feed/usage/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有饲料使用记录（用于下拉选择）
 * @returns {Promise} 返回所有使用记录列表
 */
export const getAllFeedUsages = () => {
  return request({
    url: '/feed/usage/all',
    method: 'get'
  })
}

/**
 * 根据使用记录ID查询详情
 * @param {Number} usageId - 使用记录ID
 * @returns {Promise} 返回使用记录信息
 */
export const getFeedUsageById = (usageId) => {
  return request({
    url: `/feed/usage/${usageId}`,
    method: 'get'
  })
}

/**
 * 新增饲料使用记录
 * @param {Object} data - 使用记录数据
 * @returns {Promise} 返回操作结果
 */
export const saveFeedUsage = (data) => {
  return request({
    url: '/feed/usage',
    method: 'post',
    data
  })
}

/**
 * 更新饲料使用记录信息
 * @param {Object} data - 使用记录数据
 * @returns {Promise} 返回操作结果
 */
export const updateFeedUsage = (data) => {
  return request({
    url: '/feed/usage',
    method: 'put',
    data
  })
}

/**
 * 删除饲料使用记录
 * @param {Number} usageId - 使用记录ID
 * @returns {Promise} 返回操作结果
 */
export const deleteFeedUsage = (usageId) => {
  return request({
    url: `/feed/usage/${usageId}`,
    method: 'delete'
  })
}

