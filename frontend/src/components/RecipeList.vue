<template>
	<div class="list-container">
		<!-- back button -->
		<el-button @click="back" class="back-btn">← Back to Home</el-button>

		<h2>📋 Recipe List</h2>

		<!-- filter bar -->
		<div class="filter-bar">
			<el-input v-model="searchText" placeholder="Search by title..." clearable class="search-input" />
			<el-select v-model="difficultyFilter" placeholder="Difficulty" clearable class="difficulty-select">
				<el-option label="Easy" value="EASY" />
				<el-option label="Medium" value="MEDIUM" />
				<el-option label="Hard" value="HARD" />
			</el-select>
			<el-select v-model="ingredientFilter" placeholder="# Ingredients" clearable class="ingredient-select">
				<el-option label="≤ 3" value="<=3" />
				<el-option label="4-6" value="4-6" />
				<el-option label="≥ 7" value=">=7" />
			</el-select>
		</div>

		<!-- table view -->
		<el-table :data="paginatedRecipes" stripe style="width: 100%" @row-click="viewDetail">
			<el-table-column prop="title" label="Title" />
			<el-table-column prop="difficulty" label="Difficulty" />
			<el-table-column label="# Ingredients" :formatter="row => row.ingredients.length" />
			<el-table-column prop="creatorName" label="Creator" />
			<el-table-column prop="createdDate" label="Created At" :formatter="formatDate" />
		</el-table>

		<!-- pagination -->
		<el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="filteredRecipes.length"
			layout="prev, pager, next" class="pagination" />
	</div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRecipeStore } from '../stores/recipeStore'

const store = useRecipeStore()
const recipes = computed(() => store.recipes)

// fetch recipes on mount
onMounted(() => {
	store.fetchRecipes()
})

// bind search filters
const searchText = computed({
	get: () => store.searchText,
	set: val => store.searchText = val
})

const difficultyFilter = computed({
	get: () => store.difficultyFilter,
	set: val => store.difficultyFilter = val
})

const ingredientFilter = computed({
	get: () => store.ingredientFilter,
	set: val => store.ingredientFilter = val
})

const currentPage = ref(1)
const pageSize = 5

// filter based on title, difficulty, ingredients
const filteredRecipes = computed(() => {
	return recipes.value.filter(recipe => {
		const matchesTitle = recipe.title.toLowerCase().includes(searchText.value.toLowerCase())
		const matchesDifficulty = !difficultyFilter.value || recipe.difficulty === difficultyFilter.value

		const count = recipe.ingredients.length
		const matchesIngredientCount =
			!ingredientFilter.value ||
			(ingredientFilter.value === '<=3' && count <= 3) ||
			(ingredientFilter.value === '4-6' && count >= 4 && count <= 6) ||
			(ingredientFilter.value === '>=7' && count >= 7)

		return matchesTitle && matchesDifficulty && matchesIngredientCount
	})
})

// paginate result
const paginatedRecipes = computed(() => {
	const start = (currentPage.value - 1) * pageSize
	return filteredRecipes.value.slice(start, start + pageSize)
})

// go to detail view
const viewDetail = (row) => {
	store.setPreviousPage('RecipeList')
	store.setSelectedRecipe(row)
	window.setComponent('RecipeDetail')
}

// format date string
const formatDate = (row) => {
	const datetime = row.createdDate
	if (!datetime) return ''
	return datetime.split('T')[0]
}

// go back to homepage
const back = () => {
	store.resetFilters()
	window.setComponent?.('HomePage')
}
</script>

<style scoped>
.list-container {
	max-width: 900px;
	margin: 0 auto;
	padding: 30px;
}

.filter-bar {
	display: flex;
	gap: 16px;
	margin-bottom: 20px;
	align-items: center;
}

.search-input {
	flex: 1;
}

.difficulty-select {
	width: 180px;
}

.ingredient-select {
	width: 140px;
}

.pagination {
	margin-top: 20px;
	text-align: center;
}
</style>