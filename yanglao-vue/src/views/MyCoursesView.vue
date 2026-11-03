<template>
  <div class="my-courses-view">
    <header class="main-header">
      <nav class="navbar navbar-expand-lg">
        <div class="container">
          <router-link to="/" class="navbar-brand">
            <i class="bi bi-heart-pulse"></i> 智慧养老
          </router-link>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
              <li class="nav-item">
                <router-link to="/" class="nav-link"><i class="bi bi-house"></i> 首页</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/health" class="nav-link"><i class="bi bi-heart-pulse"></i> 健康守护</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/anti-fraud" class="nav-link"><i class="bi bi-shield-plus"></i> 数字反诈</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/classroom" class="nav-link active"><i class="bi bi-mortarboard"></i> 银发课堂</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/profile" class="nav-link"><i class="bi bi-person-circle"></i> 个人中心</router-link>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    </header>

    <div class="page-section active">
      <div class="container">
        <h2 class="section-title">
          <i class="bi bi-bookmark"></i> 我的课程
          <el-button size="small" @click="router.push('/classroom')" style="margin-left: auto;">
            <i class="bi bi-arrow-left"></i> 返回课程列表
          </el-button>
        </h2>

        <div v-if="loading" class="loading-box">
          <el-skeleton :rows="3" animated />
        </div>

        <div v-else-if="myCourses.length > 0">
          <div class="course-filter">
            <el-radio-group v-model="typeFilter" size="small">
              <el-radio-button label="all">全部（{{ myCourses.length }}）</el-radio-button>
              <el-radio-button label="ONLINE">线上课（{{ onlineCount }}）</el-radio-button>
              <el-radio-button label="OFFLINE">线下课（{{ offlineCount }}）</el-radio-button>
            </el-radio-group>
          </div>

          <div class="course-grid">
            <el-card v-for="(course, i) in filteredCourses" :key="course.id" :body-style="{ padding: '0px' }" shadow="hover">
              <div class="course-cover" :style="{ background: styleOf(i).bgColor }">
                <i :class="styleOf(i).icon"></i>
                <span class="course-tag">{{ course.tag || '学习中' }}</span>
                <span class="course-type" :class="isOffline(course) ? 'type-offline' : 'type-online'">
                  <i :class="isOffline(course) ? 'bi bi-geo-alt' : 'bi bi-camera-video'"></i>
                  {{ isOffline(course) ? '线下课' : '线上课' }}
                </span>
              </div>
              <div class="course-info">
                <h5>{{ course.name }}</h5>
                <p>{{ course.description }}</p>

                <!-- 线下课显示开课时间与地点 -->
                <div v-if="isOffline(course)" class="course-meta">
                  <span v-if="course.startTime"><i class="bi bi-clock"></i> {{ formatTime(course.startTime) }}</span>
                  <span v-if="course.location"><i class="bi bi-geo-alt"></i> {{ course.location }}</span>
                </div>

                <el-button
                  v-if="isOffline(course)"
                  type="success"
                  style="width: 100%;"
                  @click="viewBooking(course)"
                >
                  <i class="bi bi-calendar-check"></i> 查看预约安排
                </el-button>
                <el-button v-else type="primary" style="width: 100%;" @click="continueCourse(course)">
                  <i class="bi bi-play-circle"></i> 继续学习
                </el-button>
              </div>
            </el-card>
          </div>
        </div>

        <el-empty v-else description="还没有加入任何课程">
          <el-button type="primary" @click="router.push('/classroom')">去银发课堂看看</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyCourses } from '../api/course'
import { getToken } from '../utils/session'

const router = useRouter()
const loading = ref(false)
const myCourses = ref([])
const typeFilter = ref('all')

// 课程卡片样式（按顺序循环套用）
const courseStyles = [
  { icon: 'bi bi-phone', bgColor: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { icon: 'bi bi-credit-card-2-front', bgColor: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { icon: 'bi bi-person-arms-up', bgColor: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { icon: 'bi bi-heart-pulse', bgColor: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
  { icon: 'bi bi-people', bgColor: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }
]

// 课程配套教学视频（courseId → 视频地址）
const courseVideoUrls = {
  1: 'https://b23.tv/U7v8r0J',
  2: 'https://www.bilibili.com/video/BV1Ey4y1t73H/?share_source=copy_web&vd_source=2d4d797fed6c1874eb569d769a6fd911',
  3: 'https://www.bilibili.com/video/BV1h4mmY2ESL/?share_source=copy_web&vd_source=2d4d797fed6c1874eb569d769a6fd911',
  4: 'https://www.bilibili.com/video/BV1oR4y1x7yD/?share_source=copy_web&vd_source=2d4d797fed6c1874eb569d769a6fd911',
  5: 'https://www.bilibili.com/video/BV1bK411F7bT/?share_source=copy_web&vd_source=2d4d797fed6c1874eb569d769a6fd911'
}

const styleOf = (index) => courseStyles[index % courseStyles.length]

// 是否线下课
const isOffline = (course) => course.type === 'OFFLINE'

const onlineCount = computed(() => myCourses.value.filter(c => !isOffline(c)).length)
const offlineCount = computed(() => myCourses.value.filter(c => isOffline(c)).length)

const filteredCourses = computed(() => {
  if (typeFilter.value === 'all') return myCourses.value
  return myCourses.value.filter(c => typeFilter.value === 'OFFLINE' ? isOffline(c) : !isOffline(c))
})

// 后端返回 ISO 时间，展示时转成「YYYY-MM-DD HH:mm」
const formatTime = (time) => {
  if (!time) return ''
  return String(time).replace('T', ' ').slice(0, 16)
}

// 线下课：跳转到课程预约页查看/管理预约
const viewBooking = () => {
  router.push({ path: '/classroom', query: { tab: 'book' } })
}

// 继续学习：跳转到该课程配套的教学视频
const continueCourse = (course) => {
  const url = courseVideoUrls[course.id]
  if (url) {
    window.open(url, '_blank')
  } else {
    ElMessage.info('该课程暂无配套教学视频')
  }
}

const loadMyCourses = async () => {
  loading.value = true
  try {
    const res = await getMyCourses()
    myCourses.value = (res.code === 200 && Array.isArray(res.data)) ? res.data : []
  } catch (e) {
    myCourses.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // 「我的课程」需要登录，未登录时引导到个人中心
  if (!getToken()) {
    ElMessage.warning('请先登录后查看我的课程')
    router.push('/profile')
    return
  }
  loadMyCourses()
})
</script>

<style scoped>
.my-courses-view {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.loading-box {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.course-filter {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.course-cover {
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 4rem;
  color: white;
  position: relative;
}

.course-cover i {
  position: relative;
  z-index: 1;
}

.course-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 0.25rem 0.75rem;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  font-size: 0.8rem;
  color: #333;
}

/* 线上/线下课程类型标识 */
.course-type {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 0.2rem 0.6rem;
  border-radius: 20px;
  font-size: 0.78rem;
  color: #fff;
}

.type-online {
  background: rgba(64, 158, 255, 0.9);
}

.type-offline {
  background: rgba(103, 194, 58, 0.9);
}

/* 线下课的上课时间与地点 */
.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem 1rem;
  margin-bottom: 10px;
  font-size: 0.85rem;
  color: #666;
}

.course-meta span {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.course-info {
  padding: 15px;
}

.course-info h5 {
  margin-bottom: 10px;
}

.course-info p {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
  min-height: 40px;
}
</style>
