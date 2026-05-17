import request, { type ApiResponse } from '@/utils/request'

export interface AppAddRequest {
  appName: string
  appDesc?: string
  appCover?: string
  appType: number
  scoringStrategy: number
}

export interface AppReviewRequest {
  id: number
  reviewStatus: number
  reviewMessage?: string
}

export interface AppVO {
  id: number
  appName: string
  appDesc: string
  appCover: string
  appType: number
  scoringStrategy: number
  reviewStatus: number
  reviewMessage: string
  userId: number
  createTime: string
  updateTime: string
}

export interface AppPage {
  records: AppVO[]
  total: number
  current: number
  size: number
}

export const addApp = (data: AppAddRequest): Promise<ApiResponse<number>> => {
  return request.post('/app/add', data) as Promise<ApiResponse<number>>
}

export const reviewApp = (data: AppReviewRequest): Promise<ApiResponse<boolean>> => {
  return request.post('/app/review', data) as Promise<ApiResponse<boolean>>
}

export const listApp = (params: {
  appName?: string
  appType?: number
  reviewStatus?: number
  current?: number
  pageSize?: number
}): Promise<ApiResponse<AppPage>> => {
  return request.get('/app/list', { params }) as Promise<ApiResponse<AppPage>>
}

export const getAppById = (id: number): Promise<ApiResponse<AppVO>> => {
  return request.get('/app/get', { params: { id } }) as Promise<ApiResponse<AppVO>>
}

export const deleteApp = (id: number): Promise<ApiResponse<boolean>> => {
  // 使用查询参数传递id，后端使用@ReqeustParam接收
  return request.post('/app/delete', null, {
    params: { id }
  }) as Promise<ApiResponse<boolean>>
}
