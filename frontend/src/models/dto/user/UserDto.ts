declare module 'UserDto/Request' {
  export interface Create {
    username: string;
    firstname: string;
    lastname: string;
    isAccountNonLocked: boolean;
    email: string;
    password: string;
    adminLevel: number;
  }
}

declare module 'UserDto/Response' {
  export interface Detailed {
    id: bigint;
    username: string;
    firstname: string;
    lastname: string;
    isAccountNonLocked: boolean;
    email: string;
    adminLevel: number;
  }
}
