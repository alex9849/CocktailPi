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
          class="col bg-card-secondary"
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
      <div class="row q-col-gutter-md">
        <div class="col-12 col-md-6">
          <q-card
            flat
            bordered
          >
            <p class="text-subtitle2 q-pt-sm q-pl-md">Interface colors</p>
            <q-card-section class="q-pt-sm q-gutter-sm">
              <c-color-selector-field
                v-model:model-value="form.colors.normal.header"
                label="Header"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.sidebar"
                label="Sidebar"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.background"
                label="Background"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.btnPrimary"
                label="Button / Primary"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.btnNavigationActive"
                label="Button / Navigation active"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.cardHeader"
                label="Card / Header"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.cardBody"
                label="Card / Body"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.cardItemGroup"
                label="Card / Item group"
              />
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-md-6">
          <q-card
            flat
            bordered
          >
            <p class="text-subtitle2 q-pt-sm q-pl-md">Simple view colors</p>
            <q-card-section class="q-pt-sm q-gutter-sm">
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.header"
                label="Header + Footer"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.sidebar"
                label="Sidebar"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.background"
                label="Background"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.btnPrimary"
                label="Button / Primary"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.btnNavigation"
                label="Button / Navigation"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.btnNavigationActive"
                label="Button / Navigation active"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.cocktailProgress"
                label="Cocktail progress"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.cardPrimary"
                label="Card"
              />
            </q-card-section>
          </q-card>
        </div>
      </div>
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
import CColorSelectorField from 'components/CColorSelectorField.vue'

export default {
  name: 'CSettingsAppearance',
  components: { CColorSelectorField },
  data () {
    return {
      saving: false,
      form: {
        language: '',
        colors: {
          normal: {
            header: '#85452b',
            sidebar: '#bf947b',
            btnPrimary: '#85452b',
            btnNavigation: '#bf947b',
            btnNavigationActive: '#fddfb1',
            cardPrimary: '#f3f3f3',
            cardSecondary: '#fdfdfe'
          },
          simpleView: {
            header: '#1a237e',
            sidebar: '#616161',
            btnPrimary: '#616161',
            btnNavigation: '#616161',
            btnNavigationActive: '#b968c7',
            cocktailProgress: '#1b5e20'
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
      setAppearanceSettings: 'appearance/setAppearanceSettings'
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
      getAppearanceSettings: 'appearance/getAppearanceSettings'
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
