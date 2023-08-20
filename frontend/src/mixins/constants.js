
export const eventActionTriggerDisplayNames = {
  data () {
    return {
      eventActionTriggerDisplayNames: [{
        label: 'Cocktail production started',
        value: 'COCKTAIL_PRODUCTION_STARTED'
      }, {
        label: 'Cocktail production manual interaction requested',
        value: 'COCKTAIL_PRODUCTION_MANUAL_INTERACTION_REQUESTED'
      }, {
        label: 'Cocktail production finished',
        value: 'COCKTAIL_PRODUCTION_FINISHED'
      }, {
        label: 'Cocktail production canceled',
        value: 'COCKTAIL_PRODUCTION_CANCELED'
      }, {
        label: 'Application started',
        value: 'APPLICATION_STARTUP'
      }]
    }
  },
  methods: {
    eventTriggerToDisplayName (eventTrigger) {
      for (const item of this.eventActionTriggerDisplayNames) {
        if (item.value === eventTrigger) {
          return item.label
        }
      }
      return null
    }
  }
}

export const recipeOrderOptions = {
  data: () => {
    return {
      recipeOrderByOptions: [{
        label: 'Name asc',
        value: 'nameAsc'
      }, {
        label: 'Name desc',
        value: 'nameDesc'
      }, {
        label: 'Last update',
        value: 'lastUpdateAsc'
      }, {
        label: 'Least update',
        value: 'lastUpdateDesc'
      }]
    }
  }
}

export const i2cExpanderBoardTypes = {
  data: () => {
    return {
      i2cExpanderBoardTypes: [{
        label: 'MCP23017',
        value: 'MCP23017'
      }]
    }
  }
}
