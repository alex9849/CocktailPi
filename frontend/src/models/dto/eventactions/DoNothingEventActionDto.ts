declare module 'DoNothingEventActionDto/Request' {
  import EventActionDtoRequest from 'EventActionDto/Request'

  export interface Create extends EventActionDtoRequest.Create {
    type: 'doNothing'
  }
}

declare module 'DoNothingEventActionDto/Response' {
  import EventActionDtoResponse from 'EventActionDto/Response'

  export interface Detailed extends EventActionDtoResponse.Detailed {
    type: 'doNothing'
  }
}
