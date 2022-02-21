import axios from 'axios'

const API_PATH = 'api/category/'

class CategoryService {
  getAllCategories () {
    return axios.get(API_PATH)
      .then(response => response.data)
  }

  getCategory (id) {
    return axios.get(API_PATH + String(id))
      .then(response => response.data)
  }

  createCategory (categoryName) {
    return axios.post(API_PATH, { name: categoryName })
  }

  updateCategory (category) {
    return axios.put(API_PATH + String(category.id), category)
  }

  deleteCategory (categoryId) {
    return axios.delete(API_PATH + String(categoryId))
  }
}

export default new CategoryService()
