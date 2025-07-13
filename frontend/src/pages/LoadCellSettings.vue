<template>
  <q-page class="page-content q-gutter-y-lg" padding>
    <h5>{{ $t('page.load_cell_mgmt.headline') }}</h5>
    <q-card
      class="q-pa-md bg-card-body text-card-body"
      flat
      bordered
    >
      <div class="row">
        <p class="text-weight-medium q-pb-md">{{ $t('page.load_cell_mgmt.hardware_settings.headline') }}</p>
      </div>
      <q-form class="q-col-gutter-md">
        <div class="row">
          <q-card
            class="col bg-card-item-group text-card-item-group"
            flat
            bordered
          >
            <q-toggle
              :label="$t('page.load_cell_mgmt.hardware_settings.enable_btn_label')"
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
                {{ $t('page.load_cell_mgmt.hardware_settings.clk_pin_label') }}
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
                {{ $t('page.load_cell_mgmt.hardware_settings.dt_pin_label') }}
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
        <div class="row"
             v-if="v.form.enable.$model"
        >
          <q-card
            class="col bg-card-item-group text-card-item-group"
            flat
            bordered
          >
            <q-card-section>
              <div class="text-subtitle2">
                Cocktail production
              </div>
            </q-card-section>
            <q-separator/>
            <q-card-section>
              <div>
                <q-toggle
                  label="Check if glass placed in dispensing area"
                  v-model:model-value="v.form.dispensingArea.checkGlassPlaced.$model"
                />
              </div>
              <div>
                <q-toggle
                  label="Use measured weight to detect glass type"
                  v-model:model-value="v.form.dispensingArea.matchGlass.$model"
                />
              </div>
            </q-card-section>
          </q-card>
        </div>
        <div class="row justify-end">
          <div class="q-gutter-sm">
            <q-btn
              :label="$t('page.load_cell_mgmt.hardware_settings.save_btn_label')"
              color="green"
              :disable="v.form.$invalid"
              @click="onClickSave()"
            />
            <q-btn
              :label="$t('page.load_cell_mgmt.hardware_settings.save_and_return_btn_label')"
              color="green"
              :disable="v.form.$invalid"
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
        <p class="text-weight-medium q-pb-md">{{ $t('page.load_cell_mgmt.calibration.headline') }}</p>
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
              :title="$t('page.load_cell_mgmt.calibration.zero_point.headline')"
              :disable="!currentLoadCell.enable"
              :name="1"
              :done="calibration.step > 1 || currentLoadCell.calibrated"
              :header-nav="(calibration.step > 1 || currentLoadCell.calibrated) && !loadingCalibration"
            >
              <p>{{ $t('page.load_cell_mgmt.calibration.zero_point.text') }}</p>
              <q-stepper-navigation>
                <q-btn
                  :loading="loadingCalibration"
                  @click="onClickCalibrateZero()"
                  color="primary"
                  :label="$t('page.load_cell_mgmt.calibration.next_btn_label')"
                  :disable="!currentLoadCell.enable"
                />
              </q-stepper-navigation>
            </q-step>
            <q-step
              :title="$t('page.load_cell_mgmt.calibration.known_weight.headline')"
              :name="2"
              :done="calibration.step > 2 || currentLoadCell.calibrated"
              :header-nav="currentLoadCell.calibrated && !loadingCalibration"
              :disable="!currentLoadCell.enable"
            >
              <p>
                {{ $t('page.load_cell_mgmt.calibration.known_weight.text') }}
              </p>
              <q-input
                v-model:model-value.number="calibration.referenceWeight"
                :disable="!currentLoadCell.enable || loadingCalibration"
                :label="$t('page.load_cell_mgmt.calibration.known_weight.ref_weight_field_label')"
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
                  :loading="loadingCalibration"
                  :label="$t('page.load_cell_mgmt.calibration.next_btn_label')"
                />
              </q-stepper-navigation>
            </q-step>
            <q-step
              :title="$t('page.load_cell_mgmt.calibration.validation.headline')"
              :name="3"
              :disable="!currentLoadCell.enable"
              :done="currentLoadCell.calibrated"
              :header-nav="currentLoadCell.calibrated && !loadingCalibration"
            >
              <p>
                {{ $t('page.load_cell_mgmt.calibration.validation.text') }}
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
                :label="$t('page.load_cell_mgmt.calibration.validation.measure_field_label')"
                :dark="color.cardItemGroupDark"
              >
                <template v-slot:append>
                  <q-btn
                    :label="$t('page.load_cell_mgmt.calibration.validation.measure_btn_label')"
                    color="primary"
                    outline
                    no-caps
                    size="md"
                    :loading="loadingMeasure"
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
                  :label="$t('page.load_cell_mgmt.calibration.validation.finish_and_return_btn_label')"
                />
                <q-btn
                  @click="calibration.step = 1"
                  :disable="!currentLoadCell.enable"
                  color="negative"
                  :icon="mdiReload"
                  :label="$t('page.load_cell_mgmt.calibration.validation.start_over_btn_label')"
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
      loadingMeasure: false,
      loadingCalibration: false,
      calibration: {
        step: 3,
        referenceWeight: null,
        measureWeight: null
      },
      form: {
        enable: false,
        clkPin: null,
        dtPin: null,
        dispensingArea: {
          checkGlassPlaced: false,
          matchGlass: false
        }
      },
      currentLoadCell: {
        enable: false,
        clkPin: null,
        dtPin: null,
        calibrated: false,
        dispensingArea: {
          checkGlassPlaced: false,
          matchGlass: false
        }
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
            message: this.$t('page.load_cell_mgmt.hardware_settings.update_success_message')
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
      this.loadingCalibration = true
      PumpSettingsService.calibrateLoadCellZero()
        .then(loadcell => {
          this.receiveLoadCellFromBackend(loadcell)
          this.calibration.step = 2
        })
        .finally(() => {
          this.loadingCalibration = false
        })
    },
    onClickCalibrateReference (knownWeight) {
      this.loadingCalibration = true
      PumpSettingsService.calibrateLoadCellRefWeight(knownWeight)
        .then(loadcell => {
          this.receiveLoadCellFromBackend(loadcell)
          this.calibration.step = 3
        })
        .finally(() => {
          this.loadingCalibration = false
        })
    },
    onClickMeasureLoadCell () {
      this.loadingMeasure = true
      PumpSettingsService.readLoadCell()
        .then(measurement => {
          this.calibration.measureWeight = measurement
        })
        .finally(() => {
          this.loadingMeasure = false
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
        },
        dispensingArea: {
          checkGlassPlaced: {},
          matchGlass: {}
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
