import request from './request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request({
    url: '/auth/getUserInfo',
    method: 'post'
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}
