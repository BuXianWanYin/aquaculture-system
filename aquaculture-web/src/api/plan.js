import request from '@/utils/request'

export const getPlanList = (params) => {
  return request({
    url: '/plan/page',
    method: 'get',
    params
  })
}

export const getAllPlans = () => {
  return request({
    url: '/plan/all',
    method: 'get'
  })
}

export const getPlanById = (planId) => {
  return request({
    url: `/plan/${planId}`,
    method: 'get'
  })
}

export const savePlan = (data) => {
  return request({
    url: '/plan',
    method: 'post',
    data
  })
}

export const updatePlan = (data) => {
  return request({
    url: '/plan',
    method: 'put',
    data
  })
}

export const deletePlan = (planId) => {
  return request({
    url: `/plan/${planId}`,
    method: 'delete'
  })
}

export const approvePlan = (data) => {
  return request({
    url: '/plan/approve',
    method: 'post',
    data
  })
}

