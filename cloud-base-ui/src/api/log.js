import request from './request'

export function getOperLogList(params) {
  return request({
    url: '/logs/oper/list',
    method: 'get',
    params
  })
}

export function getLoginLogList(params) {
  return request({
    url: '/logs/login/list',
    method: 'get',
    params
  })
}

export function deleteLog(ids) {
  return request({
    url: `/logs/${ids}`,
    method: 'delete'
  })
}

export function cleanLog() {
  return request({
    url: '/logs/clean',
    method: 'delete'
  })
}
