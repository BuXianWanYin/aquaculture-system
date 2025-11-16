import request from '@/utils/request'

/**
 * 分页查询部门列表
 * @param {Object} params - 查询参数 {current, size, departmentName, status}
 * @returns {Promise} 返回分页部门列表
 */
export const getDepartmentList = (params) => {
  return request({
    url: '/department/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有部门（用于下拉选择）
 * @returns {Promise} 返回所有部门列表
 */
export const getAllDepartments = () => {
  return request({
    url: '/department/all',
    method: 'get'
  })
}

/**
 * 根据部门ID查询部门详情
 * @param {Number} departmentId - 部门ID
 * @returns {Promise} 返回部门信息
 */
export const getDepartmentById = (departmentId) => {
  return request({
    url: `/department/${departmentId}`,
    method: 'get'
  })
}

/**
 * 新增部门
 * @param {Object} data - 部门数据
 * @returns {Promise} 返回操作结果
 */
export const saveDepartment = (data) => {
  return request({
    url: '/department',
    method: 'post',
    data
  })
}

/**
 * 更新部门
 * @param {Object} data - 部门数据
 * @returns {Promise} 返回操作结果
 */
export const updateDepartment = (data) => {
  return request({
    url: '/department',
    method: 'put',
    data
  })
}

/**
 * 删除部门
 * @param {Number} departmentId - 部门ID
 * @returns {Promise} 返回操作结果
 */
export const deleteDepartment = (departmentId) => {
  return request({
    url: `/department/${departmentId}`,
    method: 'delete'
  })
}

