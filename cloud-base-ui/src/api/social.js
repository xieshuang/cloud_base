import request from './request'

export function weixinMiniLogin(data) {
  return request({
    url: '/auth/weixin/mini/login',
    method: 'post',
    data
  })
}

export function weixinMiniBind(data) {
  return request({
    url: '/auth/weixin/mini/bind',
    method: 'post',
    data
  })
}

export function weixinMiniCreate(data) {
  return request({
    url: '/auth/weixin/mini/create',
    method: 'post',
    data
  })
}

export function weixinMpLogin(data) {
  return request({
    url: '/auth/weixin/mp/login',
    method: 'post',
    data
  })
}

export function weixinMpBind(data) {
  return request({
    url: '/auth/weixin/mp/bind',
    method: 'post',
    data
  })
}

export function weixinMpCreate(data) {
  return request({
    url: '/auth/weixin/mp/create',
    method: 'post',
    data
  })
}

export function getSocialBindStatus(userId) {
  return request({
    url: '/auth/social/bind/status',
    method: 'get',
    params: { userId }
  })
}