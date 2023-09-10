import { Stomp } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import authHeader from './auth-header'
import store from '../store'
import axios from 'axios'

class WebsocketService {
  subscriptions = new Map()
  activeSubscriptions = new Map()
  callbackData = new Map()

  reconnectTasks = []
  stompClient = null
  csrf = null

  constructor () {
    addEventListener('beforeunload', (event) => {
      this.disconnectWebsocket()
    })
  }

  async connectWebsocket () {
    this.stompClient = Stomp.over(() => new SockJS(store().getters['auth/getFormattedServerAddress'] + '/websocket'))
    this.stompClient.connectHeaders = {
      Authorization: authHeader()
    }
    const vm = this
    this.stompClient.onConnect = async function () {
      store().commit('websocket/setReconnectThrottleInSeconds', 5)
      store().commit('websocket/setShowReconnectDialog', false)

      for (const [path, callback] of vm.subscriptions.entries()) {
        const activeSub = vm.stompClient.subscribe(path, callback)
        vm.activeSubscriptions.set(path, activeSub)
      }
      store().commit('websocket/setConnected', true)
    }
    if (!process.env.DEV) {
      this.stompClient.debug = function (str) {}
    }
    for (const id of this.reconnectTasks) {
      clearTimeout(id)
    }
    this.reconnectTasks = []
    this.stompClient.onWebSocketClose = function () {
      vm.stompClient = null
      store().commit('websocket/setConnected', false)
      vm.activeSubscriptions.clear()
      store().commit('websocket/setShowReconnectDialog', true)
      const reconnectThrottle = store().getters['websocket/getReconnectThrottleInSeconds']
      store().commit('websocket/setReconnectThrottleInSeconds',
        vm.reconnectThrottleInSeconds = Math.min(20, reconnectThrottle * 2))
      for (let i = reconnectThrottle; i > 0; i--) {
        vm.reconnectTasks.push(setTimeout(() => {
          store().commit('websocket/setSecondsTillWebsocketReconnect', i)
        }, (reconnectThrottle - i) * 1000))
      }
      vm.reconnectTasks.push(setTimeout(() => {
        vm.connectWebsocket()
      }, reconnectThrottle * 1000))
    }
    try {
      this.stompClient.activate()
    } catch (e) {
      this.stompClient.onWebSocketClose()
    }
  }

  disconnectWebsocket () {
    if (this.stompClient != null) {
      store().commit('websocket/setConnected', false)
      this.stompClient.onWebSocketClose = () => {}
      this.stompClient.deactivate()
      this.stompClient = null
    }
    this.activeSubscriptions.clear()
  }

  subscribe (component, path, callback, getLastMsg = false) {
    if (!this.callbackData.has(path)) {
      this.callbackData.set(path, {
        subscribers: new Map(),
        lastMsg: null
      })
    }
    const callbackDataPath = this.callbackData.get(path)
    callbackDataPath.subscribers.set(component, callback)

    if (getLastMsg && !!callbackDataPath.lastMsg) {
      callback(callbackDataPath.lastMsg)
    }

    if (!this.subscriptions.has(path)) {
      const onMessage = (data) => {
        const callbackDataPath = this.callbackData.get(path)
        callbackDataPath.lastMsg = data
        for (const cb of callbackDataPath.subscribers.values()) {
          cb(data)
        }
      }
      this.subscriptions.set(path, onMessage)
      if (store().getters['websocket/isConnected']) {
        const activeSub = this.stompClient.subscribe(path, onMessage)
        this.activeSubscriptions.set(path, activeSub)
      }
    }
  }

  unsubscribe (component, path) {
    const callbackDataPath = this.callbackData.get(path)
    if (callbackDataPath) {
      callbackDataPath.subscribers.delete(component)

      if (callbackDataPath.subscribers.size !== 0) {
        return
      }
    }
    this.callbackData.delete(path)

    this.subscriptions.delete(path)
    if (!this.activeSubscriptions.has(path)) {
      return
    }
    this.activeSubscriptions.get(path).unsubscribe()
    this.activeSubscriptions.delete(path)
  }
}

export default new WebsocketService()
