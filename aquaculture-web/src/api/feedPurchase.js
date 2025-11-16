import request from '@/utils/request'

/**
 * 分页查询饲料采购列表
 * @param {Object} params - 查询参数 {current, size, feedName, feedType, supplier, status}
 * @returns {Promise} 返回分页采购列表
 */
export const getFeedPurchaseList = (params) => {
  return request({
    url: '/feed/purchase/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有饲料采购（用于下拉选择）
 * @returns {Promise} 返回所有采购列表
 */
export const getAllFeedPurchases = () => {
  return request({
    url: '/feed/purchase/all',
    method: 'get'
  })
}

/**
 * 根据采购ID查询采购详情
 * @param {Number} purchaseId - 采购ID
 * @returns {Promise} 返回采购信息
 */
export const getFeedPurchaseById = (purchaseId) => {
  return request({
    url: `/feed/purchase/${purchaseId}`,
    method: 'get'
  })
}

/**
 * 新增饲料采购
 * @param {Object} data - 采购数据
 * @returns {Promise} 返回操作结果
 */
export const saveFeedPurchase = (data) => {
  return request({
    url: '/feed/purchase',
    method: 'post',
    data
  })
}

/**
 * 更新饲料采购信息
 * @param {Object} data - 采购数据
 * @returns {Promise} 返回操作结果
 */
export const updateFeedPurchase = (data) => {
  return request({
    url: '/feed/purchase',
    method: 'put',
    data
  })
}

/**
 * 删除饲料采购
 * @param {Number} purchaseId - 采购ID
 * @returns {Promise} 返回操作结果
 */
export const deleteFeedPurchase = (purchaseId) => {
  return request({
    url: `/feed/purchase/${purchaseId}`,
    method: 'delete'
  })
}

