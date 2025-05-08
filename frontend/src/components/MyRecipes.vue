<template>
  <div class="my-recipes-container">
    <el-button @click="goBack" class="back-btn">← Back to Home</el-button>

    <h2>🧑 My Recipes</h2>

    <el-table :data="myRecipes" stripe style="width: 100%" @row-click="viewDetail">
      <el-table-column prop="title" label="Title" />
      <el-table-column prop="difficulty" label="Difficulty" />
      <el-table-column label="# Ingredients" :formatter="row => row.ingredients.length" />
      <el-table-column prop="createdDate" label="Created At" :formatter="formatDate" />
      <el-table-column label="Actions">
        <template #default="{ row, $index }">
          <el-button size="small" type="primary" @click.stop="editRecipe($index)">✏️ Edit</el-button>
          <el-button size="small" type="danger" @click.stop="deleteRecipe($index)">🗑️ Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRecipeStore } from '../stores/recipeStore'
import { ElMessageBox, ElMessage } from 'element-plus'

const store = useRecipeStore()

// fetch recipes on page load
onMounted(() => {
  if (store.recipes.length === 0) {
    store.fetchRecipes()
  }
})

// filter recipes by current user
const myRecipes = computed(() =>
  store.recipes.filter(r => r.creatorName === store.currentUser)
)

// back to homepage
const goBack = () => {
  window.setComponent('HomePage')
}

// view recipe detail
const viewDetail = (row) => {
  store.setPreviousPage('MyRecipes')
  store.setSelectedRecipe(row)
  window.setComponent('RecipeDetail')
}

// edit recipe by index
const editRecipe = (index) => {
  const recipe = myRecipes.value[index]
  const fullIndex = store.recipes.findIndex(r => r.id === recipe.id)
  store.setEditing(fullIndex)
  window.setComponent('RecipeForm')
}

// delete recipe with confirm dialog
const deleteRecipe = async (index) => {
  const recipe = myRecipes.value[index]
  const recipeId = recipe.id

  try {
    await ElMessageBox.confirm(
      `Are you sure you want to delete "${recipe.title}"?`,
      'Confirm Deletion',
      {
        confirmButtonText: 'Yes',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    )

    await store.deleteRecipeById(recipeId)
    ElMessage.success('Recipe deleted successfully')
  } catch (err) {
    if (err !== 'cancel') {
      console.error('Failed to delete recipe:', err)
      ElMessage.error('Failed to delete recipe')
    }
  }
}

// format date (YYYY-MM-DD)
const formatDate = (row) => {
  return row.createdDate?.split('T')[0] || ''
}
</script>

<style scoped>
.my-recipes-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px;
}

.back-btn {
  margin-bottom: 20px;
}
</style>