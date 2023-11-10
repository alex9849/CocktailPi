<template>
  <q-card
    flat
    bordered
    class="bg-card-primary q-pa-md"
  >
    <q-form
      class="q-col-gutter-md"
      @submit="onSubmit"
    >
      <div class="row">
        <q-card
          class="col"
          flat
          bordered
        >
          <q-card-section>
            <q-select
              :label="$t('component.settings_appearance.language')"
              v-model:model-value="v.form.language.$model"
              :options="languageOptions"
              map-options
              :option-value="option => option"
              outlined
              hide-bottom-space
            >
              <template v-slot:selected>
                {{ v.form.language.$model.inEnglish}} / <i>({{v.form.language.$model.inNative}})</i>
              </template>
              <template v-slot:option="scope">
                <q-item v-bind="scope.itemProps">
                  <q-item-section>
                    <q-item-label>{{ scope.opt.inEnglish }} / <i>({{scope.opt.inNative}})</i></q-item-label>
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
          </q-card-section>
        </q-card>
      </div>
      <!--div class="row">
        <q-card
          class="col"
          flat
          bordered
        >
          <p class="text-subtitle2 q-pt-sm q-pl-md">Interface colors</p>
          <q-card-section class="q-pt-sm q-gutter-sm">
            <q-input
              outlined
              label="Primary"
              readonly
              :model-value="colors.normal.primary"
            >
              <template v-slot:append>
                <q-btn
                  icon="colorize"
                  label="Pick"
                  no-caps
                  :style="{backgroundColor: colors.normal.primary}"
                >
                  <q-popup-proxy
                    transition-show="scale"
                    transition-hide="scale">
                    <q-color
                      no-header-tabs
                      format-model="hex"
                      v-model:model-value="colors.normal.primary"
                    />
                  </q-popup-proxy>
                </q-btn>
              </template>
            </q-input>
            <q-input
              outlined
              label="Secondary"
              readonly
              :model-value="colors.normal.secondary"
            >
              <template v-slot:append>
                <q-btn
                  icon="colorize"
                  label="Pick"
                  no-caps
                  :style="{backgroundColor: colors.normal.secondary}"
                >
                  <q-popup-proxy
                    transition-show="scale"
                    transition-hide="scale">
                    <q-color
                      no-header-tabs
                      format-model="hex"
                      v-model:model-value="colors.normal.secondary"
                    />
                  </q-popup-proxy>
                </q-btn>
              </template>
            </q-input>
          </q-card-section>
        </q-card>
      </div>
      <div class="row">
        <q-card
          class="col"
          flat
          bordered
        >
          <p class="text-subtitle2 q-pt-sm q-pl-md">Simple view colors</p>
          <q-card-section class="q-pt-sm q-gutter-sm">
            <q-input
              outlined
              label="Primary"
              readonly
              :model-value="colors.simpleView.primary"
            >
              <template v-slot:append>
                <q-btn
                  icon="colorize"
                  label="Pick"
                  no-caps
                  :style="{backgroundColor: colors.simpleView.primary}"
                >
                  <q-popup-proxy
                    transition-show="scale"
                    transition-hide="scale">
                    <q-color
                      no-header-tabs
                      format-model="hex"
                      v-model:model-value="colors.simpleView.primary"
                    />
                  </q-popup-proxy>
                </q-btn>
              </template>
            </q-input>
            <q-input
              outlined
              label="Secondary"
              readonly
              :model-value="colors.simpleView.secondary"
            >
              <template v-slot:append>
                <q-btn
                  icon="colorize"
                  label="Pick"
                  no-caps
                  :style="{backgroundColor: colors.simpleView.secondary}"
                >
                  <q-popup-proxy
                    transition-show="scale"
                    transition-hide="scale">
                    <q-color
                      no-header-tabs
                      format-model="hex"
                      v-model:model-value="colors.simpleView.secondary"
                    />
                  </q-popup-proxy>
                </q-btn>
              </template>
            </q-input>
          </q-card-section>
        </q-card>
      </div-->
      <div class="row justify-end">
        <q-btn
          :label="$t('component.settings_appearance.save_btn_label')"
          :loading="saving"
          :disable="v.form.$invalid"
          color="green"
          type="submit"
        />
      </div>
    </q-form>
  </q-card>
</template>

<script>

import useVuelidate from '@vuelidate/core'
import { required } from '@vuelidate/validators'
import SystemService from 'src/services/system.service'
import { mapGetters, mapMutations } from 'vuex'

export default {
  name: 'CSettingsAppearance',
  data () {
    return {
      saving: false,
      form: {
        language: '',
        colors: {
          normal: {
            primary: '#ffcccc',
            secondary: '#ffcccc'
          },
          simpleView: {
            primary: '#ffcccc',
            secondary: '#ffcccc'
          }
        }
      },
      languageOptions: []
    }
  },
  setup () {
    return {
      v: useVuelidate()
    }
  },
  created () {
    this.fetchLanguages()
  },
  methods: {
    ...mapMutations({
      setAppearanceSettings: 'common/setAppearanceSettings'
    }),
    fetchLanguages () {
      SystemService.getLanguages()
        .then(data => {
          this.languageOptions = data
        })
    },
    onSubmit () {
      if (this.v.form.$invalid || this.saving) {
        return
      }
      this.saving = true
      SystemService.setAppearanceSettings(this.form)
        .then((data) => {
          this.$q.notify({
            type: 'positive',
            message: this.$t('component.settings_appearance.notifications.settings_updated')
          })
          this.setAppearanceSettings(data)
        })
        .finally(() => {
          this.saving = false
        })
    }
  },
  computed: {
    ...mapGetters({
      getAppearanceSettings: 'common/getAppearanceSettings'
    })
  },
  watch: {
    getAppearanceSettings: {
      deep: true,
      immediate: true,
      handler (newVal) {
        this.form = JSON.parse(JSON.stringify(newVal))
      }
    }
  },
  validations () {
    return {
      form: {
        language: {
          required
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
