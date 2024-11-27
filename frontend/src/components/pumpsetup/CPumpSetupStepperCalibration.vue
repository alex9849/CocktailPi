<template>
  <c-assistant-container>
    <template v-slot:explanations>
      <p>
        {{ $t('component.pump_setup_stepper_calibration.acceleration_desc') }}
      </p>
    </template>
    <template v-slot:fields>
      <q-input
        :dark="color.cardBodyDark"
        :model-value="acceleration"
        @update:model-value="$emit('update:acceleration', $event)"
        :error-message="accelerationErrorMsg"
        :error="!!accelerationErrorMsg"
        :loading="accelerationLoading"
        debounce="600"
        outlined
        type="number"
        filled
        :label="$t('component.pump_setup_stepper_calibration.acceleration_label')"
      >
        <template v-slot:append>
          st/s²
        </template>
      </q-input>
    </template>
  </c-assistant-container>
  <q-separator
    :dark="color.cardBodyDark"
    class="q-mb-md"
  />
  <c-assistant-container>
    <template v-slot:explanations>
      <p v-html="$t('component.pump_setup_stepper_calibration.max_steps_per_second_desc')"></p>
    </template>
    <template v-slot:fields>
      <q-input
        :dark="color.cardBodyDark"
        :model-value="maxStepsPerSecond"
        @update:model-value="$emit('update:maxStepsPerSecond', $event)"
        :error-message="maxStepsPerSecondErrorMsg"
        :error="!!maxStepsPerSecondErrorMsg"
        :loading="maxStepsPerSecondLoading"
        debounce="600"
        outlined
        type="number"
        filled
        :label="$t('component.pump_setup_stepper_calibration.max_steps_per_second_label')"
      >
        <template v-slot:append>
          ms
        </template>
      </q-input>
    </template>
  </c-assistant-container>
  <q-separator
    :dark="color.cardBodyDark"
    class="q-mb-md"
  />
  <c-assistant-container>
    <template v-slot:explanations>
      {{ $t('component.pump_setup_stepper_calibration.steps_per_cl_desc') }}
    </template>
    <template v-slot:fields>
      <q-input
        :dark="color.cardBodyDark"
        :model-value="stepsPerCl"
        @update:model-value="$emit('update:stepsPerCl', $event)"
        :error-message="stepsPerClErrorMsg"
        :error="!!stepsPerClErrorMsg"
        :loading="stepsPerClLoading"
        debounce="600"
        outlined
        type="number"
        filled
        :label="$t('component.pump_setup_stepper_calibration.steps_per_cl_label')"
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
import { mapGetters } from 'vuex'

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
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    })
  }
})
</script>
