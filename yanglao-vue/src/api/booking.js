import request from '../utils/axios'

// 创建活动预约
export const createBooking = (data) => {
  // 期望 data: { courseId, bookDate, timeSlot, note }
  return request.post('/bookings', data)
}

// 获取预约列表
export const getBookings = ({ page = 1, pageSize = 10 } = {}) => {
  return request.get('/bookings', { params: { page, pageSize } })
}

// 取消预约
export const cancelBooking = (id) => {
  return request.put(`/bookings/${id}/cancel`)
}

// 获取预约数量
export const getBookingCount = () => {
  return request.get('/bookings/count')
}

// 预约名单（健康管理员看自己课程，系统管理员看全部）
export const getManagedBookings = (courseId) => {
  return request.get('/bookings/manage', { params: courseId ? { courseId } : {} })
}

// 确认预约（医生/管理员）
export const confirmBooking = (id) => {
  return request.put(`/bookings/${id}/confirm`)
}
