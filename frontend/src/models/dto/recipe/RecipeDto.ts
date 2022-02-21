declare module 'RecipeDto/Request' {
  import ProductionStepDtoRequest from 'ProductionStepDto/Request'

  export interface Create {
    name: string;
    ownerId: bigint;
    description: string;
    productionSteps: ProductionStepDtoRequest.Create[];
    categoryIds: bigint[];
    defaultAmountToFill: bigint;
  }
}

declare module 'RecipeDto/Response' {
  import IngredientResponse from 'IngredientDto/Response'
  import CategoryDtoDuplex from 'CategoryDto/Duplex'
  import ProductionStepDtoResponse from 'ProductionStepDto/Response'

  export interface Detailed {
    id: bigint;
    name: string;
    ownerId: bigint;
    description: string;
    productionSteps: ProductionStepDtoResponse.Detailed[];
    categories: CategoryDtoDuplex.Detailed[];
    hasImage: boolean;
    defaultAmountToFill: bigint;
    lastUpdate: Date;
  }

  export interface SearchResult {
    id: bigint;
    name: string;
    ownerName: string;
    description: string;
    hasImage: boolean;
    ingredients: IngredientResponse.Reduced;
    lastUpdate: Date;
  }
}
