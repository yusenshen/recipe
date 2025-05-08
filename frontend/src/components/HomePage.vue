<template>
	<div class="home-container">

		<h1 class="title">Welcome to Recipe Vault</h1>

		<!-- search bar -->
		<div class="search-bar">
			<el-input v-model="searchText" placeholder="Search recipes by title..." @keyup.enter="handleSearch"
				class="search-input" clearable />
			<el-button type="primary" @click="handleSearch">Search</el-button>
		</div>

		<!-- feature buttons -->
		<div class="action-buttons">
			<el-button type="success" size="large" @click="setComponent('RecipeForm')">
				➕ Add New Recipe
			</el-button>
			<el-button type="primary" size="large" @click="setComponent('RecipeList')">
				📋 View All Recipes
			</el-button>
			<el-button type="warning" size="large" @click="setComponent('MyRecipes')">
				🧑 My Recipes
			</el-button>
		</div>
	</div>
</template>

<script setup>
import { ref } from 'vue'
import { useRecipeStore } from '../stores/recipeStore'

const store = useRecipeStore()
const searchText = ref('')

// set visible component
const setComponent = (name) => {
	window.setComponent?.(name)
}

// handle search and navigate to list
const handleSearch = () => {
	store.searchText = searchText.value.trim()
	store.difficultyFilter = ''
	store.ingredientFilter = ''
	setComponent('RecipeList')
}
</script>

<style scoped>
.home-container {
	max-width: 700px;
	margin: 0 auto;
	padding: 60px 20px;
	text-align: center;
}

.title {
	font-size: 32px;
	margin-bottom: 40px;
	font-weight: 600;
}

.search-bar {
	display: flex;
	justify-content: center;
	gap: 12px;
	margin-bottom: 60px;
}

.search-input {
	width: 300px;
}

.action-buttons {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 20px;
}

.action-buttons .el-button {
	width: 240px;
	text-align: center;
	margin-left: 0px;
	justify-content: center;
	font-weight: 600;
	font-size: 16px;
}
</style>