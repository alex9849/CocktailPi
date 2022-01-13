import axios from 'axios'

const API_PATH = 'api/eventaction/'

class EventActionService {
  getAllEvents () {
    return axios.get(API_PATH)
      .then(response => response.data)
  }

  createEvent (event) {
    return axios.post(API_PATH, event)
  }

  updateEvent (event) {
    return axios.put(API_PATH + event.id, event)
  }

  deleteEvent (event) {
    return axios.delete(API_PATH + event.id)
  }

  getAllExecutionGroups () {
    return axios.get(API_PATH + '/executiongroup')
      .then(response => response.data)
  }
}

export default new EventActionService()
