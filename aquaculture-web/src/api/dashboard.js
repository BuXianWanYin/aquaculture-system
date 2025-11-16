import request from '@/utils/request'

/**
 * 获取首页统计数据
 * @returns {Promise} 返回统计数据 {userCount, planCount, yieldCount, pendingPlanCount}
 */
export const getStatistics = () => {
  return request({
    url: '/dashboard/statistics',
    method: 'get'
  })
}

