import request from '@/utils/request'

export const getAdjustList = (params) => {
  return request({
    url: '/planAdjust/page',
    method: 'get',
    params
  })
}

export const getAdjustByPlanId = (planId) => {
  return request({
    url: `/planAdjust/plan/${planId}`,
    method: 'get'
  })
}

export const getAdjustById = (adjustId) => {
  return request({
    url: `/planAdjust/${adjustId}`,
    method: 'get'
  })
}

export const saveAdjust = (data) => {
  return request({
    url: '/planAdjust',
    method: 'post',
    data
  })
}

export const updateAdjust = (data) => {
  return request({
    url: '/planAdjust',
    method: 'put',
    data
  })
}

export const approveAdjust = (data) => {
  return request({
    url: '/planAdjust/approve',
    method: 'post',
    data
  })
}

