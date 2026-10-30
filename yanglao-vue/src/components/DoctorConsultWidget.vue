<template>
  <div class="consult-widget">
    <!-- 浮动入口按钮 -->
    <button class="consult-fab" @click="openDrawer" title="问医生">
      <i class="bi bi-chat-dots"></i>
      <span class="fab-label">问医生</span>
    </button>

    <!-- 抽屉面板 -->
    <el-drawer v-model="visible" title="健康咨询 · 问医生" size="420px" direction="rtl">
      <!-- 未登录 -->
      <div v-if="!isLoggedIn" class="login-hint">
        <i class="bi bi-shield-lock" style="font-size: 42px; color: #909399;"></i>
        <p>登录后即可向健康管理员提问</p>
        <el-button type="primary" @click="$router.push('/profile')">去登录</el-button>
      </div>

      <template v-else>
        <el-tabs v-model="tab">
          <!-- 用户：提问 -->
          <el-tab-pane label="我要提问" name="ask" v-if="!isStaff">
            <el-input
              v-model="question"
              type="textarea"
              :rows="5"
              maxlength="500"
              show-word-limit
              placeholder="请描述您的健康问题，健康管理员会尽快回复您…"
            />
            <el-button type="primary" style="width: 100%; margin-top: 12px;" :loading="submitting" @click="submitQuestion">
              <i class="bi bi-send"></i> 提交提问
            </el-button>
          </el-tab-pane>

          <!-- 用户：我的提问 -->
          <el-tab-pane label="我的提问" name="mine" v-if="!isStaff">
            <div v-if="myList.length === 0" class="empty-hint">您还没有提问过</div>
            <div v-for="item in myList" :key="item.id" class="qa-item">
              <div class="q">
                <span class="tag q-tag">问</span>
                <span>{{ item.question }}</span>
              </div>
              <div v-if="item.status === 'answered'" class="a">
                <span class="tag a-tag">答</span>
                <span>{{ item.answer }}</span>
                <div class="doctor">—— {{ item.doctorName || '健康管理员' }}</div>
              </div>
              <div v-else class="a pending">等待医生回复中…</div>
            </div>
          </el-tab-pane>

          <!-- 医生/管理员：待回复 -->
          <el-tab-pane label="待回复" name="pending" v-if="isStaff">
            <div v-if="pendingList.length === 0" class="empty-hint">暂时没有待回复的提问</div>
            <div v-for="item in pendingList" :key="item.id" class="qa-item">
              <div class="q">
                <span class="tag q-tag">问</span>
                <span>{{ item.question }}</span>
                <div class="asker">{{ item.userName }} 提问</div>
              </div>
              <el-input v-model="answers[item.id]" type="textarea" :rows="3" placeholder="输入回复内容…" />
              <el-button type="success" size="small" style="margin-top: 8px;" @click="submitAnswer(item.id)">
                回复
              </el-button>
            </div>
          </el-tab-pane>

          <!-- 所有人：已回复问答 -->
          <el-tab-pane label="健康问答" name="answered">
            <div v-if="answeredList.length === 0" class="empty-hint">暂无问答记录</div>
            <div v-for="item in answeredList" :key="item.id" class="qa-item">
              <div class="q">
                <span class="tag q-tag">问</span>
                <span>{{ item.question }}</span>
              </div>
              <div class="a">
                <span class="tag a-tag">答</span>
                <span>{{ item.answer }}</span>
                <div class="doctor">—— {{ item.doctorName || '健康管理员' }}</div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { askQuestion, getMyConsultations, getAnsweredConsultations, getPendingConsultations, answerConsultation } from '../api/consultation'

const visible = ref(false)
const tab = ref('ask')
const submitting = ref(false)
const question = ref('')
const myList = ref([])
const pendingList = ref([])
const answeredList = ref([])
const answers = reactive({})

const isLoggedIn = computed(() => !!localStorage.getItem('token'))
const roles = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('currentUser') || 'null')?.roles || []
  } catch { return [] }
})
const isStaff = computed(() => roles.value.includes('ROLE_DOCTOR') || roles.value.includes('ROLE_ADMIN'))

const openDrawer = async () => {
  visible.value = true
  if (!isLoggedIn.value) return
  tab.value = isStaff.value ? 'pending' : 'ask'
  await loadAll()
}

const loadAll = async () => {
  if (!isLoggedIn.value) return
  const jobs = []
  jobs.push(getAnsweredConsultations().then(r => { if (r.code === 200) answeredList.value = r.data || [] }).catch(() => {}))
  if (!isStaff.value) {
    jobs.push(getMyConsultations().then(r => { if (r.code === 200) myList.value = r.data || [] }).catch(() => {}))
  } else {
    jobs.push(getPendingConsultations().then(r => { if (r.code === 200) pendingList.value = r.data || [] }).catch(() => {}))
  }
  await Promise.all(jobs)
}

const submitQuestion = async () => {
  if (!question.value.trim()) {
    ElMessage.warning('请输入要咨询的问题')
    return
  }
  submitting.value = true
  try {
    const res = await askQuestion(question.value.trim())
    if (res.code === 200) {
      ElMessage.success('提问已提交，等待医生回复')
      question.value = ''
      tab.value = 'mine'
      await loadAll()
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } catch (e) {
    ElMessage.error('提交失败，请检查网络')
  } finally {
    submitting.value = false
  }
}

const submitAnswer = async (id) => {
  const text = (answers[id] || '').trim()
  if (!text) {
    ElMessage.warning('请输入回复内容')
    return
  }
  const res = await answerConsultation(id, text)
  if (res.code === 200) {
    ElMessage.success('已回复')
    answers[id] = ''
    await loadAll()
  } else {
    ElMessage.error(res.message || '回复失败')
  }
}

onMounted(() => {})
</script>

<style scoped>
.consult-fab {
  position: fixed;
  right: 24px;
  bottom: 32px;
  z-index: 9999;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 18px;
  border: none;
  border-radius: 999px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 15px;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(102, 126, 234, .45);
  transition: transform .15s ease;
}
.consult-fab:hover {
  transform: translateY(-2px);
}
.consult-fab i {
  font-size: 18px;
}

.login-hint {
  text-align: center;
  padding: 60px 0;
  color: #606266;
}
.login-hint p {
  margin: 16px 0;
}

.qa-item {
  border-bottom: 1px solid #ebeef5;
  padding: 12px 0;
}
.qa-item .q, .qa-item .a {
  display: flex;
  gap: 8px;
  align-items: flex-start;
  line-height: 1.6;
  font-size: 14px;
}
.qa-item .a {
  margin-top: 8px;
  color: #606266;
}
.qa-item .a.pending {
  color: #e6a23c;
}
.tag {
  flex-shrink: 0;
  font-size: 12px;
  padding: 1px 6px;
  border-radius: 4px;
  color: #fff;
}
.q-tag { background: #667eea; }
.a-tag { background: #67c23a; }
.doctor {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}
.asker {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.empty-hint {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}
</style>
