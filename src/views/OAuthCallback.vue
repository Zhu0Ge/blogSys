<template>
  <div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="text-center">
      <div class="spinner-border text-primary mb-3" role="status"></div>
      <p>GitHub login successful, redirecting...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

onMounted(() => {
  const token = route.query.token
  if (token) {
    localStorage.setItem('token', token)
    localStorage.setItem('username', 'GitHub User')
    localStorage.setItem('userId', '')
    router.push('/')
  } else {
    alert('Login failed')
    router.push('/login')
  }
})
</script>