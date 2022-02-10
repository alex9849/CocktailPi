import axios from 'axios'

const API_PATH = 'api/system/'

class SystemService {
  getInstalledPythonLibraries () {
    return axios.get(API_PATH + 'pythonlibraries')
      .then(response => response.data)
  }

  getAudioDevices () {
    return axios.get(API_PATH + 'audiodevices')
      .then(response => response.data)
  }
}

export default new SystemService()
