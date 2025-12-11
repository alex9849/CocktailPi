<template>
  <q-page class="page-content" padding>
    <h5>{{ $t('component.reverse_pump_settings.headline') }}</h5>
    <q-card
      class="q-pa-md bg-card-body text-card-body"
      flat
      bordered
    >
      <q-form class="q-col-gutter-md">
        <div
          v-if="getUser.adminLevel >= 4"
          class="row"
        >
          <q-card
            class="col bg-card-item-group text-card-item-group"
            flat
            bordered
          >
            <q-toggle
              :label="$t('component.reverse_pump_settings.form.enable_label')"
              color="green"
              :disable="disableForm"
              :model-value="v.form.enable.$model"
              @update:model-value="onToggleEnable($event)"
            />
          </q-card>
        </div>
        <div class="row"
             v-if="v.form.enable.$model && getUser.adminLevel >= 4"
        >
          <q-card class="col bg-card-item-group text-card-item-group"
                  flat
                  bordered>
            <q-card-section>
              <div class="text-subtitle2">
                {{ $t('component.reverse_pump_settings.form.vd_pin_headline') }}
              </div>
            </q-card-section>
            <q-separator/>
            <q-card-section class="q-gutter-sm">
              <c-gpio-selector
                :dark="color.cardItemGroupDark"
                v-model:model-value="v.form.settings.directorPin.$model"
                :error-message="v.form.settings.directorPin.$errors[0]?.$message"
                :error="v.form.settings.directorPin.$errors.length > 0"
                :label="$t('component.reverse_pump_settings.form.vd_pin_label')"
                :disable="disableForm"
                clearable
              />
              <q-select
                :dark="color.cardItemGroupDark"
                :options="forwardStateOptions"
                v-model:model-value="v.form.settings.forwardStateHigh.$model"
                map-options
                emit-value
                outlined
                clearable
                hide-bottom-space
                :label="$t('component.reverse_pump_settings.form.forward_state_high_label')"
                :error-message="v.form.settings.forwardStateHigh.$errors[0]?.$message"
                :error="v.form.settings.forwardStateHigh.$errors.length > 0"
              />
            </q-card-section>
          </q-card>
        </div>
        <div class="row"
             v-if="v.form.enable.$model"
        >
          <q-card class="col bg-card-item-group text-card-item-group"
                  flat
                  bordered
          >
            <q-card-section class="q-gutter-md">
              <q-input
                :dark="color.cardItemGroupDark"
                :label="$t('component.reverse_pump_settings.form.overshoot_label')"
                outlined
                type="number"
                v-model:model-value="v.form.settings.overshoot.$model"
                :error-message="v.form.settings.overshoot.$errors[0]?.$message"
                :error="v.form.settings.overshoot.$errors.length > 0"
                :disable="disableForm"
                :hint="$t('component.reverse_pump_settings.form.overshoot_hint')"
              >
                <template v-slot:append>
                  %
                </template>
              </q-input>
              <q-select
                :dark="color.cardItemGroupDark"
                v-model:model-value="v.form.settings.autoPumpBackTimer.$model"
                :options="autoPumpBackTimerOptions"
                map-options
                emit-value
                outlined
                :disable="disableForm"
                hide-bottom-space
                :label="$t('component.reverse_pump_settings.form.auto_pump_back_timer_label')"
              />
            </q-card-section>
          </q-card>
        </div>
        <div class="row justify-end">
          <q-btn
            :label="$t('component.reverse_pump_settings.form.save_btn_label')"
            :loading="saving"
            :disable="v.form.$invalid"
            color="green"
            @click="onClickSave"
          />
        </div>
      </q-form>
      <q-inner-loading
        :showing="loading"
        color="info"
      />
    </q-card>
  </q-page>
</template>

<script>
import CGpioSelector from 'components/CGpioSelector.vue'
import useVuelidate from '@vuelidate/core'
import { mapActions, mapGetters } from 'vuex'
import PumpSettingsService from 'src/services/pumpsettings.service'
import { maxValue, minValue, required, requiredIf } from '@vuelidate/validators'

export default {
  name: 'ReversePumpSettings',
  components: { CGpioSelector },
  data () {
    return {
      saving: false,
      loading: true,
      form: {
        enable: false,
        settings: {
          overshoot: 0,
          directorPin: null,
          autoPumpBackTimer: 0,
          forwardStateHigh: false
        }
      },
      autoPumpBackTimerOptions: [{
        label: this.$t('component.reverse_pump_settings.form.timer_options.never'),
        value: 0
      },
      {
        label: this.$t('component.reverse_pump_settings.form.timer_options.in_minutes', { nr: 10 }),
        value: 10
      },
      {
        label: this.$t('component.reverse_pump_settings.form.timer_options.in_minutes', { nr: 20 }),
        value: 20
      },
      {
        label: this.$t('component.reverse_pump_settings.form.timer_options.in_minutes', { nr: 30 }),
        value: 30
      },
      {
        label: this.$t('component.reverse_pump_settings.form.timer_options.in_minutes', { nr: 60 }),
        value: 60
      }]
    }
  },
  setup () {
    return {
      v: useVuelidate()
    }
  },
  created () {
    this.fetchSettings()
  },
  methods: {
    ...mapActions({
      fetchGlobalSettings: 'common/fetchGlobalSettings'
    }),
    onToggleEnable (newValue) {
      this.v.form.enable.$model = newValue
      this.v.$touch()
    },
    onClickSave () {
      this.saving = true
      PumpSettingsService.setReversePumpSettings(this.form)
        .then(() => {
          this.$q.notify({
            type: 'positive',
            message: this.$t('component.reverse_pump_settings.notifications.updated')
          })
          this.$router.push({ name: 'pumpmanagement' })
        })
        .finally(() => {
          this.saving = false
          this.fetchGlobalSettings()
        })
    },
    fetchSettings () {
      this.loading = true
      PumpSettingsService.getReversePumpSettings()
        .then(settings => {
          this.v.form.$model = Object.assign(this.form, settings)
        })
        .finally(() => {
          this.loading = false
        })
    }
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors',
      getUser: 'auth/getUser'
    }),
    disableForm () {
      return this.saving
    },
    forwardStateOptions () {
      return [{
        label: this.$t('component.reverse_pump_settings.form.forward_state.high'),
        value: true
      }, {
        label: this.$t('component.reverse_pump_settings.form.forward_state.low'),
        value: false
      }]
    }
  },
  validations () {
    const val = {
      form: {
        enable: {
          required
        },
        settings: {
          overshoot: {
            required: requiredIf(() => this.form.enable)
          },
          directorPin: {
            required: requiredIf(() => this.form.enable)
          },
          autoPumpBackTimer: {
            required: requiredIf(() => this.form.enable)
          },
          forwardStateHigh: {
            required: requiredIf(() => this.form.enable)
          }
        }
      }
    }
    if (this.form.enable) {
      val.form.settings.overshoot.minValue = minValue(0)
      val.form.settings.overshoot.maxValue = maxValue(200)
    }
    return val
  }
}
</script>

<style scoped>

</style>
