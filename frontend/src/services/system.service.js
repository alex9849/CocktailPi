import axios from 'axios'
import { pinDtoMapper } from 'src/services/gpio.service'

const API_PATH = 'api/system/'

class SystemService {
  getInstalledPythonLibraries () {
    return axios.get(API_PATH + 'pythonlibraries')
      .then(response => response.data)
  }

  doShutdown () {
    return axios.put(API_PATH + 'shutdown')
  }

  getAudioDevices () {
    return axios.get(API_PATH + 'audiodevices')
      .then(response => response.data)
  }

  setReversePumpSettings (settings) {
    const dto = {
      enable: settings.enable
    }
    if (settings.settings) {
      dto.settings = {
        directorPin: pinDtoMapper.toPinSelectDto(settings.settings.directorPin),
        overshoot: settings.settings.overshoot,
        autoPumpBackTimer: settings.settings.autoPumpBackTimer
      }
    }
    return axios.put(API_PATH + 'settings/reversepumping', dto)
  }

  getReversePumpSettings () {
    return axios.get(API_PATH + 'settings/reversepumping')
      .then(response => {
        if (!response.data.settings) {
          delete response.data.settings
        }
        return response.data
      })
  }

  getGlobalSettings () {
    return axios.get(API_PATH + 'settings/global')
      .then(response => response.data)
  }

  setI2cSettings (settings) {
    const dto = {
      enable: settings.enable
    }
    if (settings.enable) {
      dto.sdaPin = {
        nr: settings.sdaPin.nr,
        boardId: settings.sdaPin.boardId
      }
      dto.sclPin = {
        nr: settings.sclPin.nr,
        boardId: settings.sclPin.boardId
      }
    }
    return axios.put(API_PATH + 'settings/i2c', dto)
  }
}

export default new SystemService()
