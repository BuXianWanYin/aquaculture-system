import request from '@/utils/request'

/**
 * 分页查询病害记录列表
 * @param {Object} params - 查询参数 {current, size, planId, areaId, diseaseName, diseaseType, status}
 * @returns {Promise} 返回分页病害记录列表
 */
export const getDiseaseRecordList = (params) => {
  return request({
    url: '/disease/record/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有病害记录（用于下拉选择）
 * @returns {Promise} 返回所有病害记录列表
 */
export const getAllDiseaseRecords = () => {
  return request({
    url: '/disease/record/all',
    method: 'get'
  })
}

/**
 * 根据记录ID查询病害记录详情
 * @param {Number} recordId - 记录ID
 * @returns {Promise} 返回病害记录信息
 */
export const getDiseaseRecordById = (recordId) => {
  return request({
    url: `/disease/record/${recordId}`,
    method: 'get'
  })
}

/**
 * 新增病害记录
 * @param {Object} data - 病害记录数据
 * @returns {Promise} 返回操作结果
 */
export const saveDiseaseRecord = (data) => {
  return request({
    url: '/disease/record',
    method: 'post',
    data
  })
}

/**
 * 更新病害记录信息
 * @param {Object} data - 病害记录数据
 * @returns {Promise} 返回操作结果
 */
export const updateDiseaseRecord = (data) => {
  return request({
    url: '/disease/record',
    method: 'put',
    data
  })
}

/**
 * 删除病害记录
 * @param {Number} recordId - 记录ID
 * @returns {Promise} 返回操作结果
 */
export const deleteDiseaseRecord = (recordId) => {
  return request({
    url: `/disease/record/${recordId}`,
    method: 'delete'
  })
}

