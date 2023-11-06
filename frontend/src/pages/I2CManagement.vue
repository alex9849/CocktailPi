<template>
  <q-page class="page-content" padding>
    <h5>{{ $t('page.i2c_mgmt.headline') }}</h5>
    <q-card
      flat
      bordered
      class="q-pa-md bg-card-primary"
    >
      <q-form
        class="q-col-gutter-md"
        @submit.prevent="submit"
      >
        <div
          class="col-12"
        >
          <q-card
            flat
            bordered
          >
            <q-toggle
              v-model:model-value="v.config.enable.$model"
              :disable="submitting"
              :label="$t('page.i2c_mgmt.form.enable_label')"
              color="positive"
            />
          </q-card>
        </div>
        <div
          class="col-12"
          v-if="config.enable"
        >
          <q-card
            flat
            bordered
            class="q-pa-md"
          >
            <c-assistant-container>
              <template v-slot:explanations>
                <p v-html="$t('page.i2c_mgmt.tutorial')"/>
                <q-card flat bordered style="border-color: #f12d36; border-width: 2px" class="q-pa-sm">
                  <p v-html="$t('page.i2c_mgmt.configuration_warning')"/>
                </q-card>
              </template>
              <template v-slot:fields>
                <c-gpio-selector
                  disallow-expander-pins
                  v-model:model-value="v.config.sdaPin.$model"
                  :error-message="v.config.sdaPin.$errors[0]?.$message"
                  :error="v.config.sdaPin.$errors.length > 0"
                  :label="$t('page.i2c_mgmt.form.sda_pin_label')"
                  :disable="submitting"
                />
                <c-gpio-selector
                  disallow-expander-pins
                  v-model:model-value="v.config.sclPin.$model"
                  :disable="submitting"
                  :label="$t('page.i2c_mgmt.form.scl_pin_label')"
                  :error-message="v.config.sclPin.$errors[0]?.$message"
                  :error="v.config.sclPin.$errors.length > 0"
                />
              </template>
            </c-assistant-container>
          </q-card>
        </div>
        <div class="col">
          <q-card-actions class="q-pa-none">
            <q-btn
              style="width: 100px"
              color="positive"
              :label="$t('page.i2c_mgmt.form.save_btn_label')"
              :disable="v.config.$invalid"
              :loading="submitting"
              @click="submit"
            />
            <q-btn
              style="width: 100px"
              color="negative"
              :label="$t('page.i2c_mgmt.form.abort_btn_label')"
              :disable="submitting"
              @click="$router.push({name: 'gpiomanagement'})"
            />
          </q-card-actions>
        </div>
      </q-form>
    </q-card>
  </q-page>

</template>

<script>
import CAssistantContainer from 'components/CAssistantContainer.vue'
import {required, requiredIf} from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'
import CGpioSelector from 'components/CGpioSelector.vue'
import SystemService from 'src/services/system.service'

export default {
  name: 'I2CManagement',
  components: {CGpioSelector, CAssistantContainer},
  data: () => {
    return {
      config: {
        enable: false,
        sdaPin: null,
        sclPin: null
      },
      submitting: false
    }
  },
  async beforeRouteEnter(to, from, next) {
    const cfg = await SystemService.getI2cSettings()
    next(vm => {
      vm.config = cfg
    })
  },
  setup() {
    return {
      v: useVuelidate()
    }
  },
  methods: {
    submit() {
      if (this.v.config.$invalid) {
        return
      }
      this.submitting = true
      SystemService.setI2cSettings(this.config)
        .then(() => this.$router.push({name: 'gpiomanagement'}))
        .finally(() => {
          this.submitting = false
        })
    }
  },
  validations() {
    return {
      config: {
        enable: {
          required
        },
        sdaPin: {
          required: requiredIf(() => this.config.enable)
        },
        sclPin: {
          required: requiredIf(() => this.config.enable)
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
