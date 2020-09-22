import axios from 'axios';

const API_PATH = 'api/pump/';

class PumpService {

  getAllPumps() {
    return axios.get(API_PATH)
      .then(response => response.data);
  }

}

export default new PumpService();
