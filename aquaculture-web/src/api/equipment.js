import request from '@/utils/request'

/**
 * 分页查询设备列表
 * @param {Object} params - 查询参数 {current, size, equipmentName, equipmentType}
 * @returns {Promise} 返回分页设备列表
 */
export const getEquipmentList = (params) => {
  return request({
    url: '/equipment/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有设备（用于下拉选择）
 * @returns {Promise} 返回所有设备列表
 */
export const getAllEquipments = () => {
  return request({
    url: '/equipment/all',
    method: 'get'
  })
}

/**
 * 根据设备ID查询设备详情
 * @param {Number} equipmentId - 设备ID
 * @returns {Promise} 返回设备信息
 */
export const getEquipmentById = (equipmentId) => {
  return request({
    url: `/equipment/${equipmentId}`,
    method: 'get'
  })
}

/**
 * 新增设备
 * @param {Object} data - 设备数据
 * @returns {Promise} 返回操作结果
 */
export const saveEquipment = (data) => {
  return request({
    url: '/equipment',
    method: 'post',
    data
  })
}

/**
 * 更新设备信息
 * @param {Object} data - 设备数据
 * @returns {Promise} 返回操作结果
 */
export const updateEquipment = (data) => {
  return request({
    url: '/equipment',
    method: 'put',
    data
  })
}

/**
 * 删除设备
 * @param {Number} equipmentId - 设备ID
 * @returns {Promise} 返回操作结果
 */
export const deleteEquipment = (equipmentId) => {
  return request({
    url: `/equipment/${equipmentId}`,
    method: 'delete'
  })
}

