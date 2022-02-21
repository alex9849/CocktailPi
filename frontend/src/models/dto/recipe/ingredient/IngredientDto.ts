declare module 'IngredientDto/Request' {
  export interface Create {
    name: string;
    parentGroupId?: bigint;
    type: string;
  }
}

declare module 'IngredientDto/Response' {
  export interface Detailed {
    id: bigint;
    name: string;
    parentGroupId?: bigint;
    parentGroupName?: string;
    type: string;
    unit: string;
    inBar: boolean;
    onPump: boolean;
  }

  export interface Reduced {
    id: bigint;
    name: string;
    type: string;
    inBar: boolean;
    onPump: boolean;
  }
}
