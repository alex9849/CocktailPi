import UserRequest from 'UserDto/Request'

export default interface UpdateUserRequest {
  updatePassword: boolean;
  userDto: UserRequest.Create;
}
