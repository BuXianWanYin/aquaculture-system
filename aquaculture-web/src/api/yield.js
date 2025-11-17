import request from '@/utils/request'

/**
 * 查询所有产量统计（用于下拉选择）
 * @returns {Promise} 返回所有产量统计列表
 */
export const getAllStatistics = () => {
  return request({
    url: '/yield/all',
    method: 'get'
  })
}

/**
 * 分页查询产量统计列表
 * @param {Object} params - 查询参数 {current, size, planId, areaId, breedId, status}
 * @returns {Promise} 返回分页产量统计列表
 */
export const getYieldList = (params) => {
  return request({
    url: '/yield/page',
    method: 'get',
    params
  })
}

/**
 * 根据产量ID查询产量统计详情
 * @param {Number} yieldId - 产量统计ID
 * @returns {Promise} 返回产量统计信息
 */
export const getYieldById = (yieldId) => {
  return request({
    url: `/yield/${yieldId}`,
    method: 'get'
  })
}

/**
 * 新增产量统计记录
 * @param {Object} data - 产量统计数据
 * @returns {Promise} 返回操作结果
 */
export const saveYield = (data) => {
  return request({
    url: '/yield',
    method: 'post',
    data
  })
}

/**
 * 更新产量统计记录
 * @param {Object} data - 产量统计数据
 * @returns {Promise} 返回操作结果
 */
export const updateYield = (data) => {
  return request({
    url: '/yield',
    method: 'put',
    data
  })
}

/**
 * 删除产量统计记录
 * @param {Number} yieldId - 产量统计ID
 * @returns {Promise} 返回操作结果
 */
export const deleteYield = (yieldId) => {
  return request({
    url: `/yield/${yieldId}`,
    method: 'delete'
  })
}

/**
 * 审核产量统计记录
 * @param {Object} data - 审核数据 {yieldId, auditorId, auditOpinion, status}
 * @returns {Promise} 返回操作结果
 */
export const auditYield = (data) => {
  return request({
    url: '/yield/audit',
    method: 'post',
    data
  })
}

/**
 * 根据计划ID查询产量统计
 * @param {Number} planId - 计划ID
 * @returns {Promise} 返回产量统计列表
 */
export const getYieldByPlanId = (planId) => {
  return request({
    url: `/yield/byPlan/${planId}`,
    method: 'get'
  })
}

