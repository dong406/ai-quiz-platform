import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserVO } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserVO | null>(null)

  const setUserInfo = (user: UserVO | null) => {
    userInfo.value = user
    if (user) {
      localStorage.setItem('userInfo', JSON.stringify(user))
    } else {
      localStorage.removeItem('userInfo')
    }
  }

  const initUserInfo = () => {
    const stored = localStorage.getItem('userInfo')
    if (stored) {
      try {
        const user = JSON.parse(stored)
        userInfo.value = user
      } catch (e) {
        console.error('解析用户信息失败', e)
      }
    }
  }

  return {
    userInfo,
    setUserInfo,
    initUserInfo
  }
})
