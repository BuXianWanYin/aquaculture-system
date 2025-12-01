import request from '@/utils/request'

/**
 * 通用图片上传接口
 * @param {FormData} formData - 文件表单数据，包含file和module
 * @param {String} module - 模块名称（breed/equipment/feed/disease）
 * @returns {Promise} 返回上传结果，包含文件路径等信息
 */
export const uploadImage = (formData, module = 'common') => {
  formData.append('module', module)
  return request({
    url: '/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

