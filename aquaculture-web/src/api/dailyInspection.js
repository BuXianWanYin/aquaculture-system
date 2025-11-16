import request from '@/utils/request'

/**
 * 分页查询日常检查记录列表
 * @param {Object} params - 查询参数 {current, size, planId, areaId, inspectionType, inspectionResult, status}
 * @returns {Promise} 返回分页检查记录列表
 */
export const getDailyInspectionList = (params) => {
  return request({
    url: '/production/inspection/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有日常检查记录（用于下拉选择）
 * @returns {Promise} 返回所有检查记录列表
 */
export const getAllDailyInspections = () => {
  return request({
    url: '/production/inspection/all',
    method: 'get'
  })
}

/**
 * 根据检查记录ID查询详情
 * @param {Number} inspectionId - 检查记录ID
 * @returns {Promise} 返回检查记录信息
 */
export const getDailyInspectionById = (inspectionId) => {
  return request({
    url: `/production/inspection/${inspectionId}`,
    method: 'get'
  })
}

/**
 * 新增日常检查记录
 * @param {Object} data - 检查记录数据
 * @returns {Promise} 返回操作结果
 */
export const saveDailyInspection = (data) => {
  return request({
    url: '/production/inspection',
    method: 'post',
    data
  })
}

/**
 * 更新日常检查记录信息
 * @param {Object} data - 检查记录数据
 * @returns {Promise} 返回操作结果
 */
export const updateDailyInspection = (data) => {
  return request({
    url: '/production/inspection',
    method: 'put',
    data
  })
}

/**
 * 删除日常检查记录
 * @param {Number} inspectionId - 检查记录ID
 * @returns {Promise} 返回操作结果
 */
export const deleteDailyInspection = (inspectionId) => {
  return request({
    url: `/production/inspection/${inspectionId}`,
    method: 'delete'
  })
}

