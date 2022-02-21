import axios from 'axios'

const API_PATH = 'api/pump/'

class PumpService {
  getAllPumps () {
    return axios.get(API_PATH)
      .then(response => response.data)
  }

  createPump (createPump) {
    return axios.post(API_PATH, createPump)
  }

  cleanPump (id) {
    return axios.put(API_PATH + String(id) + '/clean')
  }

  updatePump (id, createPump) {
    return axios.put(API_PATH + String(id), createPump)
  }

  deletePump (id) {
    return axios.delete(API_PATH + String(id))
  }
}

export class PumpDtoMapper {
  toPumpCreateDto (detailed) {
    return {
      bcmPin: detailed.bcmPin,
      fillingLevelInMl: detailed.fillingLevelInMl,
      timePerClInMs: detailed.timePerClInMs,
      tubeCapacityInMl: detailed.tubeCapacityInMl,
      currentIngredientId: detailed.currentIngredient?.id
    }
  }
}

export default new PumpService()
