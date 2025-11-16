import request from '@/utils/request'

/**
 * 分页查询投喂记录列表
 * @param {Object} params - 查询参数 {current, size, planId, areaId, feedName, status}
 * @returns {Promise} 返回分页投喂记录列表
 */
export const getFeedingRecordList = (params) => {
  return request({
    url: '/production/feeding/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有投喂记录（用于下拉选择）
 * @returns {Promise} 返回所有投喂记录列表
 */
export const getAllFeedingRecords = () => {
  return request({
    url: '/production/feeding/all',
    method: 'get'
  })
}

/**
 * 根据记录ID查询投喂记录详情
 * @param {Number} recordId - 记录ID
 * @returns {Promise} 返回投喂记录信息
 */
export const getFeedingRecordById = (recordId) => {
  return request({
    url: `/production/feeding/${recordId}`,
    method: 'get'
  })
}

/**
 * 新增投喂记录
 * @param {Object} data - 投喂记录数据
 * @returns {Promise} 返回操作结果
 */
export const saveFeedingRecord = (data) => {
  return request({
    url: '/production/feeding',
    method: 'post',
    data
  })
}

/**
 * 更新投喂记录信息
 * @param {Object} data - 投喂记录数据
 * @returns {Promise} 返回操作结果
 */
export const updateFeedingRecord = (data) => {
  return request({
    url: '/production/feeding',
    method: 'put',
    data
  })
}

/**
 * 删除投喂记录
 * @param {Number} recordId - 记录ID
 * @returns {Promise} 返回操作结果
 */
export const deleteFeedingRecord = (recordId) => {
  return request({
    url: `/production/feeding/${recordId}`,
    method: 'delete'
  })
}

