declare module 'ManualIngredientDto/Request' {
  import AddableIngredientRequest from 'AddableIngredientDto/Request'

  export interface Create extends AddableIngredientRequest.Create {
    type: 'manual';
    unit: string;
  }
}

declare module 'ManualIngredientDto/Response' {
  import AddableIngredientRequest from 'AddableIngredientDto/Response'

  export interface Detailed extends AddableIngredientRequest.Detailed {
    type: 'manual';
  }

  export interface Reduced extends AddableIngredientRequest.Reduced {
    type: 'manual';
  }
}
