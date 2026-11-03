/**
 * 角色判断工具
 *
 * 后端返回的角色带 ROLE_ 前缀（如 ROLE_DOCTOR），这里统一去掉前缀，
 * 便于页面按角色控制导航与功能入口的显示。
 */
import { getUser } from './session'

export const getRoleCodes = () => {
  const roles = getUser()?.roles || []
  return roles.map(role => String(role).replace('ROLE_', ''))
}

export const hasRole = (role) => getRoleCodes().includes(role)

// 健康管理员（医生）或系统管理员：走管理控制台，不展示普通用户的业务入口
export const isManager = () => hasRole('DOCTOR') || hasRole('ADMIN')

export const isAdmin = () => hasRole('ADMIN')
