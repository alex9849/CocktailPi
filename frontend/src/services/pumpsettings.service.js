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
        autoPumpBackTimer: settings.settings.autoPumpBackTimer
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
}

export default new PumpSettingsService()
