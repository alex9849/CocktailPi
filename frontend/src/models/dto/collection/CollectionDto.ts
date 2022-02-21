declare module 'CollectionDto/Request' {
  export interface Create {
    name: string;
    description: string;
    completed: boolean;
  }
}

declare module 'CollectionDto/Response' {
  export interface Detailed {
    id: bigint;
    name: string;
    description: string;
    completed: boolean;
    hasImage: boolean;
    size: number;
    lastUpdate: Date;
  }
}
