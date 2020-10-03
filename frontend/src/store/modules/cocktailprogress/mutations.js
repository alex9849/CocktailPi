export const setCocktailProgress = (state, payload) => {
  state.cocktailProgress = payload;
  state.hasCocktailProgress = !!payload;
};

export const setShowProgressDialog = (status, payload) => {
  status.currentProgressDialog = payload;
};
