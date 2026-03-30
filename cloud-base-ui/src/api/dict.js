import request from './request'

export function getDictList(params) {
  return request({
    url: '/system/dict/list',
    method: 'get',
    params
  })
}

export function getDict(id) {
  return request({
    url: `/system/dict/${id}`,
    method: 'get'
  })
}

export function addDict(data) {
  return request({
    url: '/system/dict',
    method: 'post',
    data
  })
}

export function updateDict(data) {
  return request({
    url: '/system/dict',
    method: 'put',
    data
  })
}

export function deleteDict(ids) {
  return request({
    url: `/system/dict/${ids}`,
    method: 'delete'
  })
}
