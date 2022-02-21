import * as UserDtoResponse from 'UserDto/Response';

export interface JwtResponseNoUser {
  user?: UserDtoResponse.Detailed;
  accessToken: string;
  tokenExpiration: Date;
  tokenType: string;
}

export default interface JwtResponse extends JwtResponseNoUser {
  user: UserDtoResponse.Detailed;
}
