import axios from 'axios'
import CategoryDtoRequest from 'CategoryDto/Request'
import CategoryDtoDuplex from 'CategoryDto/Duplex'

const API_PATH = 'api/category/'

class CategoryService {
  getAllCategories () {
    return axios.get<CategoryDtoDuplex.Detailed[]>(API_PATH)
      .then(response => response.data)
  }

  getCategory (id: bigint) {
    return axios.get<CategoryDtoDuplex.Detailed>(API_PATH + String(id))
      .then(response => response.data)
  }

  createCategory (categoryName: string) {
    return axios.post<CategoryDtoDuplex.Detailed>(API_PATH, {name: categoryName} as CategoryDtoRequest.Create)
  }

  updateCategory (category: CategoryDtoDuplex.Detailed) {
    return axios.put(API_PATH + String(category.id), category)
  }

  deleteCategory (categoryId: bigint) {
    return axios.delete(API_PATH + String(categoryId))
  }
}

export default new CategoryService()
