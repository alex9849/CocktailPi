import axios from 'axios'

const API_PATH = 'api/glass/'

class GlassService {
  getAllGlasses () {
    return axios.get(API_PATH)
      .then(response => response.data)
  }

  getGlass (id) {
    return axios.get(API_PATH + String(id))
      .then(response => response.data)
  }

  createGlass (glass) {
    return axios.post(API_PATH, glass)
  }

  updateGlass (glass) {
    return axios.put(API_PATH + String(glass.id), glass)
  }

  deleteGlass (glassId) {
    return axios.delete(API_PATH + String(glassId))
  }
}

export default new GlassService()
