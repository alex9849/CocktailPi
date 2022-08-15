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
            :model-value="v.form.enable.$model"
            @update:model-value="(e) => {v.form.enable.$model = e; v.form.$validate()}"
          />
        </q-card>
      </div>
      <div class="row q-col-gutter-md"
           v-if="v.form.enable.$model"
      >
        <div class="col-12 col-sm-6"
             :key="index"
             v-for="(dPin, index) in v.form.directorPins.$model"
        >
          <q-card flat
                  bordered>
            <q-card-section>
              <div class="text-subtitle2">Voltage director pin {{ index + 1 }}</div>
            </q-card-section>
            <q-separator/>
            <q-card-section class="q-gutter-sm">
              <q-input
                v-model:model-value="dPin.bcmPin"
                :error-message="v.form.directorPins.$each.$response.$errors[index].bcmPin[0]?.$message"
                :error="v.form.directorPins.$each.$response.$errors[index].bcmPin.length > 0"
                type="number"
                outlined
                hide-bottom-space
                label="BCM-Pin"
              />
              <q-select
                v-model:model-value="dPin.forwardState"
                :options="[{label: 'High', value: true}, {label:'Low', value: false}]"
                :error-message="v.form.directorPins.$each.$response.$errors[index].forwardState[0]?.$message"
                :error="v.form.directorPins.$each.$response.$errors[index].forwardState.length > 0"
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
      <div class="row"
           v-if="v.form.enable.$model"
      >
        <q-card class="col"
                flat
                bordered
        >
          <q-card-section class="q-gutter-md">
            <q-input label="Overshoot"
                     outlined
                     v-model:model-value="v.form.overshoot.$model"
                     :error-message="v.form.overshoot.$errors[0]?.$message"
                     :error="v.form.overshoot.$errors.length > 0"
                     hint="How strongly should number of ml be overshoot on pump back?">
              <template v-slot:append>
                %
              </template>
            </q-input>
            <q-select
              v-model:model-value="v.form.autoPumpBackTimer.$model"
              :options="autoPumpBackTimerOptions"
              map-options
              emit-value
              outlined
              hide-bottom-space
              label="Inactive time till automatic pump back"
            />
          </q-card-section>
        </q-card>
      </div>
      <div class="row justify-end">
        <q-btn
          label="Save"
          :disable="v.form.$error"
          color="green"
        />
      </div>
    </q-form>
  </q-card>
</template>

<script>
import { maxValue, minValue, required, maxLength, minLength, requiredIf, helpers } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'CReversePumpingSettings',
  data: () => {
    return {
      form: {
        enable: false,
        overshoot: 0,
        directorPins: [{
          bcmPin: null,
          forwardState: false
        }, {
          bcmPin: null,
          forwardState: false
        }],
        autoPumpBackTimer: 0
      },
      autoPumpBackTimerOptions: [
        { label: 'Never', value: 0 },
        { label: '10 Minutes', value: 600 },
        { label: '20 Minutes', value: 1200 },
        { label: '30 Minutes', value: 1800 },
        { label: '60 Minutes', value: 3600 }
      ]
    }
  },
  setup () {
    return {
      v: useVuelidate()
    }
  },
  validations () {
    const val = {
      form: {
        enable: {
          required
        },
        overshoot: {
          required: requiredIf(() => this.form.enable)
        },
        directorPins: {
          required: requiredIf(() => this.form.enable),
          minLength: minLength(2),
          maxLength: maxLength(2),
          $each: helpers.forEach({
            bcmPin: {
              required: requiredIf(() => this.form.enable)
            },
            forwardState: {
              required: requiredIf(() => this.form.enable)
            }
          })
        },
        autoPumpBackTimer: {
          required: requiredIf(() => this.form.enable)
        }
      }
    }
    if (this.form.enable) {
      val.form.overshoot.minValue = minValue(0)
      val.form.directorPins.$each = helpers.forEach({
        bcmPin: {
          required: requiredIf(() => this.form.enable),
          minValue: minValue(0),
          maxValue: maxValue(30)
        },
        forwardState: {
          required: requiredIf(() => this.form.enable)
        }
      })
    }
    return val
  }
}
</script>

<style scoped>

</style>
