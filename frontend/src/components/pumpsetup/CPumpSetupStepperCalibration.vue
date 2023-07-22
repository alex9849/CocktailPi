<template>
  <c-assistant-container>
    <template v-slot:explanations>
      <p>
        The acceleration field determines how fast your motor should speed up or break down.
        If the acceleration is too high, the motor might skip steps on speed up or do too many on break
        down.
        The acceleration is given in steps per second per second.
      </p>
    </template>
    <template v-slot:fields>
      <q-input
        :model-value="acceleration"
        @update:model-value="$emit('update:acceleration', $event)"
        :error-message="accelerationErrorMsg"
        :error="!!accelerationErrorMsg"
        :loading="accelerationLoading"
        debounce="600"
        outlined
        type="number"
        filled
        label="Acceleration"
      >
        <template v-slot:append>
          st/s²
        </template>
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
        The "max steps per second"-field determines fast the motor should spin at max.
        One revolution is normally divided into 200 steps. This can vary depending on the motor and motor
        driver settings.
        If it is too high, the motor might not be able to keep up and skips steps or even doesn't run at
        all.
        If it is too low, the motor will run slower than necessary.<br>
      </p>
      <p>
        The rule is:
      </p>
      <ul>
        <li>higher = faster motor</li>
        <li>lower = slower motor</li>
      </ul>
    </template>
    <template v-slot:fields>
      <q-input
        :model-value="maxStepsPerSecond"
        @update:model-value="$emit('update:maxStepsPerSecond', $event)"
        :error-message="maxStepsPerSecondErrorMsg"
        :error="!!maxStepsPerSecondErrorMsg"
        :loading="maxStepsPerSecondLoading"
        debounce="600"
        outlined
        type="number"
        filled
        label="Max steps per second"
      >
        <template v-slot:append>
          ms
        </template>
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
      This field determines how many steps the motor must make to produce one cl.
    </template>
    <template v-slot:fields>
      <q-input
        :model-value="stepsPerCl"
        @update:model-value="$emit('update:stepsPerCl', $event)"
        :error-message="stepsPerClErrorMsg"
        :error="!!stepsPerClErrorMsg"
        :loading="stepsPerClLoading"
        debounce="600"
        outlined
        type="number"
        filled
        label="Steps per cl"
      >
        <template v-slot:append>
          st/cl
        </template>
      </q-input>
    </template>
  </c-assistant-container>
</template>

<script>
import { defineComponent } from 'vue'
import CAssistantContainer from 'components/CAssistantContainer.vue'

export default defineComponent({
  name: 'CPumpSetupStepperCalibration',
  components: { CAssistantContainer },
  props: {
    acceleration: {},
    accelerationErrorMsg: {},
    accelerationLoading: {},
    maxStepsPerSecond: {},
    maxStepsPerSecondErrorMsg: {},
    maxStepsPerSecondLoading: {},
    stepsPerCl: {},
    stepsPerClErrorMsg: {},
    stepsPerClLoading: {}
  }
})
</script>
