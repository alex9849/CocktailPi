import axios from 'axios'
import { pinDtoMapper } from 'src/services/gpio.service'

const API_PATH = 'api/system/'

class SystemService {
  getInstalledPythonLibraries () {
    return axios.get(API_PATH + 'pythonlibraries')
      .then(response => response.data)
  }

  doShutdown (isReboot = false) {
    return axios.put(API_PATH + 'shutdown', null, { params: { isReboot } })
  }

  getAudioDevices () {
    return axios.get(API_PATH + 'audiodevices')
      .then(response => response.data)
  }

  setReversePumpSettings (settings) {
    const dto = {
      enable: settings.enable
    }
    if (settings.enable) {
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

  getAppearanceSettings () {
    return axios.get(API_PATH + 'settings/appearance')
      .then(response => response.data)
  }

  setAppearanceSettings (settings) {
    return axios.put(API_PATH + 'settings/appearance', settings)
      .then(response => response.data)
  }

  getLanguages (settings) {
    return axios.get(API_PATH + 'settings/appearance/language')
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

  getI2cSettings () {
    return axios.get(API_PATH + 'settings/i2c')
      .then(x => x.data)
  }

  setDonated (value) {
    return axios.put(API_PATH + 'settings/donated', value,
      { headers: { 'Content-Type': 'application/json' } })
  }

  setOpenedDonationDisclaimer () {
    return axios.put(API_PATH + 'settings/sawdonationdisclaimer')
  }

  getI2CProbe () {
    return axios.get(API_PATH + 'i2cprobe')
      .then(x => x.data)
  }

  getDefaultFilter () {
    return axios.get(API_PATH + 'settings/defaultfilter')
      .then(x => {
        x = x.data
        if (!x.enable) {
          x.filter = {
            fabricable: ''
          }
        }
        return x
      })
  }

  setDefaultFilter (defaultFilter) {
    return axios.put(API_PATH + 'settings/defaultfilter', defaultFilter)
      .then(x => {
        x = x.data
        if (!x.enable) {
          x.filter = {
            fabricable: ''
          }
        }
        return x
      })
  }

  getVersion () {
    return axios.get(API_PATH + 'version')
      .then(x => x.data)
  }

  getCheckUpdate (onErrorNotify = false) {
    return axios.get(API_PATH + 'checkupdate', { onErrorNotify })
      .then(x => x.data)
  }

  performUpdate () {
    return axios.post(API_PATH + 'performupdate')
  }
}

export default new SystemService()
