import axios from 'axios'

const API_PATH = 'api/event/'

class EventService {
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
}

export default new EventService()
