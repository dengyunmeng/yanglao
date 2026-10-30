import request from '../utils/axios'

// 登录
export const login = async (phone, password) => {
  const res = await request.post('/auth/login', { phone, password })
  if (res.code === 200) {
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('currentUser', JSON.stringify(res.data.user))
  }
  return res
}

// 注册（后端仅返回成功标记，不含 token，注册后需手动登录）
export const register = async (name, phone, password) => {
  return request.post('/auth/register', { name, phone, password })
}

// 忘记密码：手机号 + 新密码 直接重置
export const resetPassword = async (phone, newPassword) => {
  return request.put('/auth/reset-password', { phone, newPassword })
}

// 当前用户
export const getCurrentUser = async () => {
  const res = await request.get('/auth/me')
  if (res.code === 200) {
    localStorage.setItem('currentUser', JSON.stringify(res.data))
  }
  return res
}

// 登出
export const logout = async () => {
  try {
    await request.post('/auth/logout')
  } catch (e) { /* 忽略后端可能的未登录错误 */ }
  localStorage.removeItem('token')
  localStorage.removeItem('currentUser')
  return { code: 200, message: '退出成功' }
}

// 更新用户
export const updateUser = async (userData) => {
  const res = await request.put('/user', userData)
  if (res.code === 200) {
    localStorage.setItem('currentUser', JSON.stringify(res.data))
  }
  return res
}

// 修改密码
export const changePassword = async (oldPassword, newPassword) => {
  return request.put('/user/password', { oldPassword, newPassword })
}
