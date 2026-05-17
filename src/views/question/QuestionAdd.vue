<template>
  <div class="question-add-container">
    <a-card>
      <template #title>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>添加题目</span>
          <div>
            <a-button type="dashed" @click="showAiGenerateModal = true" style="margin-right: 10px;">
              <IconPlusComponent /> AI生成题目
            </a-button>
            <a-button type="primary" @click="handleAddQuestion">保存题目</a-button>
          </div>
        </div>
      </template>
      
      <div v-for="(question, index) in questionList" :key="index" class="question-item">
        <a-card :title="`题目 ${index + 1}`" style="margin-bottom: 20px;">
          <a-form-item label="题目">
            <a-input v-model="question.title" placeholder="请输入题目" />
          </a-form-item>
          <a-form-item label="选项">
            <div v-for="(option, optIndex) in question.options" :key="optIndex" style="display: flex; margin-bottom: 10px;">
              <a-input
                v-model="option.key"
                placeholder="选项key（A/B/C/D）"
                style="width: 120px; margin-right: 10px;"
              />
              <a-input
                v-model="option.value"
                placeholder="选项值"
                style="flex: 1;"
              />
              <a-button type="text" status="danger" @click="removeOption(index, optIndex)">删除</a-button>
            </div>
            <a-button type="dashed" @click="addOption(index)">
              <IconPlusComponent /> 添加选项
            </a-button>
          </a-form-item>
          <a-form-item v-if="appType === 0" label="分值">
            <a-input-number v-model="question.score" :min="0" placeholder="请输入分值" />
          </a-form-item>
          <a-form-item v-if="appType === 1" label="结果">
            <a-input v-model="question.result" placeholder="请输入结果" />
          </a-form-item>
          <a-button type="text" status="danger" @click="removeQuestion(index)">删除题目</a-button>
        </a-card>
      </div>
      
      <a-button type="dashed" @click="addQuestionItem" style="width: 100%;">
        <IconPlusComponent /> 添加题目
      </a-button>
    </a-card>

    <!-- AI生成题目弹窗 -->
    <a-modal v-model:visible="showAiGenerateModal" title="AI生成题目" @ok="handleAiGenerate" @cancel="showAiGenerateModal = false">
      <a-form :model="aiForm" :rules="aiRules" ref="aiFormRef">
        <a-form-item name="questionNumber" label="题目数量">
          <a-input-number v-model="aiForm.questionNumber" :min="1" :max="50" placeholder="请输入题目数量" />
        </a-form-item>
        <a-form-item name="optionNumber" label="选项数量">
          <a-input-number v-model="aiForm.optionNumber" :min="2" :max="10" placeholder="请输入选项数量" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { IconPlus } from '@arco-design/web-vue/es/icon'

const IconPlusComponent = IconPlus

import { addQuestion as addQuestionApi, aiGenerateQuestion, listQuestion, type QuestionContentDTO } from '@/api/question'
import { getAppById, type AppVO } from '@/api/app'

const route = useRoute()
const router = useRouter()
const appId = ref<number>(Number(route.params.appId))
const appType = ref<number>(0)
const appInfo = ref<AppVO | null>(null)
const questionList = ref<QuestionContentDTO[]>([])
const showAiGenerateModal = ref(false)
const aiFormRef = ref()

// 使用 reactive 确保响应式，避免 toRefs 警告
const aiForm = reactive({
  questionNumber: 5,
  optionNumber: 4
})

// 表单验证规则（使用普通对象即可，Arco Design 会自动处理）
const aiRules = {
  questionNumber: [{ required: true, message: '请输入题目数量', trigger: 'blur' }],
  optionNumber: [{ required: true, message: '请输入选项数量', trigger: 'blur' }]
}

// 加载应用信息
const loadAppInfo = async () => {
  try {
    const res = await getAppById(appId.value)
    if (res && res.code === 0) {
      appInfo.value = res.data
      appType.value = res.data.appType
    }
  } catch (error: any) {
    Message.error(error.message || '加载应用信息失败')
  }
}

// 加载题目列表
const loadQuestionList = async () => {
  try {
    const res = await listQuestion(appId.value)
    if (res && res.code === 0) {
      console.log('加载题目成功，返回数据:', res.data)
      questionList.value = res.data || []
      // 如果后端返回空数组，给出提示，便于排查
      if (!res.data || res.data.length === 0) {
        Message.warning('当前暂无题目，您可以手动添加或使用 AI 生成')
      }
    }
  } catch (error: any) {
    Message.error(error.message || '加载题目失败')
  }
}

// 添加题目
const addQuestionItem = () => {
  questionList.value.push({
    title: '',
    options: [{ key: 'A', value: '' }],
    score: 0,
    result: ''
  })
}

// 删除题目
const removeQuestion = (index: number) => {
  questionList.value.splice(index, 1)
}

// 添加选项
const addOption = (questionIndex: number) => {
  const question = questionList.value[questionIndex]
  const keys = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']
  const nextKey = keys[question.options.length] || String.fromCharCode(65 + question.options.length)
  question.options.push({ key: nextKey, value: '' })
}

// 删除选项
const removeOption = (questionIndex: number, optionIndex: number) => {
  questionList.value[questionIndex].options.splice(optionIndex, 1)
}

// 预处理 AI 返回的内容，尽量还原为 JSON 数组字符串
const normalizeAiData = (raw: any): QuestionContentDTO[] | null => {
  // 已经是数组则直接返回
  if (Array.isArray(raw)) {
    return raw
  }

  // 期望是字符串，做清洗再解析
  if (typeof raw === 'string') {
    let text = raw.trim()

    // 去掉 markdown 代码块标记
    if (text.startsWith('```')) {
      text = text.replace(/^```[a-zA-Z]*\s*/i, '').replace(/```$/, '').trim()
    }

    // 去掉开头可能的提示文本，保留第一个 '[' 之后的内容
    const firstBracket = text.indexOf('[')
    if (firstBracket >= 0) {
      text = text.slice(firstBracket)
    }
    // 补全尾部 ']'（如果缺失）
    if (!text.trim().endsWith(']')) {
      text = text + ']'
    }

    try {
      const parsed = JSON.parse(text)
      if (Array.isArray(parsed)) {
        return parsed
      }
      console.error('AI解析后不是数组', parsed)
      return null
    } catch (e) {
      console.error('AI返回数据解析失败，原始内容:', raw, '清洗后内容:', text, e)
      return null
    }
  }

  console.error('AI返回数据类型异常', raw)
  return null
}

// AI生成题目
const handleAiGenerate = async () => {
  try {
    await aiFormRef.value.validate()
    Message.info('正在生成题目，请稍候...')
    
    const res = await aiGenerateQuestion({
      appId: appId.value,
      questionNumber: aiForm.questionNumber,
      optionNumber: aiForm.optionNumber,
      appName: appInfo.value?.appName,
      appDesc: appInfo.value?.appDesc
    })
    
    console.log('AI生成题目响应:', res)

    if (res && res.code === 0) {
      const parsed = normalizeAiData(res.data)
      if (parsed && parsed.length > 0) {
        questionList.value.push(...parsed)
        console.log('更新后的题目列表:', questionList.value)
        Message.success('AI生成题目成功')
        showAiGenerateModal.value = false
      } else {
        Message.error('AI返回数据格式不正确或为空，请重试')
      }
    } else {
      Message.error(res?.message || '生成失败')
    }
  } catch (error: any) {
    console.error('AI生成题目失败:', error)
    
    // 根据错误类型显示不同的提示
    if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      Message.error('AI生成超时，网络波动，请稍后重试')
    } else if (!error.response && error.request) {
      Message.error('无法连接到服务器，请检查后端服务是否启动')
    } else if (error.response?.data?.message) {
      // 显示后端返回的具体错误信息
      Message.error(error.response.data.message)
    } else if (error.message) {
      Message.error(error.message)
    } else {
      Message.error('生成失败，请重试')
    }
  }
}

// 保存题目
const handleAddQuestion = async () => {
  try {
    if (questionList.value.length === 0) {
      Message.warning('请至少添加一道题目')
      return
    }
    // 校验题目
    for (let i = 0; i < questionList.value.length; i++) {
      const question = questionList.value[i]
      if (!question.title || question.title.trim() === '') {
        Message.warning(`请填写第 ${i + 1} 题的题目`)
        return
      }
      if (!question.options || question.options.length < 2) {
        Message.warning(`第 ${i + 1} 题至少需要2个选项`)
        return
      }
      for (let j = 0; j < question.options.length; j++) {
        const option = question.options[j]
        if (!option.key || !option.value || option.value.trim() === '') {
          Message.warning(`第 ${i + 1} 题的第 ${j + 1} 个选项不完整`)
          return
        }
      }
    }
    
    // 准备请求数据
    const requestData = {
      appId: appId.value,
      questionContent: questionList.value.map(q => {
        const content: any = {
          title: q.title,
          options: q.options
        }
        // 根据应用类型设置score或result
        if (appType.value === 0) {
          // 打分类：必须有score
          content.score = q.score ?? 0
        } else {
          // 测评类：必须有result
          content.result = q.result || ''
        }
        return content
      })
    }
    
    // 添加详细的请求日志
    console.log('=== 准备保存题目 ===')
    console.log('请求数据:', JSON.stringify(requestData, null, 2))
    console.log('题目数量:', requestData.questionContent.length)
    
    try {
      Message.loading('正在保存题目...', 0)
      console.log('开始调用 addQuestion API...')
      const res = await addQuestionApi(requestData)
      Message.clear()
      console.log('收到响应:', res)
      
      // 检查响应是否有效
      if (!res) {
        console.error('响应为空')
        Message.error('保存失败：未收到服务器响应')
        return
      }
      // 检查数据是否存在且格式正确
      if (res.data === undefined || res.data === null) {
        console.error('响应data为空或格式不正确', res)
        Message.error('保存失败：接口响应异常（data为空）')
        return
      }
      
      if (res.code === 0) {
        console.log('保存成功，题目ID:', res.data)
        Message.success('保存成功')
        router.push('/app/list')
      } else {
        console.error('保存失败，错误码:', res.code, '错误信息:', res.message)
        Message.error(res.message || '保存失败')
      }
    } catch (apiError: any) {
      Message.clear()
      // 处理API调用错误
      console.error('=== 保存题目API调用错误 ===')
      console.error('错误对象:', apiError)
      console.error('错误类型:', typeof apiError)
      console.error('错误属性:', Object.keys(apiError))
      
      // 如果错误对象有code和message（来自响应拦截器）
      if (apiError && typeof apiError === 'object' && 'code' in apiError) {
        console.error('业务错误:', apiError.code, apiError.message)
        Message.error(apiError.message || '保存失败')
        return
      }
      
      // 处理其他类型的错误
      if (apiError.response) {
        console.error('收到HTTP响应但状态码错误')
        console.error('状态码:', apiError.response.status)
        console.error('响应数据:', apiError.response.data)
        const errorData = apiError.response.data
        if (errorData && errorData.message) {
          Message.error(errorData.message)
        } else {
          Message.error(`保存失败：${apiError.response.status} ${apiError.response.statusText}`)
        }
      } else if (apiError.request) {
        console.error('请求已发出但未收到响应')
        console.error('请求对象:', apiError.request)
        console.error('错误代码:', apiError.code)
        console.error('错误消息:', apiError.message)
        // 检查是否是超时错误
        if (apiError.code === 'ECONNABORTED' || apiError.message?.includes('timeout')) {
          Message.error('保存超时，网络波动，请稍后重试')
        } else {
          Message.error('无法连接到服务器，请检查后端服务是否启动在 http://localhost:8081')
        }
      } else {
        console.error('请求配置错误')
        Message.error(apiError.message || '保存失败，请重试')
      }
    }
  } catch (error: any) {
    console.error('保存题目失败:', error)
    Message.error(error.message || '保存失败，请重试')
  }
}

onMounted(() => {
  loadAppInfo()
  loadQuestionList()
})
</script>

<style scoped>
.question-add-container {
  padding: 20px;
}

.question-item {
  margin-bottom: 20px;
}
</style>
