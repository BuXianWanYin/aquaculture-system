import request from '@/utils/request'

export const getEquipmentList = (params) => {
  return request({
    url: '/equipment/page',
    method: 'get',
    params
  })
}

export const getAllEquipments = () => {
  return request({
    url: '/equipment/all',
    method: 'get'
  })
}

export const getEquipmentById = (equipmentId) => {
  return request({
    url: `/equipment/${equipmentId}`,
    method: 'get'
  })
}

export const saveEquipment = (data) => {
  return request({
    url: '/equipment',
    method: 'post',
    data
  })
}

export const updateEquipment = (data) => {
  return request({
    url: '/equipment',
    method: 'put',
    data
  })
}

export const deleteEquipment = (equipmentId) => {
  return request({
    url: `/equipment/${equipmentId}`,
    method: 'delete'
  })
}

