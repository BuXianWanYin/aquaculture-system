import request from '@/utils/request'

/**
 * 分页查询用药记录列表
 * @param {Object} params - 查询参数 {current, size, recordId, preventionId, planId, areaId, medicineName, status}
 * @returns {Promise} 返回分页用药记录列表
 */
export const getMedicineUsageList = (params) => {
  return request({
    url: '/disease/medicine/page',
    method: 'get',
    params
  })
}

/**
 * 根据病害记录ID查询用药记录列表
 * @param {Number} recordId - 病害记录ID
 * @returns {Promise} 返回用药记录列表
 */
export const getMedicineUsagesByRecordId = (recordId) => {
  return request({
    url: `/disease/medicine/record/${recordId}`,
    method: 'get'
  })
}

/**
 * 根据防治记录ID查询用药记录列表
 * @param {Number} preventionId - 防治记录ID
 * @returns {Promise} 返回用药记录列表
 */
export const getMedicineUsagesByPreventionId = (preventionId) => {
  return request({
    url: `/disease/medicine/prevention/${preventionId}`,
    method: 'get'
  })
}

/**
 * 新增用药记录
 * @param {Object} data - 用药记录数据
 * @returns {Promise} 返回操作结果
 */
export const saveMedicineUsage = (data) => {
  return request({
    url: '/disease/medicine',
    method: 'post',
    data
  })
}

/**
 * 更新用药记录信息
 * @param {Object} data - 用药记录数据
 * @returns {Promise} 返回操作结果
 */
export const updateMedicineUsage = (data) => {
  return request({
    url: '/disease/medicine',
    method: 'put',
    data
  })
}

/**
 * 删除用药记录
 * @param {Number} usageId - 用药记录ID
 * @returns {Promise} 返回操作结果
 */
export const deleteMedicineUsage = (usageId) => {
  return request({
    url: `/disease/medicine/${usageId}`,
    method: 'delete'
  })
}

