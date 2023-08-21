<template>
  <c-assistant-container>
    <template v-slot:explanations>
      <p>
        A stepper motor driver usually has three important pins, that are used to control the motor.
      </p>
      <ul>
        <li>
          The step pin, which receives one pulse for each step the motor is to make.
        </li>
        <li>
          The enable pin. This pin decides whether the motor should be energized and thus actively
          hold its current position or not.
        </li>
        <li>
          The direction pin. It decides on the direction that the motor takes. The direction that
          the motor is running to is decided by one single pin, that controls the direction for all motors.
          That pin gets defined globally and is not part of this setup!
          Please build your machine in a way that connects that pin with with the direction logic of all
          your motors.
        </li>
        <li>
          Your motor driver very likely also provides more pins (step resolution/sleep/...). Please
          configure these statically in hardware!
        </li>
      </ul>
      <p><b>Important:</b> For the local board, that belongs to the Raspberry Pi Pin-numbers don't necessarily correspond
        to GPIO numbers, but BCM numbers. BCM refers to the “Broadcom SOC channel” number, which is the numbering inside
        the chip which is used on the Raspberry Pi.
        These numbers changed between board versions. This link may help:
        <q-btn
          class="text-info"
          dense
          flat
          no-caps
          @click="openExternalLink('https://pi4j.com/getting-started/understanding-the-pins/#overview')"
        >
          <u>Pi4J - Understanding the pins</u>
        </q-btn>
      </p>
    </template>
    <template v-slot:fields>
      <c-gpio-selector
        :model-value="stepPin"
        @update:model-value="$emit('update:stepPin', $event)"
        :error-message="stepPinErrorMsg"
        :error="!!stepPinErrorMsg"
        :loading="stepPinLoading"
        label="(local) Step Pin"
        disallow-expander-pins
        clearable
      />
      <c-gpio-selector
        :model-value="enablePin"
        @update:model-value="$emit('update:enablePin', $event)"
        :error-message="enablePinErrorMsg"
        :error="!!enablePinErrorMsg"
        :loading="enablePinLoading"
        label="Enable Pin"
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
