<template>
  <div class="container">
    <div class="header">
      <h1>Write Article</h1>
      <router-link to="/" class="back-link">← Back</router-link>
    </div>

    <form @submit.prevent="handleSubmit" class="form">
      <div class="form-group">
        <label>Title</label>
        <input type="text" v-model="title" placeholder="Article title" required>
      </div>

      <div class="form-group">
        <label>Content</label>
        <textarea v-model="content" placeholder="Write your article here..." rows="15" required></textarea>
      </div>

      <button type="submit" class="btn" :disabled="submitting">
        {{ submitting ? 'Publishing...' : 'Publish' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const title = ref('')
const content = ref('')
const submitting = ref(false)

const handleSubmit = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    alert('Please login first')
    router.push('/login')
    return
  }

  submitting.value = true
  try {
    const res = await fetch('/api/articles', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: title.value,
        content: content.value,
        userId: parseInt(userId)
      })
    })
    const data = await res.json()

    if (res.ok) {
      router.push('/')
    } else {
      alert(data.message)
    }
  } catch (e) {
    alert('Failed to publish article')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.container { max-width: 800px; margin: 0 auto; padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.back-link { color: #4169E1; text-decoration: none; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; font-weight: bold; margin-bottom: 8px; color: #555; }
input, textarea {
  width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px;
  font-size: 1rem; box-sizing: border-box;
}
textarea { font-family: inherit; resize: vertical; }
.btn {
  padding: 12px 24px; background: #4169E1; color: white; border: none;
  border-radius: 8px; cursor: pointer; font-size: 1rem;
}
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
</style>