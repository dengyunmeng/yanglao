import request from '../utils/axios'

// ===== 系统管理员审核 =====
// 待审核课程
export const getPendingCourses = () => request.get('/admin/courses/pending')
// 审核课程（approved: true/false）
export const reviewCourse = (id, approved) => request.put(`/admin/courses/${id}/review`, { approved })
// 待审核资讯
export const getPendingNews = () => request.get('/admin/news/pending')
// 审核资讯
export const reviewNews = (id, approved) => request.put(`/admin/news/${id}/review`, { approved })
