declare module 'ExecutePythonEventActionDto/Request' {
  import FileEventActionDtoRequest from 'FileEventActionDto/Request'

  export interface Create extends FileEventActionDtoRequest.Create {
    type: 'execPy'
  }
}

declare module 'ExecutePythonEventActionDto/Response' {
  import FileEventActionDtoResponse from 'FileEventActionDto/Response'

  export interface Detailed extends FileEventActionDtoResponse.Detailed {
    type: 'execPy'
  }
}
