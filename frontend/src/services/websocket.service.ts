import {CompatClient, messageCallbackType, Stomp, StompSubscription} from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import authHeader from 'src/services/auth-header'
import store from '../store'

class WebsocketService {
  reconnectTasks = [] as NodeJS.Timeout[]
  subscriptions = [] as {path: string, callback: messageCallbackType}[]
  activeSubscriptions = new Map<string, StompSubscription>()
  stompClient: CompatClient | null = null

  connectWebsocket () {
    this.stompClient = Stomp.over(() => new SockJS(store().getters.auth.getFormattedServerAddress + '/ws'))
    this.stompClient.connectHeaders = {
      Authorization: authHeader()
    }
    this.stompClient.onConnect = () => {
      store().commit.websocket.setReconnectThrottleInSeconds(5)
      store().commit.websocket.setShowReconnectDialog(false)

      if(!this.stompClient) {
        throw 'stompClient not defined!'
      }
      for (const subscription of this.subscriptions) {
        const activeSub = this.stompClient.subscribe(subscription.path, subscription.callback)
        this.activeSubscriptions.set(subscription.path, activeSub)
      }
      store().commit.websocket.setConnected(true)
    }
    if (!process.env.DEV) {
      this.stompClient.debug = function (str) {
        //
      }
    }
    for (const id of this.reconnectTasks) {
      clearTimeout(id)
    }
    this.reconnectTasks = []
    this.stompClient.onWebSocketClose = () => {
      this.stompClient = null
      store().commit.websocket.setConnected(false)
      this.activeSubscriptions.clear()
      store().commit.websocket.setShowReconnectDialog(true)
      const reconnectThrottle = store().getters.websocket.getReconnectThrottleInSeconds
      store().commit.websocket.setReconnectThrottleInSeconds(Math.min(20, reconnectThrottle * 2))

      for (let i = reconnectThrottle; i > 0; i--) {
        this.reconnectTasks.push(setTimeout(() => {
          store().commit.websocket.setSecondsTillWebsocketReconnect(i)
        }, (reconnectThrottle - i) * 1000))
      }
      this.reconnectTasks.push(setTimeout(() => {
        this.connectWebsocket()
      }, reconnectThrottle * 1000))
    }
    this.stompClient.activate()
  }

  disconnectWebsocket () {
    if (this.stompClient != null) {
      store().commit.websocket.setConnected(false)
      this.stompClient.onWebSocketClose = () => {
        //
      }
      void this.stompClient.deactivate()
      this.stompClient = null
    }
    this.activeSubscriptions.clear()
  }

  subscribe (path: string, callback: messageCallbackType) {
    if (this.activeSubscriptions.has(path)) {
      this.unsubscribe(path)
    }
    this.subscriptions.push({ path, callback })
    if (store().getters.websocket.isConnected && this.stompClient) {
      const activeSub = this.stompClient.subscribe(path, callback)
      this.activeSubscriptions.set(path, activeSub)
    }
  }

  unsubscribe (path: string) {
    const subscription = this.subscriptions.find(x => x.path === path)
    if (!subscription) {
      return false
    }
    const index = this.subscriptions.indexOf(subscription)
    this.subscriptions.splice(index, 1)
    if (!this.activeSubscriptions.has(path)) {
      return false
    }
    this.activeSubscriptions.get(path)?.unsubscribe()
    this.activeSubscriptions.delete(path)
    return true
  }
}

export default new WebsocketService()
