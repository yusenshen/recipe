<template>
	<div class="detail-container" v-if="recipe && recipe.title">
		<el-button class="back-btn" @click="goBack">← Back to List</el-button>
		<h1>{{ recipe.title }}</h1>
		<div class="content-layout">
			<!-- left section -->
			<div class="text-section">
				<p><strong>Difficulty:</strong> {{ recipe.difficulty }}</p>

				<h3>Ingredients</h3>
				<ul>
					<li v-for="(item, i) in recipe.ingredients" :key="i">{{ item }}</li>
				</ul>

				<h3>Instructions</h3>
				<p>{{ recipe.instruction }}</p>
			</div>

			<!-- right section (image & meta) -->
			<div class="image-section">
				<img v-if="recipe.imageUrl" :src="recipe.imageUrl" alt="Recipe Image" class="detail-image" />

				<p class="meta"><strong>Creator:</strong> {{ recipe.creatorName }}</p>
				<p class="meta"><strong>Created At:</strong> {{ formatDate(recipe.createdDate) }}</p>
			</div>
		</div>
	</div>
</template>

<script setup>
import { computed } from 'vue'
import { useRecipeStore } from '../stores/recipeStore'

const store = useRecipeStore()
const recipe = computed(() => store.selectedRecipe)

// go back to previous view
const goBack = () => {
	console.log('Going back to:', store.previousPage)
	window.setComponent(store.previousPage || 'RecipeList')
}

// format datetime to YYYY-MM-DD
const formatDate = (datetime) => {
	if (!datetime) return ''
	return datetime.split('T')[0]
}
</script>

<style scoped>
.detail-container {
	max-width: 1000px;
	margin: 0 auto;
	padding: 40px 60px;
	font-size: 16px;
	line-height: 1.6;
}

.back-btn {
	margin-bottom: 20px;
}

/* main layout: left & right */
.content-layout {
	display: flex;
	gap: 40px;
	align-items: flex-start;
	margin-top: 40px;
}

.text-section {
	flex: 1;
	padding-left: 12px;
	margin-left: 10%;
}

.text-section p strong,
.text-section h3 {
	font-size: 24px;
	font-weight: 600;
	margin-top: 20px;
	display: inline-block;
	color: #ffffff;
}

.text-section ul li {
	font-size: 20px;
	margin-bottom: 6px;
	color: #dddddd;
}

.text-section p:not(:has(strong)) {
	font-size: 18px;
	margin-top: 10px;
	color: #eeeeee;
}

.image-section {
	flex: 1.2;
	min-width: 320px;
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-top: 5%;
}

.detail-image {
	width: 100%;
	border-radius: 4px;
	margin-bottom: 30px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.meta {
	font-size: 20px;
	color: #bbbbbb;
	margin-top: 8px;
	font-style: italic;
	text-align: center;
}

@media (max-width: 768px) {
	.content-layout {
		flex-direction: column;
	}

	.image-section {
		align-items: flex-start;
		margin-top: 24px;
	}

	.text-section {
		padding-left: 0;
	}
}
</style>