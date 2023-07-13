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

  patchPump (id, patchPump) {
    return axios.patch(API_PATH + String(id), patchPump)
      .then(response => response.data)
  }

  updatePump (id, updatePump, onErrorNotify = true) {
    return axios.put(API_PATH + String(id), updatePump, {
      onErrorNotify
    })
      .then(response => response.data)
  }

  deletePump (id) {
    return axios.delete(API_PATH + String(id))
  }

  getPump (id) {
    return axios.get(API_PATH + String(id))
      .then(response => response.data)
  }
}

export class PumpDtoMapper {
  toPumpCreateDto (detailed) {
    return {
      type: detailed.type,
      name: detailed.name,
      enablePin: detailed.enablePin,
      stepPin: detailed.stepPin,
      acceleration: detailed.acceleration,
      maxStepsPerSecond: detailed.maxStepsPerSecond,
      stepsPerCl: detailed.stepsPerCl,
      tubeCapacityInMl: detailed.tubeCapacityInMl,
      fillingLevelInMl: detailed.fillingLevelInMl,
      isPumpedUp: detailed.pumpedUp,
      currentIngredientId: detailed.currentIngredient?.id,

      timePerClInMs: detailed.timePerClInMs,
      powerStateHigh: detailed.powerStateHigh
    }
  }

  toPumpPatchDto (detailed) {
    const dto = {
      fillingLevelInMl: detailed.fillingLevelInMl,
      isPumpedUp: detailed.pumpedUp,
      currentIngredientId: detailed.currentIngredient?.id
    }
    if (detailed.currentIngredient === null) {
      dto.isRemoveIngredient = true
    }
    return dto
  }
}

export default new PumpService()

export const pumpDtoMapper = new PumpDtoMapper()
