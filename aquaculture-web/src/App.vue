<template>
  <div class="app">
    <header>
      <h1>Aquaculture System</h1>
      <p>欢迎使用水产养殖管理系统</p>
    </header>
    <main>
      <div class="card">
        <p>Vue 3 + JavaScript + Vite</p>
        <button @click="count++">计数: {{ count }}</button>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const count = ref(0)
</script>

<style scoped>
.app {
  max-width: 1280px;
  margin: 0 auto;
  padding: 2rem;
  text-align: center;
}

header {
  margin-bottom: 2rem;
}

header h1 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

header p {
  font-size: 1.2rem;
  color: #888;
}

.card {
  padding: 2rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.card p {
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

button {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

button:hover {
  background: #35a372;
}
</style>

