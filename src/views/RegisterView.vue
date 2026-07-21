<template>
  <div class="container-fluid vh-100">
    <div class="row h-100">
      <!-- 左栏 -->
      <div class="col-md-6 d-flex flex-column justify-content-center ps-5">
        <h1 class="display-3 fw-bold mb-4">Create Account<br/>to Access</h1>
        <p class="text-muted fs-5">
          Already have an account?<br/>
          you can <router-link to="/login" class="text-primary text-decoration-none">Sign in here</router-link>.
        </p>
      </div>

      <!-- 右栏：注册表单 -->
      <div class="col-md-6 d-flex justify-content-center align-items-center">
        <div class="w-75" style="max-width: 400px;">
          <form @submit.prevent="handleRegister">
            <div class="mb-3">
              <input type="text" class="form-control form-control-lg" v-model="username" placeholder="Your username" required>
            </div>
            <div class="mb-3">
              <input type="email" class="form-control form-control-lg" v-model="email" placeholder="Your email address" required>
            </div>
            <div class="mb-3">
              <input type="password" class="form-control form-control-lg" v-model="password" placeholder="Password" required>
            </div>
            <div class="mb-3">
              <input type="password" class="form-control form-control-lg" v-model="confirmPassword" placeholder="Confirm password" required>
            </div>
            <button type="submit" class="btn btn-primary btn-lg w-100">Create Account</button>
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
const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')

// const handleRegister = () => {
//   if (password.value !== confirmPassword.value) {
//     alert('Passwords do not match!')
//     return
//   }
//   localStorage.setItem('username', username.value)
//   router.push('/login')
// }

const handleRegister = async () => {
  if (password.value !== confirmPassword.value) {
    alert('Passwords do not match!')
    return
  }

  try {
    const data = await api('/api/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: username.value,
        email: email.value,
        password: password.value
      })
    })

    alert('Registration successful! Please login.')
    router.push('/login')
    } catch (error) {
    alert(error.message || 'Registration failed')
  }
}
</script>

<style scoped>
/* .login-container {
  min-height: 100vh;
  display: flex;
  background: linear-gradient(135deg, #f5f7ff 0%, #fff1f9 100%);
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