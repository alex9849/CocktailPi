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
      <p><b>Important:</b> Pin-numbers don't correspond to GPIO numbers, but BCM numbers. BCM refers to the
        “Broadcom SOC channel” number, which is the numbering inside the chip which is used on the Raspberry
        Pi.
        These numbers changed between board versions. These link may help:
        <a href="https://pi4j.com/getting-started/understanding-the-pins/#overview" target="_blank">Pi4J -
          Understanding the pins</a>
      </p>
    </template>
    <template v-slot:fields>
      <q-input
        :model-value="stepPin"
        @update:model-value="$emit('update:stepPin', $event)"
        :error-message="stepPinErrorMsg"
        :error="!!stepPinErrorMsg"
        :loading="stepPinLoading"
        debounce="600"
        outlined
        type="number"
        filled
        label="Step BCM-Pin"
      />
      <q-input
        :model-value="enablePin"
        @update:model-value="$emit('update:enablePin', $event)"
        :error-message="enablePinErrorMsg"
        :error="!!enablePinErrorMsg"
        :loading="enablePinLoading"
        debounce="600"
        outlined
        type="number"
        filled
        label="Enable BCM-Pin"
      >
      </q-input>
    </template>
  </c-assistant-container>
</template>

<script>
import { defineComponent } from 'vue'
import CAssistantContainer from 'components/CAssistantContainer.vue'

export default defineComponent({
  name: 'CPumpSetupStepperHardwarePins',
  components: { CAssistantContainer },
  props: {
    enablePin: {},
    enablePinErrorMsg: {},
    enablePinLoading: {},
    stepPin: {},
    stepPinErrorMsg: {},
    stepPinLoading: {}
  }
})
</script>
