import request from '@/utils/request'

/**
 * 分页查询饲料库存列表
 * @param {Object} params - 查询参数 {current, size, feedName, feedType, status}
 * @returns {Promise} 返回分页库存列表
 */
export const getFeedInventoryList = (params) => {
  return request({
    url: '/feed/inventory/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有饲料库存（用于下拉选择）
 * @returns {Promise} 返回所有库存列表
 */
export const getAllFeedInventories = () => {
  return request({
    url: '/feed/inventory/all',
    method: 'get'
  })
}

/**
 * 根据库存ID查询库存详情
 * @param {Number} inventoryId - 库存ID
 * @returns {Promise} 返回库存信息
 */
export const getFeedInventoryById = (inventoryId) => {
  return request({
    url: `/feed/inventory/${inventoryId}`,
    method: 'get'
  })
}

/**
 * 新增饲料库存
 * @param {Object} data - 库存数据
 * @returns {Promise} 返回操作结果
 */
export const saveFeedInventory = (data) => {
  return request({
    url: '/feed/inventory',
    method: 'post',
    data
  })
}

/**
 * 更新饲料库存信息
 * @param {Object} data - 库存数据
 * @returns {Promise} 返回操作结果
 */
export const updateFeedInventory = (data) => {
  return request({
    url: '/feed/inventory',
    method: 'put',
    data
  })
}

/**
 * 删除饲料库存
 * @param {Number} inventoryId - 库存ID
 * @returns {Promise} 返回操作结果
 */
export const deleteFeedInventory = (inventoryId) => {
  return request({
    url: `/feed/inventory/${inventoryId}`,
    method: 'delete'
  })
}

