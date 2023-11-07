<template>
  <c-assistant-container>
    <template v-slot:explanations>
      <p v-html="$t('component.pump_setup_dc_hw_pins.control_pin_desc')"/>
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
        :model-value="pin"
        @update:model-value="$emit('update:pin', $event)"
        :error-message="pinErrorMsg"
        :error="!!pinErrorMsg"
        :loading="pinLoading"
        :label="$t('component.pump_setup_dc_hw_pins.control_pin_label')"
        clearable
      />
    </template>
  </c-assistant-container>
  <q-separator
    class="q-my-md"
  />
  <c-assistant-container>
    <template v-slot:explanations>
      <p v-html="$t('component.pump_setup_dc_hw_pins.power_state_desc')"/>
    </template>
    <template v-slot:fields>
      <q-select
        :model-value="isPowerStateHigh"
        :options="powerStateOptions"
        map-options
        emit-value
        outlined
        clearable
        hide-bottom-space
        :label="$t('component.pump_setup_dc_hw_pins.power_state_label')"
        @update:model-value="$emit('update:isPowerStateHigh', $event)"
        :error-message="isPowerStateHighErrorMsg"
        :error="!!isPowerStateHighErrorMsg"
        :loading="isPowerStateHighLoading"
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
  name: 'CPumpSetupDcHardwarePins',
  components: { CGpioSelector, CAssistantContainer },
  props: {
    pin: {},
    pinErrorMsg: {},
    pinLoading: {},
    isPowerStateHigh: {},
    isPowerStateHighErrorMsg: {},
    isPowerStateHighLoading: {}
  },
  methods: {
    ...mapMutations({
      openExternalLink: 'common/openExternalLink'
    })
  },
  data () {
    return {
      powerStateOptions: [{
        label: this.$t('component.pump_setup_dc_hw_pins.power_state_options.high'),
        value: true
      }, {
        label: this.$t('component.pump_setup_dc_hw_pins.power_state_options.low'),
        value: false
      }]
    }
  }
})
</script>
