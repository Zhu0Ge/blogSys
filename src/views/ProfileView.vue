<template>
  <div class="container">
    <div class="header">
      <h1>Profile</h1>
      <router-link to="/" class="back-link">← Back</router-link>
    </div>

    <div class="profile-card">
      <div class="avatar-section">
        <!-- 点击头像触发隐藏的文件选择框 -->
        <div class="avatar" @click="$refs.fileInput.click()">
            <img v-if="avatar" :src="avatar" alt="avatar">
            <span v-else class="avatar-placeholder">{{ username[0]?.toUpperCase() }}</span>
        </div>
        <p class="hint">Click to change avatar</p>
        <!-- 隐藏的文件选择框 -->
        <input 
            type="file" 
            ref="fileInput" 
            accept="image/*"
            style="display: none"
            @change="handleFileUpload"
        >
        <!-- 上传进度提示 -->
        <p v-if="uploading" class="hint">Uploading...</p>
      </div>

      <div class="info-section">
        <div class="field">
          <label>Username</label>
          <p class="value">{{ username }}</p>
        </div>
        <div class="field">
          <label>Email</label>
          <p class="value">{{ email }}</p>
        </div>
        <div class="field">
          <label>Avatar URL</label>
          <input type="text" v-model="avatarInput" placeholder="Paste image URL...">
        </div>
        <div class="field">
          <label>Bio</label>
          <textarea v-model="bio" placeholder="Write something about yourself..." rows="4"></textarea>
        </div>
        <button @click="handleSave" class="btn" :disabled="saving">
          {{ saving ? 'Saving...' : 'Save' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../http.js'

const router = useRouter()
const username = ref('')
const email = ref('')
const avatar = ref('')
const avatarInput = ref('')
const bio = ref('')
const saving = ref(false)

onMounted(async () => {
  const res = await api('/api/profile')
  if (res.ok) {
    const data = await res.json()
    username.value = data.username
    email.value = data.email
    avatar.value = data.avatar || ''
    avatarInput.value = data.avatar || ''
    bio.value = data.bio || ''
  }
})

const handleSave = async () => {
  saving.value = true
  const res = await api('/api/profile', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      avatar: avatarInput.value,
      bio: bio.value
    })
  })
  if (res.ok) {
    avatar.value = avatarInput.value
    alert('Profile updated!')
  } else {
    alert('Failed to update profile')
  }
  saving.value = false
}

const uploading = ref(false)

const handleFileUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  uploading.value = true
  const formData = new FormData()
  formData.append('file', file)

  const token = localStorage.getItem('token')
  const res = await fetch('/api/upload/avatar', {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}` },
    body: formData
  })

  if (res.ok) {
    const data = await res.json()
    avatar.value = data.url        // 立即显示新头像
    avatarInput.value = data.url   // 同步到输入框（保存时提交）
  } else {
    alert('Upload failed')
  }
  uploading.value = false
  // 清空 input，允许重复选择同一个文件
  e.target.value = ''
}
</script>

<style scoped>
.container { max-width: 600px; margin: 0 auto; padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.back-link { color: #4169E1; text-decoration: none; }
.profile-card { background: #f9f9f9; border-radius: 12px; padding: 30px; }
.avatar-section { text-align: center; margin-bottom: 30px; }
.avatar {
  width: 100px; height: 100px; border-radius: 50%; overflow: hidden;
  margin: 0 auto; cursor: pointer; background: #4169E1;
  display: flex; align-items: center; justify-content: center;
}
.avatar img { width: 100%; height: 100%; object-fit: cover; }
.avatar-placeholder { color: white; font-size: 2.5rem; font-weight: bold; }
.hint { color: #999; font-size: 0.8rem; margin-top: 8px; }
.field { margin-bottom: 20px; }
.field label { display: block; font-weight: bold; margin-bottom: 6px; color: #555; font-size: 0.9rem; }
.value { color: #333; padding: 8px 0; }
input, textarea {
  width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 8px;
  font-size: 1rem; box-sizing: border-box; font-family: inherit;
}
.btn {
  width: 100%; padding: 12px; background: #4169E1; color: white;
  border: none; border-radius: 8px; cursor: pointer; font-size: 1rem;
}
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
</style>