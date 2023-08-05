<template>
  <c-assistant-container>
    <template v-slot:explanations>
      <p>
        A DC motor can be switched on and off by connecting it to and disconnecting it from a power source.
        This is usually done with the aid of a relay. The relay opens and closes the electronic circuit to which the motor is connected.
        The "BCM-Pin" field contains the BCM number of the pin that controls the relay.
      </p>
      <p>
        <b>Important:</b> Pin-numbers don't necessarily correspond to GPIO numbers, but BCM numbers. BCM refers to the
        “Broadcom SOC channel” number, which is the numbering inside the chip which is used on the Raspberry
        Pi.
        These numbers changed between board versions. These link may help:
        <a href="https://pi4j.com/getting-started/understanding-the-pins/#overview" target="_blank">Pi4J -
          Understanding the pins</a>
      </p>
    </template>
    <template v-slot:fields>
      <q-input
        :model-value="pin"
        @update:model-value="$emit('update:pin', $event)"
        :error-message="pinErrorMsg"
        :error="!!pinErrorMsg"
        :loading="pinLoading"
        debounce="600"
        outlined
        type="number"
        filled
        label="BCM-Pin"
      >
      </q-input>
    </template>
  </c-assistant-container>
  <q-splitter
    :model-value="10"
    horizontal
    class="q-pb-md"
  />
  <c-assistant-container>
    <template v-slot:explanations>
      <p>
        Depending on our setup the motor might run either if the GPIO-pin that controls the pump is set to high or low.
        Please select the pin-state in which your pump would run in your configuration.
      </p>
    </template>
    <template v-slot:fields>
      <q-select
        :model-value="isPowerStateHigh"
        :options="[{label: 'High', value: true}, {label:'Low', value: false}]"
        map-options
        emit-value
        outlined
        hide-bottom-space
        label="Power State"
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

export default defineComponent({
  name: 'CPumpSetupDcHardwarePins',
  components: { CAssistantContainer },
  props: {
    pin: {},
    pinErrorMsg: {},
    pinLoading: {},
    isPowerStateHigh: {},
    isPowerStateHighErrorMsg: {},
    isPowerStateHighLoading: {}
  }
})
</script>
