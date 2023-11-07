
export const eventActionTriggerDisplayNames = {
  data () {
    return {
      eventActionTriggerDisplayNames: [{
        label: this.$t('constants.event_action_trigger_display_names.cocktail_prod_started'),
        value: 'COCKTAIL_PRODUCTION_STARTED'
      }, {
        label: this.$t('constants.event_action_trigger_display_names.cocktail_prod_manual_interaction_requested'),
        value: 'COCKTAIL_PRODUCTION_MANUAL_INTERACTION_REQUESTED'
      }, {
        label: this.$t('constants.event_action_trigger_display_names.cocktail_prod_finished'),
        value: 'COCKTAIL_PRODUCTION_FINISHED'
      }, {
        label: this.$t('constants.event_action_trigger_display_names.cocktail_prod_canceled'),
        value: 'COCKTAIL_PRODUCTION_CANCELED'
      }, {
        label: this.$t('constants.event_action_trigger_display_names.application_started'),
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
        label: this.$t('constants.recipe_order_options.name_asc'),
        value: 'nameAsc'
      }, {
        label: this.$t('constants.recipe_order_options.name_desc'),
        value: 'nameDesc'
      }, {
        label: this.$t('constants.recipe_order_options.last_update_asc'),
        value: 'lastUpdateAsc'
      }, {
        label: this.$t('constants.recipe_order_options.last_update_desc'),
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
