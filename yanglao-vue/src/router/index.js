import { createRouter, createWebHistory } from 'vue-router'

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
    meta: { title: '健康守护 - 智慧养老' }
  },
  {
    path: '/anti-fraud',
    name: 'AntiFraud',
    component: () => import('../views/AntiFraudView.vue'),
    meta: { title: '数字反诈 - 智慧养老' }
  },
  {
    path: '/classroom',
    name: 'Classroom',
    component: () => import('../views/ClassroomView.vue'),
    meta: { title: '银发课堂 - 智慧养老' }
  },
  {
    path: '/my-courses',
    name: 'MyCourses',
    component: () => import('../views/MyCoursesView.vue'),
    meta: { title: '我的课程 - 智慧养老' }
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
    meta: { title: '管理控制台 - 智慧养老' }
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
  next()
})

export default router
