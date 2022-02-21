import axios from 'axios'
import UserDtoResponse from 'UserDto/Response'
import UserDtoRequest from 'UserDto/Request'
import UpdateUserRequest from 'src/models/request/UpdateUserRequest';

const API_PATH = 'api/user/'

class UserService {
  getAllUsers () {
    return axios.get<UserDtoResponse.Detailed[]>(API_PATH)
      .then(response => response.data)
  }

  getUser (userId: bigint) {
    return axios.get<UserDtoResponse.Detailed>(API_PATH + String(userId))
      .then(response => response.data)
  }

  getMe () {
    return axios.get<UserDtoResponse.Detailed>(API_PATH + 'current')
      .then(response => response.data)
  }

  updateMe (updateRequest: UpdateUserRequest) {
    return axios.put(API_PATH + 'current', updateRequest)
  }

  deleteUser (userId: bigint) {
    return axios.delete(API_PATH + String(userId))
  }

  updateUser (updateRequest: UpdateUserRequest, id: bigint) {
    return axios.put(API_PATH + String(id), updateRequest)
  }

  createUser (createUser: UserDtoRequest.Create) {
    return axios.post(API_PATH, createUser)
  }
}

export default new UserService()
