import request from '@/utils/request'

/**
 * 分页查询养殖品种列表
 * @param {Object} params - 查询参数 {current, size, breedName, category}
 * @returns {Promise} 返回分页品种列表
 */
export const getBreedList = (params) => {
  return request({
    url: '/breed/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有养殖品种（用于下拉选择）
 * @returns {Promise} 返回所有品种列表
 */
export const getAllBreeds = () => {
  return request({
    url: '/breed/all',
    method: 'get'
  })
}

/**
 * 根据品种ID查询品种详情
 * @param {Number} breedId - 品种ID
 * @returns {Promise} 返回品种信息
 */
export const getBreedById = (breedId) => {
  return request({
    url: `/breed/${breedId}`,
    method: 'get'
  })
}

/**
 * 新增养殖品种
 * @param {Object} data - 品种数据
 * @returns {Promise} 返回操作结果
 */
export const saveBreed = (data) => {
  return request({
    url: '/breed',
    method: 'post',
    data
  })
}

/**
 * 更新养殖品种信息
 * @param {Object} data - 品种数据
 * @returns {Promise} 返回操作结果
 */
export const updateBreed = (data) => {
  return request({
    url: '/breed',
    method: 'put',
    data
  })
}

/**
 * 删除养殖品种
 * @param {Number} breedId - 品种ID
 * @returns {Promise} 返回操作结果
 */
export const deleteBreed = (breedId) => {
  return request({
    url: `/breed/${breedId}`,
    method: 'delete'
  })
}

