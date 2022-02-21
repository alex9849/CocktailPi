declare module 'CocktailProgressDto/Response' {
  import RecipeDtoResponse from 'RecipeDto/Response'
  import ProductionStepIngredientDtoResponse from 'ProductionStepIngredientDto/Response'
  import CocktailProgressState from 'CocktailProgressState'

  export interface Detailed {
    recipe: RecipeDtoResponse.SearchResult;
    progress: number;
    userId: bigint;
    state: CocktailProgressState.State;
    currentIngredientsToAddManually: ProductionStepIngredientDtoResponse.Detailed[];
    writtenInstruction: string;
  }
}

declare module 'CocktailProgressState' {
  export enum State {
    RUNNING, MANUAL_INGREDIENT_ADD, MANUAL_ACTION_REQUIRED, CANCELLED, FINISHED, READY_TO_START
  }
}
