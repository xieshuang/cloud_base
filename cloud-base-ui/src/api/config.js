import request from './request'

export function getConfigList(params) {
  return request({
    url: '/system/config/list',
    method: 'get',
    params
  })
}

export function getConfig(id) {
  return request({
    url: `/system/config/${id}`,
    method: 'get'
  })
}

export function getConfigByKey(configKey) {
  return request({
    url: `/system/config/configKey/${configKey}`,
    method: 'get'
  })
}

export function addConfig(data) {
  return request({
    url: '/system/config',
    method: 'post',
    data
  })
}

export function updateConfig(data) {
  return request({
    url: '/system/config',
    method: 'put',
    data
  })
}

export function deleteConfig(ids) {
  return request({
    url: `/system/config/${ids}`,
    method: 'delete'
  })
}
