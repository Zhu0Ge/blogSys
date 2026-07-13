<template>
  <div class="container">
    <!-- 顶部 -->
    <div class="header">
      <router-link to="/" class="back-link">← Back</router-link>
      <div v-if="isAuthor" class="header-actions">
        <button @click="handleDelete" class="btn danger">Delete</button>
      </div>
    </div>

    <!-- 文章内容 -->
    <div v-if="article" class="article">
      <h1>{{ article.title }}</h1>
      <p class="meta">{{ formatDate(article.createdAt) }}</p>
      <div class="content">{{ article.content }}</div>
    </div>
    <p v-else class="loading">Loading...</p>

    <!-- 评论区 -->
    <div class="comments-section">
      <h2>Comments ({{ comments.length }})</h2>

      <!-- 发表评论 -->
      <div class="comment-form">
        <textarea v-model="newComment" placeholder="Write a comment..." rows="3"></textarea>
        <button @click="handleAddComment(null)" class="btn" :disabled="!newComment.trim()">Post</button>
      </div>

      <!-- 评论列表（递归组件） -->
      <CommentItem
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        class="comment"
        @reply="handleAddComment"
      />
      <p v-if="comments.length === 0" class="empty">No comments yet.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import CommentItem from './CommentItem.vue'
import { api } from '../http.js'

const route = useRoute()
const router = useRouter()
const article = ref(null)
const comments = ref([])
const newComment = ref('')

const currentUserId = parseInt(localStorage.getItem('userId'))
const isAuthor = ref(false)

onMounted(async () => {
  const articleId = route.params.id

  // 获取文章
  const articleRes = await api(`/api/articles/${articleId}`)
  if (articleRes.ok) {
    article.value = await articleRes.json()
    isAuthor.value = article.value.userId === currentUserId
  }

  // 获取评论
  const commentRes = await api(`/api/comments/article/${articleId}`)
  if (commentRes.ok) {
    comments.value = await commentRes.json()
  }
})

const handleAddComment = async (parentId, content) => {
  const commentContent = parentId ? content : newComment.value
  if (!commentContent.trim()) return

  const res = await api('/api/comments', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      content: commentContent,
      articleId: parseInt(route.params.id),
      parentId: parentId  // 可以为 null（顶级评论）或数字（回复）
    })
  })

  if (res.ok) {
    // 重新获取评论（树形结构刷新）
    const commentRes = await api(`/api/comments/article/${route.params.id}`)
    if (commentRes.ok) {
      comments.value = await commentRes.json()
    }
    newComment.value = ''
  } else {
    alert('Failed to add comment')
  }
}

const handleDelete = async () => {
  if (!confirm('Delete this article?')) return
  const res = await api(`/api/articles/${article.value.id}`, {
    method: 'DELETE'
  })
  if (res.ok) {
    router.push('/')
  } else {
    alert('Failed to delete')
  }
}

const formatDate = (dateStr) => new Date(dateStr).toLocaleDateString()
</script>

<style scoped>
.container { max-width: 800px; margin: 0 auto; padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.back-link { color: #4169E1; text-decoration: none; }
.header-actions { display: flex; gap: 10px; }
.btn { padding: 8px 16px; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; background: #4169E1; color: white; }
.danger { background: #e74c3c; }
.btn:disabled { opacity: 0.5; cursor: not-allowed; }

.article h1 { font-size: 2rem; margin-bottom: 10px; }
.meta { color: #888; font-size: 0.9rem; margin-bottom: 30px; }
.content { line-height: 1.8; font-size: 1.1rem; white-space: pre-wrap; }

.comments-section { margin-top: 50px; border-top: 1px solid #eee; padding-top: 30px; }
.comment-form { display: flex; gap: 10px; margin-bottom: 30px; }
.comment-form textarea { flex: 1; padding: 10px; border: 1px solid #ddd; border-radius: 8px; resize: vertical; font-family: inherit; }
.comment { padding: 15px 0; border-bottom: 1px solid #f0f0f0; }
.comment-text { margin-bottom: 5px; }
.comment-meta { color: #999; font-size: 0.8rem; }
.empty, .loading { text-align: center; color: #999; margin-top: 30px; }
</style>