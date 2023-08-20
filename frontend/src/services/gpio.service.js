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

  createGpioBoard (gpioBoardDto) {
    return axios.post(API_PATH, gpioBoardDto)
  }

  updateGpioBoard (id, gpioBoardDto) {
    return axios.put(API_PATH + '/' + id, gpioBoardDto)
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

export class GpioBoardDtoMapper {
  toGpioBoardDto (gpioBoard) {
    return {
      name: gpioBoard.name,
      address: gpioBoard.address,
      boardModel: gpioBoard.boardModel
    }
  }
}

export default new GpioService()

export const pinDtoMapper = new PinDtoMapper()

export const gpioBoardDtoMapper = new GpioBoardDtoMapper()
