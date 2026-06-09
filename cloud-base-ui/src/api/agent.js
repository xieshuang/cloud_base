import request from './request'

/**
 * 天气查询聊天接口
 * @param {Object} data - { message: string }
 * @returns {Promise<{code: number, data: string, message: string}>}
 */
export function weatherChat(data) {
  return request({
    url: '/agent/weather/chat',
    method: 'post',
    data
  })
}
