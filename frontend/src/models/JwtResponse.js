export default class JwtResponse {
  constructor(accessToken, tokenExpiration, user) {
    this.accessToken = accessToken;
    this.tokenExpiration = tokenExpiration;
    this.tokenType = "Bearer";
    this.user = user;
  }

}
