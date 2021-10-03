import axios from 'axios';

const API_PATH = 'api/category/';

class CategoryService {
  getAllCategories() {
    return axios.get(API_PATH)
      .then(response => response.data);
  }

  getCategory(id) {
    return axios.get(API_PATH + id)
      .then(response => response.data);
  }

  createCategory(category) {
    return axios.post(API_PATH, category);
  }

  updateCategory(category) {
    return axios.put(API_PATH + category.id, category);
  }

  deleteCategory(categoryId) {
    return axios.delete(API_PATH + categoryId);
  }
}

export default new CategoryService();
