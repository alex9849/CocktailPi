<template>
  <c-assistant-container>
    <template v-slot:explanations>
      <p>
        A DC motor can be switched on and off by connecting and disconnecting it from a power source.
        This is usually being done by using a relay. The relay opens and closes the electronic circuit that the motor is connected to.
        The "BCM-Pin"-field holds that BCM-number of the pin that controls that relay.
      </p>
      <p>
        <b>Important:</b> Pin-numbers don't correspond to GPIO numbers, but BCM numbers. BCM refers to the
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
    pinLoading: {}
  }
})
</script>
