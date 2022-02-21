declare module 'CallUrlEventActionDto/Request' {
  import EventActionDtoRequest from 'EventActionDto/Request'

  export interface Create extends EventActionDtoRequest.Create {
    requestMethod: string;
    url: string;
    type: 'callUrl'
  }
}

declare module 'CallUrlEventActionDto/Response' {
  import EventActionDtoResponse from 'EventActionDto/Response'

  export interface Detailed extends EventActionDtoResponse.Detailed {
    requestMethod: string;
    url: string;
    type: 'callUrl'
  }
}
