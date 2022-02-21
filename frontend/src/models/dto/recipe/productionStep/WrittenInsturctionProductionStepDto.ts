declare module 'WrittenInstructionProductionStepDto/Request' {
  import ProductionStepDtoRequest from 'ProductionStepDto/Request'

  export interface Create extends ProductionStepDtoRequest.Create {
    type: 'writtenInstruction';
    message: string;
  }
}

declare module 'WrittenInstructionProductionStepDto/Response' {
  import ProductionStepDtoResponse from 'ProductionStepDto/Response'

  export interface Detailed extends ProductionStepDtoResponse.Detailed {
    type: 'writtenInstruction';
    message: string;
  }
}
