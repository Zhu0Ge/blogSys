<template>
  <div>
    <router-view :searchResults="searchResults" @clearSearch="searchResults = null"></router-view>
    <ChatBox @search-results="handleSearchResults" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ChatBox from './components/ChatBox.vue'

const searchResults = ref(null)

const handleSearchResults = (articles) => {
  searchResults.value = articles
}
</script>