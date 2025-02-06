import { defineStore } from 'pinia'

export const useRecipeStore = defineStore('recipes', {
  state: () => ({
    cachedRecipes: [],
    scrollPosition: 0,
    pagination: {
      page: 0,
      totalPages: 1
    },
    lastUpdate: Date.now()
  }),

  getters: {
    recipes: (state) => state.cachedRecipes
  },

  actions: {
    setRecipes (recipes) {
      this.cachedRecipes = recipes
    },

    addRecipes (newRecipes) {
      this.cachedRecipes.push(...newRecipes)
    },

    setScrollPosition (position) {
      this.scrollPosition = position
    },

    setPagination (pagination) {
      this.pagination = pagination
    },

    resetState () {
      this.cachedRecipes = []
      this.scrollPosition = 0
      this.pagination = {
        page: 0,
        totalPages: 1
      }
      this.lastUpdate = Date.now()
    }
  }
})
