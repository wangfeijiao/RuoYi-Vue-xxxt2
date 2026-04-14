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

export function getApplicationDetail(applicationId) {
  return request({ url: '/information/application/' + applicationId + '/detail', method: 'get' })
}

export function listApplicationProjects(applicationId, query) {
  return request({ url: '/information/application/' + applicationId + '/projects', method: 'get', params: query })
}

export function addApplicationProject(applicationId, data) {
  return request({ url: '/information/application/' + applicationId + '/projects', method: 'post', data })
}

export function delApplicationProject(applicationId, relId) {
  return request({ url: '/information/application/' + applicationId + '/projects/' + relId, method: 'delete' })
}

export function listApplicationDependencies(applicationId, query) {
  return request({ url: '/information/application/' + applicationId + '/dependencies', method: 'get', params: query })
}

export function addApplicationDependency(applicationId, data) {
  return request({ url: '/information/application/' + applicationId + '/dependencies', method: 'post', data })
}

export function updateApplicationDependency(applicationId, dependencyId, data) {
  return request({ url: '/information/application/' + applicationId + '/dependencies/' + dependencyId, method: 'put', data })
}

export function delApplicationDependency(applicationId, dependencyId) {
  return request({ url: '/information/application/' + applicationId + '/dependencies/' + dependencyId, method: 'delete' })
}

export function getApplicationStatusOverview(applicationId) {
  return request({ url: '/information/application/' + applicationId + '/status-overview', method: 'get' })
}

export function recalculateApplicationStatus(applicationId) {
  return request({ url: '/information/application/' + applicationId + '/status/recalculate', method: 'post' })
}

export function listApplicationAlerts(applicationId, query) {
  return request({ url: '/information/application/' + applicationId + '/alerts', method: 'get', params: query })
}

export function listApplicationNotices(applicationId, query) {
  return request({ url: '/information/application/' + applicationId + '/notices', method: 'get', params: query })
}

export function ackApplicationAlert(alertId, data) {
  return request({ url: '/information/application/alerts/' + alertId + '/ack', method: 'post', data })
}

export function resolveApplicationAlert(alertId, data) {
  return request({ url: '/information/application/alerts/' + alertId + '/resolve', method: 'post', data })
}

export function getApplicationStatisticsOverview(query) {
  return request({ url: '/information/application/statistics/overview', method: 'get', params: query })
}
