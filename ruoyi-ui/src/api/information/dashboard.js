import request from '@/utils/request'

export function getOverview() {
  return request({
    url: '/information/dashboard/overview',
    method: 'get'
  })
}
