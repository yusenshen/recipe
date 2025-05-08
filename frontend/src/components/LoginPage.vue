<template>
	<div class="login-container">
		<h1>🔐 Login to Recipe Vault</h1>

		<!-- input fields -->
		<el-input v-model="username" placeholder="Username" class="username-input" clearable />
		<el-input v-model="password" type="password" placeholder="Password" class="password-input" show-password />

		<!-- login & register buttons -->
		<div class="button-group">
			<el-button type="primary" @click="handleLogin" :disabled="!canSubmit">
				Login
			</el-button>
			<el-button type="success" @click="handleRegister" :disabled="!canSubmit">
				Register
			</el-button>
		</div>

		<!-- error message -->
		<p v-if="errorMsg" class="error">{{ errorMsg }}</p>
	</div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRecipeStore } from '../stores/recipeStore'
import api from '../api/api'

const username = ref('')
const password = ref('')
const errorMsg = ref('')
const store = useRecipeStore()

// check if input is valid
const canSubmit = computed(() => username.value.trim() && password.value.trim())

// login request
const handleLogin = async () => {
	try {
		const res = await api.post('/auth/login', {
			username: username.value.trim(),
			password: password.value.trim()
		})
		store.setUser(res.data.token)
		window.setComponent('HomePage')
	} catch (err) {
		errorMsg.value = err.response?.data?.message || 'Login failed'
	}
}

// register request
const handleRegister = async () => {
	try {
		await api.post('/auth/register', {
			username: username.value.trim(),
			password: password.value.trim()
		})
		errorMsg.value = '✅ Registered successfully! Now login.'
	} catch (err) {
		errorMsg.value = err.response?.data?.message || 'Register failed'
	}
}
</script>

<style scoped>
.login-container {
	max-width: 400px;
	margin: 100px auto;
	text-align: center;
	padding: 40px;
}

.username-input,
.password-input {
	margin-bottom: 20px;
	width: 100%;
}

.button-group {
	display: flex;
	justify-content: space-between;
	gap: 10px;
	margin-bottom: 10px;
}

.error {
	color: red;
	margin-top: 10px;
}
</style>