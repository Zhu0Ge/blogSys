<template>
  <div class="mb-2">
    <div class="bg-light rounded p-2">
      <p class="mb-1 small">{{ comment.content }}</p>
      <p class="mb-0 text-muted" style="font-size: 0.8rem;">
        {{ comment.username }} · {{ formatDate(comment.createdAt) }}
        <button @click="toggleReply" class="btn btn-link btn-sm p-0 ms-2 text-decoration-none">Reply</button>
        <button v-if="comment.replies?.length" @click="toggleCollapse" class="btn btn-link btn-sm p-0 ms-2 text-decoration-none">
          {{ collapsed ? `+ Expand (${comment.replies.length})` : `− Collapse` }}
        </button>
      </p>
      <div v-if="showReplyInput" class="d-flex gap-2 mt-2">
        <textarea v-model="replyContent" class="form-control form-control-sm" placeholder="Write a reply..." rows="1"></textarea>
        <button @click="submitReply" class="btn btn-primary btn-sm" :disabled="!replyContent.trim()">Reply</button>
      </div>
    </div>
    <div v-if="!collapsed && comment.replies?.length" class="ms-4 mt-1 border-start ps-3">
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
/* .comment-item { margin-bottom: 12px; }
.comment-main { padding: 10px 12px; background: #f9f9f9; border-radius: 8px; }
.comment-text { margin-bottom: 4px; }
.comment-meta { color: #999; font-size: 0.8rem; }
.link-btn { background: none; border: none; color: #4169E1; cursor: pointer; font-size: 0.8rem; padding: 0 4px; }
.link-btn:hover { text-decoration: underline; }
.reply-form { margin-top: 8px; display: flex; gap: 8px; }
.reply-form textarea { flex: 1; padding: 6px; border: 1px solid #ddd; border-radius: 6px; resize: vertical; font-family: inherit; font-size: 0.9rem; }
.btn-sm { padding: 4px 12px; font-size: 0.8rem; }
.replies { margin-left: 24px; margin-top: 8px; border-left: 2px solid #e0e0e0; padding-left: 12px; } */
</style>