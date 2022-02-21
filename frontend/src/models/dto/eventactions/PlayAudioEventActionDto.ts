declare module 'PlayAudioEventActionDto/Request' {
  import FileEventActionDtoRequest from 'FileEventActionDto/Request'

  export interface Create extends FileEventActionDtoRequest.Create {
    onRepeat: boolean;
    volume: number;
    soundDevice: string;
    type: 'playAudio'
  }
}

declare module 'PlayAudioEventActionDto/Response' {
  import FileEventActionDtoResponse from 'FileEventActionDto/Response'

  export interface Detailed extends FileEventActionDtoResponse.Detailed {
    onRepeat: boolean;
    volume: number;
    soundDevice: string;
    type: 'playAudio'
  }
}
