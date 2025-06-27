import axios from 'axios'
import { pinDtoMapper } from 'src/services/gpio.service'

const API_PATH = 'api/pump/settings/'

class PumpSettingsService {
  setReversePumpSettings (settings) {
    const dto = {
      enable: settings.enable
    }
    if (settings.enable) {
      dto.settings = {
        directorPin: pinDtoMapper.toPinSelectDto(settings.settings.directorPin),
        overshoot: settings.settings.overshoot,
        autoPumpBackTimer: settings.settings.autoPumpBackTimer,
        forwardStateHigh: settings.settings.forwardStateHigh
      }
    }
    return axios.put(API_PATH + 'reversepumping', dto)
  }

  getReversePumpSettings () {
    return axios.get(API_PATH + 'reversepumping')
      .then(response => {
        if (!response.data.settings) {
          delete response.data.settings
        }
        return response.data
      })
  }

  setLoadCell (loadcell) {
    let dto = null
    if (loadcell != null) {
      dto = {
        clkPin: pinDtoMapper.toPinSelectDto(loadcell.clkPin),
        dtPin: pinDtoMapper.toPinSelectDto(loadcell.dtPin)
      }
    }
    return axios.put(API_PATH + 'loadcell', dto,
      { headers: { 'Content-Type': 'application/json' } })
      .then(response => response.data)
  }

  getLoadCell () {
    return axios.get(API_PATH + 'loadcell')
      .then(response => response.data)
  }

  readLoadCell () {
    return axios.get(API_PATH + 'loadcell/read')
      .then(response => response.data)
  }

  calibrateLoadCellZero () {
    return axios.put(API_PATH + 'loadcell/calibratezero')
      .then(response => response.data)
  }

  calibrateLoadCellRefWeight (referenceWeight) {
    return axios.put(API_PATH + 'loadcell/calibratereference', referenceWeight,
      { headers: { 'Content-Type': 'application/json' } })
      .then(response => response.data)
  }

  setPowerLimit (powerLimit) {
    return axios.put(API_PATH + 'powerlimit', powerLimit,
      { headers: { 'Content-Type': 'application/json' } })
      .then(response => response.data)
  }

  getPowerLimit () {
    return axios.get(API_PATH + 'powerlimit')
      .then(response => response.data)
  }
}

export default new PumpSettingsService()
