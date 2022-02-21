import axios from 'axios'

const API_PATH = 'api/eventaction/'

class EventActionService {
  getAllEvents () {
    return axios.get(API_PATH)
      .then(response => response.data)
  }

  createEvent (createEventAction, file) {
    const uploadData = new FormData()
    const stringEventAction = JSON.stringify(createEventAction)
    const blobEventAction = new Blob([stringEventAction], {
      type: 'application/json'
    })
    uploadData.append('eventAction', blobEventAction)
    if (file) {
      uploadData.append('file', file)
    }
    return axios.post(API_PATH, uploadData,
      { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(response => response.data)
  }

  updateEvent (id, createEventAction, file) {
    const uploadData = new FormData()
    const stringEventAction = JSON.stringify(createEventAction)
    const blobEventAction = new Blob([stringEventAction], {
      type: 'application/json'
    })
    uploadData.append('eventAction', blobEventAction)
    if (file) {
      uploadData.append('file', file)
    }
    return axios.put(API_PATH + String(id), uploadData,
      { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(response => response.data)
  }

  deleteEvent (eventActionId) {
    return axios.delete(API_PATH + String(eventActionId))
  }

  getAllExecutionGroups () {
    return axios.get(API_PATH + 'executiongroup')
      .then(response => response.data)
  }

  killEventAction (processId) {
    return axios.delete(API_PATH + 'process/' + String(processId))
  }
}

export default new EventActionService()

export class EventActionDtoMapper {

  toEventActionCreateDto(detailed) {
    if(detailed.type === 'doNothing') {
      return {
        type: detailed.type,
        comment: detailed.comment,
        executionGroups: detailed.executionGroups,
        trigger: detailed.trigger
      }
    }
    else if(detailed.type === 'callUrl') {
      return {
        type: detailed.type,
        comment: detailed.comment,
        trigger: detailed.trigger,
        executionGroups: detailed.executionGroups,
        url: detailed.url,
        requestMethod: detailed.requestMethod
      }
    }
    else if(detailed.type === 'playAudio') {
      return {
        type: detailed.type,
        comment: detailed.comment,
        trigger: detailed.trigger,
        executionGroups: detailed.executionGroups,
        onRepeat: detailed.onRepeat,
        soundDevice: detailed.soundDevice,
        volume: detailed.volume
      }
    }
    else if(detailed.type === 'execPy') {
      return {
        type: detailed.type,
        comment: detailed.comment,
        trigger: detailed.trigger,
        executionGroups: detailed.executionGroups
      }
    }
    throw 'Unknown event-action type: ' + detailed.type
  }

}
