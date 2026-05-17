<template>
  <div class="app-add-container">
    <a-card title="创建应用">
      <a-form :model="appForm" :rules="appRules" ref="appFormRef" @submit="handleAddApp">
        <a-form-item name="appName" label="应用名称">
          <a-input v-model="appForm.appName" placeholder="请输入应用名称" />
        </a-form-item>
        <a-form-item name="appDesc" label="应用描述">
          <a-textarea v-model="appForm.appDesc" placeholder="请输入应用描述" :rows="4" />
        </a-form-item>
        <a-form-item name="appType" label="应用类型">
          <a-select v-model="appForm.appType" placeholder="请选择应用类型">
            <a-option :value="0">打分类</a-option>
            <a-option :value="1">测评类</a-option>
          </a-select>
        </a-form-item>
        <a-form-item name="scoringStrategy" label="评分策略">
          <a-select v-model="appForm.scoringStrategy" placeholder="请选择评分策略">
            <a-option :value="0">自定义打分</a-option>
            <a-option :value="1">自定义测评</a-option>
            <a-option :value="2">AI测评</a-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading">下一步（添加题目）</a-button>
          <a-button type="default" @click="goBack" style="margin-left: 10px;">返回</a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { addApp, type AppAddRequest } from '@/api/app'

const router = useRouter()
const loading = ref(false)
const appFormRef = ref()

// 应用表单
const appForm = reactive<AppAddRequest>({
  appName: '',
  appDesc: '',
  appCover: '',
  appType: 0,
  scoringStrategy: 0
})

// 表单校验规则
const appRules = reactive({
  appName: [{ required: true, message: '请输入应用名称', trigger: 'blur' }],
  appType: [{ required: true, message: '请选择应用类型', trigger: 'change' }],
  scoringStrategy: [{ required: true, message: '请选择评分策略', trigger: 'change' }]
})

// 创建应用
const handleAddApp = async () => {
  try {
    await appFormRef.value.validate()
    loading.value = true
    const res = await addApp(appForm)
    if (res.code === 0) {
      Message.success('创建成功')
      router.push(`/question/add/${res.data}`)
    } else {
      Message.error(res.message || '创建失败')
    }
  } catch (error: any) {
    console.error('创建失败', error)
    Message.error(error.message || '创建失败，请重试')
  } finally {
    loading.value = false
  }
}

// 返回
const goBack = () => {
  router.push('/app/list')
}
</script>

<style scoped>
.app-add-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
</style>
