declare module 'AddableIngredientDto/Request' {
  import IngredientRequest from 'IngredientDto/Request'

  export interface Create extends IngredientRequest.Create {
    alcoholContent: number;
  }
}

declare module 'AddableIngredientDto/Response' {
  import IngredientResponse from 'IngredientDto/Response'

  export interface Detailed extends IngredientResponse.Detailed {
    alcoholContent: number;
  }

  export type Reduced = IngredientResponse.Reduced
}
