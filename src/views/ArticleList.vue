<template>
  <div class="container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <h1>Blog</h1>
      <div class="search-bar">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="Search articles..."
          @input="handleSearch"
        >
      </div>
      <div class="header-right">
        <span>Welcome, {{ username }}</span>
        <router-link to="/profile" class="btn profile-btn">Profile</router-link>
        <router-link to="/articles/new" class="btn">Write</router-link>
        <button @click="handleLogout" class="btn logout">Logout</button>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="articles">
      <div v-for="article in articles" :key="article.id" class="article-card">
        <router-link :to="`/articles/${article.id}`" class="article-title">
          {{ article.title }}
        </router-link>
        <p class="article-meta">
          By {{ article.username || 'Unknown' }} · {{ formatDate(article.createdAt) }}
        </p>
      </div>
      <p v-if="articles.length === 0" class="empty">No articles yet.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../http.js'

const router = useRouter()
const username = ref(localStorage.getItem('username') || 'User')
const articles = ref([])


const loadArticles = async () => {
  const res = await api('/api/articles')
  if (res.ok) {
    articles.value = await res.json()
  }
}

onMounted(loadArticles)


const searchQuery = ref('')
let searchTimer = null

const handleSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    if (!searchQuery.value.trim()) {
      loadArticles()  // 清空搜索时加载全部
      return
    }
    const res = await api(`/api/articles/search?q=${encodeURIComponent(searchQuery.value)}`)
    if (res.ok) {
      articles.value = await res.json()
    }
  }, 300)  // 防抖 300ms，防止每输入一个字就发请求
}

const handleLogout = () => {
  localStorage.removeItem('username')
  localStorage.removeItem('userId')
  localStorage.removeItem('token')
  router.push('/login')
}

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString()
}
</script>

<style scoped>
.container { max-width: 800px; margin: 0 auto; padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.header-right { display: flex; align-items: center; gap: 15px; }
.btn {
  padding: 8px 16px; background: #4169E1; color: white; text-decoration: none;
  border-radius: 6px; border: none; cursor: pointer; font-size: 14px;
}
.logout { background: #888; }
.article-card {
  padding: 20px; border: 1px solid #eee; border-radius: 8px; margin-bottom: 15px;
  transition: box-shadow 0.2s;
}
.article-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.article-title { font-size: 1.3rem; font-weight: bold; color: #333; text-decoration: none; }
.article-title:hover { color: #4169E1; }
.article-meta { color: #888; font-size: 0.9rem; margin-top: 8px; }
.profile-btn { background: #555; }
.empty { text-align: center; color: #999; margin-top: 50px; }
</style>