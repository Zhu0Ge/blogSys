<template>
  <div class="container py-4" style="max-width: 800px;">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h3>Edit Article</h3>
      <router-link :to="`/articles/${articleId}`" class="btn btn-outline-secondary btn-sm">← Back</router-link>
    </div>

    <form @submit.prevent="handleSubmit">
      <div class="mb-3">
        <label class="form-label">Title</label>
        <input type="text" class="form-control" v-model="title" placeholder="Article title" required>
      </div>
      <div class="mb-3">
        <label class="form-label">Content</label>
        <textarea class="form-control" v-model="content" placeholder="Write your article here..." rows="15" required></textarea>
      </div>
      <button type="submit" class="btn btn-primary" :disabled="submitting">
        {{ submitting ? 'Saving...' : 'Save' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '../http.js'

const route = useRoute()
const router = useRouter()
const articleId = route.params.id

const title = ref('')
const content = ref('')
const submitting = ref(false)
const loading = ref(true)

onMounted(async () => {
  // 加载文章数据
  const data = await api(`/api/articles/${articleId}`)
  try {
    const article = data
    // 检查是否是作者
    const currentUserId = parseInt(localStorage.getItem('userId'))
    if (article.userId !== currentUserId) {
      alert('You can only edit your own articles')
      router.push(`/articles/${articleId}`)
      return
    }
    title.value = article.title
    content.value = article.content
  } catch (error) {
    alert(error.message || 'Failed to load article')
    router.push('/')
  }
  loading.value = false
})

const handleSubmit = async () => {
  submitting.value = true
  try {
    const data = await api(`/api/articles/${articleId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: title.value,
        content: content.value
      })
    })
    router.push(`/articles/${articleId}`)
  } catch (error) {
    alert(error.message || 'Failed to update article')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
/* .container { max-width: 800px; margin: 0 auto; padding: 20px; }
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
.btn:disabled { opacity: 0.6; cursor: not-allowed; } */
</style>