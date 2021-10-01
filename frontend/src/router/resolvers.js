import UserService from '../services/user.service'

export const userIdResolver = (userIdParam) => {
  return async (to, from, next) => {
    to.meta['user'] = await UserService.getUser(to.params[userIdParam])
    next();
  }
}
