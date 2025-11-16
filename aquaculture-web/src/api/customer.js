import request from '@/utils/request'

/**
 * 分页查询客户列表
 * @param {Object} params - 查询参数 {current, size, customerName, customerType, status}
 * @returns {Promise} 返回分页客户列表
 */
export const getCustomerList = (params) => {
  return request({
    url: '/sales/customer/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有客户（用于下拉选择）
 * @returns {Promise} 返回所有客户列表
 */
export const getAllCustomers = () => {
  return request({
    url: '/sales/customer/all',
    method: 'get'
  })
}

/**
 * 根据客户ID查询客户详情
 * @param {Number} customerId - 客户ID
 * @returns {Promise} 返回客户信息
 */
export const getCustomerById = (customerId) => {
  return request({
    url: `/sales/customer/${customerId}`,
    method: 'get'
  })
}

/**
 * 新增客户
 * @param {Object} data - 客户数据
 * @returns {Promise} 返回操作结果
 */
export const saveCustomer = (data) => {
  return request({
    url: '/sales/customer',
    method: 'post',
    data
  })
}

/**
 * 更新客户信息
 * @param {Object} data - 客户数据
 * @returns {Promise} 返回操作结果
 */
export const updateCustomer = (data) => {
  return request({
    url: '/sales/customer',
    method: 'put',
    data
  })
}

/**
 * 删除客户
 * @param {Number} customerId - 客户ID
 * @returns {Promise} 返回操作结果
 */
export const deleteCustomer = (customerId) => {
  return request({
    url: `/sales/customer/${customerId}`,
    method: 'delete'
  })
}

