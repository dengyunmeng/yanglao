/**
 * 认证信息存储
 *
 * 使用 sessionStorage 而不是 localStorage：
 * sessionStorage 按标签页/窗口隔离，同一浏览器可以同时打开多个窗口，
 * 分别用普通用户、健康管理员、系统管理员登录，互不影响，方便演示。
 */
const TOKEN_KEY = 'token'
const USER_KEY = 'currentUser'

export const getToken = () => sessionStorage.getItem(TOKEN_KEY)

export const setToken = (token) => sessionStorage.setItem(TOKEN_KEY, token)

export const getUser = () => {
  try {
    return JSON.parse(sessionStorage.getItem(USER_KEY) || 'null')
  } catch (e) {
    return null
  }
}

export const setUser = (user) => sessionStorage.setItem(USER_KEY, JSON.stringify(user))

export const clearAuth = () => {
  sessionStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(USER_KEY)
}
