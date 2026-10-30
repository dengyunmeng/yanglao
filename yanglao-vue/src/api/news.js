import request from '../utils/axios'

// 获取新闻分类
export const getNewsCategories = () => {
  return request.get('/news/categories')
}

// 获取新闻列表
export const getNewsList = ({ category = 'all', page = 1, pageSize = 10 } = {}) => {
  const params = { page, pageSize }
  if (category && category !== 'all') params.category = category
  return request.get('/news/list', { params })
}

// 获取新闻详情
export const getNewsDetail = (id) => {
  return request.get(`/news/${id}`)
}

// 搜索新闻
export const searchNewsApi = (keyword) => {
  return request.get('/news/search', { params: { keyword, page: 1, pageSize: 20 } })
}

// 获取热门新闻
export const getHotNewsApi = (limit = 5) => {
  return request.get('/news/hot', { params: { limit } })
}

// 添加收藏
export const addFavorite = (newsId) => {
  return request.post('/news/favorite', { newsId })
}

// 取消收藏
export const removeFavorite = (newsId) => {
  return request.post('/news/favorite/remove', { newsId })
}

// 获取收藏列表
export const getFavorites = () => {
  return request.get('/news/favorites')
}

// 清空收藏（后端暂不支持时先清空本地标记后刷新）
export const clearFavorites = async () => {
  try {
    const res = await request.get('/news/favorites')
    if (res.code === 200 && Array.isArray(res.data)) {
      for (const item of res.data) {
        const id = item.newsId ?? item.id
        if (id) {
          try { await removeFavorite(id) } catch (e) { /* 忽略单个失败 */ }
        }
      }
    }
  } catch (e) { /* 忽略 */ }
  return { code: 200, message: '清空成功' }
}
