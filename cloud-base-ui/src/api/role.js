import request from './request'

export function getRoleList(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}

export function getRole(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

export function deleteRole(ids) {
  return request({
    url: `/system/role/${ids}`,
    method: 'delete'
  })
}

export function changeRoleStatus(data) {
  return request({
    url: '/system/role/changeStatus',
    method: 'put',
    data
  })
}

export function getRoleMenus(id) {
  return request({
    url: `/system/role/${id}/menus`,
    method: 'get'
  })
}

export function assignRoleMenus(id, menuIds) {
  return request({
    url: `/system/role/${id}/menus`,
    method: 'put',
    data: menuIds
  })
}
