import request from './request'

export function getUserList(params) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params
  })
}

export function getUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'get'
  })
}

export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

export function deleteUser(ids) {
  return request({
    url: `/system/user/${ids}`,
    method: 'delete'
  })
}

export function resetPwd(data) {
  return request({
    url: '/system/user/resetPwd',
    method: 'put',
    data
  })
}

export function changeStatus(data) {
  return request({
    url: '/system/user/changeStatus',
    method: 'put',
    data
  })
}
