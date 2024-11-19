<template>
  <q-page class="page-content" padding>
    <h5>Load cell</h5>
    <q-card
      class="q-pa-md bg-card-body text-card-body"
      flat
      bordered
    >
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
          <q-btn
            label="Save"
            color="green"
          />
        </div>
      </q-form>
    </q-card>
  </q-page>
</template>

<script>
import useVuelidate from '@vuelidate/core'
import { required, requiredIf } from '@vuelidate/validators'
import CGpioSelector from 'components/CGpioSelector.vue'
import { mapGetters } from 'vuex'

export default {
  name: 'LoadCellSettings',
  components: { CGpioSelector },
  setup () {
    return {
      v: useVuelidate()
    }
  },
  data () {
    return {
      form: {
        enable: false,
        settings: {
          clkPin: null,
          dtPin: null
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
