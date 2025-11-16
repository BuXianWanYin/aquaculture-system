import request from '@/utils/request'

/**
 * 分页查询计划调整列表
 * @param {Object} params - 查询参数 {current, size, planId}
 * @returns {Promise} 返回分页调整列表
 */
export const getAdjustList = (params) => {
  return request({
    url: '/planAdjust/page',
    method: 'get',
    params
  })
}

/**
 * 根据计划ID查询调整记录
 * @param {Number} planId - 计划ID
 * @returns {Promise} 返回调整记录列表
 */
export const getAdjustByPlanId = (planId) => {
  return request({
    url: `/planAdjust/plan/${planId}`,
    method: 'get'
  })
}

/**
 * 根据调整ID查询调整详情
 * @param {Number} adjustId - 调整ID
 * @returns {Promise} 返回调整信息
 */
export const getAdjustById = (adjustId) => {
  return request({
    url: `/planAdjust/${adjustId}`,
    method: 'get'
  })
}

/**
 * 新增计划调整申请
 * @param {Object} data - 调整数据
 * @returns {Promise} 返回操作结果
 */
export const saveAdjust = (data) => {
  return request({
    url: '/planAdjust',
    method: 'post',
    data
  })
}

/**
 * 更新计划调整信息
 * @param {Object} data - 调整数据
 * @returns {Promise} 返回操作结果
 */
export const updateAdjust = (data) => {
  return request({
    url: '/planAdjust',
    method: 'put',
    data
  })
}

/**
 * 审批计划调整
 * @param {Object} data - 审批数据 {adjustId, approverId, approveOpinion, status}
 * @returns {Promise} 返回操作结果
 */
export const approveAdjust = (data) => {
  return request({
    url: '/planAdjust/approve',
    method: 'post',
    data
  })
}

