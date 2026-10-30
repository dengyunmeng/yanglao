import request from '../utils/axios'

// 提交健康打卡
// 前端 checkinForm.bloodPressure 是 "120/80" 格式的字符串，需要拆分
export const submitHealthCheckin = async (healthData) => {
  const { bloodPressure, ...rest } = healthData
  let systolicPressure = null
  let diastolicPressure = null
  if (bloodPressure && typeof bloodPressure === 'string' && bloodPressure.includes('/')) {
    const [s, d] = bloodPressure.split('/').map(v => parseInt(v.trim(), 10))
    if (!isNaN(s)) systolicPressure = s
    if (!isNaN(d)) diastolicPressure = d
  }
  const body = {
    systolicPressure,
    diastolicPressure,
    bloodSugar: healthData.bloodSugar,
    sleepHours: healthData.sleepHours,
    heartRate: healthData.heartRate,
    feeling: healthData.feeling,
    note: healthData.note
  }
  return request.post('/health/records', body)
}

// 获取健康记录
export const getHealthRecords = ({ page = 1, pageSize = 10 } = {}) => {
  return request.get('/health/records', { params: { page, pageSize } })
}

// 获取健康统计
export const getHealthStats = () => {
  return request.get('/health/records/stats')
}

// 获取打卡天数
export const getCheckinDays = () => {
  return request.get('/health/records/checkinDays')
}
