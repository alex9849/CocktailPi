declare module 'ProductionStepIngredientDto/Request' {
  export interface Create {
    ingredientId: bigint;
    amount: number;
    scale: boolean;
  }
}

declare module 'ProductionStepIngredientDto/Response' {
  import IngredientDtoResponse from 'IngredientDto/Response'

  export interface Detailed {
    ingredient: IngredientDtoResponse.Detailed;
    amount: number;
    scale: boolean;
  }
}
