<template>
  <q-page class="page-content q-gutter-y-lg" padding>
    <h5>Load cell</h5>
    <q-card
      class="q-pa-md bg-card-body text-card-body"
      flat
      bordered
    >
      <div class="row">
        <p class="text-weight-medium q-pb-md">Load cell settings</p>
      </div>
      <q-form class="q-col-gutter-md">
        <div class="row">
          <q-card
            class="col bg-card-item-group text-card-item-group"
            flat
            bordered
          >
            <q-toggle
              label="Enable load cell"
              color="green"
              v-model:model-value="v.form.enable.$model"
            />
          </q-card>
        </div>
        <div class="row"
             v-if="v.form.enable.$model"
        >
          <q-card class="col bg-card-item-group text-card-item-group"
                  flat
                  bordered>
            <q-card-section>
              <div class="text-subtitle2">
                CLK-Pin
              </div>
            </q-card-section>
            <q-separator/>
            <q-card-section>
              <c-gpio-selector
                :dark="color.cardItemGroupDark"
                v-model:model-value="v.form.clkPin.$model"
                :error-message="v.form.$errors[0]?.$message"
                :error="v.form.$errors.length > 0"
                label="CLK"
                clearable
              />
            </q-card-section>
          </q-card>
        </div>
        <div class="row"
             v-if="v.form.enable.$model"
        >
          <q-card class="col bg-card-item-group text-card-item-group"
                  flat
                  bordered>
            <q-card-section>
              <div class="text-subtitle2">
                DT-Pin
              </div>
            </q-card-section>
            <q-separator/>
            <q-card-section>
              <c-gpio-selector
                disallow-expander-pins
                :dark="color.cardItemGroupDark"
                v-model:model-value="v.form.dtPin.$model"
                :error-message="v.form.dtPin.$errors[0]?.$message"
                :error="v.form.dtPin.$errors.length > 0"
                label="DT"
                clearable
              />
            </q-card-section>
          </q-card>
        </div>
        <div class="row justify-end">
          <div class="q-gutter-sm">
            <q-btn
              label="Save"
              color="green"
              @click="onClickSave()"
            />
            <q-btn
              label="Save & Return"
              color="green"
              @click="onClickSave(true)"
            />
          </div>
        </div>
      </q-form>
    </q-card>
    <q-card
      class="q-pa-md bg-card-body text-card-body"
      :class="{disabled: !currentLoadCell.enable}"
      flat
      bordered
    >
      <div class="row">
        <p class="text-weight-medium q-pb-md">Calibration</p>
      </div>
      <div class="row">
        <q-card
          class="col bg-card-item-group text-card-item-group"
          flat
          bordered
        >
          <q-stepper
            class="bg-card-item-group text-card-item-group"
            v-model:model-value="calibration.step"
            vertical
            header-nav
            :dark="color.cardItemGroupDark"
          >
            <q-step
              title="Zero-Point Calibration (No Weight)"
              :disable="!currentLoadCell.enable"
              :name="1"
              :done="calibration.step > 1 || currentLoadCell.calibrated"
              :header-nav="calibration.step > 1 || currentLoadCell.calibrated"
            >
              <p>Ensure the dispensing area is empty. Press the Next button to record the load cell's zero-point measurement.</p>
              <q-stepper-navigation>
                <q-btn
                  @click="onClickCalibrateZero()"
                  color="primary"
                  label="Next"
                  :disable="!currentLoadCell.enable"
                />
              </q-stepper-navigation>
            </q-step>
            <q-step
              title="Known Weight Calibration"
              :name="2"
              :done="calibration.step > 2 || currentLoadCell.calibrated"
              :header-nav="currentLoadCell.calibrated"
              :disable="!currentLoadCell.enable"
            >
              <p>
                Place a known weight on the dispensing area.
                Enter the weight value in grams using the input field.
                Press the Next button to record the load cell's response to the known weight.
              </p>
              <q-input
                v-model:model-value.number="calibration.referenceWeight"
                :disable="!currentLoadCell.enable"
                label="Reference weight (in g)"
                type="number"
                filled
                :dark="color.cardItemGroupDark"
                outlined
              />
              <q-stepper-navigation>
                <q-btn
                  :disable="calibration.referenceWeight < 1 || calibration.referenceWeight == null || !currentLoadCell.enable"
                  @click="onClickCalibrateReference(calibration.referenceWeight)"
                  color="primary"
                  label="Next"
                />
              </q-stepper-navigation>
            </q-step>
            <q-step
              title="Validation Test"
              :name="3"
              :disable="!currentLoadCell.enable"
              :done="currentLoadCell.calibrated"
              :header-nav="currentLoadCell.calibrated"
            >
              <p>
                Place a random weight on the dispensing area.
                Press the Measure button to read the load cell's response to the known weight and compare it with the response.
              </p>
              <q-input
                :model-value="formattedMeasurementWeight"
                :disable="!currentLoadCell.enable"
                style="padding-inline: 12px"
                square
                filled
                hide-bottom-space
                readonly
                borderless
                label="Measurement"
                :dark="color.cardItemGroupDark"
              >
                <template v-slot:append>
                  <q-btn
                    label="Measure"
                    color="primary"
                    outline
                    no-caps
                    size="md"
                    :icon="mdiReload"
                    :disable="!currentLoadCell.enable"
                    @click="onClickMeasureLoadCell()"
                  />
                </template>
              </q-input>
              <q-stepper-navigation class="q-gutter-sm">
                <q-btn
                  @click="$router.back()"
                  :disable="!currentLoadCell.enable"
                  color="positive"
                  label="Finish & Return"
                />
                <q-btn
                  @click="calibration.step = 1"
                  :disable="!currentLoadCell.enable"
                  color="negative"
                  :icon="mdiReload"
                  label="Start over"
                />
              </q-stepper-navigation>
            </q-step>
          </q-stepper>
        </q-card>
      </div>
    </q-card>
  </q-page>
</template>

<script>
import useVuelidate from '@vuelidate/core'
import { required, requiredIf } from '@vuelidate/validators'
import CGpioSelector from 'components/CGpioSelector.vue'
import { mapGetters } from 'vuex'
import { mdiReload, mdiBullseyeArrow } from '@mdi/js'
import PumpSettingsService from 'src/services/pumpsettings.service'

export default {
  name: 'LoadCellSettings',
  components: { CGpioSelector },
  setup () {
    return {
      mdiBullseyeArrow,
      mdiReload,
      v: useVuelidate()
    }
  },
  created () {
    this.fetchSettings()
  },
  data () {
    return {
      saving: false,
      loading: true,
      calibration: {
        step: 3,
        referenceWeight: null,
        measureWeight: null
      },
      form: {
        enable: false,
        clkPin: null,
        dtPin: null
      },
      currentLoadCell: {
        enable: false,
        clkPin: null,
        dtPin: null,
        calibrated: false
      }
    }
  },
  methods: {
    receiveLoadCellFromBackend (data) {
      this.v.form.$model = Object.assign(this.form, data)
      this.v.form.enable.$model = !!data
      this.currentLoadCell = Object.assign(this.currentLoadCell, data)
      this.currentLoadCell.calibrated = !!(data?.calibrated)
      this.currentLoadCell.enable = !!data
      this.calibration.step = this.currentLoadCell.calibrated ? 3 : 1
    },
    fetchSettings () {
      PumpSettingsService.getLoadCell()
        .then(loadcell => this.receiveLoadCellFromBackend(loadcell))
        .finally(() => {
          this.loading = false
        })
    },
    onClickSave (pushBack = false) {
      this.saving = true
      PumpSettingsService.setLoadCell(this.form.enable ? this.form : null)
        .then(loadcell => {
          this.$q.notify({
            type: 'positive',
            message: this.$t('Loadcell updated')
          })
          if (pushBack) {
            this.$router.back()
          } else {
            this.receiveLoadCellFromBackend(loadcell)
          }
          return loadcell
        })
        .finally(loadcell => {
          this.saving = false
        })
    },
    onClickCalibrateZero () {
      PumpSettingsService.calibrateLoadCellZero()
        .then(loadcell => {
          this.receiveLoadCellFromBackend(loadcell)
          this.calibration.step = 2
        })
    },
    onClickCalibrateReference (knownWeight) {
      PumpSettingsService.calibrateLoadCellRefWeight(knownWeight)
        .then(loadcell => {
          this.receiveLoadCellFromBackend(loadcell)
          this.calibration.step = 3
        })
    },
    onClickMeasureLoadCell () {
      PumpSettingsService.readLoadCell()
        .then(measurement => {
          this.calibration.measureWeight = measurement
        })
    }
  },
  validations () {
    const val = {
      form: {
        enable: {
          required
        },
        clkPin: {
          required: requiredIf(() => this.form.enable)
        },
        dtPin: {
          required: requiredIf(() => this.form.enable)
        }
      }
    }
    return val
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    }),
    formattedMeasurementWeight () {
      if (this.calibration.measureWeight === null) {
        return null
      }
      return this.calibration.measureWeight + ' g'
    }
  }
}
</script>

<style scoped>

</style>
