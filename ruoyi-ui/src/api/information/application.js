import request from '@/utils/request'

export function listApplication(query) {
  return request({ url: '/information/application/list', method: 'get', params: query })
}

export function getApplication(applicationId) {
  return request({ url: '/information/application/' + applicationId, method: 'get' })
}

export function addApplication(data) {
  return request({ url: '/information/application', method: 'post', data })
}

export function updateApplication(data) {
  return request({ url: '/information/application', method: 'put', data })
}

export function delApplication(applicationId) {
  return request({ url: '/information/application/' + applicationId, method: 'delete' })
}
