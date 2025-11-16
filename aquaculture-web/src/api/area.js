import request from '@/utils/request'

/**
 * 分页查询养殖区域列表
 * @param {Object} params - 查询参数 {current, size, areaCode, areaName}
 * @returns {Promise} 返回分页区域列表
 */
export const getAreaList = (params) => {
  return request({
    url: '/area/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有养殖区域（用于下拉选择）
 * @returns {Promise} 返回所有区域列表
 */
export const getAllAreas = () => {
  return request({
    url: '/area/all',
    method: 'get'
  })
}

/**
 * 根据区域ID查询区域详情
 * @param {Number} areaId - 区域ID
 * @returns {Promise} 返回区域信息
 */
export const getAreaById = (areaId) => {
  return request({
    url: `/area/${areaId}`,
    method: 'get'
  })
}

/**
 * 新增养殖区域
 * @param {Object} data - 区域数据
 * @returns {Promise} 返回操作结果
 */
export const saveArea = (data) => {
  return request({
    url: '/area',
    method: 'post',
    data
  })
}

/**
 * 更新养殖区域信息
 * @param {Object} data - 区域数据
 * @returns {Promise} 返回操作结果
 */
export const updateArea = (data) => {
  return request({
    url: '/area',
    method: 'put',
    data
  })
}

/**
 * 删除养殖区域
 * @param {Number} areaId - 区域ID
 * @returns {Promise} 返回操作结果
 */
export const deleteArea = (areaId) => {
  return request({
    url: `/area/${areaId}`,
    method: 'delete'
  })
}

