<template>
  <div class="console-view">
    <header class="main-header">
      <nav class="navbar navbar-expand-lg">
        <div class="container">
          <router-link to="/" class="navbar-brand">
            <i class="bi bi-heart-pulse"></i> 智慧养老
          </router-link>
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
              <li class="nav-item">
                <router-link to="/" class="nav-link"><i class="bi bi-house"></i> 首页</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/classroom" class="nav-link"><i class="bi bi-mortarboard"></i> 银发课堂</router-link>
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
        <h2 class="section-title"><i class="bi bi-speedometer2"></i> 管理控制台</h2>

        <el-card v-if="!currentUser" style="max-width: 480px; margin: 40px auto;">
          <el-result icon="warning" title="请先登录" sub-title="登录后根据角色显示对应功能">
            <template #extra>
              <el-button type="primary" @click="$router.push('/profile')">去登录</el-button>
            </template>
          </el-result>
        </el-card>

        <el-card v-else-if="!isAdmin && !isDoctor" style="max-width: 480px; margin: 40px auto;">
          <el-result icon="error" title="无管理权限" sub-title="当前账号是普通用户，管理控制台仅对健康管理员和系统管理员开放">
            <template #extra>
              <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
            </template>
          </el-result>
        </el-card>

        <el-card v-else>
          <el-tabs v-model="activeTab">
            <!-- ============ 系统管理员：审核 ============ -->
            <el-tab-pane label="课程审核" name="courseReview" v-if="isAdmin">
              <el-table :data="pendingCourses" stripe v-loading="loading">
                <el-table-column prop="name" label="课程名称" min-width="160" />
                <el-table-column label="类型" width="90">
                  <template #default="s">
                    <el-tag :type="s.row.type === 'ONLINE' ? 'primary' : 'warning'">
                      {{ s.row.type === 'ONLINE' ? '线上' : '线下' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="location" label="地点" min-width="130" />
                <el-table-column prop="maxStudents" label="名额" width="70" />
                <el-table-column prop="description" label="简介" min-width="200" show-overflow-tooltip />
                <el-table-column label="操作" width="160">
                  <template #default="s">
                    <el-button type="success" size="small" @click="doReviewCourse(s.row.id, true)">通过</el-button>
                    <el-button type="danger" size="small" @click="doReviewCourse(s.row.id, false)">驳回</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="pendingCourses.length === 0 && !loading" description="暂无待审核课程" />
            </el-tab-pane>

            <el-tab-pane label="资讯审核" name="newsReview" v-if="isAdmin">
              <el-table :data="pendingNews" stripe v-loading="loading">
                <el-table-column prop="title" label="标题" min-width="200" />
                <el-table-column prop="category" label="分类" width="100" />
                <el-table-column prop="excerpt" label="摘要" min-width="220" show-overflow-tooltip />
                <el-table-column label="操作" width="160">
                  <template #default="s">
                    <el-button type="success" size="small" @click="doReviewNews(s.row.id, true)">发布</el-button>
                    <el-button type="danger" size="small" @click="doReviewNews(s.row.id, false)">驳回</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="pendingNews.length === 0 && !loading" description="暂无待审核资讯" />
            </el-tab-pane>

            <!-- ============ 健康管理员：发布课程 / 预约名单 ============ -->
            <el-tab-pane label="发布课程" name="publish" v-if="isDoctor || isAdmin">
              <el-form :model="courseForm" :rules="courseRules" ref="courseFormRef" label-width="100px" size="large" style="max-width: 640px;">
                <el-form-item label="课程名称" prop="name">
                  <el-input v-model="courseForm.name" placeholder="如：高血压饮食管理课" maxlength="50" show-word-limit />
                </el-form-item>
                <el-form-item label="课程类型" prop="type">
                  <el-radio-group v-model="courseForm.type">
                    <el-radio value="ONLINE">线上课程</el-radio>
                    <el-radio value="OFFLINE">线下课程（可预约）</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="课程标签" prop="tag">
                  <el-select v-model="courseForm.tag" style="width: 100%;">
                    <el-option label="新上" value="新上" />
                    <el-option label="热门" value="热门" />
                    <el-option label="推荐" value="推荐" />
                  </el-select>
                </el-form-item>
                <template v-if="courseForm.type === 'OFFLINE'">
                  <el-form-item label="开课时间" prop="startTime">
                    <el-date-picker v-model="courseForm.startTime" type="datetime"
                      value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择开课时间" style="width: 100%;" />
                  </el-form-item>
                  <el-form-item label="上课地点" prop="location">
                    <el-input v-model="courseForm.location" placeholder="如：社区老年活动中心" maxlength="50" />
                  </el-form-item>
                  <el-form-item label="人数上限" prop="maxStudents">
                    <el-input-number v-model="courseForm.maxStudents" :min="1" :max="500" />
                  </el-form-item>
                </template>
                <el-form-item label="课程简介" prop="description">
                  <el-input v-model="courseForm.description" type="textarea" :rows="3" placeholder="课程内容简介" maxlength="500" show-word-limit />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="publishing" @click="submitCourse">
                    <i class="bi bi-cloud-arrow-up"></i> 提交审核
                  </el-button>
                  <span style="color:#909399; font-size:13px; margin-left:12px;">发布后经系统管理员审核才会对学员可见</span>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="我发布的课程" name="myCourses" v-if="isDoctor || isAdmin">
              <el-table :data="myManagedCourses" stripe v-loading="loading">
                <el-table-column prop="name" label="课程名称" min-width="150" />
                <el-table-column label="类型" width="90">
                  <template #default="s">
                    <el-tag :type="s.row.type === 'ONLINE' ? 'primary' : 'warning'">
                      {{ s.row.type === 'ONLINE' ? '线上' : '线下' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="currentStudents" label="已报名" width="80" />
                <el-table-column prop="maxStudents" label="名额" width="70" />
                <el-table-column label="状态" width="100">
                  <template #default="s">
                    <el-tag :type="courseStatusType(s.row.status)">{{ courseStatusText(s.row.status) }}</el-tag>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="myManagedCourses.length === 0 && !loading" description="还没有发布过课程" />
            </el-tab-pane>

            <el-tab-pane label="预约名单" name="bookings" v-if="isDoctor || isAdmin">
              <el-table :data="managedBookings" stripe v-loading="loading">
                <el-table-column prop="courseName" label="课程" min-width="140" />
                <el-table-column prop="userName" label="预约人" width="100" />
                <el-table-column prop="userPhone" label="电话" width="130" />
                <el-table-column prop="bookDate" label="日期" width="110" />
                <el-table-column prop="timeSlot" label="时间段" width="140" />
                <el-table-column label="状态" width="100">
                  <template #default="s">
                    <el-tag :type="bookingStatusType(s.row.status)">{{ bookingStatusText(s.row.status) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="160">
                  <template #default="s">
                    <el-button v-if="s.row.status === 'pending'" type="success" size="small" @click="doConfirmBooking(s.row.id)">
                      确认
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="managedBookings.length === 0 && !loading" description="暂无预约记录" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentUser } from '../api/auth'
import { getPendingCourses, reviewCourse, getPendingNews, reviewNews } from '../api/admin'
import { createCourse, getManagedCourses } from '../api/course'
import { getManagedBookings, confirmBooking } from '../api/booking'

const currentUser = ref(null)
const activeTab = ref('courseReview')
const loading = ref(false)
const publishing = ref(false)
const courseFormRef = ref(null)

const pendingCourses = ref([])
const pendingNews = ref([])
const myManagedCourses = ref([])
const managedBookings = ref([])

const isAdmin = computed(() => (currentUser.value?.roles || []).includes('ROLE_ADMIN'))
const isDoctor = computed(() => (currentUser.value?.roles || []).includes('ROLE_DOCTOR'))

const courseForm = reactive({
  name: '',
  type: 'OFFLINE',
  tag: '新上',
  startTime: '',
  location: '',
  maxStudents: 30,
  description: ''
})

const courseRules = {
  name: [{ required: true, message: '请填写课程名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择课程类型', trigger: 'change' }]
}

const courseStatusText = (s) => ({ approved: '已通过', pending: '待审核', rejected: '已驳回' }[s] || s)
const courseStatusType = (s) => ({ approved: 'success', pending: 'warning', rejected: 'danger' }[s] || 'info')
const bookingStatusText = (s) => ({ pending: '待确认', confirmed: '已确认', rejected: '已拒绝', cancelled: '已取消' }[s] || s)
const bookingStatusType = (s) => ({ pending: 'warning', confirmed: 'success', rejected: 'danger', cancelled: 'info' }[s] || 'info')

const loadData = async () => {
  loading.value = true
  try {
    const tasks = []
    if (isAdmin.value) {
      tasks.push(getPendingCourses().then(r => { if (r.code === 200) pendingCourses.value = r.data || [] }).catch(() => {}))
      tasks.push(getPendingNews().then(r => { if (r.code === 200) pendingNews.value = r.data || [] }).catch(() => {}))
    }
    tasks.push(getManagedCourses().then(r => { if (r.code === 200) myManagedCourses.value = r.data || [] }).catch(() => {}))
    tasks.push(getManagedBookings().then(r => { if (r.code === 200) managedBookings.value = r.data || [] }).catch(() => {}))
    await Promise.all(tasks)
  } finally {
    loading.value = false
  }
}

const doReviewCourse = async (id, approved) => {
  const res = await reviewCourse(id, approved)
  if (res.code === 200) {
    ElMessage.success(approved ? '课程已通过' : '课程已驳回')
    loadData()
  } else {
    ElMessage.error(res.message || '操作失败')
  }
}

const doReviewNews = async (id, approved) => {
  const res = await reviewNews(id, approved)
  if (res.code === 200) {
    ElMessage.success(approved ? '资讯已发布' : '资讯已驳回')
    loadData()
  } else {
    ElMessage.error(res.message || '操作失败')
  }
}

const submitCourse = async () => {
  if (!courseFormRef.value) return
  const valid = await courseFormRef.value.validate()
  if (!valid) return

  publishing.value = true
  try {
    const res = await createCourse({ ...courseForm })
    if (res.code === 200) {
      ElMessage.success('课程已提交，等待管理员审核')
      courseFormRef.value.resetFields()
      courseForm.name = ''
      courseForm.description = ''
      loadData()
    } else {
      ElMessage.error(res.message || '发布失败')
    }
  } catch (e) {
    ElMessage.error('发布失败，请检查网络')
  } finally {
    publishing.value = false
  }
}

const doConfirmBooking = async (id) => {
  const res = await confirmBooking(id)
  if (res.code === 200) {
    ElMessage.success('已确认该预约')
    loadData()
  } else {
    ElMessage.error(res.message || '操作失败')
  }
}

onMounted(async () => {
  if (!localStorage.getItem('token')) return
  try {
    const res = await getCurrentUser()
    currentUser.value = res.data
    // 进入页面后默认切到第一个有权限的 tab
    if (isAdmin.value) activeTab.value = 'courseReview'
    else if (isDoctor.value) activeTab.value = 'publish'
    await loadData()
  } catch (e) {
    currentUser.value = null
  }
})
</script>

<style scoped>
.console-view {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.page-section {
  padding: 40px 0 80px;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 30px;
}
</style>
