export default class User {
  constructor(id, username, firstname, lastname, nonLocked, email, password, roles) {
    this.id = id;
    this.username = username;
    this.firstname = firstname;
    this.lastname = lastname;
    this.accountNonLocked = nonLocked;
    this.email = email;
    this.password = password;
    this.roles = roles;
  }
}
