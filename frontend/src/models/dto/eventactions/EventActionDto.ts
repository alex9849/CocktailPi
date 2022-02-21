declare module 'EventActionDto/Request' {
  import EventActionTrigger from 'EventActionTrigger'

  export interface Create {
    trigger: EventActionTrigger.EventTrigger;
    executionGroups: string[];
    comment: string;
  }
}

declare module 'EventActionDto/Response' {
  import EventActionTrigger from 'EventActionTrigger'

  export interface Detailed {
    id: bigint;
    trigger: EventActionTrigger.EventTrigger;
    executionGroups: string[];
    description: string;
    comment: string;
    type: string;
  }
}

declare module 'EventActionTrigger' {
  enum EventTrigger {
    COCKTAIL_PRODUCTION_STARTED, COCKTAIL_PRODUCTION_MANUAL_INTERACTION_REQUESTED,
    COCKTAIL_PRODUCTION_FINISHED, COCKTAIL_PRODUCTION_CANCELED,
    APPLICATION_STARTUP
  }
}
