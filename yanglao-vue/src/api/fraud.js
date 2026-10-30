import request from '../utils/axios'

export const getFraudCases = () => request.get('/fraud/cases')
export const submitFraudReport = (data) => request.post('/fraud/reports', data)
export const getFraudReports = () => request.get('/fraud/reports')
export const submitFraudTest = (answers) => request.post('/fraud/tests/submit', { answers })
export const getFraudTestHistory = () => request.get('/fraud/tests/history')
