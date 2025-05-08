<template>
  <!-- back button -->
  <el-button type="info" @click="back" class="back-btn">← Back to Home</el-button>

  <!-- page title -->
  <h1>🍽️ Add a New Recipe</h1>

  <!-- main form -->
  <div class="form-wrapper">
    <!-- left: title, difficulty, instruction, image -->
    <div class="left-column">
      <label for="title">Recipe Title:</label>
      <input id="title" v-model="title" type="text" :class="{ 'error': errors.title }" @blur="validateField('title')" />
      <span v-if="errors.title" class="error-message">{{ errors.title }}</span>

      <label for="difficulty">Difficulty:</label>
      <select id="difficulty" v-model="difficulty">
        <option value="EASY">Easy</option>
        <option value="MEDIUM">Medium</option>
        <option value="HARD">Hard</option>
      </select>

      <label for="instruction">Instruction:</label>
      <textarea id="instruction" v-model="instruction" rows="4" placeholder="e.g., Boil water, add rice..."
        :class="{ 'error': errors.instruction }" @blur="validateField('instruction')"></textarea>
      <span v-if="errors.instruction" class="error-message">{{ errors.instruction }}</span>

      <label for="image">Recipe Image:</label>
      <input id="image" type="file" @change="handleImageChange" />

      <div v-if="imagePreview">
        <p>Image Preview:</p>
        <img :src="imagePreview" alt="preview" class="image-preview" />
      </div>
    </div>

    <!-- right: ingredients -->
    <div class="right-column">
      <label>Ingredients:</label>
      <span v-if="errors.ingredients" class="error-message">{{ errors.ingredients }}</span>

      <div v-for="(ingredient, index) in confirmedIngredients" :key="'confirmed-' + index" class="ingredient-item">
        <span>{{ ingredient }}</span>
        <button type="button" @click="removeIngredient(index)">❌</button>
      </div>

      <div v-if="showIngredientInput" class="ingredient-input-container">
        <input type="text" v-model="newIngredient" ref="ingredientInput" placeholder="Enter ingredient"
          @keyup.enter="confirmIngredient" :class="{ 'error': errors.newIngredient }" />
        <button type="button" @click="confirmIngredient">✓</button>
        <button type="button" @click="cancelIngredient">✕</button>
      </div>

      <button v-else type="button" @click="startAddingIngredient" :class="{ 'error-btn': errors.ingredients }">
        Add Ingredient
      </button>

      <button @click="handleSubmit" :disabled="isSubmitting || hasErrors" class="submit-btn">
        {{ isSubmitting ? 'Submitting...' : 'Submit' }}
      </button>
    </div>
  </div>
</template>


<script setup>
import { ref, nextTick, computed, onMounted } from 'vue'
import { useRecipeStore } from '../stores/recipeStore'
import api from '../api/api'

const store = useRecipeStore()

// form state
const title = ref('')
const difficulty = ref('EASY')
const instruction = ref('')
const confirmedIngredients = ref([])
const newIngredient = ref('')
const showIngredientInput = ref(false)
const ingredientInput = ref(null)
const imageFile = ref(null)
const imagePreview = ref(null)
const existingImageUrl = ref('')
const isSubmitting = ref(false)

// field-level errors
const errors = ref({
  title: '',
  instruction: '',
  ingredients: '',
  newIngredient: ''
})

// check if any field is invalid
const hasErrors = computed(() => {
  return Object.values(errors.value).some(error => error) ||
    title.value.trim() === '' ||
    instruction.value.trim() === '' ||
    confirmedIngredients.value.length === 0
})

// if editing, populate form with existing data
onMounted(() => {
  if (store.editingRecipeIndex !== null) {
    const editing = store.recipes[store.editingRecipeIndex]
    if (editing) {
      title.value = editing.title || ''
      difficulty.value = editing.difficulty || 'EASY'
      instruction.value = editing.instruction || ''
      confirmedIngredients.value = [...editing.ingredients]
      imagePreview.value = editing.imageUrl || null
      existingImageUrl.value = editing.imageUrl || ''
    }
  }
})

// validate one field
const validateField = (field) => {
  switch (field) {
    case 'title':
      errors.value.title = title.value.trim() ? '' : 'Title is required'
      break
    case 'instruction':
      errors.value.instruction = instruction.value.trim() ? '' : 'Instruction is required'
      break
    case 'ingredients':
      errors.value.ingredients = confirmedIngredients.value.length ? '' : 'At least one ingredient is required'
      break
    case 'newIngredient':
      errors.value.newIngredient = newIngredient.value.trim() ? '' : 'Ingredient cannot be empty'
      break
  }
}

const validateForm = () => {
  validateField('title')
  validateField('instruction')
  validateField('ingredients')
  return !hasErrors.value
}

// show input for new ingredient
const startAddingIngredient = () => {
  showIngredientInput.value = true
  newIngredient.value = ''
  nextTick(() => ingredientInput.value?.focus())
}

// confirm adding ingredient
const confirmIngredient = () => {
  validateField('newIngredient')
  if (!errors.value.newIngredient) {
    confirmedIngredients.value.push(newIngredient.value.trim())
    newIngredient.value = ''
    showIngredientInput.value = false
    errors.value.ingredients = ''
  }
}

// cancel adding ingredient
const cancelIngredient = () => {
  newIngredient.value = ''
  showIngredientInput.value = false
  errors.value.newIngredient = ''
  validateField('ingredients')
}

// remove existing ingredient
const removeIngredient = (index) => {
  confirmedIngredients.value.splice(index, 1)
  validateField('ingredients')
}

// image upload & preview
const handleImageChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    imageFile.value = file
    imagePreview.value = URL.createObjectURL(file)
  }
}

// submit form (create or update)
const handleSubmit = async () => {
  if (!validateForm()) return
  isSubmitting.value = true

  try {
    let imageUrlToUse = existingImageUrl.value

    if (imageFile.value) {
      const formData = new FormData()
      formData.append('file', imageFile.value)

      const res = await api.post('/recipes/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })

      imageUrlToUse = res.data.imageUrl
    }

    const recipeData = {
      title: title.value,
      difficulty: difficulty.value,
      instruction: instruction.value,
      ingredients: confirmedIngredients.value,
      creatorName: store.currentUser,
      createdDate: new Date().toISOString().split('T')[0],
      imageUrl: imageUrlToUse
    }

    if (store.editingRecipeIndex !== null) {
      const recipeId = store.recipes[store.editingRecipeIndex]?.id
      if (recipeId) {
        await store.updateRecipeById(recipeId, recipeData)
      }
    } else {
      await store.addRecipe(recipeData)
    }

    store.setEditing(null)
    window.setComponent('HomePage')

  } catch (err) {
    console.error('Submit failed:', err)
  } finally {
    isSubmitting.value = false
  }
}

// back to home page
const back = () => {
  store.setEditing(null)
  window.setComponent('HomePage')
}
</script>


<style scoped>
.form-wrapper {
  display: flex;
  justify-content: space-between;
  gap: 80px;
  padding: 30px;
  height: 90%;
  width: 90%;
  margin: 0 auto;
  border-radius: 2px;
}

.left-column {
  flex: 0.8;
  min-width: 47%;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.right-column {
  flex: 0.8;
  min-width: 47%;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

label {
  font-weight: 600;
  margin-bottom: 6px;
  font-size: 16px;
  color: #cdcdcd;
}

input,
select,
textarea {
  padding: 12px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  width: 100%;
  transition: border 0.3s ease;
}

#instruction {
  height: 100px;
  resize: vertical;
}

input:focus,
select:focus,
textarea:focus {
  border-color: #4CAF50;
  outline: none;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.image-preview {
  max-width: 100%;
  max-height: 300px;
  border-radius: 8px;
  border: 1px solid #eee;
  margin-top: 12px;
  object-fit: contain;
}

button {
  padding: 12px 24px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.submit-btn {
  background-color: #4CAF50;
  color: white;
  font-weight: 600;
  margin-top: 20px;
}

.submit-btn:disabled {
  background-color: #cccccc;
  color: #888888;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.submit-btn:hover:not(:disabled) {
  background-color: #3e8e41;
}

.ingredient-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2px 2px;
  border-radius: 2px;
  margin-bottom: 2px;
  color: #cdcdcd;
  border-bottom: 1px solid #ccc;
}

.ingredient-item button {
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: #999;
  padding: 0 6px;
}

.ingredient-item button:hover {
  color: #ff4d4f;
}

@media (max-width: 1024px) {
  .form-wrapper {
    flex-direction: column;
    gap: 30px;
    padding: 20px;
  }

  .left-column,
  .right-column {
    width: 100%;
    min-width: auto;
  }
}
</style>