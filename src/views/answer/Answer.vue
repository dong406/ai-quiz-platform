<template>
  <div class="answer-container">
    <a-card>
      <template #title>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>{{ appInfo?.appName || '答题' }}</span>
          <a-progress :percent="progress" :show-text="true" style="width: 200px;" />
        </div>
      </template>
      
      <div v-if="currentQuestion" class="question-content">
        <h3>{{ currentQuestion.title }}</h3>
        <a-radio-group v-model="selectedChoices[currentIndex]" @change="handleChoiceChange">
          <a-radio
            v-for="option in currentQuestion.options"
            :key="option.key"
            :value="option.key"
            style="display: block; margin-bottom: 10px;"
          >
            {{ option.key }}. {{ option.value }}
          </a-radio>
        </a-radio-group>
        
        <div style="margin-top: 30px; display: flex; justify-content: space-between;">
          <a-button @click="prevQuestion" :disabled="currentIndex === 0">上一题</a-button>
          <a-button v-if="currentIndex < questionList.length - 1" type="primary" @click="nextQuestion">下一题</a-button>
          <a-button v-else type="primary" @click="submitAnswer" :loading="submitting">提交答案</a-button>
        </div>
      </div>
    </a-card>

    <!-- AI 总结弹窗 -->
    <a-modal
      v-model:visible="showResultModal"
      title="答题总结"
      @ok="handleResultOk"
      @cancel="handleResultCancel"
      ok-text="查看详细结果"
      cancel-text="关闭"
    >
      <div v-if="resultSummary">
        <!-- 打分类显示总分：只有在总分不为 null / undefined 时才走这个分支 -->
        <div v-if="resultSummary.totalScore != null">
          <h3>您的得分：{{ resultSummary.totalScore }} 分</h3>
          <p v-if="resultSummary.resultDesc" style="margin-top: 12px; white-space: pre-line; text-align: left;">
            {{ resultSummary.resultDesc }}
          </p>
        </div>
        <!-- 测评类显示 AI 总结（包括 AI 测评） -->
        <div v-else>
          <h3>{{ resultSummary.resultName }}</h3>
          <p style="margin-top: 16px; line-height: 1.8;">{{ resultSummary.resultDesc }}</p>
        </div>
      </div>
      <div v-else>
        暂无总结结果，请稍后重试。
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { listQuestion, type QuestionContentDTO } from '@/api/question'
import { getAppById, type AppVO } from '@/api/app'
import { doScoring, type UserAnswer } from '@/api/scoring'

const route = useRoute()
const router = useRouter()
const appId = ref<number>(Number(route.params.appId))
const appInfo = ref<AppVO | null>(null)
const questionList = ref<QuestionContentDTO[]>([])
const currentIndex = ref(0)
const selectedChoices = ref<string[]>([])
const submitting = ref(false)
const showResultModal = ref(false)
const resultSummary = ref<UserAnswer | null>(null)
const lastRecordId = ref<number | null>(null)

const currentQuestion = computed(() => {
  return questionList.value[currentIndex.value]
})

// 已回答题目数量
const answeredCount = computed(() => {
  return selectedChoices.value.filter(choice => !!choice && choice.trim() !== '').length
})

// 进度条：已回答题数 / 总题数
const progress = computed(() => {
  if (questionList.value.length === 0) return 0
  return Math.round((answeredCount.value / questionList.value.length) * 100)
})

// 加载应用信息
const loadAppInfo = async () => {
  try {
    const res = await getAppById(appId.value)
    if (res.code === 0) {
      appInfo.value = res.data
    }
  } catch (error: any) {
    Message.error(error.message || '加载应用信息失败')
  }
}

// 加载题目列表
const loadQuestionList = async () => {
  try {
    const res = await listQuestion(appId.value)
    if (res.code === 0) {
      questionList.value = res.data
      selectedChoices.value = new Array(res.data.length).fill('')
    }
  } catch (error: any) {
    Message.error(error.message || '加载题目失败')
  }
}

// 选择变化
const handleChoiceChange = () => {
  // 可以在这里添加一些逻辑
}

// 上一题
const prevQuestion = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

// 下一题
const nextQuestion = () => {
  if (currentIndex.value < questionList.value.length - 1) {
    currentIndex.value++
  }
}

// 提交答案
const submitAnswer = async () => {
  // 检查是否所有题目都已作答
  const unanswered = selectedChoices.value.some(choice => !choice || choice.trim() === '')
  if (unanswered) {
    Message.warning('请完成所有题目后再提交')
    return
  }

  // 检查应用是否配置了评分策略
  if (!appInfo.value) {
    Message.error('未获取到应用信息，请刷新页面后重试')
    return
  }
  if (
    appInfo.value.scoringStrategy === null ||
    appInfo.value.scoringStrategy === undefined
  ) {
    Message.error('当前应用未配置评分策略，请在应用管理中先配置评分策略')
    return
  }
  
  try {
    submitting.value = true
    const res = await doScoring({
      appId: appId.value,
      choices: selectedChoices.value
    })
    if (res.code === 0) {
      Message.success('提交成功')
      // 记录 AI 总结结果并弹出
      resultSummary.value = res.data.userAnswer
      lastRecordId.value = res.data.recordId
      showResultModal.value = true
    } else {
      Message.error(res.message || '提交失败')
    }
  } catch (error: any) {
    Message.error(error.message || '提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 结果弹窗：查看详细结果（跳转到结果页）
const handleResultOk = () => {
  if (lastRecordId.value != null) {
    router.push(`/result/${lastRecordId.value}`)
  } else {
    showResultModal.value = false
  }
}

// 结果弹窗：仅关闭
const handleResultCancel = () => {
  showResultModal.value = false
}

onMounted(() => {
  loadAppInfo()
  loadQuestionList()
})
</script>

<style scoped>
.answer-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.question-content {
  padding: 20px 0;
}

.question-content h3 {
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: 500;
}
</style>
