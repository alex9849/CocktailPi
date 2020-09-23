export const setCocktailProgress = (state, payload) => {
  state.cocktailProgress = payload;
  state.hasCocktailProgress = !!payload;
};
