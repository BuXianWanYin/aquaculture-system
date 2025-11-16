import request from '@/utils/request'

export const getAreaList = (params) => {
  return request({
    url: '/area/page',
    method: 'get',
    params
  })
}

export const getAllAreas = () => {
  return request({
    url: '/area/all',
    method: 'get'
  })
}

export const getAreaById = (areaId) => {
  return request({
    url: `/area/${areaId}`,
    method: 'get'
  })
}

export const saveArea = (data) => {
  return request({
    url: '/area',
    method: 'post',
    data
  })
}

export const updateArea = (data) => {
  return request({
    url: '/area',
    method: 'put',
    data
  })
}

export const deleteArea = (areaId) => {
  return request({
    url: `/area/${areaId}`,
    method: 'delete'
  })
}

