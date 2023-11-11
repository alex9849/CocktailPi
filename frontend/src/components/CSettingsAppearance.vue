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
            <p class="text-subtitle2 q-pt-sm q-pl-md">
              {{ $t('component.settings_appearance.colors.interface.headline') }}
            </p>
            <q-card-section class="q-pt-sm q-gutter-sm">
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
            <p class="text-subtitle2 q-pt-sm q-pl-md">
              {{ $t('component.settings_appearance.colors.simple_view.headline') }}
            </p>
            <q-card-section class="q-pt-sm q-gutter-sm">
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
