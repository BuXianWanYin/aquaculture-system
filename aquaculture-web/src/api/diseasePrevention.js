import request from '@/utils/request'

/**
 * 分页查询防治记录列表
 * @param {Object} params - 查询参数 {current, size, recordId, planId, areaId, status}
 * @returns {Promise} 返回分页防治记录列表
 */
export const getDiseasePreventionList = (params) => {
  return request({
    url: '/disease/prevention/page',
    method: 'get',
    params
  })
}

/**
 * 根据病害记录ID查询防治记录列表
 * @param {Number} recordId - 病害记录ID
 * @returns {Promise} 返回防治记录列表
 */
export const getDiseasePreventionsByRecordId = (recordId) => {
  return request({
    url: `/disease/prevention/record/${recordId}`,
    method: 'get'
  })
}

/**
 * 新增防治记录
 * @param {Object} data - 防治记录数据
 * @returns {Promise} 返回操作结果
 */
export const saveDiseasePrevention = (data) => {
  return request({
    url: '/disease/prevention',
    method: 'post',
    data
  })
}

/**
 * 更新防治记录信息
 * @param {Object} data - 防治记录数据
 * @returns {Promise} 返回操作结果
 */
export const updateDiseasePrevention = (data) => {
  return request({
    url: '/disease/prevention',
    method: 'put',
    data
  })
}

/**
 * 删除防治记录
 * @param {Number} preventionId - 防治记录ID
 * @returns {Promise} 返回操作结果
 */
export const deleteDiseasePrevention = (preventionId) => {
  return request({
    url: `/disease/prevention/${preventionId}`,
    method: 'delete'
  })
}

