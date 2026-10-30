import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api', // 基础URL，将来对接真实后端时修改这里
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token（如果有）
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 统一处理响应数据
    const res = response.data
    
    // 这里可以根据后端返回的数据结构进行调整
    // 假设后端返回格式为 { code: 200, data: {}, message: 'success' }
    return res
  },
  error => {
    console.error('响应错误:', error)
    
    // 统一错误处理
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          // 未登录时访问需登录的接口属于正常浏览行为，不弹提示
          if (localStorage.getItem('token')) {
            ElMessage.error('登录已过期，请重新登录')
          }
          localStorage.removeItem('token')
          localStorage.removeItem('currentUser')
          break
        case 403:
          // 未登录（无 token）被拦截属于正常情况，静默处理；带了 token 仍被拒才提示
          if (localStorage.getItem('token')) {
            ElMessage.error('拒绝访问')
          }
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(data?.message || '请求失败')
      }
    } else if (error.request) {
      ElMessage.error('网络错误，请检查网络连接')
    } else {
      ElMessage.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

export default request