import UserService from '../services/user.service'
import CategoryService from '../services/category.service'

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
