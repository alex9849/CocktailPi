import { PumpDtoMapper } from 'src/services/pump.service'

const API_PATH = 'api/gpio/'
import axios from 'axios'

class GpioService {
  types = {
    LOCAL: 'local',
    I2C: 'ic2'
  }

  getBoardsByType (dType) {
    return axios.get(API_PATH, { params: { dType } })
      .then(x => x.data)
  }

  getBoards () {
    return axios.get(API_PATH)
      .then(x => x.data)
  }

  getBoardPins (boardId) {
    return axios.get(API_PATH + boardId + '/pin')
      .then(x => x.data)
  }

  getGpioStatus () {
    return axios.get(API_PATH + '/status')
      .then(x => x.data)
  }
}

export class PinDtoMapper {
  toPinSelectDto (reduced) {
    return {
      nr: reduced.nr,
      boardId: reduced.boardId
    }
  }
}

export default new GpioService()

export const pinDtoMapper = new PinDtoMapper()
