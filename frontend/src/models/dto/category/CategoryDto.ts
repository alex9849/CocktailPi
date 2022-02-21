declare module 'CategoryDto/Request' {
  export interface Create {
    name: string;
  }
}

declare module 'CategoryDto/Duplex' {
  export interface Detailed {
    id: bigint;
    name: string;
  }
}
