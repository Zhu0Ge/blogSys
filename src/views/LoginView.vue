<template>
  <div class="container-fluid vh-100">
    <div class="row h-100">
      <!-- 左栏：标题 -->
      <div class="col-md-6 d-flex flex-column justify-content-center ps-5">
        <h1 class="display-3 fw-bold mb-4">Sign In to<br/>Open the World</h1>
        <p class="text-muted fs-5">
          If you don't have an account,<br/>
          you can <router-link to="/register" class="text-primary text-decoration-none">Register here</router-link>.
        </p>
      </div>

      <!-- 右栏：登录表单 -->
      <div class="col-md-6 d-flex justify-content-center align-items-center">
        <div class="w-75" style="max-width: 400px;">
          <form @submit.prevent="handleLogin">
            <div class="mb-3">
              <input type="email" class="form-control form-control-lg" v-model="email" placeholder="Your email address" required>
            </div>
            <div class="mb-3">
              <input type="password" class="form-control form-control-lg" v-model="password" placeholder="Password" required>
            </div>
            <div class="text-end mb-3">
              <a href="#" class="text-decoration-none small text-muted">Recovery password</a>
            </div>
            <button type="submit" class="btn btn-primary btn-lg w-100">Sign In</button>

            <div class="text-center my-4 position-relative">
              <hr><span class="px-2 bg-white position-relative text-muted small" style="top: -12px;">or continue with</span>
            </div>

            <div class="d-flex justify-content-center gap-3">
              <button @click="handleGithubLogin" class="btn btn-outline-secondary d-flex align-items-center justify-content-center p-0 rounded-circle" 
                      style="width:48px;height:48px;" title="GitHub Login">
                <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                  <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0 0 24 12c0-6.63-5.37-12-12-12z"/>
                </svg>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../http.js'

const router = useRouter()
const email = ref('')
const password = ref('')

// const handleLogin = () => {
//   localStorage.setItem('username', email.value.split('@')[0])
//   router.push('/')
//   console.log('登录成功')
// }

const handleLogin = async () => {
  try {
    const data = await api('/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: email.value,
        password: password.value
      })
    })


    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.user?.username)
    localStorage.setItem('userId', data.user?.id)
    router.push('/')

  } catch (error) {
    alert(error.message || '无法连接到服务器')
  }
}

const handleGithubLogin = async () => {
  try {
    const data = await api('/api/oauth/github/authorize')
    window.location.href = data.authUrl
  } catch (error) {
    alert('GitHub login failed: ' + (error.message || 'unknown error'))
  }
}
</script>

<style scoped>
/* .login-container {
  min-height: 100vh;
  display: flex;
  background: linear-gradient(135deg, #292c3a 0%, #fff1f9 100%);
  padding: 20px;
}

.left-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding-left: 15%;
  padding-top: -80px; /* 向上移动内容 
}

.right-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding-right: 15%;
}

.login-form {
  width: 100%;
  max-width: 400px;
}

.title-area {
  height: 120px;
  display: flex;
  align-items: center;
}

.typing-title {
  font-size: 4.2rem;
  font-weight: bold;
  color: #000;
  position: relative;
  width: fit-content;
  white-space: nowrap;
  border-right: 3px solid;
  animation: typing 1.5s steps(9),
             cursor .4s step-end 3 forwards;
  overflow: hidden;
}

h1 {
  font-size: 3.5rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  color: #000;
  line-height: 1.2;
}

.register-text {
  color: #666;
  font-size: 1.1rem;
  line-height: 1.6;
}

.register-link {
  color: #4169E1;
  text-decoration: none;
}

.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 1.5rem;
}

input {
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #f8f9fa;
  font-size: 1rem;
}

.recovery-link {
  text-align: right;
  color: #666;
  text-decoration: none;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.sign-in-btn {
  width: 100%;
  background: #4169E1;
  color: white;
  padding: 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  font-size: 1rem;
  margin-top: 1rem;
}

.divider {
  text-align: center;
  position: relative;
  margin: 2rem 0;
}

.divider::before,
.divider::after {
  content: "";
  position: absolute;
  top: 50%;
  width: 45%;
  height: 1px;
  background: #ddd;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider span {
  background: #f5f7ff;
  padding: 0 10px;
  color: #666;
  font-size: 0.9rem;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.social-btn {
  width: 48px;
  height: 48px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
}

.social-btn svg {
  width: 100%;
  height: 100%;
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    padding: 20px;
  }

  .left-section,
  .right-section {
    padding: 20px;
    text-align: center;
  }

  h1 {
    font-size: 2.5rem;
  }
}

@keyframes typing {
  from { width: 0 }
  to { width: 100% }
}

@keyframes cursor {
  50% { border-color: transparent }
  100% { border-color: transparent }
} */
</style>