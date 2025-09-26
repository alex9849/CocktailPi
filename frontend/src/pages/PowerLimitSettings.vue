<template>
  <q-page class="page-content q-gutter-y-lg" padding>
    <h5>{{ $t('page.power_limit_mgmt.headline') }}</h5>
    <q-card
      :dark="color.cardBodyDark"
      class="q-pa-md bg-card-body text-card-body"
      flat
      bordered
    >
      <div class="row">
        <p class="text-weight-medium q-pb-md">{{ $t('page.power_limit_mgmt.hardware_settings.headline') }}</p>
      </div>
      <q-form class="q-col-gutter-md">
        <div class="row">
          <q-card
            :dark="color.cardItemGroupDark"
            class="col bg-card-item-group text-card-item-group"
            flat
            bordered
          >
            <q-toggle
              :dark="color.cardItemGroupDark"
              :label="$t('page.power_limit_mgmt.hardware_settings.enable_btn_label')"
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
                  :dark="color.cardItemGroupDark"
                  bordered
          >
            <q-card-section>
              <q-input
                outlined
                type="number"
                :dark="color.cardItemGroupDark"
                v-model:model-value.number="v.form.limit.$model"
                :error-message="v.form.$errors[0]?.$message"
                :error="v.form.$errors.length > 0"
                :label="$t('page.power_limit_mgmt.hardware_settings.power_limit_label')"
                clearable
              >
                <template v-slot:append>
                  mW
                </template>
              </q-input>
            </q-card-section>
          </q-card>
        </div>
        <div class="row justify-end">
          <div class="q-gutter-sm">
            <q-btn
              :label="$t('page.power_limit_mgmt.hardware_settings.save_btn_label')"
              color="green"
              @click="onClickSave()"
            />
            <q-btn
              :label="$t('page.power_limit_mgmt.hardware_settings.save_and_return_btn_label')"
              color="green"
              @click="onClickSave(true)"
            />
          </div>
        </div>
      </q-form>
    </q-card>
  </q-page>
</template>

<script>

import useVuelidate from '@vuelidate/core'
import { minValue, required, requiredIf } from '@vuelidate/validators'
import { mapGetters } from 'vuex'
import PumpSettingsService from 'src/services/pumpsettings.service'

export default {
  name: 'PowerLimitSettings',
  setup () {
    return {
      v: useVuelidate()
    }
  },
  data () {
    return {
      saving: false,
      loading: true,
      form: {
        enable: false,
        limit: null
      }
    }
  },
  created () {
    this.fetchSettings()
  },
  methods: {
    receiveLoadCellFromBackend (data) {
      this.v.form.$model = Object.assign(this.form, data)
    },
    fetchSettings () {
      PumpSettingsService.getPowerLimit()
        .then(powerLimit => this.receiveLoadCellFromBackend(powerLimit))
        .finally(() => {
          this.loading = false
        })
    },
    onClickSave (pushBack = false) {
      this.saving = true
      PumpSettingsService.setPowerLimit(this.form.enable ? this.form : null)
        .then(powerLimit => {
          this.$q.notify({
            type: 'positive',
            message: this.$t('page.power_limit_mgmt.hardware_settings.update_success_message')
          })
          if (pushBack) {
            this.$router.back()
          } else {
            this.receiveLoadCellFromBackend(powerLimit)
          }
          return powerLimit
        })
        .finally(powerLimit => {
          this.saving = false
        })
    }
  },
  validations () {
    return {
      form: {
        enable: {
          required
        },
        limit: {
          required: requiredIf(() => this.form.enable),
          minValue: minValue(1)
        }
      }
    }
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
