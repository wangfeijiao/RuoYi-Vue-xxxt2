import request from '@/utils/request'

export function listProject(query) {
  return request({ url: '/information/project/list', method: 'get', params: query })
}

export function getProject(projectId) {
  return request({ url: '/information/project/' + projectId, method: 'get' })
}

export function addProject(data) {
  return request({ url: '/information/project', method: 'post', data })
}

export function updateProject(data) {
  return request({ url: '/information/project', method: 'put', data })
}

export function delProject(projectId) {
  return request({ url: '/information/project/' + projectId, method: 'delete' })
}

export function listProjectTemplate(query) {
  return request({ url: '/information/project/template/list', method: 'get', params: query })
}

export function getProjectTemplate(templateId) {
  return request({ url: '/information/project/template/' + templateId, method: 'get' })
}

export function addProjectTemplate(data) {
  return request({ url: '/information/project/template', method: 'post', data })
}

export function updateProjectTemplate(data) {
  return request({ url: '/information/project/template', method: 'put', data })
}

export function delProjectTemplate(templateId) {
  return request({ url: '/information/project/template/' + templateId, method: 'delete' })
}

export function listProjectOrder(query) {
  return request({ url: '/information/project/order/list', method: 'get', params: query })
}

export function addProjectOrder(data) {
  return request({ url: '/information/project/order', method: 'post', data })
}

export function updateProjectOrder(data) {
  return request({ url: '/information/project/order', method: 'put', data })
}

export function approveProjectOrder(workOrderId, data) {
  return request({ url: '/information/project/order/' + workOrderId + '/approve', method: 'post', data })
}

export function executeProjectOrder(workOrderId, data) {
  return request({ url: '/information/project/order/' + workOrderId + '/execute', method: 'post', data })
}

export function listProjectPermission(projectId, query) {
  return request({ url: '/information/project/' + projectId + '/permissions', method: 'get', params: query })
}

export function addProjectPermission(projectId, data) {
  return request({ url: '/information/project/' + projectId + '/permissions', method: 'post', data })
}

export function updateProjectPermission(projectId, permissionId, data) {
  return request({ url: '/information/project/' + projectId + '/permissions/' + permissionId, method: 'put', data })
}

export function delProjectPermission(projectId, permissionId) {
  return request({ url: '/information/project/' + projectId + '/permissions/' + permissionId, method: 'delete' })
}

export function getProjectPermissionMatrix(projectId) {
  return request({ url: '/information/project/' + projectId + '/permission-matrix', method: 'get' })
}
