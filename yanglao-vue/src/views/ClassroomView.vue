<template>
  <div class="classroom-view">
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

    <div id="classroom" class="page-section active">
      <div class="container">
        <h2 class="section-title">
          <i class="bi bi-mortarboard"></i> 银发课堂
          <el-button size="small" type="primary" @click="router.push('/my-courses')" style="margin-left: auto;">
            <i class="bi bi-bookmark"></i> 我的课程
          </el-button>
        </h2>

        <el-tabs v-model="activeTab" type="border-card">
          <el-tab-pane label="课程列表" name="list">
            <div class="course-grid">
              <el-card v-for="course in courses" :key="course.id" :body-style="{ padding: '0px' }" shadow="hover" v-loading="loading">
                <div class="course-cover" :style="{ background: course.bgColor }">
                  <i :class="course.icon"></i>
                  <span class="course-tag">{{ course.tag }}</span>
                </div>
                <div class="course-info">
                  <h5>{{ course.name }}</h5>
                  <p>{{ course.description }}</p>
                  <div class="course-meta">
                    <span><i class="bi bi-people"></i> {{ course.students }}人学习</span>
                  </div>
                  <el-button type="primary" @click="startLearning(course)" style="width: 100%;">
                    <i class="bi bi-play-circle"></i> 开始学习
                  </el-button>
                </div>
              </el-card>
            </div>
          </el-tab-pane>

          <el-tab-pane label="课程预约" name="book">
            <el-card>
              <template #header>
                <span><i class="bi bi-calendar-plus"></i> 线下课程预约</span>
              </template>

              <el-form ref="bookFormRef" :model="bookForm" :rules="bookRules" label-width="100px" size="large">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="姓名" prop="name">
                      <el-input v-model="bookForm.name" placeholder="请输入姓名" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="联系电话" prop="phone">
                      <el-input v-model="bookForm.phone" placeholder="请输入11位手机号" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="选择课程" prop="course">
                  <el-select v-model="bookForm.course" placeholder="请选择要预约的线下课程" style="width: 100%;">
                    <el-option
                      v-for="c in offlineCourses"
                      :key="c.id"
                      :label="`${c.name}（${c.location || '地点待定'}）`"
                      :value="c.id" />
                  </el-select>
                  <div v-if="offlineCourses.length === 0" style="color: #909399; font-size: 13px; margin-top: 6px;">
                    暂无可预约的线下课程
                  </div>
                </el-form-item>

                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="预约日期" prop="date">
                      <el-date-picker v-model="bookForm.date" type="date" value-format="YYYY-MM-DD"
                        :disabled-date="(d) => d && d.getTime() < Date.now() - 86400000"
                        placeholder="选择日期" style="width: 100%;" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="时间段" prop="time">
                      <el-select v-model="bookForm.time" placeholder="请选择时间段" style="width: 100%;">
                        <el-option label="上午（9:00-11:00）" value="上午（9:00-11:00）" />
                        <el-option label="下午（14:00-16:00）" value="下午（14:00-16:00）" />
                        <el-option label="晚上（19:00-21:00）" value="晚上（19:00-21:00）" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="备注">
                  <el-input v-model="bookForm.note" type="textarea" :rows="2" placeholder="如有特殊需求请在此说明" />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" size="large" @click="submitBooking" :loading="submitting" style="width: 100%;">
                    <i class="bi bi-calendar-check"></i> 确认预约
                  </el-button>
                </el-form-item>
              </el-form>

              <el-divider>我的预约</el-divider>

              <el-table :data="bookings" stripe style="width: 100%" v-loading="loading">
                <el-table-column prop="courseName" label="课程" min-width="140" />
                <el-table-column prop="bookDate" label="日期" width="110" />
                <el-table-column prop="timeSlot" label="时间段" width="140" />
                <el-table-column label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="statusTagType(scope.row.status)">
                      {{ statusText(scope.row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="90">
                  <template #default="scope">
                    <el-button v-if="scope.row.status === 'pending' || scope.row.status === 'confirmed'"
                      type="danger" size="small" link @click="cancelOneBooking(scope.row.id)">
                      取消
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>

              <el-empty v-if="bookings.length === 0 && !loading" description="暂无预约记录" />
            </el-card>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <el-dialog v-model="courseDialogVisible" :title="selectedCourse?.name" width="600px" center>
      <div class="course-detail">
        <p><strong>课程介绍：</strong></p>
        <p>{{ selectedCourse?.description }}</p>
        <p><strong>学习人数：</strong>{{ selectedCourse?.students }}人</p>
      </div>
      <template #footer>
        <el-button @click="courseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmStartCourse">开始学习</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCourses as fetchCourses, joinCourse } from '../api/course'
import { createBooking, getBookings as fetchBookings, cancelBooking } from '../api/booking'

const route = useRoute()
const router = useRouter()
const activeTab = ref(route.query.tab || 'list')
const loading = ref(false)
const submitting = ref(false)
const bookFormRef = ref(null)
const courseDialogVisible = ref(false)
const selectedCourse = ref(null)

// 课程卡片样式（按顺序循环套用，兼容后端返回数据没有 icon/bgColor 的情况）
const courseStyles = [
  { icon: 'bi bi-phone', bgColor: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { icon: 'bi bi-credit-card-2-front', bgColor: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { icon: 'bi bi-person-arms-up', bgColor: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { icon: 'bi bi-heart-pulse', bgColor: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
  { icon: 'bi bi-people', bgColor: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }
]

// 兜底课程数据：后端未启动时页面也能正常展示
const fallbackCourses = [
  { id: 1, name: '智能手机基础操作', description: '从零学习智能手机基本功能，包括打电话、发短信、微信视频。', tag: '热门', students: 128 },
  { id: 2, name: '移动支付安全指南', description: '教您安全使用微信支付和支付宝，防止上当受骗。', tag: '推荐', students: 96 },
  { id: 3, name: '太极拳入门', description: '线下太极拳培训，强身健体、修身养性。', tag: '新上', students: 45 },
  { id: 4, name: '高血压防治知识', description: '由健康医生讲解高血压的饮食、运动和药物管理。', tag: '推荐', students: 210 },
  { id: 5, name: '老年人安全出行指南', description: '教您安全乘坐公交地铁、用手机打车，以及出行防走失、防诈骗的实用技巧。', tag: '热门', students: 300 }
]

// 补全卡片展示所需字段（icon/bgColor/students），兼容后端返回结构
const decorateCourses = (list) => (list || []).map((c, i) => {
  const style = courseStyles[i % courseStyles.length]
  return {
    ...c,
    icon: c.icon || style.icon,
    bgColor: c.bgColor || style.bgColor,
    students: c.students ?? c.currentStudents ?? 0
  }
})

// 课程列表（从后端加载，后端不可用时回退到兜底数据）
const courses = ref([])

// 线下课程供预约下拉选择
const offlineCourses = computed(() => courses.value.filter(c => c.type === 'OFFLINE'))

const bookings = ref([])

const bookForm = reactive({
  name: '',
  phone: '',
  course: '',
  date: '',
  time: '',
  note: ''
})

const bookRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  course: [{ required: true, message: '请选择课程', trigger: 'change' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  time: [{ required: true, message: '请选择时间段', trigger: 'change' }]
}

const loadCourses = async () => {
  loading.value = true
  try {
    const res = await fetchCourses()
    let list = []
    if (res.code === 200 && Array.isArray(res.data)) {
      list = res.data
    } else if (res.code === 200 && Array.isArray(res.data?.list)) {
      list = res.data.list
    }
    courses.value = list.length ? decorateCourses(list) : decorateCourses(fallbackCourses)
  } catch (e) {
    courses.value = decorateCourses(fallbackCourses)
  } finally {
    loading.value = false
  }
}

const startLearning = (course) => {
  selectedCourse.value = course
  courseDialogVisible.value = true
}

// 点击课程卡「开始学习」→ 加入课程后跳转到「我的课程」页面
const confirmStartCourse = async () => {
  const course = selectedCourse.value
  if (!course) return

  courseDialogVisible.value = false

  if (!localStorage.getItem('token')) {
    ElMessage.warning('请先登录后再学习课程')
    router.push('/profile')
    return
  }

  try {
    const res = await joinCourse(course.id)
    // code 200 = 加入成功；code 400「已加入该课程」也直接进入我的课程
    if (res.code === 200 || (res.message || '').includes('已加入')) {
      ElMessage.success(`已加入课程：${course.name}`)
      router.push('/my-courses')
    } else {
      ElMessage.error(res.message || '加入课程失败')
    }
  } catch (e) {
    ElMessage.error('加入课程失败，请检查网络')
  }
}

const submitBooking = async () => {
  if (!bookFormRef.value) return

  await bookFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true

      try {
        // 后端字段：courseId 为数字课程ID，bookDate 为 YYYY-MM-DD，timeSlot 为中文时间段
        const dto = {
          courseId: bookForm.course,
          userName: bookForm.name,
          userPhone: bookForm.phone,
          bookDate: bookForm.date,
          timeSlot: bookForm.time,
          note: bookForm.note
        }

        const res = await createBooking(dto)
        if (res.code === 200) {
          ElMessage.success('预约成功，等待管理员确认！')
          bookFormRef.value.resetFields()
          loadBookings()
        } else {
          ElMessage.error(res.message || '预约失败')
        }
      } catch (e) {
        ElMessage.error('预约失败，请检查网络')
      } finally {
        submitting.value = false
      }
    }
  })
}

const statusText = (s) => ({
  pending: '待确认',
  confirmed: '已确认',
  rejected: '已拒绝',
  cancelled: '已取消'
}[s] || s)

const statusTagType = (s) => ({
  pending: 'warning',
  confirmed: 'success',
  rejected: 'danger',
  cancelled: 'info'
}[s] || 'info')

const cancelOneBooking = async (id) => {
  try {
    const res = await cancelBooking(id)
    if (res.code === 200) {
      ElMessage.success('预约已取消')
      loadBookings()
    } else {
      ElMessage.error(res.message || '取消失败')
    }
  } catch (e) {
    ElMessage.error('取消失败，请检查网络')
  }
}

const loadBookings = async () => {
  loading.value = true
  try {
    const res = await fetchBookings({ page: 1, pageSize: 50 })
    if (res.code === 200 && Array.isArray(res.data)) {
      bookings.value = res.data
    } else if (res.code === 200 && Array.isArray(res.data?.list)) {
      bookings.value = res.data.list
    } else {
      bookings.value = []
    }
  } catch (e) {
    bookings.value = []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadCourses()
  // 「课程预约」需要登录，未登录时不发请求，避免 403
  if (localStorage.getItem('token')) {
    loadBookings()
    // 已登录时预填姓名和手机号，少输几个字
    try {
      const user = JSON.parse(localStorage.getItem('currentUser') || 'null')
      if (user) {
        bookForm.name = user.name || ''
        bookForm.phone = user.phone || ''
      }
    } catch (e) { /* 忽略解析失败 */ }
  }
  if (route.query.tab) activeTab.value = route.query.tab
})

watch(() => route.query.tab, (val) => {
  if (val) activeTab.value = val
})
</script>

<style scoped>
.classroom-view {
  min-height: 100vh;
  background-color: #f5f5f5;
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
}

.course-meta {
  margin-bottom: 15px;
  color: #999;
  font-size: 14px;
}

.course-detail {
  padding: 20px;
}

.course-detail p {
  margin-bottom: 10px;
  line-height: 1.8;
}
</style>
