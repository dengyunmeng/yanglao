/**
 * 健康打卡记录本地存储（按用户隔离）
 *
 * 之前的 key 是全局的 healthRecords，导致未登录或切换账号时
 * 会看到上一个用户留下的打卡数据。这里统一按 userId 加后缀。
 */
import { getUser } from './session'

const storageKey = () => {
  const userId = getUser()?.id
  return userId ? `healthRecords_${userId}` : null
}

export const getHealthRecords = () => {
  const key = storageKey()
  if (!key) return []
  try {
    return JSON.parse(localStorage.getItem(key) || '[]')
  } catch (e) {
    return []
  }
}

export const saveHealthRecords = (records) => {
  const key = storageKey()
  if (!key) return false
  localStorage.setItem(key, JSON.stringify(records))
  return true
}
