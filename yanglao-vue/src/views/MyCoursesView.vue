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

        <div v-else-if="myCourses.length > 0" class="course-grid">
          <el-card v-for="(course, i) in myCourses" :key="course.id" :body-style="{ padding: '0px' }" shadow="hover">
            <div class="course-cover" :style="{ background: styleOf(i).bgColor }">
              <i :class="styleOf(i).icon"></i>
              <span class="course-tag">{{ course.tag || '学习中' }}</span>
            </div>
            <div class="course-info">
              <h5>{{ course.name }}</h5>
              <p>{{ course.description }}</p>
              <el-button type="primary" style="width: 100%;" @click="continueCourse(course)">
                <i class="bi bi-play-circle"></i> 继续学习
              </el-button>
            </div>
          </el-card>
        </div>

        <el-empty v-else description="还没有加入任何课程">
          <el-button type="primary" @click="router.push('/classroom')">去银发课堂看看</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyCourses } from '../api/course'

const router = useRouter()
const loading = ref(false)
const myCourses = ref([])

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
  if (!localStorage.getItem('token')) {
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
