<template>
  <div class="home-view">
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
                <router-link to="/" class="nav-link active">
                  <i class="bi bi-house"></i> 首页
                </router-link>
              </li>
              <li class="nav-item">
                <router-link to="/health" class="nav-link">
                  <i class="bi bi-heart-pulse"></i> 健康守护
                </router-link>
              </li>
              <li class="nav-item">
                <router-link to="/anti-fraud" class="nav-link">
                  <i class="bi bi-shield-plus"></i> 数字反诈
                </router-link>
              </li>
              <li class="nav-item">
                <router-link to="/classroom" class="nav-link">
                  <i class="bi bi-mortarboard"></i> 银发课堂
                </router-link>
              </li>
              <li class="nav-item">
                <router-link to="/profile" class="nav-link">
                  <i class="bi bi-person-circle"></i> 个人中心
                </router-link>
              </li>
            </ul>
            <div class="accessibility-controls ms-3">
              <el-button size="small" @click="toggleDarkMode" :icon="isDarkMode ? 'Sunny' : 'Moon'" circle />
            </div>
          </div>
        </div>
      </nav>
    </header>

    <div id="home" class="page-section active">
      <div class="hero-carousel">
        <div id="heroCarousel" class="carousel slide" data-bs-ride="carousel">
          <div class="carousel-indicators">
            <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="0" class="active"></button>
            <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="1"></button>
            <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="2"></button>
          </div>
          <div class="carousel-inner">
            <div class="carousel-item active">
              <div class="carousel-bg bg-1"><img :src="hero" class="hero-img" alt="首页图"/></div>
              <div class="carousel-caption">
                <h2>温馨养老 智慧生活</h2>
                <p>为老年人提供全方位的数字化养老服务</p>
              </div>
            </div>
            <div class="carousel-item">
              <div class="carousel-bg bg-2"><img :src="first2" class="hero-img" alt="健康守护"/></div>
              <div class="carousel-caption">
                <h2>健康守护 安心晚年</h2>
                <p>科学管理健康数据，专业医疗指导</p>
              </div>
            </div>
            <div class="carousel-item">
              <div class="carousel-bg bg-3"><img :src="first3" class="hero-img" alt="数字反诈"/></div>
              <div class="carousel-caption">
                <h2>数字反诈 安全上网</h2>
                <p>提升防骗意识，保障财产安全</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="container mt-5">
        <div class="stats-dashboard">
          <h3 class="section-title text-center mb-4">
            <i class="bi bi-bar-chart-line"></i> 社区养老数据概览
          </h3>
          <div class="row">
            <div class="col-md-3 col-6 mb-3">
              <div class="stat-card">
                <div class="stat-icon bg-primary">
                  <svg class="stat-svg" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                    <path fill="white" d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-3.33 0-6 1.67-6 3v1h12v-1c0-1.33-2.67-3-6-3z"/>
                  </svg>
                </div>
                <div class="stat-info">
                  <span class="stat-number" data-count="1256">{{ animatedStats.served }}</span>
                  <span class="stat-label">服务老人</span>
                </div>
              </div>
            </div>
            <div class="col-md-3 col-6 mb-3">
              <div class="stat-card">
                <div class="stat-icon bg-success">
                  <svg class="stat-svg" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                    <path fill="white" d="M19 4h-1V2h-2v2H8V2H6v2H5a2 2 0 0 0-2 2v11a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2zM9 13l-2-2 1.4-1.4L9 10.2l4.6-4.6L15 7l-6 6z"/>
                  </svg>
                </div>
                <div class="stat-info">
                  <span class="stat-number">{{ animatedStats.activities }}</span>
                  <span class="stat-label">活动预约</span>
                </div>
              </div>
            </div>
            <div class="col-md-3 col-6 mb-3">
              <div class="stat-card">
                <div class="stat-icon bg-info">
                  <svg class="stat-svg" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                    <path fill="white" d="M3 5v14a1 1 0 0 0 1 1h14V5H4a1 1 0 0 0-1 0zM5 6h12v11H5V6z"/>
                  </svg>
                </div>
                <div class="stat-info">
                  <span class="stat-number">{{ animatedStats.courses }}</span>
                  <span class="stat-label">课程学习</span>
                </div>
              </div>
            </div>
            <div class="col-md-3 col-6 mb-3">
              <div class="stat-card">
                <div class="stat-icon bg-warning">
                  <svg class="stat-svg" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                    <path fill="white" d="M12 1l8 4v5c0 5-3.5 9.7-8 11-4.5-1.3-8-6-8-11V5l8-4zM9.5 12.5l-1.4-1.4 1.4-1.4 1.4 1.4 3.6-3.6 1.4 1.4L9.5 12.5z"/>
                  </svg>
                </div>
                <div class="stat-info">
                  <span class="stat-number">{{ animatedStats.fraud }}</span>
                  <span class="stat-label">成功阻骗</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="quick-services mt-5">
          <h3 class="section-title text-center mb-4">
            <i class="bi bi-grid"></i> 便捷服务
          </h3>
          <div class="row g-4">
            <div class="col-lg-3 col-md-6">
              <div class="service-card" @click="goTo('/health')">
                <div class="service-icon health">
                  <svg class="service-svg" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                    <path fill="white" d="M12 21s-7-4.35-9-7.5C1.5 10.5 4 7 7 7c1.7 0 3.1.9 4 2 0.9-1.1 2.3-2 4-2 3 0 5.5 3.5 4 6.5-2 3.15-9 7.5-9 7.5z"/>
                  </svg>
                </div>
                <h4>健康打卡</h4>
                <p>记录血压、血糖、睡眠等健康数据</p>
              </div>
            </div>
            <div class="col-lg-3 col-md-6">
              <div class="service-card" @click="goTo('/anti-fraud')">
                <div class="service-icon report">
                  <svg class="service-svg" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                    <path fill="white" d="M5 3v18a1 1 0 0 0 2 0V13h10a1 1 0 0 0 .6-1.8L15 8l2.6-3.2A1 1 0 0 0 16 3H7a2 2 0 0 0-2 0z"/>
                  </svg>
                </div>
                <h4>我要举报</h4>
                <p>遇到诈骗行为及时举报</p>
              </div>
            </div>
            <div class="col-lg-3 col-md-6">
              <div class="service-card" @click="goTo('/classroom')">
                <div class="service-icon learn">
                  <svg class="service-svg" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                    <path fill="white" d="M8 5v14l11-7z"/>
                  </svg>
                </div>
                <h4>在线学习</h4>
                <p>丰富的银发课堂视频课程</p>
              </div>
            </div>
            <div class="col-lg-3 col-md-6">
              <div class="service-card" @click="goTo('/profile')">
                <div class="service-icon reserve">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="service-svg" aria-hidden="true">
                    <path fill="white" d="M12 7.5a.75.75 0 0 1 .75.75v1.5h1.5a.75.75 0 0 1 0 1.5h-1.5v1.5a.75.75 0 0 1-1.5 0v-1.5H9.75a.75.75 0 0 1 0-1.5H11.25V8.25A.75.75 0 0 1 12 7.5z"/>
                    <path fill="white" d="M6 3.5h12a1 1 0 0 1 1 1v13a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1v-13a1 1 0 0 1 1-1zm0 2a4 4 0 0 0-1 0v1h14v-1a4 4 0 0 0-1 0H6z"/>
                  </svg>
                </div>
                <h4>活动预约</h4>
                <p>预约社区养老服务活动</p>
              </div>
            </div>
          </div>
        </div>

        <div class="news-section mt-5">
          <div class="news-header-section">
            <h3 class="section-title">
              <i class="bi bi-newspaper"></i> 最新资讯
            </h3>
            <div class="news-controls">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索资讯..."
                size="default"
                @keyup.enter="handleSearch"
                class="search-input"
              >
                <template #append>
                  <el-button @click="handleSearch" icon="Search"></el-button>
                </template>
              </el-input>
            </div>
          </div>

          <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange" class="news-tabs">
            <el-tab-pane
              v-for="category in newsCategories"
              :key="category.id"
              :label="category.name"
              :name="category.id"
            >
            </el-tab-pane>
          </el-tabs>

          <div v-if="searchKeyword && searchResults.length === 0" class="search-empty">
            <el-empty description="未找到相关资讯">
              <el-button type="primary" @click="clearSearch">清除搜索</el-button>
            </el-empty>
          </div>

          <div v-else class="news-grid">
            <div class="row">
              <div class="col-lg-8">
                <div class="news-main-area">
                  <div v-if="displayNews.length > 0" class="featured-news">
                    <div class="news-card featured" @click="goToNews(displayNews[0].id)">
                      <img :src="displayNews[0] && displayNews[0].image ? displayNews[0].image : heroImage" class="news-image featured-img" alt=""/>
                      <div class="news-content">
                        <div class="news-tags">
                          <el-tag size="small" :type="getCategoryType(displayNews[0].category)">
                            {{ displayNews[0].categoryName }}
                          </el-tag>
                          <span v-if="displayNews[0].isHot" class="hot-tag">
                            <i class="bi bi-fire"></i> 热门
                          </span>
                        </div>
                        <h4>{{ displayNews[0].title }}</h4>
                        <p>{{ displayNews[0].excerpt }}</p>
                        <div class="news-meta">
                          <span><i class="bi bi-calendar"></i> {{ displayNews[0].publishDate }}</span>
                          <span><i class="bi bi-eye"></i> {{ displayNews[0].viewCount }}</span>
                          <span><i class="bi bi-chat"></i> {{ Math.floor(displayNews[0].viewCount / 10) }}</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="news-list mt-4">
                    <div
                      v-for="(news, idx) in displayNews.slice(1)"
                      :key="news.id"
                      class="news-card horizontal"
                      @click="goToNews(news.id)"
                    >
                      <img class="news-list-img" :src="news.image || listImages[idx % listImages.length]" alt=""/>
                      <div class="news-content">
                        <div class="news-tags">
                          <el-tag size="small" :type="getCategoryType(news.category)">
                            {{ news.categoryName }}
                          </el-tag>
                          <span v-if="news.isHot" class="hot-tag">
                            <i class="bi bi-fire"></i> 热门
                          </span>
                        </div>
                        <h5>{{ news.title }}</h5>
                        <p>{{ news.excerpt }}</p>
                        <div class="news-meta">
                          <span><i class="bi bi-calendar"></i> {{ news.publishDate }}</span>
                          <span><i class="bi bi-eye"></i> {{ news.viewCount }}</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div v-if="hasMoreNews && !searchKeyword" class="load-more-section text-center mt-4">
                    <el-button type="primary" @click="loadMoreNews" :loading="loading">
                      加载更多资讯
                    </el-button>
                  </div>
                </div>
              </div>

              <div class="col-lg-4">
                <div class="news-sidebar">
                  <div class="sidebar-card hot-topics">
                    <h5 class="sidebar-title">
                      <i class="bi bi-fire"></i> 热门话题
                    </h5>
                    <div class="hot-topic-list">
                      <div
                        v-for="(topic, index) in hotTopics"
                        :key="index"
                        class="hot-topic-item"
                        @click="goToNews(topic.id)"
                      >
                        <span class="topic-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
                        <div class="topic-content">
                          <h6>{{ topic.title }}</h6>
                          <span class="topic-views">{{ topic.viewCount }}阅读</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="sidebar-card quick-access mt-4">
                    <h5 class="sidebar-title">
                      <i class="bi bi-bookmark"></i> 便捷入口
                    </h5>
                    <div class="quick-link-list">
                      <router-link to="/health" class="quick-link-item">
                        <i class="bi bi-heart-pulse"></i>
                        <span>健康打卡</span>
                      </router-link>
                      <router-link to="/anti-fraud" class="quick-link-item">
                        <i class="bi bi-shield-plus"></i>
                        <span>防诈中心</span>
                      </router-link>
                      <router-link to="/classroom" class="quick-link-item">
                        <i class="bi bi-mortarboard"></i>
                        <span>银发课堂</span>
                      </router-link>
                      <router-link to="/profile" class="quick-link-item">
                        <i class="bi bi-person-circle"></i>
                        <span>个人中心</span>
                      </router-link>
                    </div>
                  </div>

                  <div class="sidebar-card my-collections mt-4" v-if="myCollections.length > 0">
                    <h5 class="sidebar-title">
                      <i class="bi bi-star"></i> 我的收藏
                    </h5>
                    <div class="collection-list">
                      <div
                        v-for="news in myCollections"
                        :key="news.id"
                        class="collection-item"
                        @click="goToNews(news.id)"
                      >
                        <i :class="getCategoryIcon(news.category)"></i>
                        <span>{{ news.title }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <footer class="main-footer">
      <div class="container">
        <div class="row">
          <div class="col-md-4 mb-3">
            <h5><i class="bi bi-heart-pulse"></i> 智慧养老</h5>
            <p class="small">为老年人提供温馨、便捷的数字化养老服务</p>
          </div>
          <div class="col-md-4 mb-3">
            <h5>联系我们</h5>
            <p class="small mb-1"><i class="bi bi-telephone"></i> 服务热线：400-XXX-XXXX</p>
            <p class="small mb-1"><i class="bi bi-envelope"></i> 邮箱：service@example.com</p>
            <p class="small"><i class="bi bi-geo-alt"></i> 地址：XX市XX区XX路XX号</p>
          </div>
          <div class="col-md-4 mb-3">
            <h5>关注我们</h5>
            <div class="social-links">
              <a href="#"><i class="bi bi-wechat"></i></a>
              <a href="#"><i class="bi bi-whatsapp"></i></a>
              <a href="#"><i class="bi bi-sina-weibo"></i></a>
            </div>
            <p class="small mt-2">扫描二维码关注公众号</p>
          </div>
        </div>
        <hr>
        <p class="text-center small mb-0">© 2026 智慧养老服务平台 版权所有</p>
      </div>
    </footer>

    <el-dialog v-model="toastVisible" title="系统提示" width="300px" center>
      <span>{{ toastMessage }}</span>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { newsCategories, newsList, getNewsByCategory, searchNews, getHotNews, getNewsById } from '../data/news'
import hero from '@/assets/hero.png'
import heroImage from '@/assets/elderly-couple.png'
import first2 from '@/assets/banner/first2.png'
import first3 from '@/assets/banner/first3.png'
import newsImg1 from '@/assets/news-1.png'
import newsImg2 from '@/assets/news-2.png'
import newsImg3 from '@/assets/news-3.png'
import newsImg4 from '@/assets/news-4.png'

const listImages = [newsImg1, newsImg2, newsImg3, newsImg4]

const router = useRouter()
const isDarkMode = ref(localStorage.getItem('darkMode') === 'true')
const toastVisible = ref(false)
const toastMessage = ref('')
const activeCategory = ref('all')
const searchKeyword = ref('')
const displayCount = ref(6)
const loading = ref(false)

const animatedStats = reactive({
  served: 0,
  activities: 0,
  courses: 0,
  fraud: 0
})

const targetStats = {
  served: 1256,
  activities: 486,
  courses: 89,
  fraud: 32
}

const filteredNews = computed(() => {
  if (searchKeyword.value) {
    return searchNews(searchKeyword.value)
  }
  return getNewsByCategory(activeCategory.value)
})

const displayNews = computed(() => {
  return filteredNews.value.slice(0, displayCount.value)
})

const hasMoreNews = computed(() => {
  return filteredNews.value.length > displayCount.value
})

const hotTopics = computed(() => {
  return getHotNews().slice(0, 5)
})

const myCollections = computed(() => {
  const collectedIds = JSON.parse(localStorage.getItem('collectedNews') || '[]')
  return collectedIds.slice(0, 3).map(id => getNewsById(id)).filter(Boolean)
})

const searchResults = computed(() => {
  if (!searchKeyword.value) return []
  return searchNews(searchKeyword.value)
})

const animateNumber = (target, key) => {
  let current = 0
  const increment = target / 50
  const timer = setInterval(() => {
    current += increment
    if (current >= target) {
      current = target
      clearInterval(timer)
    }
    animatedStats[key] = Math.floor(current)
  }, 30)
}

const animateAllStats = () => {
  Object.keys(targetStats).forEach(key => {
    animateNumber(targetStats[key], key)
  })
}

const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value
  document.body.classList.toggle('dark-mode', isDarkMode.value)
  localStorage.setItem('darkMode', isDarkMode.value.toString())
  ElMessage.success(isDarkMode.value ? '已开启深色模式' : '已关闭深色模式')
}

const goTo = (path) => {
  router.push(path)
}

const goToNews = (id) => {
  router.push(`/news/${id}`)
}

const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  displayCount.value = filteredNews.value.length
}

const clearSearch = () => {
  searchKeyword.value = ''
  displayCount.value = 6
}

const handleCategoryChange = () => {
  searchKeyword.value = ''
  displayCount.value = 6
}

const loadMoreNews = () => {
  loading.value = true
  setTimeout(() => {
    displayCount.value += 3
    loading.value = false
  }, 500)
}

const getCategoryType = (category) => {
  const types = {
    'health': 'danger',
    'anti-fraud': 'warning',
    'activity': 'success',
    'policy': 'primary',
    'life': 'info'
  }
  return types[category] || 'info'
}

const getCategoryIcon = (category) => {
  const icons = {
    'health': 'bi bi-heart',
    'anti-fraud': 'bi bi-shield-exclamation',
    'activity': 'bi bi-calendar-event',
    'policy': 'bi bi-file-text',
    'life': 'bi bi-sun'
  }
  return icons[category] || 'bi bi-newspaper'
}

const showToast = (message) => {
  toastMessage.value = message
  toastVisible.value = true
}

watch(isDarkMode, (newVal) => {
  document.body.classList.toggle('dark-mode', newVal)
})

watch(searchKeyword, (newVal) => {
  if (!newVal) {
    displayCount.value = 6
  }
})

onMounted(() => {
  if (isDarkMode.value) {
    document.body.classList.add('dark-mode')
  }
  animateAllStats()

  const script = document.createElement('script')
  script.src = 'https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'
  document.body.appendChild(script)
})
</script>

<style scoped>
.home-view {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.page-section {
  flex: 1;
}

.hero-img { width: 100%; height: auto; display: block; border-radius: 8px; }

.news-section {
  background: var(--white);
  border-radius: var(--border-radius);
  padding: 2rem;
  box-shadow: var(--shadow-sm);
}

.news-header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.news-controls {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.search-input {
  width: 250px;
}

.news-tabs {
  margin-bottom: 1.5rem;
}

.news-tabs :deep(.el-tabs__item) {
  font-size: 1rem;
}

.news-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

.search-empty {
  padding: 2rem 0;
}

.news-grid {
  margin-top: 1.5rem;
}

.news-card {
  cursor: pointer;
  transition: var(--transition);
  background: var(--white);
  border-radius: var(--border-radius);
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.news-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.news-card.featured {
  display: flex;
  flex-direction: column;
}

.news-card.featured .news-image {
  height: 250px;
}

.news-card.horizontal {
  display: flex;
  margin-bottom: 1rem;
}

.news-card.horizontal .news-image {
  width: 200px;
  min-width: 200px;
  height: 150px;
}

.news-image {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  display: flex;
  align-items: center;
  justify-content: center;
}

.news-image i {
  font-size: 4rem;
  color: var(--white);
  opacity: 0.9;
}

.news-content {
  padding: 1.5rem;
}

.news-tags {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.8rem;
}

.hot-tag {
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.news-card h4,
.news-card h5 {
  color: var(--text-color);
  margin-bottom: 0.8rem;
  line-height: 1.4;
}

.news-card p {
  color: var(--text-light);
  font-size: 0.9rem;
  line-height: 1.6;
  margin-bottom: 0.8rem;
}

.news-meta {
  display: flex;
  gap: 1rem;
  color: var(--text-light);
  font-size: 0.85rem;
}

.news-meta span {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
}

.news-sidebar {
  position: sticky;
  top: 100px;
}

.sidebar-card {
  background: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 1.5rem;
}

.sidebar-title {
  color: var(--primary-color);
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid var(--primary-color);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.hot-topic-list {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.hot-topic-item {
  display: flex;
  align-items: flex-start;
  gap: 0.8rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 8px;
  transition: var(--transition);
}

.hot-topic-item:hover {
  background: var(--white);
}

.topic-rank {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  font-weight: bold;
  font-size: 0.85rem;
  flex-shrink: 0;
}

.topic-rank.rank-1 {
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  color: white;
}

.topic-rank.rank-2 {
  background: linear-gradient(135deg, #ffa502, #ffbe4d);
  color: white;
}

.topic-rank.rank-3 {
  background: linear-gradient(135deg, #7bed9f, #a8f0c6);
  color: white;
}

.topic-rank.rank-4,
.topic-rank.rank-5 {
  background: var(--border-color);
  color: var(--text-light);
}

.topic-content h6 {
  margin: 0 0 0.2rem 0;
  font-size: 0.9rem;
  line-height: 1.4;
  color: var(--text-color);
}

.topic-views {
  font-size: 0.75rem;
  color: var(--text-light);
}

.quick-link-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.quick-link-item {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  padding: 0.8rem;
  background: var(--white);
  border-radius: 8px;
  text-decoration: none;
  color: var(--text-color);
  transition: var(--transition);
}

.quick-link-item:hover {
  background: var(--primary-color);
  color: var(--white);
  transform: translateX(4px);
}

.quick-link-item i {
  font-size: 1.2rem;
  color: var(--primary-color);
}

.quick-link-item:hover i {
  color: var(--white);
}

.collection-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.collection-item {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  padding: 0.5rem;
  cursor: pointer;
  border-radius: 8px;
  transition: var(--transition);
}

.collection-item:hover {
  background: var(--white);
}

.collection-item i {
  color: var(--accent-color);
  font-size: 1.2rem;
}

.collection-item span {
  font-size: 0.9rem;
  color: var(--text-color);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.load-more-section {
  padding: 1rem 0;
}

@media (max-width: 991px) {
  .news-sidebar {
    position: static;
    margin-top: 2rem;
  }
}

@media (max-width: 768px) {
  .news-card.horizontal {
    flex-direction: column;
  }

  .news-card.horizontal .news-image {
    width: 100%;
    height: 200px;
  }

  .news-header-section {
    flex-direction: column;
    align-items: flex-start;
  }

  .search-input {
    width: 100%;
  }
}

.service-card {
  background: var(--white);
  border-radius: 12px;
  padding: 2.2rem 1.2rem;
  text-align: center;
  box-shadow: 0 6px 16px rgba(13,38,59,0.04);
  cursor: pointer;
  transition: transform .14s ease, box-shadow .14s ease;
}
.service-card:hover { transform: translateY(-6px); box-shadow: 0 12px 28px rgba(13,38,59,0.08); }
.service-icon {
  width: 110px;
  height: 110px;
  margin: 0 auto 18px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 22px rgba(15,40,60,0.06);
}
.service-icon i { color: #fff; font-size: 40px; }
.service-icon .service-svg { width: 56px; height: 56px; display: block; }
.service-icon.health { background: linear-gradient(135deg,#6ea8ff,#4d7cff); }
.service-icon.report { background: linear-gradient(135deg,#f6a5a5,#e86b6b); }
.service-icon.learn { background: linear-gradient(135deg,#86d68f,#3fb36a); }
.service-icon.reserve { background: linear-gradient(135deg,#f7b76b,#f89b5b); }

.stat-icon .stat-svg { width: 28px; height: 28px; display: block; }

.news-image { background: none; display: block; }
.featured-img { width: 100%; height: 250px; object-fit: contain; border-radius: 8px 8px 0 0; display: block; }
.news-list-img { width: 200px; height: 150px; object-fit: contain; border-radius: 8px; display: block; }
@media (max-width: 768px) {
  .news-list-img { width: 100%; height: 200px; }
  .featured-img { height: 200px; }
}
</style>