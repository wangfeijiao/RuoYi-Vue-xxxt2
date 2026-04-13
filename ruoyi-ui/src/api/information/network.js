import request from '@/utils/request'

export function listNetwork(query) {
  return request({ url: '/information/network/list', method: 'get', params: query })
}

export function getNetwork(networkId) {
  return request({ url: '/information/network/' + networkId, method: 'get' })
}

export function addNetwork(data) {
  return request({ url: '/information/network', method: 'post', data })
}

export function updateNetwork(data) {
  return request({ url: '/information/network', method: 'put', data })
}

export function delNetwork(networkId) {
  return request({ url: '/information/network/' + networkId, method: 'delete' })
}

export function listNetworkOrder(query) {
  return request({ url: '/information/network/order/list', method: 'get', params: query })
}

export function getNetworkOrder(workOrderId) {
  return request({ url: '/information/network/order/' + workOrderId, method: 'get' })
}

export function addNetworkOrder(data) {
  return request({ url: '/information/network/order', method: 'post', data })
}

export function updateNetworkOrder(data) {
  return request({ url: '/information/network/order', method: 'put', data })
}

export function approveNetworkOrder(workOrderId, data) {
  return request({ url: '/information/network/order/' + workOrderId + '/approve', method: 'post', data })
}

export function executeNetworkOrder(workOrderId, data) {
  return request({ url: '/information/network/order/' + workOrderId + '/execute', method: 'post', data })
}
