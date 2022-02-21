declare module 'AutomatedIngredientDto/Request' {
  import AddableIngredientRequest from 'AddableIngredientDto/Request'

  export interface Create extends AddableIngredientRequest.Create {
    type: 'automated';
    pumpTimeMultiplier: number
  }
}

declare module 'AutomatedIngredientDto/Response' {
  import AddableIngredientRequest from 'AddableIngredientDto/Response'

  export interface Detailed extends AddableIngredientRequest.Detailed {
    type: 'automated';
    pumpTimeMultiplier: number
  }

  export interface Reduced extends AddableIngredientRequest.Reduced {
    type: 'automated';
  }
}
