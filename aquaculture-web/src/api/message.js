import request from '@/utils/request'

/**
 * 分页查询消息列表（按接收人）
 * @param {Object} params - 查询参数 {current, size, receiverId, messageType, status, keyword, startTime, endTime}
 * @returns {Promise} 返回分页消息列表
 */
export const getMessageList = (params) => {
  return request({
    url: '/message/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询消息详情
 * @param {Number} messageId - 消息ID
 * @returns {Promise} 返回消息信息
 */
export const getMessageById = (messageId) => {
  return request({
    url: `/message/${messageId}`,
    method: 'get'
  })
}

/**
 * 发送消息
 * @param {Object} data - 消息数据
 * @returns {Promise} 返回操作结果
 */
export const sendMessage = (data) => {
  return request({
    url: '/message',
    method: 'post',
    data
  })
}

/**
 * 标记消息为已读
 * @param {Number} messageId - 消息ID
 * @param {Number} receiverId - 接收人ID
 * @returns {Promise} 返回操作结果
 */
export const markAsRead = (messageId, receiverId) => {
  return request({
    url: `/message/${messageId}/read`,
    method: 'put',
    params: { receiverId }
  })
}

/**
 * 批量标记为已读
 * @param {Array} messageIds - 消息ID数组
 * @param {Number} receiverId - 接收人ID
 * @returns {Promise} 返回操作结果
 */
export const markBatchAsRead = (messageIds, receiverId) => {
  return request({
    url: '/message/batch/read',
    method: 'put',
    data: messageIds,
    params: { receiverId }
  })
}

/**
 * 标记全部为已读
 * @param {Number} receiverId - 接收人ID
 * @returns {Promise} 返回操作结果
 */
export const markAllAsRead = (receiverId) => {
  return request({
    url: '/message/all/read',
    method: 'put',
    params: { receiverId }
  })
}

/**
 * 删除消息
 * @param {Number} messageId - 消息ID
 * @param {Number} receiverId - 接收人ID
 * @returns {Promise} 返回操作结果
 */
export const deleteMessage = (messageId, receiverId) => {
  return request({
    url: `/message/${messageId}`,
    method: 'delete',
    params: { receiverId }
  })
}

/**
 * 批量删除消息
 * @param {Array} messageIds - 消息ID数组
 * @param {Number} receiverId - 接收人ID
 * @returns {Promise} 返回操作结果
 */
export const deleteBatch = (messageIds, receiverId) => {
  return request({
    url: '/message/batch',
    method: 'delete',
    data: messageIds,
    params: { receiverId }
  })
}

/**
 * 获取未读消息数量
 * @param {Number} receiverId - 接收人ID
 * @returns {Promise} 返回未读消息数量
 */
export const getUnreadCount = (receiverId) => {
  return request({
    url: '/message/unread/count',
    method: 'get',
    params: { receiverId }
  })
}

/**
 * 获取未读消息列表
 * @param {Number} receiverId - 接收人ID
 * @param {Number} current - 当前页
 * @param {Number} size - 每页数量
 * @returns {Promise} 返回未读消息列表
 */
export const getUnreadMessages = (receiverId, current = 1, size = 10) => {
  return request({
    url: '/message/unread',
    method: 'get',
    params: { receiverId, current, size }
  })
}

