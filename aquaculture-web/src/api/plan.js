import request from '@/utils/request'

/**
 * 分页查询养殖计划列表
 * @param {Object} params - 查询参数 {current, size, planName, areaId, breedId, status}
 * @returns {Promise} 返回分页计划列表
 */
export const getPlanList = (params) => {
  return request({
    url: '/plan/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有养殖计划（用于下拉选择）
 * @returns {Promise} 返回所有计划列表
 */
export const getAllPlans = () => {
  return request({
    url: '/plan/all',
    method: 'get'
  })
}

/**
 * 查询已审核通过的计划（用于下拉选择）
 * @returns {Promise} 返回已审核通过的计划列表
 */
export const getApprovedPlans = () => {
  return request({
    url: '/plan/approved',
    method: 'get'
  })
}

/**
 * 根据计划ID查询计划详情
 * @param {Number} planId - 计划ID
 * @returns {Promise} 返回计划信息
 */
export const getPlanById = (planId) => {
  return request({
    url: `/plan/${planId}`,
    method: 'get'
  })
}

/**
 * 新增养殖计划
 * @param {Object} data - 计划数据
 * @returns {Promise} 返回操作结果
 */
export const savePlan = (data) => {
  return request({
    url: '/plan',
    method: 'post',
    data
  })
}

/**
 * 更新养殖计划信息
 * @param {Object} data - 计划数据
 * @returns {Promise} 返回操作结果
 */
export const updatePlan = (data) => {
  return request({
    url: '/plan',
    method: 'put',
    data
  })
}

/**
 * 删除养殖计划
 * @param {Number} planId - 计划ID
 * @returns {Promise} 返回操作结果
 */
export const deletePlan = (planId) => {
  return request({
    url: `/plan/${planId}`,
    method: 'delete'
  })
}

/**
 * 审批养殖计划
 * @param {Object} data - 审批数据 {planId, approverId, approveOpinion, status}
 * @returns {Promise} 返回操作结果
 */
export const approvePlan = (data) => {
  return request({
    url: '/plan/approve',
    method: 'post',
    data
  })
}

/**
 * 获取计划完成率
 * @param {Number} planId - 计划ID
 * @returns {Promise} 返回完成率（0-100）
 */
export const getPlanCompletionRate = (planId) => {
  return request({
    url: `/plan/${planId}/completion-rate`,
    method: 'get'
  })
}

