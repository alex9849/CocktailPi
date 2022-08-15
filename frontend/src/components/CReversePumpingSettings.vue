<template>
  <h5>Reverse pumping</h5>
  <q-card class="q-pa-md"
          style="background-color: #f3f3fa"
  >
    <q-form class="q-col-gutter-md">
      <div class="row">
        <q-card class="col" flat bordered>
          <q-toggle
            label="Enable reverse pumping"
            color="green"
            v-model:model-value="form.enable"
          />
        </q-card>
      </div>
      <div class="row q-col-gutter-md">
        <div class="col-12 col-sm-6"
             :key="i"
             v-for="i in form.directorPins.length"
        >
          <q-card flat
                  bordered>
            <q-card-section>
              <div class="text-subtitle2">Voltage director pin {{ i }}</div>
            </q-card-section>
            <q-separator/>
            <q-card-section class="q-gutter-sm">
              <q-input
                v-model:model-value="form.directorPins[i - 1].bcmPin"
                :disable="!form.enable"
                type="number"
                outlined
                hide-bottom-space
                label="BCM-Pin"
              />
              <q-select
                v-model:model-value="form.directorPins[i - 1].forwardState"
                :options="[{label: 'High', value: true}, {label:'Low', value: false}]"
                :disable="!form.enable"
                map-options
                emit-value
                outlined
                hide-bottom-space
                label="Forward state"
              />
            </q-card-section>
          </q-card>
        </div>
      </div>
      <div class="row">
        <q-card class="col"
                flat
                bordered
        >
          <q-card-section>
            <q-input label="Overshoot"
                     outlined
                     v-model:model-value="form.overshoot"
                     hint="How strongly should number of ml be overshoot on pump back?">
              <template v-slot:append>
                %
              </template>
            </q-input>

          </q-card-section>
        </q-card>
      </div>
      <div class="row">
        <q-card flat
                class="col"
                bordered>
          <q-card-section class="">
            <q-toggle color="green"
                      dense
                      left-label
                      :disable="!form.enable"
                      v-model:model-value="form.automaticPumpBack"
            >
              <div class="text-subtitle2">
                Automatic pump back
              </div>
            </q-toggle>
          </q-card-section>
          <q-separator/>
          <q-card-section class="q-gutter-sm">
            <q-select
              v-model:model-value="form.autoPumpBackTimer"
              :options="autoPumpBackTimerOptions"
              :disable="!form.automaticPumpBack || !form.enable"
              map-options
              emit-value
              outlined
              hide-bottom-space
              label="Inactive time till pump back"
            />
          </q-card-section>
        </q-card>
      </div>
      <div class="row justify-end">
        <q-btn
          label="Save"
          color="green"
        />
      </div>
    </q-form>
  </q-card>
</template>

<script>
export default {
  name: 'CReversePumpingSettings',
  data: () => {
    return {
      form: {
        enable: false,
        overshoot: 0,
        automaticPumpBack: false,
        directorPins: [{
          bcmPin: 0,
          forwardState: false
        }, {
          bcmPin: 0,
          forwardState: false
        }],
        autoPumpBackTimer: 600
      },
      autoPumpBackTimerOptions: [
        { label: '10 Minutes', value: 600 },
        { label: '20 Minutes', value: 1200 },
        { label: '30 Minutes', value: 1800 },
        { label: '60 Minutes', value: 3600 }
      ]
    }
  }
}
</script>

<style scoped>

</style>
