import request from '@/utils/request'

/**
 * 分页查询销售记录列表
 * @param {Object} params - 查询参数 {current, size, planId, areaId, breedId, customerId, paymentStatus, status}
 * @returns {Promise} 返回分页销售记录列表
 */
export const getSalesRecordList = (params) => {
  return request({
    url: '/sales/record/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有销售记录（用于下拉选择）
 * @returns {Promise} 返回所有销售记录列表
 */
export const getAllSalesRecords = () => {
  return request({
    url: '/sales/record/all',
    method: 'get'
  })
}

/**
 * 根据销售记录ID查询详情
 * @param {Number} salesId - 销售记录ID
 * @returns {Promise} 返回销售记录信息
 */
export const getSalesRecordById = (salesId) => {
  return request({
    url: `/sales/record/${salesId}`,
    method: 'get'
  })
}

/**
 * 新增销售记录
 * @param {Object} data - 销售记录数据
 * @returns {Promise} 返回操作结果
 */
export const saveSalesRecord = (data) => {
  return request({
    url: '/sales/record',
    method: 'post',
    data
  })
}

/**
 * 更新销售记录信息
 * @param {Object} data - 销售记录数据
 * @returns {Promise} 返回操作结果
 */
export const updateSalesRecord = (data) => {
  return request({
    url: '/sales/record',
    method: 'put',
    data
  })
}

/**
 * 删除销售记录
 * @param {Number} salesId - 销售记录ID
 * @returns {Promise} 返回操作结果
 */
export const deleteSalesRecord = (salesId) => {
  return request({
    url: `/sales/record/${salesId}`,
    method: 'delete'
  })
}

/**
 * 统计销售收入
 * @param {Object} params - 查询参数 {startDate, endDate, areaId, breedId}
 * @returns {Promise} 返回统计数据
 */
export const getSalesStatistics = (params) => {
  return request({
    url: '/sales/record/statistics',
    method: 'get',
    params
  })
}

