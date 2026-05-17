import request, { type ApiResponse } from '@/utils/request'

export interface QuestionOptionDTO {
  key: string
  value: string
}

export interface QuestionContentDTO {
  title: string
  options: QuestionOptionDTO[]
  score?: number
  result?: string
}

export interface QuestionAddRequest {
  appId: number
  questionContent: QuestionContentDTO[]
}

export interface AiGenerateQuestionRequest {
  appId: number
  questionNumber: number
  optionNumber: number
  appName?: string
  appDesc?: string
}

// 带重试的请求函数
const requestWithRetry = async <T = any>(
  requestFn: () => Promise<T>,
  retries: number = 2,
  retryDelay: number = 1000
): Promise<T> => {
  let lastError: any
  
  for (let i = 0; i <= retries; i++) {
    try {
      return await requestFn()
    } catch (error: any) {
      lastError = error
      
      // 只对超时错误和网络错误进行重试
      const shouldRetry = 
        (error.code === 'ECONNABORTED' || 
         error.message?.includes('timeout') || 
         (!error.response && error.request)) &&
        i < retries
      
      if (shouldRetry) {
        console.warn(`请求失败，${retryDelay}ms后进行第 ${i + 1} 次重试...`, error.message)
        await new Promise(resolve => setTimeout(resolve, retryDelay))
        continue
      }
      
      throw error
    }
  }
  
  throw lastError
}

export const addQuestion = (data: QuestionAddRequest): Promise<ApiResponse<number>> => {
  console.log('addQuestion API调用，数据:', JSON.stringify(data, null, 2))
  return requestWithRetry(
    () => {
      console.log('执行 addQuestion 请求...')
      return request.post('/question/add', data) as Promise<ApiResponse<number>>
    },
    2, // 重试2次
    1000 // 延迟1秒
  )
}

export const listQuestion = (appId: number): Promise<ApiResponse<QuestionContentDTO[]>> => {
  return request.get('/question/list', { params: { appId } }) as Promise<ApiResponse<QuestionContentDTO[]>>
}

export const aiGenerateQuestion = (data: AiGenerateQuestionRequest): Promise<ApiResponse<QuestionContentDTO[]>> => {
  // AI生成接口使用更长的超时和更多重试次数
  return requestWithRetry(
    () => request.post('/question/ai_generate', data) as Promise<ApiResponse<QuestionContentDTO[]>>,
    3, // AI接口重试3次
    2000 // 延迟2秒
  )
}
