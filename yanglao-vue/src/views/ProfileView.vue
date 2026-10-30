<template>
  <div class="profile-view">
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
                <router-link to="/classroom" class="nav-link"><i class="bi bi-mortarboard"></i> 银发课堂</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/profile" class="nav-link active"><i class="bi bi-person-circle"></i> 个人中心</router-link>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    </header>

    <div id="profile" class="page-section active">
      <div class="container">
        <h2 class="section-title">
          <i class="bi bi-person-circle"></i> 个人中心
        </h2>

        <el-row :gutter="20">
          <el-col :xs="24" :sm="8">
            <el-card class="profile-card">
              <div class="profile-avatar">
                <img v-if="currentUser?.avatar" :src="currentUser.avatar" alt="头像" />
                <i v-else class="bi bi-person-circle"></i>
              </div>
              <div class="profile-name mt-3">{{ currentUser?.name || '未登录用户' }}</div>
              <div class="profile-phone text-muted">{{ currentUser?.phone || '请先登录' }}</div>
              <el-divider />
              <div class="profile-stats">
                <div class="stat-item">
                  <span class="stat-num">{{ checkinDays }}</span>
                  <span class="stat-label">打卡天数</span>
                </div>
                <div class="stat-item">
                  <span class="stat-num">{{ courseCount }}</span>
                  <span class="stat-label">学习课程</span>
                </div>
                <div class="stat-item">
                  <span class="stat-num">{{ bookingCount }}</span>
                  <span class="stat-label">活动预约</span>
                </div>
              </div>

              <template v-if="currentUser">
                <el-button v-if="isManager" type="warning" plain @click="$router.push('/console')" style="width: 100%; margin-top: 20px;">
                  <i class="bi bi-speedometer2"></i> 管理控制台
                </el-button>
                <el-button type="danger" @click="handleLogout" style="width: 100%; margin-top: 12px; margin-left: 0;">
                  <i class="bi bi-box-arrow-right"></i> 退出登录
                </el-button>
              </template>
            </el-card>

            <el-card class="auth-card mt-3" v-if="!currentUser">
              <el-tabs v-model="authTab">
                <el-tab-pane label="登录" name="login">
                  <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large">
                    <el-form-item prop="phone">
                      <el-input v-model="loginForm.phone" placeholder="手机号" prefix-icon="User" />
                    </el-form-item>
                    <el-form-item prop="password">
                      <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="handleLogin" style="width: 100%;">
                        <i class="bi bi-box-arrow-in-right"></i> 登录
                      </el-button>
                    </el-form-item>
                    <el-form-item style="margin-bottom: 0;">
                      <el-link type="primary" @click="forgotVisible = true" style="float: right;">
                        <i class="bi bi-question-circle"></i> 忘记密码？
                      </el-link>
                    </el-form-item>
                  </el-form>
                </el-tab-pane>

                <el-tab-pane label="注册" name="register">
                  <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" size="large">
                    <el-form-item prop="name">
                      <el-input v-model="registerForm.name" placeholder="姓名" prefix-icon="User" />
                    </el-form-item>
                    <el-form-item prop="phone">
                      <el-input v-model="registerForm.phone" placeholder="手机号" prefix-icon="Phone" />
                    </el-form-item>
                    <el-form-item prop="password">
                      <el-input v-model="registerForm.password" type="password" placeholder="密码（至少6位）" prefix-icon="Lock" show-password />
                    </el-form-item>
                    <el-form-item prop="passwordConfirm">
                      <el-input v-model="registerForm.passwordConfirm" type="password" placeholder="确认密码" prefix-icon="Lock" show-password />
                    </el-form-item>
                    <el-form-item>
                      <el-button type="success" @click="handleRegister" style="width: 100%;">
                        <i class="bi bi-person-plus"></i> 注册
                      </el-button>
                    </el-form-item>
                  </el-form>
                </el-tab-pane>
              </el-tabs>
            </el-card>
          </el-col>

          <el-col :xs="24" :sm="16">
            <el-card>
              <el-tabs v-model="profileTab">
                <el-tab-pane label="健康档案" name="health">
                  <div class="health-summary">
                    <h5><i class="bi bi-clipboard2-pulse"></i> 健康数据概览</h5>
                    <el-row :gutter="20" class="mt-3">
                      <el-col :span="12" :xs="24">
                        <div class="summary-item">
                          <div class="summary-label">平均血压</div>
                          <div class="summary-value">{{ avgBp }}</div>
                        </div>
                      </el-col>
                      <el-col :span="12" :xs="24">
                        <div class="summary-item">
                          <div class="summary-label">平均血糖</div>
                          <div class="summary-value">{{ avgBs }}</div>
                        </div>
                      </el-col>
                      <el-col :span="12" :xs="24">
                        <div class="summary-item">
                          <div class="summary-label">平均睡眠</div>
                          <div class="summary-value">{{ avgSleep }}</div>
                        </div>
                      </el-col>
                      <el-col :span="12" :xs="24">
                        <div class="summary-item">
                          <div class="summary-label">最近打卡</div>
                          <div class="summary-value">{{ lastCheckin }}</div>
                        </div>
                      </el-col>
                    </el-row>

                    <el-divider />

                    <h5>最新打卡记录</h5>
                    <el-table :data="recentRecords" stripe style="width: 100%;" size="small">
                      <el-table-column prop="date" label="日期" width="100" />
                      <el-table-column label="血压" width="100">
                        <template #default="scope">
                          {{ scope.row.bloodPressure }}
                        </template>
                      </el-table-column>
                      <el-table-column prop="bloodSugar" label="血糖" width="80" />
                      <el-table-column prop="sleepHours" label="睡眠" width="80" />
                      <el-table-column prop="heartRate" label="心率" width="80" />
                    </el-table>

                    <el-empty v-if="recentRecords.length === 0" description="暂无打卡记录" />
                  </div>
                </el-tab-pane>

                <el-tab-pane label="我的收藏" name="favorites">
                  <div v-if="collectedNews.length > 0" class="collected-news">
                    <el-table :data="collectedNews" stripe style="width: 100%">
                      <el-table-column prop="title" label="标题" min-width="200">
                        <template #default="scope">
                          <router-link :to="`/news/${scope.row.id}`" class="news-link">
                            {{ scope.row.title }}
                          </router-link>
                        </template>
                      </el-table-column>
                      <el-table-column prop="categoryName" label="分类" width="100" />
                      <el-table-column prop="publishDate" label="发布日期" width="120" />
                      <el-table-column label="操作" width="80">
                        <template #default="scope">
                          <el-button type="danger" size="small" @click="removeCollect(scope.row.id)">
                            取消
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                    <div class="mt-3 text-center">
                      <el-button @click="clearAllCollect" type="danger" plain>清空全部收藏</el-button>
                    </div>
                  </div>
                  <el-empty v-else description="暂无收藏内容">
                    <template #image>
                      <i class="bi bi-star" style="font-size: 60px; color: #ddd;"></i>
                    </template>
                    <template #description>
                      <p>浏览课程和资讯时可以收藏感兴趣的内容</p>
                    </template>
                  </el-empty>
                </el-tab-pane>

                <el-tab-pane label="账户设置" name="settings" v-if="currentUser">
                  <el-form ref="settingsFormRef" :model="settingsForm" :rules="settingsRules" label-width="100px" size="large">
                    <el-form-item label="头像">
                      <div class="avatar-editor">
                        <div class="avatar-preview">
                          <img v-if="settingsForm.avatar" :src="settingsForm.avatar" alt="头像预览" />
                          <i v-else class="bi bi-person-circle"></i>
                        </div>
                        <div class="avatar-actions">
                          <el-upload
                            :auto-upload="false"
                            :show-file-list="false"
                            accept="image/*"
                            :on-change="handleAvatarChange">
                            <el-button size="small" type="primary">上传头像</el-button>
                          </el-upload>
                          <el-button v-if="settingsForm.avatar" size="small" @click="settingsForm.avatar = ''">
                            移除
                          </el-button>
                        </div>
                      </div>
                    </el-form-item>
                    <el-form-item label="设置昵称">
                      <el-input v-model="settingsForm.name" placeholder="请输入昵称" maxlength="20" show-word-limit />
                    </el-form-item>
                    <el-form-item label="原密码" prop="oldPassword">
                      <el-input v-model="settingsForm.oldPassword" type="password" placeholder="不修改密码可不填" show-password />
                    </el-form-item>
                    <el-form-item label="新密码" prop="password">
                      <el-input v-model="settingsForm.password" type="password" placeholder="留空则不修改" show-password />
                    </el-form-item>
                    <el-form-item label="确认密码" prop="passwordConfirm">
                      <el-input v-model="settingsForm.passwordConfirm" type="password" placeholder="再次输入新密码" show-password />
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="saveSettings">
                        <i class="bi bi-check-lg"></i> 保存设置
                      </el-button>
                    </el-form-item>
                  </el-form>
                </el-tab-pane>
              </el-tabs>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>

  <!-- 忘记密码弹窗 -->
  <el-dialog v-model="forgotVisible" title="忘记密码" width="420px" center>
    <el-form ref="forgotFormRef" :model="forgotForm" :rules="forgotRules" label-width="90px" size="large">
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="forgotForm.phone" placeholder="请输入注册手机号" maxlength="11" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="forgotForm.newPassword" type="password" placeholder="新密码（至少6位）" show-password />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="forgotForm.confirmPassword" type="password" placeholder="再次输入新密码" show-password />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="forgotVisible = false">取消</el-button>
      <el-button type="primary" :loading="forgotLoading" @click="handleResetPassword">重置密码</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNewsById } from '../data/news'
import { login, register, logout, updateUser, getCurrentUser, changePassword, resetPassword } from '../api/auth'
import { getFavorites, removeFavorite as removeFavoriteApi, clearFavorites as clearFavoritesApi } from '../api/news'
import { getHealthStats, getCheckinDays } from '../api/health'
import { getBookingCount } from '../api/booking'

const authTab = ref('login')
const profileTab = ref('health')
const currentUser = ref(null)
const loginFormRef = ref(null)
const registerFormRef = ref(null)
const settingsFormRef = ref(null)

const loginForm = reactive({
  phone: '',
  password: ''
})

// 角色判断：管理员/健康管理员显示管理控制台入口
const isManager = computed(() => {
  const roles = currentUser.value?.roles || []
  return roles.includes('ROLE_ADMIN') || roles.includes('ROLE_DOCTOR')
})

const registerForm = reactive({
  name: '',
  phone: '',
  password: '',
  passwordConfirm: ''
})

const settingsForm = reactive({
  name: '',
  avatar: '',
  oldPassword: '',
  password: '',
  passwordConfirm: ''
})

// 忘记密码
const forgotVisible = ref(false)
const forgotLoading = ref(false)
const forgotFormRef = ref(null)
const forgotForm = reactive({
  phone: '',
  newPassword: '',
  confirmPassword: ''
})
const forgotRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== forgotForm.newPassword) callback(new Error('两次密码输入不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ]
}

const handleResetPassword = async () => {
  if (!forgotFormRef.value) return
  try {
    const valid = await forgotFormRef.value.validate()
    if (!valid) return
    forgotLoading.value = true
    const res = await resetPassword(forgotForm.phone, forgotForm.newPassword)
    if (res.code === 200) {
      ElMessage.success('密码已重置，请用新密码登录')
      forgotVisible.value = false
      forgotFormRef.value.resetFields()
      loginForm.phone = forgotForm.phone
    } else {
      ElMessage.error(res.message || '重置失败')
    }
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('重置失败，请重试')
    }
  } finally {
    forgotLoading.value = false
  }
}

const loginRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { len: 11, message: '手机号必须为11位', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { len: 11, message: '手机号必须为11位', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  passwordConfirm: [{ required: true, message: '请确认密码', trigger: 'blur' }]
}

const settingsRules = {
  passwordConfirm: [
    {
      validator: (rule, value, callback) => {
        if (settingsForm.password && value !== settingsForm.password) {
          callback(new Error('两次密码输入不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const checkinDays = computed(() => {
  const records = JSON.parse(localStorage.getItem('healthRecords') || '[]')
  return records.length
})

const courseCount = computed(() => {
  const courses = JSON.parse(localStorage.getItem('myCourses') || '[]')
  return courses.length
})

const bookingCount = computed(() => {
  const bookings = JSON.parse(localStorage.getItem('bookings') || '[]')
  return bookings.length
})

const recentRecords = computed(() => {
  const records = JSON.parse(localStorage.getItem('healthRecords') || '[]')
  return records.slice(0, 5)
})

const avgBp = computed(() => {
  const records = JSON.parse(localStorage.getItem('healthRecords') || '[]')
  if (records.length === 0) return '--/-- mmHg'
  const total = records.reduce((sum, r) => {
    const [high] = r.bloodPressure.split('/').map(Number)
    return sum + high
  }, 0)
  const avgHigh = Math.round(total / records.length)
  return `${avgHigh} mmHg`
})

const avgBs = computed(() => {
  const records = JSON.parse(localStorage.getItem('healthRecords') || '[]')
  if (records.length === 0) return '-- mmol/L'
  const total = records.reduce((sum, r) => sum + parseFloat(r.bloodSugar), 0)
  return (total / records.length).toFixed(1) + ' mmol/L'
})

const avgSleep = computed(() => {
  const records = JSON.parse(localStorage.getItem('healthRecords') || '[]')
  if (records.length === 0) return '-- 小时'
  const total = records.reduce((sum, r) => sum + parseFloat(r.sleepHours), 0)
  return (total / records.length).toFixed(1) + ' 小时'
})

const lastCheckin = computed(() => {
  const records = JSON.parse(localStorage.getItem('healthRecords') || '[]')
  return records.length > 0 ? records[0].date : '--'
})

const collectedNews = computed(() => {
  const collectedIds = JSON.parse(localStorage.getItem('collectedNews') || '[]')
  return collectedIds.map(id => getNewsById(id)).filter(Boolean)
})

const removeCollect = async (id) => {
  try {
    await removeFavoriteApi(id)
    ElMessage.success('已取消收藏')
  } catch (error) {
    ElMessage.error('取消收藏失败')
  }
}

const clearAllCollect = async () => {
  try {
    await clearFavoritesApi()
    ElMessage.success('已清空全部收藏')
  } catch (error) {
    ElMessage.error('清空收藏失败')
  }
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    const valid = await loginFormRef.value.validate()
    if (valid) {
      const response = await login(loginForm.phone, loginForm.password)
      currentUser.value = response.data.user
      settingsForm.name = response.data.user?.name || ''
      settingsForm.avatar = response.data.user?.avatar || ''
      ElMessage.success('登录成功！')
      loginFormRef.value.resetFields()
    }
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('登录失败，请重试')
    }
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    const valid = await registerFormRef.value.validate()
    if (valid) {
      if (registerForm.password !== registerForm.passwordConfirm) {
        ElMessage.error('两次密码输入不一致！')
        return
      }

      const response = await register(registerForm.name, registerForm.phone, registerForm.password)
      if (response.code !== 200) {
        ElMessage.error(response.message || '注册失败，请重试')
        return
      }
      ElMessage.success('注册成功，请登录！')
      registerFormRef.value.resetFields()
      authTab.value = 'login'
    }
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('注册失败，请重试')
    }
  }
}

const handleLogout = async () => {
  try {
    await logout()
    currentUser.value = null
    ElMessage.success('已退出登录')
  } catch (error) {
    ElMessage.error('退出失败')
  }
}

// 选择头像图片后，压缩到 200px 以内并转为 JPEG DataURL，避免整张原图塞进数据库
const handleAvatarChange = (uploadFile) => {
  const file = uploadFile?.raw
  if (!file) return

  if (!file.type || !file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return
  }

  const reader = new FileReader()
  reader.onload = () => {
    const img = new Image()
    img.onload = () => {
      const max = 200
      let { width, height } = img
      if (width > max || height > max) {
        const ratio = Math.min(max / width, max / height)
        width = Math.round(width * ratio)
        height = Math.round(height * ratio)
      }
      const canvas = document.createElement('canvas')
      canvas.width = width
      canvas.height = height
      canvas.getContext('2d').drawImage(img, 0, 0, width, height)
      settingsForm.avatar = canvas.toDataURL('image/jpeg', 0.85)
    }
    img.onerror = () => ElMessage.error('图片解析失败，请更换图片')
    img.src = reader.result
  }
  reader.onerror = () => ElMessage.error('图片读取失败')
  reader.readAsDataURL(file)
}

const saveSettings = async () => {
  if (!settingsFormRef.value) return

  try {
    const valid = await settingsFormRef.value.validate()
    if (valid) {
      // 填写了新密码时：必须先填写原密码，并单独调用改密接口
      if (settingsForm.password) {
        if (!settingsForm.oldPassword) {
          ElMessage.error('修改密码需先填写原密码')
          return
        }
        if (settingsForm.password !== settingsForm.passwordConfirm) {
          ElMessage.error('两次密码输入不一致！')
          return
        }
        const pwdRes = await changePassword(settingsForm.oldPassword, settingsForm.password)
        if (pwdRes.code !== 200) {
          ElMessage.error(pwdRes.message || '修改密码失败')
          return
        }
      }

      const response = await updateUser({
        name: settingsForm.name,
        avatar: settingsForm.avatar
      })
      if (response.code !== 200) {
        ElMessage.error(response.message || '保存设置失败')
        return
      }
      currentUser.value = response.data
      ElMessage.success('设置已保存！')
      settingsForm.oldPassword = ''
      settingsForm.password = ''
      settingsForm.passwordConfirm = ''
    }
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('保存设置失败')
    }
  }
}

const checkLoginStatus = async () => {
  try {
    const response = await getCurrentUser()
    currentUser.value = response.data
    settingsForm.name = response.data?.name || ''
    settingsForm.avatar = response.data?.avatar || ''
  } catch (error) {
    currentUser.value = null
  }
}

onMounted(() => {
  checkLoginStatus()
})
</script>

<style scoped>
.profile-view {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.profile-card {
  text-align: center;
}

.profile-avatar {
  font-size: 80px;
  color: var(--primary-color);
}

.profile-avatar img {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-editor {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-preview {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  font-size: 40px;
  color: var(--primary-color);
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.profile-name {
  font-size: 1.5rem;
  font-weight: bold;
}

.profile-phone {
  color: #999;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-item .stat-num {
  display: block;
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--primary-color);
}

.stat-item .stat-label {
  font-size: 0.85rem;
  color: #666;
}

.summary-item {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 15px;
}

.summary-label {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 5px;
}

.summary-value {
  font-size: 1.2rem;
  font-weight: bold;
  color: var(--primary-color);
}

.auth-card :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.auth-card :deep(.el-form-item) {
  margin-bottom: 18px;
}

.collected-news {
  padding: 10px 0;
}

.collected-news .news-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
}

.collected-news .news-link:hover {
  text-decoration: underline;
}
</style>
