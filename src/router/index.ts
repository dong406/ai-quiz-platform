import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/app/list'
  },
  {
    path: '/user/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue')
  },
  {
    path: '/user/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue')
  },
  {
    path: '/app/list',
    name: 'AppList',
    component: () => import('@/views/app/AppList.vue')
  },
  {
    path: '/app/add',
    name: 'AppAdd',
    component: () => import('@/views/app/AppAdd.vue')
  },
  {
    path: '/question/add/:appId',
    name: 'QuestionAdd',
    component: () => import('@/views/question/QuestionAdd.vue')
  },
  {
    path: '/answer/:appId',
    name: 'Answer',
    component: () => import('@/views/answer/Answer.vue')
  },
  {
    path: '/result/:recordId',
    name: 'Result',
    component: () => import('@/views/result/Result.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：页面加载完成后统一做一次登录校验
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 登录 / 注册页为公开页面
  const publicPages = ['/user/login', '/user/register']
  const isPublicPage = publicPages.some(path => to.path.startsWith(path))

  // 公开页面直接放行
  if (isPublicPage) {
    next()
    return
  }

  // 初始化用户信息（从 localStorage 恢复）
  userStore.initUserInfo()

  // 只要 userStore 里没有有效的用户信息，就认为未登录
  const hasUser = !!userStore.userInfo && !!(userStore.userInfo as any).id

  if (!hasUser) {
    // 未登录：立即跳转登录页，并记录原目标地址，登录成功后可以跳回
    next({
      path: '/user/login',
      query: { redirect: to.fullPath }
    })
  } else {
    // 已登录：正常进入目标页面
    next()
  }
})

export default router
