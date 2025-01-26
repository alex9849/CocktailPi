<template>
  <q-card
    bordered
    class="bg-card-body text-card-body q-pa-md"
  >
    <q-form
      class="q-col-gutter-md"
      @submit="onSubmit"
    >
      <div class="row">
        <q-card
          class="col bg-card-item-group text-card-item-group"
          flat
          bordered
          :dark="color.cardItemGroupDark"
        >
          <q-card-section>
            <q-select
              :label="$t('component.settings_appearance.language')"
              v-model:model-value="v.form.language.$model"
              :dark="color.cardItemGroupDark"
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
                <q-item
                  v-bind="scope.itemProps"
                  :dark="color.cardItemGroupDark"
                >
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
            :dark="color.cardItemGroupDark"
            class="bg-card-item-group text-card-item-group"
          >
            <div class="q-pt-sm q-px-md row items-center justify-between">
              <p class="text-subtitle2 col-auto">
                {{ $t('component.settings_appearance.colors.interface.headline') }}
              </p>
              <div class="col-shrink q-gutter-sm">
                <q-btn
                  dense
                  no-caps
                  color="primary"
                  @click="setDefaultNormalColors(false)"
                >
                  Set light
                </q-btn>
                <q-btn
                  dense
                  no-caps
                  color="primary"
                  @click="setDefaultNormalColors(true)"
                >
                  Set dark
                </q-btn>
              </div>
            </div>
            <q-card-section class="q-pt-sm q-mt-none q-gutter-sm">
              <c-color-selector-field
                v-model:model-value="form.colors.normal.header"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.interface.header')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.sidebar"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.interface.sidebar')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.background"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.interface.background')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.btnPrimary"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.interface.btn_primary')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.btnNavigationActive"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.interface.btn_navigation_active')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.cardHeader"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.interface.card_header')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.cardBody"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.interface.card_body')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.normal.cardItemGroup"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.interface.card_item_group')"
              />
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-md-6">
          <q-card
            flat
            bordered
            class="bg-card-item-group text-card-item-group"
          >
            <div class="q-pt-sm q-px-md row items-center justify-between">
              <p class="text-subtitle2 col-auto">
                {{ $t('component.settings_appearance.colors.simple_view.headline') }}
              </p>
              <div class="col-shrink q-gutter-sm">
                <q-btn
                  dense
                  no-caps
                  color="primary"
                  @click="setDefaultSimpleViewColors()"
                >
                  Reset
                </q-btn>
              </div>
            </div>
            <q-card-section class="q-pt-sm q-mt-none q-gutter-sm">
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.header"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.simple_view.header')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.sidebar"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.simple_view.sidebar')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.background"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.simple_view.background')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.btnPrimary"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.simple_view.btn_primary')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.btnNavigation"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.simple_view.btn_navigation')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.btnNavigationActive"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.simple_view.btn_navigation_active')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.cocktailProgress"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.simple_view.cocktail_progress')"
              />
              <c-color-selector-field
                v-model:model-value="form.colors.simpleView.cardPrimary"
                :dark="color.cardItemGroupDark"
                :label="$t('component.settings_appearance.colors.simple_view.card')"
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
            header: '#f3f3fa',
            sidebar: '#30343f',
            background: '#ffffff',
            btnPrimary: '#2a7f85',
            btnNavigationActive: '#3273dc',
            cardHeader: '#c9eaf5',
            cardBody: '#f3f3fa',
            cardItemGroup: '#fafaff'
          },
          simpleView: {
            header: '#1a237e',
            sidebar: '#616161',
            background: '#000000',
            btnPrimary: '#616161',
            btnNavigation: '#616161',
            btnNavigationActive: '#b968c7',
            cocktailProgress: '#1b5e20',
            cardPrimary: '#787878'
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
    setDefaultNormalColors (isDarkMode) {
      if (isDarkMode) {
        this.form.colors.normal.header = '#3b3b3b'
        this.form.colors.normal.sidebar = '#141414'
        this.form.colors.normal.background = '#292929'
        this.form.colors.normal.btnPrimary = '#2a7f85'
        this.form.colors.normal.btnNavigationActive = '#757575'
        this.form.colors.normal.cardHeader = '#575757'
        this.form.colors.normal.cardBody = '#3d3d3d'
        this.form.colors.normal.cardItemGroup = '#1c1c1c'
      } else {
        this.form.colors.normal.header = '#f3f3fa'
        this.form.colors.normal.sidebar = '#30343f'
        this.form.colors.normal.background = '#ffffff'
        this.form.colors.normal.btnPrimary = '#2a7f85'
        this.form.colors.normal.btnNavigationActive = '#3273dc'
        this.form.colors.normal.cardHeader = '#d3eaf2'
        this.form.colors.normal.cardBody = '#f3f3fa'
        this.form.colors.normal.cardItemGroup = '#fafaff'
      }
    },
    setDefaultSimpleViewColors () {
      this.form.colors.simpleView.header = '#1a237e'
      this.form.colors.simpleView.sidebar = '#616161'
      this.form.colors.simpleView.background = '#000000'
      this.form.colors.simpleView.btnPrimary = '#9336a3'
      this.form.colors.simpleView.btnNavigation = '#616161'
      this.form.colors.simpleView.btnNavigationActive = '#a85eb5'
      this.form.colors.simpleView.cocktailProgress = '#1b5e20'
      this.form.colors.simpleView.cardPrimary = '#787878'
    },
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
      getAppearanceSettings: 'appearance/getAppearanceSettings',
      color: 'appearance/getNormalColors'
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
