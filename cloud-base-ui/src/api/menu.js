import request from './request'

export function getMenuList(params) {
  return request({
    url: '/system/menu/list',
    method: 'get',
    params
  })
}

export function getMenuTree() {
  return request({
    url: '/system/menu/tree',
    method: 'get'
  })
}

export function getUserMenuTree() {
  return request({
    url: '/system/menu/userTree',
    method: 'get'
  })
}

export function getMenu(id) {
  return request({
    url: `/system/menu/${id}`,
    method: 'get'
  })
}

export function addMenu(data) {
  return request({
    url: '/system/menu',
    method: 'post',
    data
  })
}

export function updateMenu(data) {
  return request({
    url: '/system/menu',
    method: 'put',
    data
  })
}

export function deleteMenu(id) {
  return request({
    url: `/system/menu/${id}`,
    method: 'delete'
  })
}

export function getSelectTree() {
  return request({
    url: '/system/menu/selectTree',
    method: 'get'
  })
}
