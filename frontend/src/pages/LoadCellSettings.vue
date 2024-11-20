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
                v-model:model-value="v.form.settings.clkPin.$model"
                :error-message="v.form.settings.clkPin.$errors[0]?.$message"
                :error="v.form.settings.clkPin.$errors.length > 0"
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
                v-model:model-value="v.form.settings.dtPin.$model"
                :error-message="v.form.settings.dtPin.$errors[0]?.$message"
                :error="v.form.settings.dtPin.$errors.length > 0"
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
            />
            <q-btn
              label="Save & Return"
              color="green"
              @click="$router.push({name: 'pumpmanagement'})"
            />
          </div>
        </div>
      </q-form>
    </q-card>
    <q-card
      class="q-pa-md bg-card-body text-card-body"
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
            v-model:model-value="calibration.step"
            vertical
          >
            <q-step
              title="Zero-Point Calibration (No Weight)"
              :name="1"
            >
              <p>Ensure the dispensing area is empty. Press the Next button to record the load cell's zero-point measurement.</p>
              <q-stepper-navigation>
                <q-btn
                  @click="calibration.step++"
                  color="primary"
                  label="Next"
                />
              </q-stepper-navigation>
            </q-step>
            <q-step
              title="Known Weight Calibration"
              :name="2"
            >
              <p>
                Place a known weight on the dispensing area.
                Enter the weight value in grams using the input field.
                Press the Next button to record the load cell's response to the known weight.
              </p>
              <q-input
                v-model:model-value="calibration.referenceWeight"
                label="Reference weight (in g)"
                type="number"
                filled
                outlined
              />
              <q-stepper-navigation>
                <q-btn
                  @click="calibration.step++"
                  color="primary"
                  label="Next"
                />
              </q-stepper-navigation>
            </q-step>
            <q-step
              title="Validation Test"
              :name="3"
            >
              <p>
                Place a random weight on the dispensing area.
                Press the Measure button to read the load cell's response to the known weight and compare it with the response.
              </p>
              <q-input
                :model-value="calibration.measureWeight"
                label="Measurement (g)"
                prefix="g"
                outlined
                filled
                readonly
              >
                <template v-slot:append>
                  <q-btn
                    label="Measure"
                    color="primary"
                    outline
                    dense
                    no-caps
                    size="md"
                    :icon="mdiReload"
                  />
                </template>
              </q-input>
              <q-stepper-navigation class="q-gutter-sm">
                <q-btn
                  @click="$router.push({name: 'pumpmanagement'})"
                  color="primary"
                  label="Finnish"
                />
                <q-btn
                  @click="calibration.step = 1"
                  color="primary"
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
  data () {
    return {
      calibration: {
        step: 3,
        referenceWeight: null,
        measureWeight: null
      },
      form: {
        enable: false,
        settings: {
          clkPin: null,
          dtPin: null,
          calibratedWeight: null,
          calibratedValue: null
        }
      }
    }
  },
  validations () {
    const val = {
      form: {
        enable: {
          required
        },
        settings: {
          clkPin: {
            required: requiredIf(() => this.form.enable)
          },
          dtPin: {
            required: requiredIf(() => this.form.enable)
          }
        }
      }
    }
    return val
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    })
  }
}
</script>

<style scoped>

</style>
