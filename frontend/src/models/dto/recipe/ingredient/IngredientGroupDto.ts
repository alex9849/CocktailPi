declare module 'IngredientGroupDto/Request' {
  import IngredientRequest from 'IngredientDto/Request'

  export interface Create extends IngredientRequest.Create {
    type: 'group';
  }
}

declare module 'IngredientGroupDto/Response' {
  import IngredientResponse from 'IngredientDto/Response'

  export interface Detailed extends IngredientResponse.Detailed {
    leafIds: bigint[];
    minAlcoholContent: number;
    maxAlcoholContent: number;
    type: 'group';
  }

  export interface Reduced extends IngredientResponse.Reduced {
    type: 'group';
  }
}
