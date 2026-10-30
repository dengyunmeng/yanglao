<template>
  <div class="news-detail-view">
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
                <router-link to="/" class="nav-link">
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
          </div>
        </div>
      </nav>
    </header>

    <div class="container mt-5">
      <div class="news-detail-content" v-if="currentNews">
        <div class="news-header">
          <div class="news-tags">
            <el-tag size="small" :type="getCategoryType(currentNews.category)">
              {{ currentNews.categoryName }}
            </el-tag>
            <el-tag
              v-for="tag in currentNews.tags"
              :key="tag"
              size="small"
              effect="plain"
              class="ms-2"
            >
              {{ tag }}
            </el-tag>
          </div>
          <h1 class="news-title">{{ currentNews.title }}</h1>
          <div class="news-meta">
            <span><i class="bi bi-person"></i> {{ currentNews.author }}</span>
            <span><i class="bi bi-calendar"></i> {{ currentNews.publishDate }}</span>
            <span><i class="bi bi-eye"></i> {{ currentNews.viewCount }}次阅读</span>
          </div>
        </div>

        <div class="news-body">
          <div class="news-content" v-html="formatContent(currentNews.content)"></div>
        </div>

        <div class="news-actions mt-4">
          <el-button type="primary" @click="handleCollect" :icon="isCollected ? 'Star' : 'StarFilled'">
            {{ isCollected ? '已收藏' : '收藏文章' }}
          </el-button>
          <el-button @click="handleShare" icon="Share">分享给好友</el-button>
          <el-button type="success" @click="goBack" icon="ArrowLeft">返回列表</el-button>
        </div>

        <div class="related-news mt-5">
          <h4 class="section-subtitle">
            <i class="bi bi-link"></i> 相关推荐
          </h4>
          <div class="row">
            <div class="col-md-4 mb-3" v-for="news in relatedNews" :key="news.id">
              <div class="related-card" @click="goToNews(news.id)">
                <div class="related-icon">
                  <i :class="getCategoryIcon(news.category)"></i>
                </div>
                <div class="related-info">
                  <h6>{{ news.title }}</h6>
                  <span class="small text-muted">{{ news.publishDate }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="文章不存在或已被删除" >
        <el-button type="primary" @click="goBack">返回列表</el-button>
      </el-empty>
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

    <el-dialog v-model="shareVisible" title="分享到" width="400px" center>
      <div class="share-buttons">
        <div class="share-item" @click="shareToWechat">
          <i class="bi bi-wechat"></i>
          <span>微信</span>
        </div>
        <div class="share-item" @click="shareToWeibo">
          <i class="bi bi-sina-weibo"></i>
          <span>微博</span>
        </div>
        <div class="share-item" @click="shareToQQ">
          <i class="bi bi-tencent-qq"></i>
          <span>QQ</span>
        </div>
        <div class="share-item" @click="copyLink">
          <i class="bi bi-link-45deg"></i>
          <span>复制链接</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getNewsById, getNewsByCategory, searchNews } from '../data/news'

const route = useRoute()
const router = useRouter()

const currentNews = ref(null)
const relatedNews = ref([])
const isCollected = ref(false)
const shareVisible = ref(false)

const getNewsDetail = () => {
  const newsId = parseInt(route.params.id)
  currentNews.value = getNewsById(newsId)
  
  if (currentNews.value) {
    document.title = `${currentNews.value.title} - 智慧养老服务平台`
    
    const category = currentNews.value.category
    const sameCategoryNews = getNewsByCategory(category)
      .filter(n => n.id !== currentNews.value.id)
      .slice(0, 3)
    relatedNews.value = sameCategoryNews
    
    const collectedNews = JSON.parse(localStorage.getItem('collectedNews') || '[]')
    isCollected.value = collectedNews.includes(newsId)
    
    currentNews.value.viewCount++
    localStorage.setItem(`news_${newsId}_views`, currentNews.value.viewCount)
  }
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

const formatContent = (content) => {
  return content.split('\n').map(line => {
    if (line.trim().startsWith('一、') || line.trim().startsWith('二、') || 
        line.trim().startsWith('三、') || line.trim().startsWith('四、') ||
        line.trim().startsWith('五、') || line.trim().startsWith('六、')) {
      return `<h4 class="content-heading">${line}</h4>`
    } else if (line.trim().startsWith('1.') || line.trim().startsWith('2.') || 
               line.trim().startsWith('3.') || line.trim().startsWith('4.') ||
               line.trim().startsWith('5.')) {
      return `<li>${line}</li>`
    } else if (line.trim() === '') {
      return '<br>'
    } else {
      return `<p>${line}</p>`
    }
  }).join('')
}

const handleCollect = () => {
  const newsId = currentNews.value.id
  let collectedNews = JSON.parse(localStorage.getItem('collectedNews') || '[]')
  
  if (isCollected.value) {
    collectedNews = collectedNews.filter(id => id !== newsId)
    localStorage.setItem('collectedNews', JSON.stringify(collectedNews))
    isCollected.value = false
    ElMessage.success('已取消收藏')
  } else {
    collectedNews.push(newsId)
    localStorage.setItem('collectedNews', JSON.stringify(collectedNews))
    isCollected.value = true
    ElMessage.success('收藏成功')
  }
}

const handleShare = () => {
  shareVisible.value = true
}

const shareToWechat = () => {
  ElMessage.info('请截图分享到微信群')
  shareVisible.value = false
}

const shareToWeibo = () => {
  ElMessage.info('分享链接已复制')
  shareVisible.value = false
}

const shareToQQ = () => {
  ElMessage.info('分享链接已复制')
  shareVisible.value = false
}

const copyLink = () => {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('链接已复制到剪贴板')
    shareVisible.value = false
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

const goToNews = (id) => {
  router.push(`/news/${id}`)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goBack = () => {
  router.push('/')
}

onMounted(() => {
  getNewsDetail()
})
</script>

<style scoped>
.news-detail-view {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.news-detail-content {
  background: var(--white);
  border-radius: var(--border-radius);
  padding: 2rem;
  box-shadow: var(--shadow-sm);
  margin-bottom: 2rem;
}

.news-header {
  border-bottom: 2px solid var(--border-color);
  padding-bottom: 1.5rem;
  margin-bottom: 1.5rem;
}

.news-tags {
  margin-bottom: 1rem;
}

.news-title {
  font-size: 1.8rem;
  font-weight: bold;
  color: var(--text-color);
  margin-bottom: 1rem;
  line-height: 1.4;
}

.news-meta {
  display: flex;
  gap: 1.5rem;
  color: var(--text-light);
  font-size: 0.9rem;
}

.news-meta span {
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

.news-body {
  line-height: 1.8;
  font-size: 1.05rem;
}

.news-content {
  color: var(--text-color);
}

.news-content :deep(h4.content-heading) {
  color: var(--primary-color);
  font-size: 1.2rem;
  margin-top: 1.5rem;
  margin-bottom: 0.8rem;
  padding-left: 0.5rem;
  border-left: 4px solid var(--primary-color);
}

.news-content :deep(p) {
  margin-bottom: 1rem;
  text-indent: 2em;
}

.news-content :deep(li) {
  margin-bottom: 0.5rem;
  margin-left: 1rem;
}

.news-actions {
  display: flex;
  gap: 1rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border-color);
}

.related-news {
  padding-top: 1.5rem;
  border-top: 2px solid var(--border-color);
}

.section-subtitle {
  color: var(--primary-color);
  margin-bottom: 1rem;
  font-weight: bold;
}

.related-card {
  background: var(--bg-secondary);
  padding: 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition: var(--transition);
  display: flex;
  align-items: center;
  gap: 1rem;
}

.related-card:hover {
  background: var(--primary-light);
  color: var(--white);
  transform: translateY(-2px);
}

.related-icon {
  font-size: 2rem;
  color: var(--primary-color);
}

.related-card:hover .related-icon {
  color: var(--white);
}

.related-info h6 {
  margin: 0 0 0.3rem 0;
  font-size: 0.95rem;
  line-height: 1.3;
}

.share-buttons {
  display: flex;
  justify-content: space-around;
  padding: 1rem 0;
}

.share-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  transition: var(--transition);
  padding: 1rem;
  border-radius: 8px;
}

.share-item:hover {
  background: var(--bg-secondary);
}

.share-item i {
  font-size: 2rem;
  color: var(--primary-color);
}

.share-item span {
  font-size: 0.9rem;
  color: var(--text-color);
}

.main-footer {
  margin-top: auto;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  color: var(--white);
  padding: 3rem 0 1.5rem;
}

.main-footer h5 {
  margin-bottom: 1rem;
}

.main-footer p {
  color: rgba(255, 255, 255, 0.9);
}

.main-footer .social-links a {
  color: var(--white);
  font-size: 1.5rem;
  margin-right: 1rem;
  transition: var(--transition);
}

.main-footer .social-links a:hover {
  color: var(--accent-color);
  transform: scale(1.2);
}

@media (max-width: 768px) {
  .news-title {
    font-size: 1.4rem;
  }

  .news-meta {
    flex-direction: column;
    gap: 0.5rem;
  }

  .news-actions {
    flex-wrap: wrap;
  }

  .related-card {
    flex-direction: column;
    text-align: center;
  }
}
</style>
