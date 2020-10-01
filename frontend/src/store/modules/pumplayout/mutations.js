export const setLayout = (state, payload) => {
  state.pumpLayout.splice(0, state.pumpLayout.length, payload)
  state.pumpLayout = payload;
};
