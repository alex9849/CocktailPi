declare module 'AddIngredientsProductionStepDto/Request' {
  import ProductionStepDtoRequest from 'ProductionStepDto/Request'
  import ProductionStepIngredientDtoRequest from 'ProductionStepIngredientDto/Request'

  export interface Create extends ProductionStepDtoRequest.Create {
    type: 'addIngredients';
    stepIngredients: ProductionStepIngredientDtoRequest.Create[];
  }
}

declare module 'AddIngredientsProductionStepDto/Response' {
  import ProductionStepDtoResponse from 'ProductionStepDto/Response'
  import ProductionStepIngredientDtoResponse from 'ProductionStepIngredientDto/Response'

  export interface Detailed extends ProductionStepDtoResponse.Detailed {
    type: 'addIngredients'
    stepIngredients: ProductionStepIngredientDtoResponse.Detailed[];
  }
}
