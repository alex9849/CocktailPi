import UserService from '../services/user.service'
import CategoryService from '../services/category.service'
import RecipeService from '../services/recipe.service'

export const userResolver = (userIdParam) => {
  return async (to, from, next) => {
    to.meta['user'] = await UserService.getUser(to.params[userIdParam])
    next();
  }
}

export const categoryResolver = (categoryIdParam) => {
  return async (to, from, next) => {
    to.meta['category'] = await CategoryService.getCategory(to.params[categoryIdParam])
    next();
  }
}

export const recipeResolver = (recipeIdParam) => {
  return async (to, from, next) => {
    to.meta['recipe'] = await RecipeService.getRecipe(to.params[recipeIdParam])
    next();
  }
}


