import request from '@/utils/request'

/**
 * 分页查询操作日志列表
 * @param {Object} params - 查询参数 {current, size, userId, module, operType, keyword, startTime, endTime}
 * @returns {Promise} 返回分页日志列表
 */
export const getOperLogList = (params) => {
  return request({
    url: '/operLog/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询操作日志详情
 * @param {Number} logId - 日志ID
 * @returns {Promise} 返回日志信息
 */
export const getOperLogById = (logId) => {
  return request({
    url: `/operLog/${logId}`,
    method: 'get'
  })
}

/**
 * 删除操作日志
 * @param {Number} logId - 日志ID
 * @returns {Promise} 返回操作结果
 */
export const deleteOperLog = (logId) => {
  return request({
    url: `/operLog/${logId}`,
    method: 'delete'
  })
}

/**
 * 批量删除操作日志
 * @param {Array} logIds - 日志ID数组
 * @returns {Promise} 返回操作结果
 */
export const deleteBatchOperLog = (logIds) => {
  return request({
    url: '/operLog/batch',
    method: 'delete',
    data: logIds
  })
}

/**
 * 清空操作日志
 * @returns {Promise} 返回操作结果
 */
export const clearOperLog = () => {
  return request({
    url: '/operLog/clear',
    method: 'delete'
  })
}

