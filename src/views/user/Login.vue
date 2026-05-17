<template>
  <div class="login-container">
    <a-card title="用户登录" class="login-card">
      <a-form :model="loginForm" :rules="loginRules" ref="loginFormRef" @submit="handleLogin">
        <a-form-item name="userAccount" label="账号">
          <a-input v-model="loginForm.userAccount" placeholder="请输入账号" />
        </a-form-item>
        <a-form-item name="userPassword" label="密码">
          <a-input-password v-model="loginForm.userPassword" placeholder="请输入密码" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading">登录</a-button>
          <a-button type="default" @click="goToRegister" style="margin-left: 10px;">注册</a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { userLogin } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const loginFormRef = ref()

// 登录表单
const loginForm = reactive({
  userAccount: '',
  userPassword: ''
})

// 表单校验规则
const loginRules = reactive({
  userAccount: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 4, message: '账号长度不少于4位', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码长度不少于8位', trigger: 'blur' }
  ]
})

// 登录处理
const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true
    const res = await userLogin(loginForm)
    if (res.code === 0) {
      Message.success('登录成功')
      // 存储用户信息
      userStore.setUserInfo(res.data.user)
      localStorage.setItem('userInfo', JSON.stringify({ ...res.data.user, token: res.data.token }))
      // 跳转到之前访问的页面或默认页面
      const redirect = router.currentRoute.value.query.redirect as string
      router.push(redirect || '/app/list')
    } else {
      Message.error(res.message || '登录失败')
    }
  } catch (error: any) {
    console.error('登录失败', error)
    Message.error(error.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/user/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
}
</style>
