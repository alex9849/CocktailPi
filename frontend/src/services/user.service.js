import axios from 'axios';

const API_PATH = 'api/user/';

class UserService {
  getAllUsers() {
    return axios.get(API_PATH)
      .then(response => response.data);
  }

  getUser(userId) {
    return axios.get(API_PATH + userId)
      .then(response => response.data);
  }

  deleteUser(user) {
    return axios.delete(API_PATH + user.id);
  }

  updateUser(updateRequest) {
    return axios.put(API_PATH+ updateRequest.userDto.id, updateRequest)
  }
}

export default new UserService();
