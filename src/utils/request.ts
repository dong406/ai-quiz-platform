import axios, { AxiosResponse, AxiosError } from 'axios'
import { Message } from '@arco-design/web-vue'

// 定义统一的响应类型
export interface ApiResponse<T = any> {
  code: number
  data: T
  message?: string
}


const request = axios.create({
  baseURL: '/api',
  timeout: 30000 // 增加到30秒，适配AI接口的响应耗时
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 确保请求携带Cookie（Session）
    config.withCredentials = true
    // 从localStorage获取token
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      const user = JSON.parse(userInfo)
      if (user.token) {
        // 设置Session ID到Cookie（实际项目中可能需要根据后端要求调整）
        document.cookie = `JSESSIONID=${user.token}; path=/`
      }
    }
    // 添加详细的请求日志
    console.log('=== 发送请求 ===')
    console.log('URL:', config.url)
    console.log('Method:', config.method)
    console.log('BaseURL:', config.baseURL)
    console.log('Full URL:', config.baseURL + config.url)
    console.log('Data:', JSON.stringify(config.data, null, 2))
    console.log('Headers:', config.headers)
    return config
  },
  (error) => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    console.log('=== 收到响应 ===')
    console.log('Status:', response.status)
    console.log('URL:', response.config.url)
    console.log('Response Data:', JSON.stringify(response.data, null, 2))
    
    const res = response.data
    
    // 确保res存在
    if (!res) {
      console.error('响应数据为空，完整响应对象:', response)
      Message.error('响应数据为空')
      return Promise.reject(new Error('响应数据为空'))
    }
    
    if (res.code !== 0) {
      console.warn('业务错误:', res.code, res.message)
      // 处理未登录错误
      if (res.code === 40100 || res.code === 40101 || res.message?.includes('未登录') || res.message?.includes('无权限')) {
        // 清除用户信息
        localStorage.removeItem('userInfo')
        // 使用router跳转（需要导入router）
        if (window.location.pathname !== '/user/login' && window.location.pathname !== '/user/register') {
          window.location.href = '/user/login'
        }
        return Promise.reject(new Error('未登录'))
      }
      Message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    console.log('请求成功:', res)
    // 返回res，这样调用方可以直接访问res.code和res.data
    return res as any
  },
  (error) => {
    console.error('=== 请求错误 ===')
    console.error('Error Object:', error)
    console.error('Error Code:', error.code)
    console.error('Error Message:', error.message)
    
    // 处理HTTP状态码错误
    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      console.error('Response Status:', status)
      console.error('Response Data:', data)
      
      // 如果后端返回了BaseResponse格式的错误
      if (data && typeof data === 'object' && 'code' in data && 'message' in data) {
        const errorMessage = data.message || '请求失败'
        if (status === 401 || data.code === 40100 || data.code === 40101) {
          // 未授权，清除用户信息并跳转登录
          localStorage.removeItem('userInfo')
          if (window.location.pathname !== '/user/login') {
            window.location.href = '/user/login'
          }
          Message.error('未登录，请先登录')
        } else {
          Message.error(errorMessage)
        }
        // 返回一个包含错误信息的对象，以便调用方可以访问
        return Promise.reject({
          code: data.code,
          message: errorMessage,
          response: error.response
        })
      }
      
      if (status === 401) {
        // 未授权，清除用户信息并跳转登录
        localStorage.removeItem('userInfo')
        if (window.location.pathname !== '/user/login') {
          window.location.href = '/user/login'
        }
        Message.error('未登录，请先登录')
      } else if (status === 404) {
        console.error('404错误 - 接口不存在，请检查:')
        console.error('  - 后端服务是否启动在 http://localhost:8081')
        console.error('  - 接口路径是否正确: /api/question/add')
        console.error('  - 请求方法是否正确: POST')
        Message.error('请求的接口不存在，请检查后端服务是否启动')
      } else if (status === 405) {
        // 405 Method Not Allowed - HTTP方法不匹配
        console.error('405错误 - HTTP方法不匹配:')
        console.error('  请求URL:', error.config?.url)
        console.error('  请求方法:', error.config?.method?.toUpperCase())
        console.error('  后端期望的方法:', error.response?.headers?.['allow'] || '未知')
        console.error('  请检查前端API调用是否使用了正确的HTTP方法（GET/POST/PUT/DELETE）')
        Message.error('请求方法不正确，请检查接口定义')
      } else if (status >= 500) {
        Message.error('服务器错误，请稍后重试')
      } else {
        Message.error(data?.message || error.message || '请求失败')
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应（网络错误或后端未启动）
      console.error('请求已发出但未收到响应')
      console.error('Request:', error.request)
      console.error('请检查:')
      console.error('  1. 后端服务是否启动在 http://localhost:8081')
      console.error('  2. 网络连接是否正常')
      console.error('  3. 防火墙是否拦截了请求')
      // 检查是否是超时错误
      if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
        Message.error('请求超时，网络波动，请稍后重试')
      } else {
        Message.error('无法连接到服务器，请检查后端服务是否启动在 http://localhost:8081')
      }
    } else {
      // 请求配置错误
      console.error('请求配置错误:', error.message)
      Message.error(error.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export default request
