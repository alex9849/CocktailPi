export const setShowReconnectDialog = (state, payload) => {
  state.showReconnectDialog = payload
}

export const setReconnectThrottleInSeconds = (state, payload) => {
  state.reconnectThrottleInSeconds = payload
}

export const setSecondsTillWebsocketReconnect = (state, payload) => {
  state.secondsTillWebsocketReconnect = payload
}

export const setConnected = (state, payload) => {
  state.isConnected = payload
}
