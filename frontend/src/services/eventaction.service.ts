import axios from 'axios'
import CallUrlEventActionDtoRequest from 'CallUrlEventActionDto/Request'
import CallUrlEventActionDtoResponse from 'CallUrlEventActionDto/Response'
import DoNothingEventActionDtoRequest from 'DoNothingEventActionDto/Request'
import DoNothingEventActionDtoResponse from 'DoNothingEventActionDto/Response'
import EventActionDtoRequest from 'EventActionDto/Request'
import EventActionDtoResponse from 'EventActionDto/Response'
import ExecutePythonEventActionDtoRequest from 'ExecutePythonEventActionDto/Request'
import ExecutePythonEventActionDtoResponse from 'ExecutePythonEventActionDto/Response'
import PlayAudioEventActionDtoRequest from 'PlayAudioEventActionDto/Request'
import PlayAudioEventActionDtoResponse from 'PlayAudioEventActionDto/Response'

const API_PATH = 'api/eventaction/'

class EventActionService {
  getAllEvents () {
    return axios.get<EventActionDtoResponse.Detailed[]>(API_PATH)
      .then(response => response.data)
  }

  createEvent (eventAction: EventActionDtoRequest.Create, file: Blob) {
    const uploadData = new FormData()
    const stringEventAction = JSON.stringify(eventAction)
    const blobEventAction = new Blob([stringEventAction], {
      type: 'application/json'
    })
    uploadData.append('eventAction', blobEventAction)
    if (file) {
      uploadData.append('file', file)
    }
    return axios.post<EventActionDtoResponse.Detailed>(API_PATH, uploadData,
      { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(response => response.data)
  }

  updateEvent (id: bigint, eventAction: EventActionDtoRequest.Create, file: Blob) {
    const uploadData = new FormData()
    const stringEventAction = JSON.stringify(eventAction)
    const blobEventAction = new Blob([stringEventAction], {
      type: 'application/json'
    })
    uploadData.append('eventAction', blobEventAction)
    if (file) {
      uploadData.append('file', file)
    }
    return axios.put<EventActionDtoResponse.Detailed>(API_PATH + String(id), uploadData,
      { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(response => response.data)
  }

  deleteEvent (eventActionId: bigint) {
    return axios.delete(API_PATH + String(eventActionId))
  }

  getAllExecutionGroups () {
    return axios.get<string[]>(API_PATH + 'executiongroup')
      .then(response => response.data)
  }

  killEventAction (processId: bigint) {
    return axios.delete(API_PATH + 'process/' + String(processId))
  }
}

export default new EventActionService()

export class EventActionDtoMapper {

  toEventActionCreateDto(detailed: EventActionDtoResponse.Detailed) {
    if((<DoNothingEventActionDtoResponse.Detailed>detailed)) {
      const dnEventAction = <DoNothingEventActionDtoResponse.Detailed> detailed
      return {
        type: dnEventAction.type,
        comment: dnEventAction.comment,
        executionGroups: dnEventAction.executionGroups,
        trigger: dnEventAction.trigger
      } as DoNothingEventActionDtoRequest.Create
    }
    else if((<CallUrlEventActionDtoResponse.Detailed>detailed)) {
      const cuEventAction = <CallUrlEventActionDtoResponse.Detailed> detailed
      return {
        type: cuEventAction.type,
        comment: cuEventAction.comment,
        trigger: cuEventAction.trigger,
        executionGroups: cuEventAction.executionGroups,
        url: cuEventAction.url,
        requestMethod: cuEventAction.requestMethod
      } as CallUrlEventActionDtoRequest.Create
    }
    else if((<PlayAudioEventActionDtoResponse.Detailed>detailed)) {
      const paEventAction = <PlayAudioEventActionDtoResponse.Detailed> detailed
      return {
        type: paEventAction.type,
        comment: paEventAction.comment,
        trigger: paEventAction.trigger,
        executionGroups: paEventAction.executionGroups,
        onRepeat: paEventAction.onRepeat,
        soundDevice: paEventAction.soundDevice,
        volume: paEventAction.volume
      } as PlayAudioEventActionDtoRequest.Create
    }
    else if((<ExecutePythonEventActionDtoResponse.Detailed>detailed)) {
      const epEventAction = <ExecutePythonEventActionDtoResponse.Detailed> detailed
      return {
        type: epEventAction.type,
        comment: epEventAction.comment,
        trigger: epEventAction.trigger,
        executionGroups: epEventAction.executionGroups
      } as ExecutePythonEventActionDtoRequest.Create
    }
    throw 'Unknown event-action type: ' + detailed.type
  }

}
