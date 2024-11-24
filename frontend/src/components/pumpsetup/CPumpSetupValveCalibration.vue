<template>
  <c-assistant-container>
    <template v-slot:explanations>
      <p>
        A valve uses the integrated load cell to measure the dispensed amount of liquid.
        Once a load cell has been set up no further action is required to fine adjust the dispensing unit.
        You can still test the accuracy of your setup here.
      </p>
    </template>
    <template v-slot:fields>
      <div
        class="q-gutter-y-md"
      >
        <div class="row justify-center">
          <q-badge
            :color="hasLoadCellCalibrated ? 'positive' : 'warning'"
            style="font-size: 14px"
          >
            <q-icon :name="hasLoadCellCalibrated ? mdiCheck : mdiAlertOutline" size="lg" />
            <p v-if="!hasLoadCell">Load cell has <b>not</b> been set up, and <b>not</b> been calibrated!</p>
            <p v-else-if="!hasLoadCellCalibrated">Load cell has been set up, but <b>not</b> been calibrated!</p>
            <p v-else>Load cell has been set up and calibrated!</p>
          </q-badge>
        </div>
        <div class="row justify-center">
          <q-btn
            color="positive"
            label="Setup load cell"
            :icon="mdiCogs"
            @click="$router.push({name: 'loadcellsettings'})"
          />
        </div>
      </div>
    </template>
  </c-assistant-container>
</template>

<script>
import { defineComponent } from 'vue'
import CAssistantContainer from 'components/CAssistantContainer.vue'
import { mapGetters } from 'vuex'
import { mdiCogs, mdiCheck, mdiAlertOutline } from '@quasar/extras/mdi-v4'

export default defineComponent({
  name: 'CPumpSetupValveCalibration',
  components: { CAssistantContainer },
  props: {
    hasLoadCell: {
      type: Boolean,
      required: true
    },
    hasLoadCellCalibrated: {
      type: Boolean,
      required: true
    }
  },
  setup () {
    return {
      mdiCogs, mdiCheck, mdiAlertOutline
    }
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    })
  }
})
</script>
