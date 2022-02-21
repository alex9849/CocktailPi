declare module 'FeasibilityReportDto/Response' {
  import InsufficientIngredientDtoResponse from 'InsufficientIngredientDto/Response'

  export interface Detailed {
    insufficientIngredients: InsufficientIngredientDtoResponse.Detailed;
  }
}

declare module 'InsufficientIngredientDto/Response' {
  import IngredientDtoResponse from 'IngredientDto/Response'

  export interface Detailed {
    ingredient: IngredientDtoResponse.Reduced;
    amountNeeded: number;
    amountRemaining: number;

  }
}

