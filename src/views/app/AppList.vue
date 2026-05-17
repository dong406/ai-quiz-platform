<template>
  <div class="app-list-container">
    <a-card>
      <template #title>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>应用列表</span>
          <a-button type="primary" @click="goToAddApp">
            <IconPlusComponent /> 创建应用
          </a-button>
        </div>
      </template>
      <a-table
        :columns="columns"
        :data="appList"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
      >
        <template #appType="{ record }">
          {{ record.appType === 0 ? '打分类' : '测评类' }}
        </template>
        <template #scoringStrategy="{ record }">
          {{ getScoringStrategyText(record.scoringStrategy) }}
        </template>
        <template #reviewStatus="{ record }">
          <a-tag :color="getReviewStatusColor(record.reviewStatus)">
            {{ getReviewStatusText(record.reviewStatus) }}
          </a-tag>
        </template>
        <template #action="{ record }">
          <a-button type="text" @click="goToQuestionAdd(record.id)">添加题目</a-button>
          <a-button type="text" @click="goToAnswer(record.id)">开始答题</a-button>
          <a-button type="text" status="danger" @click="handleDeleteApp(record)">删除应用</a-button>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Message, Modal } from '@arco-design/web-vue'
import { IconPlus } from '@arco-design/web-vue/es/icon'

const IconPlusComponent = IconPlus

import { listApp, deleteApp, type AppVO } from '@/api/app'

const router = useRouter()
const loading = ref(false)
const appList = ref<AppVO[]>([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

const columns = [
  { title: '应用名称', dataIndex: 'appName' },
  { title: '应用描述', dataIndex: 'appDesc' },
  { 
    title: '应用类型', 
    slotName: 'appType',
    dataIndex: 'appType'
  },
  { 
    title: '评分策略', 
    slotName: 'scoringStrategy',
    dataIndex: 'scoringStrategy'
  },
  { 
    title: '审核状态', 
    slotName: 'reviewStatus',
    dataIndex: 'reviewStatus'
  },
  { 
    title: '操作', 
    slotName: 'action',
    align: 'center'
  }
]

const getScoringStrategyText = (strategy: number) => {
  const map: Record<number, string> = {
    0: '自定义打分',
    1: '自定义测评',
    2: 'AI测评'
  }
  return map[strategy] || '未知'
}

const getReviewStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return map[status] || '未知'
}

const getReviewStatusColor = (status: number) => {
  const map: Record<number, string> = {
    0: 'orange',
    1: 'green',
    2: 'red'
  }
  return map[status] || 'gray'
}

const loadAppList = async () => {
  try {
    loading.value = true
    const res = await listApp({
      current: pagination.value.current,
      pageSize: pagination.value.pageSize,
      // 不强制过滤审核状态，创建后即可看到
    })
    if (res.code === 0) {
      appList.value = res.data.records
      pagination.value.total = res.data.total
    }
  } catch (error: any) {
    Message.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  loadAppList()
}

const goToAddApp = () => {
  router.push('/app/add')
}

const goToQuestionAdd = (appId: number) => {
  router.push(`/question/add/${appId}`)
}

const goToAnswer = (appId: number) => {
  router.push(`/answer/${appId}`)
}

const handleDeleteApp = (record: AppVO) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除应用「${record.appName}」吗？该操作不可恢复。`,
    okText: '删除',
    cancelText: '取消',
    okButtonProps: { status: 'danger' },
    async onOk() {
      try {
        loading.value = true
        const res = await deleteApp(record.id)
        if (res.code === 0 && res.data) {
          Message.success('删除成功')
          // 如果当前页只剩一条且不是第一页，自动跳到上一页
          if (appList.value.length === 1 && pagination.value.current > 1) {
            pagination.value.current -= 1
          }
          await loadAppList()
        } else {
          Message.error(res.message || '删除失败')
        }
      } catch (error: any) {
        Message.error(error.message || '删除失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  loadAppList()
})
</script>

<style scoped>
.app-list-container {
  padding: 20px;
}
</style>
