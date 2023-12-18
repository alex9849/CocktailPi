<template>
  <q-dialog
    :model-value="show"
    :persistent="loading"
    @update:model-value="$emit('update:show', $event)"
  >
    <q-card
      class="full-width"
    >
      <q-card-section>
        <div class="text-center q-mb-lg">
          <h5>{{ $t('component.pump_setup_type_selector.headline') }}</h5>
        </div>
        <div class="row justify-center q-col-gutter-lg">
          <div class="col-5">
            <q-card
              class="clickable"
              @click="onClickAddPump( 'dc')"
              :class="{'disabled': loading}"
            >
              <q-inner-loading :showing="dcLoading" />
              <q-card-section class="text-center">
                <q-icon size="lg" :name="mdiPump"/>
                <p class="text-bold">
                  {{ $t('component.pump_setup_type_selector.dc_pump') }}
                </p>
              </q-card-section>
            </q-card>
          </div>
          <div class="col-5">
            <q-card
              class="clickable"
              @click="onClickAddPump('stepper')"
              :class="{'disabled': loading}"
            >
              <q-inner-loading :showing="stepperLoading" />
              <q-card-section class="text-center">
                <q-icon size="lg">
                  <img src="~assets/icons/stepper-motor.svg" />
                </q-icon>
                <p class="text-bold">
                  {{ $t('component.pump_setup_type_selector.stepper_pump') }}
                </p>
              </q-card-section>
            </q-card>
          </div>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script>
import { defineComponent } from 'vue'
import { mdiProgressClock, mdiPump } from '@quasar/extras/mdi-v5'
import PumpService from 'src/services/pump.service'

export default defineComponent({
  name: 'CPumpSetupTypeSelector',
  props: {
    show: {
      type: Boolean,
      default: false
    }
  },
  created () {
    this.mdiPump = mdiPump
    this.mdiProgressClock = mdiProgressClock
  },
  data: () => {
    return {
      stepperLoading: false,
      dcLoading: false
    }
  },
  methods: {
    onClickAddPump (type) {
      if (this.loading) {
        return
      }
      if (type === 'dc') {
        this.dcLoading = true
      } else {
        this.stepperLoading = true
      }
      const newPump = {
        type
      }
      PumpService.createPump(newPump)
        .then(response => {
          this.$router.push({
            name: 'editpump',
            params: {
              pumpId: response.data.id
            }
          })
        }).finally(() => {
          this.stepperLoading = false
          this.dcLoading = false
        })
    }
  },
  computed: {
    loading () {
      return this.stepperLoading || this.dcLoading
    }
  }
})
</script>

<style scoped>

</style>
