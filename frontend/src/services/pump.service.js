import axios from 'axios'
import { pinDtoMapper } from 'src/services/gpio.service'

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

  dispatchPumpAdvice (id, advice) {
    return axios.put(API_PATH + String(id) + '/runjob', advice)
      .then(response => response.data)
  }

  getMetrics (jobId) {
    return axios.get(API_PATH + 'jobmetrics/' + String(jobId))
      .then(response => response.data)
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
      isPowerStateHigh: detailed.isPowerStateHigh,

      pin: detailed.pin
    }
  }

  toPumpPatchDto (detailed) {
    let dcPumpPin = null
    if (detailed.pin) {
      dcPumpPin = pinDtoMapper.toPinSelectDto(detailed.pin)
    }
    let stepperEnablePin = null
    if (detailed.enablePin) {
      stepperEnablePin = pinDtoMapper.toPinSelectDto(detailed.enablePin)
    }
    let stepperStepPin = null
    if (detailed.stepPin) {
      stepperStepPin = pinDtoMapper.toPinSelectDto(detailed.stepPin)
    }
    const dto = {
      pin: dcPumpPin,
      timePerClInMs: detailed.timePerClInMs,
      isPowerStateHigh: detailed.isPowerStateHigh,
      name: detailed.name,
      tubeCapacityInMl: detailed.tubeCapacityInMl,
      fillingLevelInMl: detailed.fillingLevelInMl,
      isPumpedUp: detailed.pumpedUp,
      currentIngredientId: detailed.currentIngredient?.id,

      enablePin: stepperEnablePin,
      stepPin: stepperStepPin,
      acceleration: detailed.acceleration,
      maxStepsPerSecond: detailed.maxStepsPerSecond,
      stepsPerCl: detailed.stepsPerCl,

      type: detailed.type,
      removeFields: (!detailed.removeFields || detailed.removeFields.length === 0) ? undefined : detailed.removeFields
    }
    return dto
  }
}

export default new PumpService()

export const pumpDtoMapper = new PumpDtoMapper()
