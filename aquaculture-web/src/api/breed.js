import request from '@/utils/request'

export const getBreedList = (params) => {
  return request({
    url: '/breed/page',
    method: 'get',
    params
  })
}

export const getAllBreeds = () => {
  return request({
    url: '/breed/all',
    method: 'get'
  })
}

export const getBreedById = (breedId) => {
  return request({
    url: `/breed/${breedId}`,
    method: 'get'
  })
}

export const saveBreed = (data) => {
  return request({
    url: '/breed',
    method: 'post',
    data
  })
}

export const updateBreed = (data) => {
  return request({
    url: '/breed',
    method: 'put',
    data
  })
}

export const deleteBreed = (breedId) => {
  return request({
    url: `/breed/${breedId}`,
    method: 'delete'
  })
}

