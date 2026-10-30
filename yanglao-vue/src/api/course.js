import request from '../utils/axios'

export const getCourses = (params = {}) => request.get('/courses', { params })
export const getMyCourses = () => request.get('/courses/my')
export const joinCourse = (courseId) => request.post('/courses/my', { courseId })
export const leaveCourse = (courseId) => request.delete(`/courses/my/${courseId}`)

// 健康管理员/系统管理员：发布课程
export const createCourse = (data) => request.post('/courses', data)
// 我发布的课程（含待审核/已驳回）
export const getManagedCourses = () => request.get('/courses/mine')
