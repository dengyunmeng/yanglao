<template>
  <div class="anti-fraud-view">
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
                <router-link to="/anti-fraud" class="nav-link active"><i class="bi bi-shield-plus"></i> 数字反诈</router-link>
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

    <div id="anti-fraud" class="page-section active">
      <div class="container">
        <h2 class="section-title">
          <i class="bi bi-shield-plus"></i> 数字反诈
        </h2>

        <el-tabs v-model="activeTab" type="border-card">
          <el-tab-pane label="诈骗案例" name="cases">
            <div class="fraud-cases">
              <el-card v-for="(caseItem, index) in fraudCases" :key="index" class="fraud-case-card" shadow="hover" v-loading="loading">
                <template #header>
                  <div class="case-header">
                    <el-tag :type="caseItem.tagType" effect="dark">{{ caseItem.tag }}</el-tag>
                    <h4>{{ caseItem.title }}</h4>
                  </div>
                </template>
                <div class="case-content">
                  <p><strong>作案手法：</strong>{{ caseItem.method }}</p>
                  <p><strong>典型案例：</strong>{{ caseItem.example }}</p>
                  <p><strong>防骗提示：</strong><span class="tip-text">{{ caseItem.tip }}</span></p>
                </div>
              </el-card>
            </div>
          </el-tab-pane>

          <el-tab-pane label="防骗测试" name="test">
            <el-card>
              <template #header>
                <span><i class="bi bi-question-circle"></i> 防骗能力小测试</span>
              </template>

              <el-form label-width="auto">
                <div v-for="(question, qIndex) in testQuestions" :key="qIndex" class="test-question">
                  <h5>{{ qIndex + 1 }}. {{ question.text }}</h5>
                  <el-radio-group v-model="testAnswers[qIndex]">
                    <el-radio v-for="(option, oIndex) in question.options" :key="oIndex" :label="option.value">
                      {{ option.label }}
                    </el-radio>
                  </el-radio-group>
                </div>

                <el-button type="primary" size="large" @click="submitTest" style="width: 100%; margin-top: 20px;">
                  <i class="bi bi-check-circle"></i> 提交答案
                </el-button>
              </el-form>

              <el-dialog v-model="resultDialogVisible" title="测试结果" width="400px" center>
                <div class="test-result">
                  <el-progress type="circle" :percentage="testScore" :color="scoreColor" />
                  <p class="result-text">{{ resultText }}</p>
                </div>
              </el-dialog>
            </el-card>
          </el-tab-pane>

          <el-tab-pane label="我要举报" name="report">
            <el-card>
              <template #header>
                <span><i class="bi bi-exclamation-triangle"></i> 我要举报</span>
              </template>

              <el-form ref="reportFormRef" :model="reportForm" :rules="reportRules" label-width="100px" size="large">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="您的姓名" prop="name">
                      <el-input v-model="reportForm.name" placeholder="请输入真实姓名" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="联系电话" prop="phone">
                      <el-input v-model="reportForm.phone" placeholder="请输入11位手机号" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="诈骗类型" prop="type">
                  <el-select v-model="reportForm.type" placeholder="请选择诈骗类型" style="width: 100%;">
                    <el-option label="冒充公检法" value="police" />
                    <el-option label="保健品诈骗" value="health" />
                    <el-option label="冒充客服" value="customer" />
                    <el-option label="投资理财诈骗" value="investment" />
                    <el-option label="其他类型" value="other" />
                  </el-select>
                </el-form-item>

                <el-form-item label="涉事平台" prop="platform">
                  <el-input v-model="reportForm.platform" placeholder="如：微信、电话、某APP等" />
                </el-form-item>

                <el-form-item label="诈骗金额">
                  <el-input v-model="reportForm.amount" placeholder="请输入金额（元）" type="number">
                    <template #append>元</template>
                  </el-input>
                </el-form-item>

                <el-form-item label="详细描述" prop="detail">
                  <el-input v-model="reportForm.detail" type="textarea" :rows="4" placeholder="请详细描述诈骗过程、对方联系方式等信息" />
                </el-form-item>

                <el-form-item>
                  <el-checkbox v-model="reportForm.agree">
                    我确认以上信息真实有效，愿意配合后续调查
                  </el-checkbox>
                </el-form-item>

                <el-form-item>
                  <el-button type="danger" size="large" @click="submitReport" :loading="submitting" style="width: 100%;">
                    <i class="bi bi-send"></i> 提交举报
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getFraudCases, submitFraudReport, submitFraudTest } from '../api/fraud'

const route = useRoute()
const activeTab = ref(route.query.tab || 'cases')
const resultDialogVisible = ref(false)
const submitting = ref(false)
const loading = ref(false)
const reportFormRef = ref(null)

// 案例标签 → el-tag 类型（兼容后端只返回 tag 文本的情况）
const tagTypeMap = {
  '高发': 'danger',
  '警惕': 'warning',
  '新型': 'info',
  '提示': 'success'
}

// 兜底案例数据：后端未启动时页面也能正常展示
const fallbackCases = [
  {
    tag: '高发',
    title: '冒充公检法诈骗',
    method: '骗子冒充警察/法官打电话，声称受害者身份证涉嫌洗钱犯罪，要求将资金转入「安全账户」。',
    example: '张大爷接到自称「市公安局」的电话，对方说他的身份证被用来诈骗，要冻结他的账户。张大爷在恐慌中分 3 次向骗子转账共计 12 万元。',
    tip: '公安机关不会通过电话办案，绝不会要求你转账。接到此类电话直接挂断并拨打 110 或 96110。'
  },
  {
    tag: '警惕',
    title: '保健品诈骗',
    method: '骗子以「免费体检」「专家坐诊」为由，夸大老年人病情，推销价格虚高的「特效药」。',
    example: '王大妈在菜市场被拉去「免费体检」，「专家」说她「三高严重」，让买 8000 元的「特效药」，结果吃了毫无效果。',
    tip: '不要相信街头「免费体检」。药品须有国药准字，保健品不能治病，身体不舒服请去正规医院。'
  },
  {
    tag: '新型',
    title: '冒充客服退款',
    method: '骗子冒充购物平台客服，说你买的东西有问题要退款，诱导你提供银行卡号和验证码。',
    example: '李奶奶接到电话说在「某东」买的保健品有问题要退款，按骗子说的在手机上操作，结果银行卡里 2 万多元被转走。',
    tip: '购物平台客服不会通过私人电话联系你退款。有问题请在 APP 里联系官方客服。'
  }
]

// 补全卡片展示所需字段（tagType），兼容后端返回结构
const decorateCases = (list) => (list || []).map(c => ({
  ...c,
  tagType: c.tagType || tagTypeMap[c.tag] || 'warning'
}))

// 诈骗案例列表（从后端加载，后端不可用时回退到兜底数据）
const fraudCases = ref([])

const loadCases = async () => {
  loading.value = true
  try {
    const res = await getFraudCases()
    let list = []
    if (res.code === 200 && Array.isArray(res.data)) {
      list = res.data
    } else if (res.code === 200 && Array.isArray(res.data?.list)) {
      list = res.data.list
    }
    fraudCases.value = list.length ? decorateCases(list) : decorateCases(fallbackCases)
  } catch (e) {
    fraudCases.value = decorateCases(fallbackCases)
  } finally {
    loading.value = false
  }
}

const testQuestions = [
  {
    text: '你收到一条短信："您好，我是XX公安局，您涉嫌一起洗钱案，请立即拨打010-xxxxxxx配合调查。"你会？',
    options: [
      { label: '立即拨打电话配合调查', value: 'a' },
      { label: '挂断短信，通过114查询官方电话核实', value: 'b' },
      { label: '回复短信询问详情', value: 'c' },
      { label: '直接把短信删除，不理会', value: 'd' }
    ]
  },
  {
    text: '有人向你推荐一款"神奇保健品"，声称可以治愈高血压、糖尿病，且现在购买"买三送一"，你会？',
    options: [
      { label: '觉得划算，先买一些试试', value: 'a' },
      { label: '咨询医生意见后再决定', value: 'b' },
      { label: '向监管部门举报这个虚假宣传', value: 'c' },
      { label: '分享给其他老年朋友一起购买', value: 'd' }
    ]
  },
  {
    text: '"尊敬的用户，您在我店的订单出现异常，请点击链接退款"。你会？',
    options: [
      { label: '点击链接查看详情', value: 'a' },
      { label: '登录电商平台官网查看订单状态', value: 'b' },
      { label: '拨打短信中的"客服电话"', value: 'c' },
      { label: '不管它，等它自动过期', value: 'd' }
    ]
  }
]

const testAnswers = reactive([])
const testScore = ref(0)
const resultText = ref('')
const scoreColor = ref('#67C23A')

const correctAnswers = ['b', 'b', 'b']

const submitTest = async () => {
  if (testAnswers.length < 3) {
    ElMessage.warning('请完成所有题目')
    return
  }

  let score = 0
  testAnswers.forEach((answer, index) => {
    if (answer === correctAnswers[index]) {
      score++
    }
  })

  testScore.value = Math.round((score / 3) * 100)

  if (score === 3) {
    resultText.value = '🎉 满分！您的防骗能力很强！继续保持警惕！'
    scoreColor.value = '#67C23A'
  } else if (score >= 2) {
    resultText.value = '👍 不错的成绩！您有一定的防骗意识，但还可以做得更好！'
    scoreColor.value = '#E6A23C'
  } else {
    resultText.value = '⚠️ 需要提高警惕！建议您多学习防骗知识，谨防上当受骗！'
    scoreColor.value = '#F56C6C'
  }

  // 尝试把答案提交到后端（失败不影响前端展示）
  try {
    await submitFraudTest([...testAnswers])
  } catch (e) { /* 忽略 */ }

  resultDialogVisible.value = true
}

const reportForm = reactive({
  name: '',
  phone: '',
  type: '',
  platform: '',
  amount: '',
  detail: '',
  agree: false
})

const reportRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  type: [{ required: true, message: '请选择诈骗类型', trigger: 'change' }],
  platform: [{ required: true, message: '请输入涉事平台', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细描述', trigger: 'blur' }]
}

const submitReport = async () => {
  if (!reportFormRef.value) return

  await reportFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!reportForm.agree) {
        ElMessage.warning('请确认信息真实有效')
        return
      }

      submitting.value = true

      try {
        // 对齐后端 FraudReportDTO: name, phone, type, platform, amount, detail
        const dto = {
          name: reportForm.name,
          phone: reportForm.phone,
          type: reportForm.type,
          platform: reportForm.platform,
          amount: reportForm.amount,
          detail: reportForm.detail
        }
        const res = await submitFraudReport(dto)
        if (res.code === 200) {
          ElMessage.success(res.message || '举报信息已提交，我们会尽快处理！')
          reportFormRef.value.resetFields()
        } else {
          ElMessage.error(res.message || '举报失败，请稍后再试')
        }
      } catch (e) {
        ElMessage.error('提交举报失败，请检查网络')
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  loadCases()
})

watch(() => route.query.tab, (val) => {
  if (val) activeTab.value = val
})
</script>

<style scoped>
.anti-fraud-view {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.fraud-case-card {
  margin-bottom: 20px;
}

.case-header {
  display: flex;
  align-items: center;
  gap: 15px;
}

.case-header h4 {
  margin: 0;
}

.case-content p {
  line-height: 1.8;
  margin-bottom: 10px;
}

.tip-text {
  color: #67c23a;
  font-weight: bold;
}

.test-question {
  margin-bottom: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.test-question h5 {
  margin-bottom: 15px;
  color: #333;
}

:deep(.el-radio) {
  display: block;
  margin: 10px 0;
}

.test-result {
  text-align: center;
  padding: 20px;
}

.result-text {
  margin-top: 20px;
  font-size: 16px;
  color: #333;
}
</style>
