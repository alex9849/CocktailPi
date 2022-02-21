declare module 'FileEventActionDto/Request' {
  import EventActionDtoRequest from 'EventActionDto/Request'

  export type Create = EventActionDtoRequest.Create
}

declare module 'FileEventActionDto/Response' {
  import EventActionDtoResponse from 'EventActionDto/Response'

  export interface Detailed extends EventActionDtoResponse.Detailed {
    fileName: string
  }
}
