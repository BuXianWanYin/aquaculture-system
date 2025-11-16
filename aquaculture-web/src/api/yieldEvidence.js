import request from '@/utils/request'

/**
 * 根据产量ID查询产量凭证列表
 * @param {Number} yieldId - 产量统计ID
 * @returns {Promise} 返回凭证列表
 */
export const getEvidenceList = (yieldId) => {
  return request({
    url: `/yieldEvidence/yield/${yieldId}`,
    method: 'get'
  })
}

/**
 * 新增产量凭证（保存凭证信息，需先上传文件）
 * @param {Object} data - 凭证数据 {yieldId, filePath, fileType, fileSize, uploaderId}
 * @returns {Promise} 返回操作结果
 */
export const saveEvidence = (data) => {
  return request({
    url: '/yieldEvidence',
    method: 'post',
    data
  })
}

/**
 * 删除产量凭证
 * @param {Number} evidenceId - 凭证ID
 * @returns {Promise} 返回操作结果
 */
export const deleteEvidence = (evidenceId) => {
  return request({
    url: `/yieldEvidence/${evidenceId}`,
    method: 'delete'
  })
}

/**
 * 上传产量凭证文件
 * @param {FormData} formData - 文件表单数据，包含file和yieldId
 * @returns {Promise} 返回上传结果，包含文件路径等信息
 */
export const uploadEvidence = (formData) => {
  return request({
    url: '/yieldEvidence/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

