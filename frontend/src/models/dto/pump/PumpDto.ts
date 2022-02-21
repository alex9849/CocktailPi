declare module 'PumpDto/Request' {
  export interface Create {
    timePerClInMs: number;
    tubeCapacityInMl: number;
    bcmPin: number;
    fillingLevelInMl: number;
    currentIngredientId?: bigint;
  }
}

declare module 'PumpDto/Response' {
  import AutomatedIngredientResponse from 'AutomatedIngredientDto/Response';

  export interface Detailed {
    id: bigint;
    timePerClInMs: number;
    tubeCapacityInMl: number;
    bcmPin: number;
    fillingLevelInMl: number;
    currentIngredient?: AutomatedIngredientResponse.Detailed;
    isCleaning: boolean;
  }
}
