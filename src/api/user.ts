import request, { type ApiResponse } from '@/utils/request'

export interface UserRegisterRequest {
  userAccount: string
  userPassword: string
  checkPassword: string
}

export interface UserLoginRequest {
  userAccount: string
  userPassword: string
}

export interface UserVO {
  id: number
  userAccount: string
  userName: string
  userAvatar: string
  userRole: string
  createTime: string
}

export interface UserLoginResponse {
  user: UserVO
  token: string
}

export const userRegister = (data: UserRegisterRequest): Promise<ApiResponse<number>> => {
  return request.post('/user/register', data) as Promise<ApiResponse<number>>
}

export const userLogin = (data: UserLoginRequest): Promise<ApiResponse<UserLoginResponse>> => {
  return request.post('/user/login', data) as Promise<ApiResponse<UserLoginResponse>>
}

export const getLoginUser = (): Promise<ApiResponse<UserVO>> => {
  return request.get('/user/get/login') as Promise<ApiResponse<UserVO>>
}

export const userLogout = (): Promise<ApiResponse<boolean>> => {
  return request.post('/user/logout') as Promise<ApiResponse<boolean>>
}
