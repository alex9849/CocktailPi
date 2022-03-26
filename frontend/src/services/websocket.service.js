import { Stomp } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import authHeader from './auth-header'
import store from '../store'

class WebsocketService {
  reconnectTasks = []
  subscriptions = []
  activeSubscriptions = new Map()
  stompClient = null

  connectWebsocket () {
    this.stompClient = Stomp.over(() => new SockJS(store().getters['auth/getFormattedServerAddress'] + '/websocket'))
    this.stompClient.connectHeaders = {
      Authorization: authHeader()
    }
    const vm = this
    this.stompClient.onConnect = function () {
      store().commit('websocket/setReconnectThrottleInSeconds', 5)
      store().commit('websocket/setShowReconnectDialog', false)

      for (const subscription of vm.subscriptions) {
        const activeSub = vm.stompClient.subscribe(subscription.path, subscription.callback)
        vm.activeSubscriptions.set(subscription.path, activeSub)
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
    this.stompClient.activate()
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

  subscribe (path, callback) {
    if (this.activeSubscriptions.has(path)) {
      this.unsubscribe(path)
    }
    this.subscriptions.push({ path, callback })
    if (store().getters['websocket/isConnected']) {
      const activeSub = this.stompClient.subscribe(path, callback)
      this.activeSubscriptions.set(path, activeSub)
    }
  }

  unsubscribe (path) {
    const subscription = this.subscriptions.find(x => x.path === path)
    if (!subscription) {
      return false
    }
    const index = this.subscriptions.indexOf(subscription)
    this.subscriptions.splice(index, 1)
    if (!this.activeSubscriptions.has(path)) {
      return false
    }
    this.activeSubscriptions.get(path).unsubscribe()
    this.activeSubscriptions.delete(path)
    return true
  }
}

export default new WebsocketService()
