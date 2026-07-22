<template>
  <div class="container py-4">
    <!-- 导航栏 -->
    <nav class="navbar navbar-expand navbar-light bg-white shadow-sm rounded mb-4 px-3">
      <span class="navbar-brand mb-0 h1">Blog</span>
      <div class="ms-auto d-flex align-items-center gap-3">
        <input class="form-control form-control-sm" type="text" v-model="searchQuery" @input="handleSearch" placeholder="Search..." style="width: 200px;">
        <span class="text-muted small">Welcome, {{ username }}</span>
        <router-link to="/profile" class="btn btn-outline-secondary btn-sm">Profile</router-link>
        <router-link to="/articles/new" class="btn btn-primary btn-sm">Write</router-link>
        <button @click="handleLogout" class="btn btn-outline-danger btn-sm">Logout</button>
      </div>
    </nav>

    <!-- 文章卡片列表 -->
    <div v-for="article in articles" :key="article.id" class="card shadow-sm mb-3">
      <div class="card-body">
        <router-link :to="`/articles/${article.id}`" class="card-title h5 text-decoration-none">{{ article.title }}</router-link>
        <p class="card-text small text-muted mt-2">By {{ article.username || 'Unknown' }} · {{ formatDate(article.createdAt) }}</p>
      </div>
    </div>
    <p v-if="articles.length === 0" class="text-center text-muted py-5">No articles yet.</p>

    <!-- 分页 -->
    <nav v-if="totalPages > 1" class="d-flex justify-content-center mt-4">
      <ul class="pagination">
        <li class="page-item" :class="{ disabled: !hasPrevious }">
          <button class="page-link" @click="loadArticles(currentPage - 1)">← Prev</button>
        </li>
        <li class="page-item disabled"><span class="page-link">Page {{ currentPage + 1 }} / {{ totalPages }}</span></li>
        <li class="page-item" :class="{ disabled: !hasNext }">
          <button class="page-link" @click="loadArticles(currentPage + 1)">Next →</button>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../http.js'

const router = useRouter()
const username = ref(localStorage.getItem('username') || 'User')
const articles = ref([])
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const hasNext = ref(false)
const hasPrevious = ref(false)
const pageSize = ref(5)


const loadArticles = async (page = 0) => {
  const data = await api(`/api/articles/paged?page=${page}&size=${pageSize.value}`)
  articles.value = data.articles
  currentPage.value = data.currentPage
  totalPages.value = data.totalPages
  totalElements.value = data.totalElements
  hasNext.value = data.hasNext
  hasPrevious.value = data.hasPrevious
}

onMounted(loadArticles)


const searchQuery = ref('')
let searchTimer = null

const handleSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    if (!searchQuery.value.trim()) {
      loadArticles(0)  // 清空搜索时加载全部
      return
    }
    const data = await api(`/api/articles/search/fulltext?q=${encodeURIComponent(searchQuery.value)}`)

    articles.value = data
    
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
/* .container { max-width: 800px; margin: 0 auto; padding: 20px; }
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
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
}
.page-btn {
  padding: 8px 16px;
  background: #4169E1;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}
.page-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.page-info {
  color: #666;
  font-size: 14px;
} */
</style>