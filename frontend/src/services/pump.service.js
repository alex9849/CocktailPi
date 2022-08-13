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

  pumpUp (id) {
    return axios.put(API_PATH + String(id) + '/pumpup')
  }

  pumpDown (id) {
    return axios.put(API_PATH + String(id) + '/pumpback')
  }

  startPump (id) {
    const config = {
      params: {
        id
      }
    }
    return axios.put(API_PATH + 'start', null, config)
  }

  stopPump (id) {
    const config = {
      params: {
        id
      }
    }
    return axios.put(API_PATH + 'stop', null, config)
  }

  updatePump (id, createPump) {
    return axios.put(API_PATH + String(id), createPump)
  }

  patchPump (id, patchPump) {
    return axios.patch(API_PATH + String(id), patchPump)
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
      powerStateHigh: detailed.powerStateHigh,
      pumpedUp: detailed.pumpedUp,
      currentIngredientId: detailed.currentIngredient?.id
    }
  }

  toPumpPatchDto (detailed) {
    return {
      fillingLevelInMl: detailed.fillingLevelInMl,
      isPumpedUp: detailed.pumpedUp,
      currentIngredientId: detailed.currentIngredient?.id
    }
  }
}

export default new PumpService()

export const pumpDtoMapper = new PumpDtoMapper()
