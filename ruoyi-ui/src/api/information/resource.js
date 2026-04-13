import request from '@/utils/request'

export function listResource(query) {
  return request({ url: '/information/resource/list', method: 'get', params: query })
}

export function getResource(resourceId) {
  return request({ url: '/information/resource/' + resourceId, method: 'get' })
}

export function addResource(data) {
  return request({ url: '/information/resource', method: 'post', data })
}

export function updateResource(data) {
  return request({ url: '/information/resource', method: 'put', data })
}

export function delResource(resourceId) {
  return request({ url: '/information/resource/' + resourceId, method: 'delete' })
}

export function listResourceOrder(query) {
  return request({ url: '/information/resource/order/list', method: 'get', params: query })
}

export function getResourceOrder(workOrderId) {
  return request({ url: '/information/resource/order/' + workOrderId, method: 'get' })
}

export function addResourceOrder(data) {
  return request({ url: '/information/resource/order', method: 'post', data })
}

export function updateResourceOrder(data) {
  return request({ url: '/information/resource/order', method: 'put', data })
}

export function approveResourceOrder(workOrderId, data) {
  return request({ url: '/information/resource/order/' + workOrderId + '/approve', method: 'post', data })
}

export function executeResourceOrder(workOrderId, data) {
  return request({ url: '/information/resource/order/' + workOrderId + '/execute', method: 'post', data })
}

export function delResourceOrder(workOrderId) {
  return request({ url: '/information/resource/order/' + workOrderId, method: 'delete' })
}
