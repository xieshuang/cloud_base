import request from './request'

export function getDeptList(params) {
  return request({
    url: '/system/dept/list',
    method: 'get',
    params
  })
}

export function getDeptTree() {
  return request({
    url: '/system/dept/tree',
    method: 'get'
  })
}

export function getDept(id) {
  return request({
    url: `/system/dept/${id}`,
    method: 'get'
  })
}

export function addDept(data) {
  return request({
    url: '/system/dept',
    method: 'post',
    data
  })
}

export function updateDept(data) {
  return request({
    url: '/system/dept',
    method: 'put',
    data
  })
}

export function deleteDept(id) {
  return request({
    url: `/system/dept/${id}`,
    method: 'delete'
  })
}
