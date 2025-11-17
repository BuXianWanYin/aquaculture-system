import request from '@/utils/request'

/**
 * 分页查询药品库存列表
 * @param {Object} params - 查询参数 {current, size, medicineName, medicineType, status}
 * @returns {Promise} 返回分页库存列表
 */
export const getMedicineInventoryList = (params) => {
  return request({
    url: '/medicine/inventory/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有药品库存（用于下拉选择）
 * @returns {Promise} 返回所有库存列表
 */
export const getAllMedicineInventories = () => {
  return request({
    url: '/medicine/inventory/all',
    method: 'get'
  })
}

/**
 * 根据库存ID查询库存详情
 * @param {Number} inventoryId - 库存ID
 * @returns {Promise} 返回库存信息
 */
export const getMedicineInventoryById = (inventoryId) => {
  return request({
    url: `/medicine/inventory/${inventoryId}`,
    method: 'get'
  })
}

