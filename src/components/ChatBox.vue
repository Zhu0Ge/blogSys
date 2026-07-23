<template>
  <div v-if="isLoggedIn">
    <!-- 聊天按钮 -->
    <button v-if="!showChat" @click="showChat = true" 
            class="btn btn-primary rounded-circle shadow position-fixed d-flex align-items-center justify-content-center"
            style="width:56px;height:56px;bottom:30px;right:30px;z-index:999;font-size:24px;">
      💬
    </button>

    <!-- 聊天框 -->
    <div v-if="showChat" class="card shadow-lg position-fixed" 
         style="width:380px;height:550px;bottom:30px;right:30px;z-index:999;">
      <div class="card-header d-flex justify-content-between align-items-center bg-primary text-white">
        <span>AI 助手</span>
        <button @click="showChat = false" class="btn btn-sm text-white">✕</button>
      </div>
      
      <div class="card-body overflow-auto d-flex flex-column" style="height:350px;" ref="chatBox">
        <div v-for="(msg, i) in messages" :key="i" 
             :class="msg.role === 'user' ? 'text-end' : 'text-start'" class="mb-2">
          <span :class="msg.role === 'user' ? 'bg-primary text-white' : 'bg-light'"
                class="d-inline-block rounded px-3 py-2" style="max-width:80%;font-size:14px;">
            {{ msg.content }}
          </span>
        </div>
        <div v-if="loading" class="text-start">
          <span class="d-inline-block rounded px-3 py-2 bg-light" style="font-size:14px;">思考中...</span>
        </div>
      </div>

      <div class="card-footer d-flex gap-2">
        <input v-model="input" @keyup.enter="send" class="form-control form-control-sm" placeholder="问我关于博客的问题...">
        <button @click="send" class="btn btn-primary btn-sm" :disabled="!input.trim() || loading">发送</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, computed } from 'vue'
import { api } from '../http.js'

const isLoggedIn = computed(() => !!localStorage.getItem('token'))

const emit = defineEmits(['search-results'])
const showChat = ref(false)
const input = ref('')
const loading = ref(false)
const messages = ref([{ role: 'assistant', content: '你好！我可以帮你搜索和总结博客文章的内容。你想了解什么？' }])
const chatBox = ref(null)

const send = async () => {
  if (!input.value.trim() || loading.value) return
  const question = input.value
  messages.value.push({ role: 'user', content: question })
  input.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const data = await api('/api/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: question })
    })
    messages.value.push({ role: 'assistant', content: data.reply })
    
    if (data.articles && data.articles.length > 0) {
      emit('search-results', data.articles)
    }
  } catch (e) {
    messages.value.push({ role: 'assistant', content: '抱歉，出了点问题，请稍后再试。' })
  }
  loading.value = false
  scrollToBottom()
}

const scrollToBottom = async () => {
  await nextTick()
  if (chatBox.value) chatBox.value.scrollTop = chatBox.value.scrollHeight
}
</script>