import request, { type ApiResponse } from '@/utils/request'

export interface ScoringRequest {
  appId: number
  choices: string[]
}

export interface UserAnswer {
  totalScore?: number
  resultName?: string
  resultDesc?: string
}

export interface ScoringResponse {
  userAnswer: UserAnswer
  recordId: number
}

export interface UserAnswerRecordAddRequest {
  appId: number
  choices: string[]
  totalScore?: number
  resultName?: string
  resultDesc?: string
}

export interface UserAnswerRecordVO {
  id: number
  userId: number
  appId: number
  appName: string
  choices: string
  totalScore?: number
  resultName?: string
  resultDesc?: string
  createTime: string
}

export interface UserAnswerRecordPage {
  records: UserAnswerRecordVO[]
  total: number
  current: number
  size: number
}

export const doScoring = (data: ScoringRequest): Promise<ApiResponse<ScoringResponse>> => {
  return request.post('/scoring/do', data) as Promise<ApiResponse<ScoringResponse>>
}

export const addUserAnswerRecord = (data: UserAnswerRecordAddRequest): Promise<ApiResponse<number>> => {
  return request.post('/userAnswerRecord/add', data) as Promise<ApiResponse<number>>
}

export const listUserAnswerRecord = (params: {
  userId?: number
  appId?: number
  current?: number
  pageSize?: number
}): Promise<ApiResponse<UserAnswerRecordPage>> => {
  return request.get('/userAnswerRecord/list', { params }) as Promise<ApiResponse<UserAnswerRecordPage>>
}

export const getUserAnswerRecordById = (id: number): Promise<ApiResponse<UserAnswerRecordVO>> => {
  return request.get('/userAnswerRecord/get', { params: { id } }) as Promise<ApiResponse<UserAnswerRecordVO>>
}
