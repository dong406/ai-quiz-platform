<template>
  <div class="register-container">
    <a-card title="用户注册" class="register-card">
      <a-form :model="registerForm" :rules="registerRules" ref="registerFormRef" @submit="handleRegister">
        <a-form-item name="userAccount" label="账号">
          <a-input v-model="registerForm.userAccount" placeholder="请输入账号（至少4位）" />
        </a-form-item>
        <a-form-item name="userPassword" label="密码">
          <a-input-password v-model="registerForm.userPassword" placeholder="请输入密码（至少8位）" />
        </a-form-item>
        <a-form-item name="checkPassword" label="确认密码">
          <a-input-password v-model="registerForm.checkPassword" placeholder="请再次输入密码" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading">注册</a-button>
          <a-button type="default" @click="goToLogin" style="margin-left: 10px;">返回登录</a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { userRegister } from '@/api/user'

const router = useRouter()
const loading = ref(false)
const registerFormRef = ref()

// 注册表单
const registerForm = reactive({
  userAccount: '',
  userPassword: '',
  checkPassword: ''
})

// 表单校验规则
const registerRules = reactive({
  userAccount: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 4, message: '账号长度不少于4位', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码长度不少于8位', trigger: 'blur' }
  ],
  checkPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (value: string, callback: (error?: string) => void) => {
        if (value !== registerForm.userPassword) {
          callback('两次输入的密码不一致')
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 注册处理
const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
    loading.value = true
    const res = await userRegister(registerForm)
    if (res.code === 0) {
      Message.success('注册成功，请登录')
      router.push('/user/login')
    } else {
      Message.error(res.message || '注册失败')
    }
  } catch (error: any) {
    console.error('注册失败', error)
    Message.error(error.message || '注册失败，请重试')
  } finally {
    loading.value = false
  }
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/user/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 400px;
}
</style>
