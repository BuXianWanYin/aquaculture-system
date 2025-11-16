import request from '@/utils/request'

/**
 * 获取首页统计数据
 * @returns {Promise} 返回统计数据 {userCount, planCount, yieldCount, pendingPlanCount, ...}
 */
export const getStatistics = () => {
  return request({
    url: '/dashboard/statistics',
    method: 'get'
  })
}

/**
 * 获取最近的计划列表
 * @param {Number} size - 数量，默认5
 * @returns {Promise} 返回最近计划列表
 */
export const getRecentPlans = (size = 5) => {
  return request({
    url: '/dashboard/recent/plans',
    method: 'get',
    params: { size }
  })
}

/**
 * 获取最近的操作日志
 * @param {Number} size - 数量，默认10
 * @returns {Promise} 返回最近操作日志列表
 */
export const getRecentLogs = (size = 10) => {
  return request({
    url: '/dashboard/recent/logs',
    method: 'get',
    params: { size }
  })
}

/**
 * 获取未读消息列表
 * @param {Number} size - 数量，默认5
 * @returns {Promise} 返回未读消息列表
 */
export const getRecentMessages = (size = 5) => {
  return request({
    url: '/dashboard/recent/messages',
    method: 'get',
    params: { size }
  })
}

