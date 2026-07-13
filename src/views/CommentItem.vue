<template>
  <div class="comment-item">
    <div class="comment-main">
      <p class="comment-text">{{ comment.content }}</p>
      <p class="comment-meta">
        {{ comment.username }} · {{ formatDate(comment.createdAt) }}
        <button @click="toggleReply" class="link-btn">Reply</button>
        <button v-if="comment.replies?.length" @click="toggleCollapse" class="link-btn">
          {{ collapsed ? `+ Expand (${comment.replies.length})` : `− Collapse` }}
        </button>
      </p>
      <!-- 回复输入框 -->
      <div v-if="showReplyInput" class="reply-form">
        <textarea v-model="replyContent" placeholder="Write a reply..." rows="2"></textarea>
        <button @click="submitReply" class="btn btn-sm" :disabled="!replyContent.trim()">Reply</button>
      </div>
    </div>
    <!-- 回复列表 -->
    <div v-if="!collapsed && comment.replies?.length" class="replies">
      <CommentItem
        v-for="reply in comment.replies"
        :key="reply.id"
        :comment="reply"
        @reply="(parentId, content) => $emit('reply', parentId, content)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({ comment: Object })
const emit = defineEmits(['reply'])

const showReplyInput = ref(false)
const collapsed = ref(false)
const replyContent = ref('')

const toggleReply = () => { showReplyInput.value = !showReplyInput.value }
const toggleCollapse = () => { collapsed.value = !collapsed.value }

const submitReply = () => {
  if (!replyContent.value.trim()) return
  emit('reply', props.comment.id, replyContent.value)
  replyContent.value = ''
  showReplyInput.value = false
}

const formatDate = (dateStr) => new Date(dateStr).toLocaleDateString()
</script>

<style scoped>
.comment-item { margin-bottom: 12px; }
.comment-main { padding: 10px 12px; background: #f9f9f9; border-radius: 8px; }
.comment-text { margin-bottom: 4px; }
.comment-meta { color: #999; font-size: 0.8rem; }
.link-btn { background: none; border: none; color: #4169E1; cursor: pointer; font-size: 0.8rem; padding: 0 4px; }
.link-btn:hover { text-decoration: underline; }
.reply-form { margin-top: 8px; display: flex; gap: 8px; }
.reply-form textarea { flex: 1; padding: 6px; border: 1px solid #ddd; border-radius: 6px; resize: vertical; font-family: inherit; font-size: 0.9rem; }
.btn-sm { padding: 4px 12px; font-size: 0.8rem; }
.replies { margin-left: 24px; margin-top: 8px; border-left: 2px solid #e0e0e0; padding-left: 12px; }
</style>