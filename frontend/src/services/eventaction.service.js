import axios from 'axios'

const API_PATH = 'api/eventaction/'

class EventActionService {
  getAllEvents () {
    return axios.get(API_PATH)
      .then(response => response.data)
  }

  createEvent (eventAction, file) {
    const uploadData = new FormData()
    const stringEventAction = JSON.stringify(eventAction)
    const blobEventAction = new Blob([stringEventAction], {
      type: 'application/json'
    })
    uploadData.append('eventAction', blobEventAction)
    if (file) {
      uploadData.append('file', file)
    }
    return axios.post(API_PATH, uploadData, { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(response => response.data)
  }

  updateEvent (eventAction, file) {
    const uploadData = new FormData()
    const stringEventAction = JSON.stringify(eventAction)
    const blobEventAction = new Blob([stringEventAction], {
      type: 'application/json'
    })
    uploadData.append('eventAction', blobEventAction)
    if (file) {
      uploadData.append('file', file)
    }
    return axios.put(API_PATH + eventAction.id, uploadData, { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(response => response.data)
  }

  deleteEvent (eventActionId) {
    return axios.delete(API_PATH + eventActionId)
  }

  getAllExecutionGroups () {
    return axios.get(API_PATH + '/executiongroup')
      .then(response => response.data)
  }

  killEventAction (processId) {
    return axios.delete(API_PATH + '/process/' + processId)
  }
}

export default new EventActionService()
