<template>
  <div class="health-view">
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
                <router-link to="/health" class="nav-link active">
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

    <div id="health" class="page-section active">
      <div class="container">
        <h2 class="section-title">
          <i class="bi bi-heart-pulse"></i> 健康守护
        </h2>

        <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
          <el-tab-pane label="健康打卡" name="checkin">
            <el-card class="checkin-card">
              <template #header>
                <div class="card-header">
                  <span><i class="bi bi-clipboard2-plus"></i> 今日健康打卡</span>
                </div>
              </template>

              <el-form ref="checkinFormRef" :model="checkinForm" :rules="checkinRules" label-width="120px" size="large">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="血压 (mmHg)" prop="bloodPressure">
                      <el-input v-model="checkinForm.bloodPressure" placeholder="如: 120/80">
                        <template #append>mmHg</template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="血糖 (mmol/L)" prop="bloodSugar">
                      <el-input v-model="checkinForm.bloodSugar" placeholder="如: 5.6" type="number">
                        <template #append>mmol/L</template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="睡眠时长" prop="sleepHours">
                      <el-input v-model="checkinForm.sleepHours" placeholder="如: 7.5" type="number">
                        <template #append>小时</template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="心率" prop="heartRate">
                      <el-input v-model="checkinForm.heartRate" placeholder="如: 72" type="number">
                        <template #append>次/分</template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="今日感受" prop="feeling">
                  <el-radio-group v-model="checkinForm.feeling">
                    <el-radio-button label="good">精神良好</el-radio-button>
                    <el-radio-button label="normal">一般</el-radio-button>
                    <el-radio-button label="tired">有些疲惫</el-radio-button>
                    <el-radio-button label="unwell">身体不适</el-radio-button>
                  </el-radio-group>
                </el-form-item>

                <el-form-item label="备注">
                  <el-input v-model="checkinForm.note" type="textarea" :rows="3" placeholder="其他需要记录的信息..." />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" size="large" @click="submitCheckin" :loading="submitting" style="width: 100%;">
                    <i class="bi bi-check-circle"></i> 提交打卡
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-tab-pane>

          <el-tab-pane label="历史记录" name="records">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span><i class="bi bi-clock-history"></i> 健康历史记录</span>
                  <el-button type="primary" @click="exportRecords" size="small">
                    <i class="bi bi-download"></i> 导出数据
                  </el-button>
                </div>
              </template>

              <el-table :data="healthRecords" stripe style="width: 100%" v-loading="loading">
                <el-table-column prop="date" label="日期" width="120" />
                <el-table-column prop="time" label="时间" width="100" />
                <el-table-column label="血压" width="120">
                  <template #default="scope">
                    {{ scope.row.bloodPressure }} mmHg
                  </template>
                </el-table-column>
                <el-table-column label="血糖" width="100">
                  <template #default="scope">
                    {{ scope.row.bloodSugar }} mmol/L
                  </template>
                </el-table-column>
                <el-table-column label="睡眠" width="100">
                  <template #default="scope">
                    {{ scope.row.sleepHours }} 小时
                  </template>
                </el-table-column>
                <el-table-column label="心率" width="100">
                  <template #default="scope">
                    {{ scope.row.heartRate }} 次/分
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="getFeelingType(scope.row.feeling)">
                      {{ getFeelingText(scope.row.feeling) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="note" label="备注" show-overflow-tooltip />
              </el-table>

              <el-empty v-if="healthRecords.length === 0 && !loading" description="暂无打卡记录" />
            </el-card>
          </el-tab-pane>

          <el-tab-pane label="健康资讯" name="tips">
            <div class="tips-hero">
              <div class="tips-hero-icon">
                <i class="bi bi-lightbulb"></i>
              </div>
              <div class="tips-hero-text">
                <h3>健康小知识</h3>
                <p>每天一点小改变，守护长辈好身体</p>
              </div>
            </div>

            <div class="tips-grid">
              <div v-for="(tip, index) in healthTips" :key="tip.title" class="tip-card" :class="`tip-card-${index}`">
                <div class="tip-card-head">
                  <span class="tip-icon"><i :class="tip.icon"></i></span>
                  <span class="tip-title">{{ tip.title }}</span>
                  <span class="tip-count">{{ tip.items.length }} 条</span>
                </div>
                <ul class="tip-list">
                  <li v-for="item in tip.items" :key="item">
                    <i class="bi bi-check-circle-fill"></i>
                    <span>{{ item }}</span>
                  </li>
                </ul>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const activeTab = ref(route.query.tab || 'checkin')
const loading = ref(false)
const submitting = ref(false)
const checkinFormRef = ref(null)

// 健康小知识（按卡片展示）
const healthTips = [
  {
    title: '晨起养生三宜',
    icon: 'bi bi-sunrise',
    items: [
      '宜慢起：醒来先躺1分钟再坐起，坐起1分钟再站立',
      '宜喝温水：补充夜间流失的水分，稀释血液',
      '宜伸展：轻柔活动四肢，促进血液循环'
    ]
  },
  {
    title: '每日饮水指南',
    icon: 'bi bi-droplet-half',
    items: [
      '晨起一杯水：约200ml，帮助排便',
      '午前一杯水：补充工作消耗',
      '午后一杯水：提神醒脑',
      '睡前少量水：不宜过多，以免影响睡眠'
    ]
  },
  {
    title: '健康饮食建议',
    icon: 'bi bi-basket',
    items: [
      '少油少盐：每日盐摄入不超过6克',
      '粗细搭配：多吃全谷物和杂粮',
      '水果蔬菜：每天500克以上',
      '定时定量：规律用餐，避免暴饮暴食'
    ]
  },
  {
    title: '适度运动建议',
    icon: 'bi bi-bicycle',
    items: [
      '散步：每天30分钟，分2-3次完成',
      '太极拳：每周3-5次，每次30分钟',
      '广场舞：适量即可，注意休息',
      '量力而行：根据身体状况调整强度'
    ]
  }
]

const checkinForm = reactive({
  bloodPressure: '',
  bloodSugar: '',
  sleepHours: '',
  heartRate: '',
  feeling: 'good',
  note: ''
})

const checkinRules = {
  bloodPressure: [
    { required: true, message: '请输入血压值', trigger: 'blur' }
  ],
  bloodSugar: [
    { required: true, message: '请输入血糖值', trigger: 'blur' }
  ],
  sleepHours: [
    { required: true, message: '请输入睡眠时长', trigger: 'blur' }
  ],
  heartRate: [
    { required: true, message: '请输入心率', trigger: 'blur' }
  ],
  feeling: [
    { required: true, message: '请选择今日感受', trigger: 'change' }
  ]
}

const healthRecords = ref([])

const getFeelingType = (feeling) => {
  const types = { good: 'success', normal: 'info', tired: 'warning', unwell: 'danger' }
  return types[feeling] || 'info'
}

const getFeelingText = (feeling) => {
  const texts = { good: '精神良好', normal: '一般', tired: '有些疲惫', unwell: '身体不适' }
  return texts[feeling] || '未知'
}

const submitCheckin = async () => {
  if (!checkinFormRef.value) return

  await checkinFormRef.value.validate((valid) => {
    if (valid) {
      submitting.value = true

      const data = {
        date: new Date().toLocaleDateString('zh-CN'),
        time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
        bloodPressure: checkinForm.bloodPressure,
        bloodSugar: checkinForm.bloodSugar,
        sleepHours: checkinForm.sleepHours,
        heartRate: checkinForm.heartRate,
        feeling: checkinForm.feeling,
        note: checkinForm.note
      }

      let records = JSON.parse(localStorage.getItem('healthRecords') || '[]')
      records.unshift(data)
      localStorage.setItem('healthRecords', JSON.stringify(records))

      setTimeout(() => {
        submitting.value = false
        ElMessage.success('健康打卡成功！')
        loadRecords()
        resetForm()
        activeTab.value = 'records'
      }, 1000)
    }
  })
}

const resetForm = () => {
  checkinForm.bloodPressure = ''
  checkinForm.bloodSugar = ''
  checkinForm.sleepHours = ''
  checkinForm.heartRate = ''
  checkinForm.feeling = 'good'
  checkinForm.note = ''
}

const loadRecords = () => {
  loading.value = true
  const records = JSON.parse(localStorage.getItem('healthRecords') || '[]')
  healthRecords.value = records
  loading.value = false
}

const exportRecords = () => {
  const records = JSON.parse(localStorage.getItem('healthRecords') || '[]')
  if (records.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }

  const csvContent = 'data:text/csv;charset=utf-8,\uFEFF'
  const headers = ['日期', '时间', '血压', '血糖', '睡眠时长', '心率', '状态', '备注']
  const rows = records.map(r => [
    r.date,
    r.time,
    r.bloodPressure,
    r.bloodSugar,
    r.sleepHours,
    r.heartRate,
    getFeelingText(r.feeling),
    r.note || ''
  ])

  const csv = [headers, ...rows].map(e => e.join(',')).join('\n')
  const link = document.createElement('a')
  link.href = encodeURI(csvContent + csv)
  link.download = `健康记录_${new Date().toLocaleDateString('zh-CN').replace(/\//g, '-')}.csv`
  link.click()

  ElMessage.success('数据导出成功')
}

const handleTabChange = (tabName) => {
  if (tabName === 'records') {
    loadRecords()
  }
}

onMounted(() => {
  loadRecords()
  if (route.query.tab) activeTab.value = route.query.tab
})

watch(() => route.query.tab, (val) => {
  if (val) activeTab.value = val
})
</script>

<style scoped>
.health-view {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.checkin-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* ===== 健康资讯 ===== */
.tips-hero {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 22px 26px;
  border-radius: 16px;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  color: #fff;
  box-shadow: 0 10px 24px rgba(91, 141, 190, 0.25);
  margin-bottom: 22px;
}

.tips-hero-icon {
  width: 54px;
  height: 54px;
  flex-shrink: 0;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.22);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.7rem;
}

.tips-hero-text h3 {
  margin: 0 0 4px;
  font-size: 1.25rem;
  font-weight: 600;
}

.tips-hero-text p {
  margin: 0;
  font-size: 0.9rem;
  opacity: 0.9;
}

.tips-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

@media (max-width: 768px) {
  .tips-grid {
    grid-template-columns: 1fr;
  }
}

.tip-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px 22px 14px;
  border-top: 4px solid var(--primary-color);
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.tip-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.12);
}

.tip-card-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.tip-icon {
  width: 38px;
  height: 38px;
  flex-shrink: 0;
  border-radius: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.15rem;
  color: #fff;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
}

.tip-title {
  flex: 1;
  font-size: 1.02rem;
  font-weight: 600;
  color: var(--primary-dark);
}

.tip-count {
  font-size: 0.75rem;
  color: var(--primary-color);
  background: rgba(91, 141, 190, 0.12);
  padding: 2px 10px;
  border-radius: 20px;
}

.tip-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.tip-list li {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 7px 0;
  color: #5a6672;
  font-size: 0.9rem;
  line-height: 1.6;
  border-bottom: 1px dashed #eef1f5;
}

.tip-list li:last-child {
  border-bottom: none;
}

.tip-list li i {
  flex-shrink: 0;
  margin-top: 2px;
  font-size: 0.95rem;
  color: var(--primary-light);
}

/* 四张卡片各自的主题色 */
.tip-card-0 {
  border-top-color: #F5A962;
}

.tip-card-0 .tip-icon {
  background: linear-gradient(135deg, #F5A962, #F0C078);
}

.tip-card-0 .tip-title,
.tip-card-0 .tip-count {
  color: #C97F33;
}

.tip-card-0 .tip-count {
  background: rgba(245, 169, 98, 0.15);
}

.tip-card-0 .tip-list li i {
  color: #F5A962;
}

.tip-card-2 {
  border-top-color: #4FB286;
}

.tip-card-2 .tip-icon {
  background: linear-gradient(135deg, #4FB286, #7ED0AC);
}

.tip-card-2 .tip-title,
.tip-card-2 .tip-count {
  color: #2F7A5B;
}

.tip-card-2 .tip-count {
  background: rgba(79, 178, 134, 0.15);
}

.tip-card-2 .tip-list li i {
  color: #4FB286;
}

.tip-card-3 {
  border-top-color: #8E86D8;
}

.tip-card-3 .tip-icon {
  background: linear-gradient(135deg, #8E86D8, #B3AEE8);
}

.tip-card-3 .tip-title,
.tip-card-3 .tip-count {
  color: #5E56AE;
}

.tip-card-3 .tip-count {
  background: rgba(142, 134, 216, 0.15);
}

.tip-card-3 .tip-list li i {
  color: #8E86D8;
}

:deep(.el-radio-button__inner) {
  padding: 12px 20px;
}

:deep(.el-tabs__content) {
  padding: 20px 0;
}
</style>
