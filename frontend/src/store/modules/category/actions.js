import CategoryService from 'src/services/category.service'

export const createCategory = ({ dispatch, commit }, categoryName) => {
  return CategoryService.createCategory(categoryName)
    .then(() => {
      return dispatch('fetchCategories')
    })
}

export const updateCategory = ({ dispatch, commit }, category) => {
  return CategoryService.updateCategory(category)
    .then(() => {
      return dispatch('fetchCategories')
    })
}

export const fetchCategories = ({ commit }) => {
  return CategoryService.getAllCategories()
    .then(data => {
      commit('setCategories', data)
      return data
    })
}

export const deleteCategories = ({ dispatch, commit }, categoryIds) => {
  const promises = []
  for (const id of categoryIds) {
    promises.push(CategoryService.deleteCategory(id))
  }
  return Promise.all(promises)
    .then(() => {
      return dispatch('fetchCategories')
    })
}
