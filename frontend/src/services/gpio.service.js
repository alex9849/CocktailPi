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
}

export default new GpioService()
