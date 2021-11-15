import axios from 'axios'

const API_PATH = 'api/pump/'

class PumpService {
  getAllPumps () {
    return axios.get(API_PATH)
      .then(response => response.data)
  }

  createPump (pump) {
    return axios.post(API_PATH, pump)
  }

  cleanPump (pump) {
    return axios.put(API_PATH + pump.id + '/clean')
  }

  updatePump (pump) {
    return axios.put(API_PATH + pump.id, pump)
  }

  deletePump (pump) {
    return axios.delete(API_PATH + pump.id)
  }
}

export default new PumpService()
