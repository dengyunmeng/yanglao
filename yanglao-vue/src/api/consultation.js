import request from '../utils/axios'

// 用户提问
export const askQuestion = (question) => request.post('/consultations', { question })
// 我的提问
export const getMyConsultations = () => request.get('/consultations/my')
// 已回复问答（所有人可看）
export const getAnsweredConsultations = () => request.get('/consultations/answered')
// 待回复提问（医生/管理员）
export const getPendingConsultations = () => request.get('/consultations/pending')
// 回复提问（医生/管理员）
export const answerConsultation = (id, answer) => request.put(`/consultations/${id}/answer`, { answer })
