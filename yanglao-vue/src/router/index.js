import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRoleCodes } from '../utils/roles'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue'),
    meta: { title: '智慧养老服务平台' }
  },
  {
    path: '/health',
    name: 'Health',
    component: () => import('../views/HealthView.vue'),
    meta: { title: '健康守护 - 智慧养老', roles: ['USER'] }
  },
  {
    path: '/anti-fraud',
    name: 'AntiFraud',
    component: () => import('../views/AntiFraudView.vue'),
    meta: { title: '数字反诈 - 智慧养老', roles: ['USER'] }
  },
  {
    path: '/classroom',
    name: 'Classroom',
    component: () => import('../views/ClassroomView.vue'),
    meta: { title: '银发课堂 - 智慧养老', roles: ['USER', 'DOCTOR'] }
  },
  {
    path: '/my-courses',
    name: 'MyCourses',
    component: () => import('../views/MyCoursesView.vue'),
    meta: { title: '我的课程 - 智慧养老', roles: ['USER'] }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue'),
    meta: { title: '个人中心 - 智慧养老' }
  },
  {
    path: '/console',
    name: 'Console',
    component: () => import('../views/ConsoleView.vue'),
    meta: { title: '管理控制台 - 智慧养老', roles: ['DOCTOR', 'ADMIN'] }
  },
  {
    path: '/news/:id',
    name: 'NewsDetail',
    component: () => import('../views/NewsDetailView.vue'),
    meta: { title: '资讯详情 - 智慧养老' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '智慧养老服务平台'

  // 按角色限制页面访问：未登录时放行（由页面自行提示登录）
  const roles = getRoleCodes()
  if (to.meta.roles && roles.length && !to.meta.roles.some(role => roles.includes(role))) {
    ElMessage.warning('当前角色无法访问该功能')
    const isManagerRole = roles.includes('DOCTOR') || roles.includes('ADMIN')
    return next(isManagerRole ? '/console' : '/')
  }

  next()
})

export default router
