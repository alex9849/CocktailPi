export default function () {
  return {
    cocktailProgress: {
      recipe: {
        id: Number,
        name: String,
        inPublic: Boolean,
        owner: {
          id: Number,
          username: String
        },
        description: String,
        shortDescription: String,
        recipeIngredients: Array,
        tags: Array
      },
      progress: Number,
      user: {
        id: Number,
        username: String
      },
      state: String,
      currentIngredientsToAddManually: []
    },
    hasCocktailProgress: false,
    currentProgressDialog: false
  }
}
