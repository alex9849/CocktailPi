import axios from 'axios'
import PumpDtoResponse from 'PumpDto/Response'
import PumpDtoRequest from 'PumpDto/Request'

const API_PATH = 'api/pump/'

class PumpService {

  getAllPumps () {
    return axios.get<PumpDtoResponse.Detailed[]>(API_PATH)
      .then(response => response.data)
  }

  createPump (pump: PumpDtoRequest.Create) {
    return axios.post<PumpDtoResponse.Detailed>(API_PATH, pump)
  }

  cleanPump (id: bigint) {
    return axios.put(API_PATH + String(id) + '/clean')
  }

  updatePump (id: bigint, pump: PumpDtoRequest.Create) {
    return axios.put(API_PATH + String(id), pump)
  }

  deletePump (id: bigint) {
    return axios.delete(API_PATH + String(id))
  }
}

export class PumpDtoMapper {

  toPumpCreateDto(detailed: PumpDtoResponse.Detailed) {
    return {
      bcmPin: detailed.bcmPin,
      fillingLevelInMl: detailed.fillingLevelInMl,
      timePerClInMs: detailed.timePerClInMs,
      tubeCapacityInMl: detailed.tubeCapacityInMl,
      currentIngredientId: detailed.currentIngredient?.id
    } as PumpDtoRequest.Create
  }

}

export default new PumpService()
