<template>
  <div class="login-container">
    <div class="login-card card">
      <h1>Tenga Knowledge Base</h1>
      <p>Enter your API key to continue</p>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="apiKey">API Key</label>
          <input
            id="apiKey"
            v-model="apiKey"
            type="password"
            class="input"
            placeholder="Enter your API key"
            required
            :disabled="loading"
          />
        </div>

        <div v-if="error" class="error">
          {{ error }}
        </div>

        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Validating...' : 'Login' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { api } from '@/services/api'

const router = useRouter()
const route = useRoute()

const apiKey = ref('')
const loading = ref(false)
const error = ref<string | null>(null)

async function handleLogin() {
  loading.value = true
  error.value = null

  try {
    api.setApiKey(apiKey.value)
    const isValid = await api.validateApiKey()

    if (isValid) {
      const redirect = (route.query.redirect as string) || '/'
      router.push(redirect)
    } else {
      error.value = 'Invalid API key. Please try again.'
      api.clearApiKey()
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Failed to validate API key'
    api.clearApiKey()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  max-width: 400px;
  width: 100%;
  margin: 20px;
}

.login-card h1 {
  margin-bottom: 10px;
  color: #333;
}

.login-card p {
  margin-bottom: 30px;
  color: #666;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.btn {
  width: 100%;
}
</style>
