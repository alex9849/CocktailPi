<template>
  <c-assistant-container>
    <template v-slot:explanations>
      <p v-html="$t('component.pump_setup_stepper_hw_pins.pin_desc')"/>
      <q-btn
        class="text-info"
        dense
        flat
        no-caps
        @click="openExternalLink('https://pi4j.com/getting-started/understanding-the-pins/#overview')"
      >
        <u>Pi4J - Understanding the pins</u>
      </q-btn>
    </template>
    <template v-slot:fields>
      <c-gpio-selector
        :model-value="stepPin"
        @update:model-value="$emit('update:stepPin', $event)"
        :error-message="stepPinErrorMsg"
        :error="!!stepPinErrorMsg"
        :loading="stepPinLoading"
        :label="$t('component.pump_setup_stepper_hw_pins.step_pin_label')"
        disallow-expander-pins
        clearable
      />
      <c-gpio-selector
        :model-value="enablePin"
        @update:model-value="$emit('update:enablePin', $event)"
        :error-message="enablePinErrorMsg"
        :error="!!enablePinErrorMsg"
        :loading="enablePinLoading"
        :label="$t('component.pump_setup_stepper_hw_pins.enable_pin_label')"
        clearable
      />
    </template>
  </c-assistant-container>
</template>

<script>
import { defineComponent } from 'vue'
import CAssistantContainer from 'components/CAssistantContainer.vue'
import CGpioSelector from 'components/CGpioSelector.vue'
import { mapMutations } from 'vuex'

export default defineComponent({
  name: 'CPumpSetupStepperHardwarePins',
  components: { CGpioSelector, CAssistantContainer },
  props: {
    enablePin: {},
    enablePinErrorMsg: {},
    enablePinLoading: {},
    stepPin: {},
    stepPinErrorMsg: {},
    stepPinLoading: {}
  },
  methods: {
    ...mapMutations({
      openExternalLink: 'common/openExternalLink'
    })
  }
})
</script>
