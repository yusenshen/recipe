<template>
  <component :is="currentComponent" />
</template>

<script setup>
import { shallowRef, watchEffect, markRaw, ref } from 'vue'
import { useRecipeStore } from './stores/recipeStore'

// import components
import LoginPage from './components/LoginPage.vue'
import HomePage from './components/HomePage.vue'
import RecipeForm from './components/RecipeForm.vue'
import RecipeList from './components/RecipeList.vue'
import MyRecipes from './components/MyRecipes.vue'
import RecipeDetail from './components/RecipeDetail.vue'

const store = useRecipeStore()

// current component name
const currentName = ref('LoginPage')

// current component instance
const currentComponent = shallowRef(markRaw(LoginPage))

// component mapping
const componentMap = {
  LoginPage: markRaw(LoginPage),
  HomePage: markRaw(HomePage),
  RecipeForm: markRaw(RecipeForm),
  RecipeList: markRaw(RecipeList),
  MyRecipes: markRaw(MyRecipes),
  RecipeDetail: markRaw(RecipeDetail)
}

// global component switcher
window.setComponent = (name) => {
  currentName.value = name
}

// watch and update current view
watchEffect(() => {
  if (!store.currentUser && currentName.value !== 'LoginPage') {
    currentComponent.value = componentMap['LoginPage']
  } else {
    currentComponent.value = componentMap[currentName.value] || markRaw(HomePage)
  }
})
</script>

<style>
html,
body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  background-color: #282828;
  color: white;
}

h1 {
  text-align: center;
  margin-top: 20px;
}
</style>
