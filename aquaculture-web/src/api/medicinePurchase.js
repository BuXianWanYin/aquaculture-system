import request from '@/utils/request'

/**
 * 分页查询药品采购列表
 * @param {Object} params - 查询参数 {current, size, medicineName, medicineType, supplier, status}
 * @returns {Promise} 返回分页采购列表
 */
export const getMedicinePurchaseList = (params) => {
  return request({
    url: '/medicine/purchase/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有药品采购（用于下拉选择）
 * @returns {Promise} 返回所有采购列表
 */
export const getAllMedicinePurchases = () => {
  return request({
    url: '/medicine/purchase/all',
    method: 'get'
  })
}

/**
 * 根据采购ID查询采购详情
 * @param {Number} purchaseId - 采购ID
 * @returns {Promise} 返回采购信息
 */
export const getMedicinePurchaseById = (purchaseId) => {
  return request({
    url: `/medicine/purchase/${purchaseId}`,
    method: 'get'
  })
}

/**
 * 新增药品采购
 * @param {Object} data - 采购数据
 * @returns {Promise} 返回操作结果
 */
export const saveMedicinePurchase = (data) => {
  return request({
    url: '/medicine/purchase',
    method: 'post',
    data
  })
}

/**
 * 更新药品采购信息
 * @param {Object} data - 采购数据
 * @returns {Promise} 返回操作结果
 */
export const updateMedicinePurchase = (data) => {
  return request({
    url: '/medicine/purchase',
    method: 'put',
    data
  })
}

/**
 * 删除药品采购
 * @param {Number} purchaseId - 采购ID
 * @returns {Promise} 返回操作结果
 */
export const deleteMedicinePurchase = (purchaseId) => {
  return request({
    url: `/medicine/purchase/${purchaseId}`,
    method: 'delete'
  })
}

/**
 * 根据药品名称和类型查询采购记录
 * @param {String} medicineName - 药品名称
 * @param {String} medicineType - 药品类型
 * @returns {Promise} 返回采购记录列表
 */
export const getMedicinePurchasesByMedicine = (medicineName, medicineType) => {
  return request({
    url: '/medicine/purchase/byMedicine',
    method: 'get',
    params: { medicineName, medicineType }
  })
}

