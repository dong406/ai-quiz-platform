<template>
  <div class="result-container">
    <a-card>
      <template #title>
        <span>答题结果</span>
      </template>
      
      <div v-if="resultData" class="result-content">
        <!-- 打分类结果：只有在总分不为 null / undefined 时才走这个分支 -->
        <div v-if="resultData.totalScore != null" class="score-result">
          <h2>您的得分：{{ resultData.totalScore }} 分</h2>
          <p v-if="resultData.resultDesc" style="margin-top: 20px; white-space: pre-line; text-align: left;">
            {{ resultData.resultDesc }}
          </p>
        </div>

        <!-- 测评类结果（包括 AI 测评） -->
        <div v-else class="test-result">
          <h2>{{ resultData.resultName }}</h2>
          <p style="margin-top: 20px; line-height: 1.8;">{{ resultData.resultDesc }}</p>
        </div>
        
        <div style="margin-top: 40px; text-align: center;">
          <a-button type="primary" @click="goToAppList">返回应用列表</a-button>
          <a-button @click="showShareModal = true" style="margin-left: 10px;">分享结果</a-button>
        </div>
      </div>
    </a-card>

    <!-- 分享弹窗 -->
    <a-modal v-model:visible="showShareModal" title="分享结果" @ok="handleShare" @cancel="showShareModal = false">
      <div style="text-align: center;">
        <div ref="qrcodeRef" style="display: inline-block; padding: 20px; background: #fff;"></div>
        <p style="margin-top: 20px;">扫描二维码查看结果</p>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import QRCode from 'qrcode'
import { getUserAnswerRecordById } from '@/api/scoring'

const route = useRoute()
const router = useRouter()
const recordId = ref<number>(Number(route.params.recordId))
const resultData = ref<{ totalScore?: number; resultName?: string; resultDesc?: string } | null>(null)
const showShareModal = ref(false)
const qrcodeRef = ref<HTMLElement>()

// 加载结果数据
const loadResult = async () => {
  try {
    const res = await getUserAnswerRecordById(recordId.value)
    if (res.code === 0) {
      resultData.value = {
        totalScore: res.data.totalScore,
        resultName: res.data.resultName,
        resultDesc: res.data.resultDesc
      }
    }
  } catch (error: any) {
    Message.error(error.message || '加载结果失败')
  }
}

// 生成二维码
const generateQRCode = async () => {
  if (!qrcodeRef.value) return
  try {
    const shareUrl = `${window.location.origin}/result/${recordId.value}`
    await QRCode.toCanvas(qrcodeRef.value, shareUrl, {
      width: 200
    })
  } catch (error) {
    console.error('生成二维码失败', error)
  }
}

// 分享
const handleShare = () => {
  generateQRCode()
}

// 返回应用列表
const goToAppList = () => {
  router.push('/app/list')
}

onMounted(() => {
  loadResult()
})
</script>

<style scoped>
.result-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.result-content {
  padding: 20px 0;
  text-align: center;
}

.score-result h2 {
  font-size: 36px;
  color: #1890ff;
  margin-bottom: 20px;
}

.test-result h2 {
  font-size: 28px;
  color: #1890ff;
  margin-bottom: 20px;
}
</style>
