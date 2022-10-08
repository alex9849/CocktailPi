import axios from 'axios'

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
    return axios.put(API_PATH + 'settings/reversepumping', settings)
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
}

export default new SystemService()
