import { defineStore } from 'pinia'
import api from '@/api/api'

export const useRecipeStore = defineStore('recipe', {
    state: () => ({
        recipes: [],

        searchText: '',
        difficultyFilter: '',
        ingredientFilter: '',
        selectedRecipe: null,
        currentUser: '',
        previousPage: 'RecipeList',
        editingRecipeIndex: null
    }),

    actions: {
        async fetchRecipes() {
            try {
                const res = await api.get('/recipes')
                this.recipes = res.data
                console.log("Fetched recipes:", res.data)
            } catch (err) {
                console.error('Failed to get recipes:', err)
            }
        },

        async addRecipe(recipe) {
            await api.post('/recipes', recipe)
            await this.fetchRecipes()
        },

        async deleteRecipeById(id) {
            await api.delete(`/recipes/${id}`)
            await this.fetchRecipes()
        },

        async updateRecipeById(id, newRecipe) {
            await api.put(`/recipes/${id}`, newRecipe)
            await this.fetchRecipes()
        },

        resetFilters() {
            this.searchText = ''
            this.difficultyFilter = ''
            this.ingredientFilter = ''
        },

        setSelectedRecipe(recipe) {
            this.selectedRecipe = recipe
        },

        setEditing(index) {
            this.editingRecipeIndex = index
        },

        clearEditing() {
            this.editingRecipeIndex = null
        },

        setUser(name) {
            this.currentUser = name
        },
        
        setPreviousPage(pageName) {
            this.previousPage = pageName
        }
    }
})
