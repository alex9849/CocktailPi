export default class User {
  constructor(id, username, firstname, lastname, locked, email, password, role) {
    this.id = id;
    this.username = username;
    this.firstname = firstname;
    this.lastname = lastname;
    this.locked = locked;
    this.email = email;
    this.password = password;
    this.role = role;
  }
}
