import request from '@/utils/request'

export function listDataAsset(query) {
  return request({ url: '/information/dataAsset/list', method: 'get', params: query })
}

export function getDataAsset(assetId) {
  return request({ url: '/information/dataAsset/' + assetId, method: 'get' })
}

export function addDataAsset(data) {
  return request({ url: '/information/dataAsset', method: 'post', data })
}

export function updateDataAsset(data) {
  return request({ url: '/information/dataAsset', method: 'put', data })
}

export function delDataAsset(assetId) {
  return request({ url: '/information/dataAsset/' + assetId, method: 'delete' })
}

export function listDataVersions(assetId) {
  return request({ url: '/information/dataAsset/' + assetId + '/versions', method: 'get' })
}

export function listDataOrder(query) {
  return request({ url: '/information/dataAsset/order/list', method: 'get', params: query })
}

export function getDataOrder(workOrderId) {
  return request({ url: '/information/dataAsset/order/' + workOrderId, method: 'get' })
}

export function addDataOrder(data) {
  return request({ url: '/information/dataAsset/order', method: 'post', data })
}

export function updateDataOrder(data) {
  return request({ url: '/information/dataAsset/order', method: 'put', data })
}

export function approveDataOrder(workOrderId, data) {
  return request({ url: '/information/dataAsset/order/' + workOrderId + '/approve', method: 'post', data })
}

export function executeDataOrder(workOrderId, data) {
  return request({ url: '/information/dataAsset/order/' + workOrderId + '/execute', method: 'post', data })
}
