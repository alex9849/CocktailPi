<template>
  <c-assistant-container>
    <template v-slot:explanations>
      <p>
        {{ $t('component.pump_setup_dc_calibration.time_per_cl_pin_desc') }}
      </p>
    </template>
    <template v-slot:fields>
      <q-input
        :dark="color.cardBodyDark"
        :model-value="timePerClInMs"
        @update:model-value="$emit('update:timePerClInMs', $event)"
        :error-message="timePerClInMsErrorMsg"
        :error="!!timePerClInMsErrorMsg"
        :loading="timePerClInMsLoading"
        debounce="600"
        outlined
        type="number"
        filled
        :label="$t('component.pump_setup_dc_calibration.time_per_cl_pin_label')"
      >
        <template v-slot:append>
          ms/cl
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
  name: 'CPumpSetupDcCalibration',
  components: { CAssistantContainer },
  props: {
    timePerClInMs: {},
    timePerClInMsErrorMsg: {},
    timePerClInMsLoading: {}
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    })
  }
})
</script>
