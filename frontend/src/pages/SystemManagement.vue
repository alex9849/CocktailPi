<template>
  <q-page class="page-content" padding>
    <h5>{{ $t('page.system_mgmt.system.headline') }}</h5>
    <q-card
      bordered
      class="bg-card-body text-card-body"
    >
      <q-card-section>
        <div class="row justify-center">
          <div class="col-auto">
            <q-btn
              size="xl"
              :icon="mdiPower"
              no-caps
              dense
              color="red"
              @click="shutdown.dialog = true"
            >
              {{ $t('page.system_mgmt.system.shutdown_btn_label') }}
            </q-btn>
          </div>
        </div>
      </q-card-section>
    </q-card>
    <h5>Update</h5>
    <c-settings-updater/>
    <h5>{{ $t('page.system_mgmt.default_filter.headline') }}</h5>
    <q-card
      bordered
      class="bg-card-body text-card-body q-pa-md"
    >
      <q-form
        class="q-col-gutter-md"
      >
        <div class="row">
          <q-card
            class="col bg-card-item-group text-card-item-group"
            :dark="color.cardItemGroupDark"
            flat
            bordered
          >
            <q-toggle
              :dark="color.cardItemGroupDark"
              :label="$t('page.system_mgmt.default_filter.enable_btn_label')"
              color="green"
              :disable="defaultFilter.saving"
              v-model:model-value="v.defaultFilter.data.enable.$model"
            />
          </q-card>
        </div>
        <div
          class="row"
          v-if="defaultFilter.data.enable"
        >
          <div class="col-12">
            <q-card
              :dark="color.cardItemGroupDark"
              class="row items-center justify-start bg-card-item-group text-card-item-group"
              flat
              bordered
            >
              <div class="col-shrink">
                <p class="q-px-sm text-grey-7">{{ $t('page.system_mgmt.default_filter.fabricable.headline') }}</p>
              </div>
              <div class="col-grow">
                <div class="row">
                  <q-radio
                    :dark="color.cardItemGroupDark"
                    :disable="defaultFilter.saving"
                    :label="$t('page.system_mgmt.default_filter.fabricable.show_all')"
                    v-model:model-value="v.defaultFilter.data.filter.fabricable.$model"
                    val=""
                    class="col-12 col-sm-4"
                  />
                  <q-radio
                    :dark="color.cardItemGroupDark"
                    :disable="defaultFilter.saving"
                    :label="$t('page.system_mgmt.default_filter.fabricable.fabricable_owned')"
                    v-model:model-value="v.defaultFilter.data.filter.fabricable.$model"
                    val="manual"
                    class="col-12 col-sm-4"
                  />
                  <q-radio
                    :dark="color.cardItemGroupDark"
                    :disable="defaultFilter.saving"
                    :label="$t('page.system_mgmt.default_filter.fabricable.fabricable_auto')"
                    v-model:model-value="v.defaultFilter.data.filter.fabricable.$model"
                    val="auto"
                    class="col-12 col-sm-4"
                  />
                </div>
              </div>
            </q-card>
          </div>
        </div>
        <div class="row justify-end">
          <q-btn
            :label="$t('page.system_mgmt.default_filter.save_btn_label')"
            :loading="defaultFilter.saving"
            :disable="v.defaultFilter.data.$invalid"
            color="green"
            @click="onClickSaveDefaultFilter"
          />
        </div>
      </q-form>
    </q-card>
    <c-question
      v-model:show="shutdown.dialog"
      :question="$t('page.system_mgmt.system.shutdown_dialog.headline')"
      @clickAbort="shutdown.dialog = false"
      @clickOk="doShutdown"
      ok-color="red"
      :abort-button-text="$t('page.system_mgmt.system.shutdown_dialog.abort_btn_label')"
      :ok-button-text="$t('page.system_mgmt.system.shutdown_dialog.shutdown_btn_label')"
    >
      <template v-slot:buttons>
        <q-btn
          color="grey"
          @click="() => shutdown.dialog = false"
          style="width: 150px"
        >
          {{ $t('page.system_mgmt.system.shutdown_dialog.abort_btn_label') }}
        </q-btn>
        <q-btn
          color="warning"
          style="width: 150px"
          @click="doReboot"
        >
          {{ $t('page.system_mgmt.system.shutdown_dialog.reboot_btn_label') }}
        </q-btn>
        <q-btn
          color="red"
          style="width: 150px"
          @click="doShutdown"
        >
          {{ $t('page.system_mgmt.system.shutdown_dialog.shutdown_btn_label') }}
        </q-btn>
      </template>
    </c-question>
    <h5>{{ $t('page.system_mgmt.appearance.headline') }}</h5>
    <c-settings-appearance />
  </q-page>
</template>

<script>

import { mdiPower } from '@quasar/extras/mdi-v5'
import CQuestion from 'components/CQuestion'
import SystemService from 'src/services/system.service'
import useVuelidate from '@vuelidate/core'
import { required } from '@vuelidate/validators'
import CSettingsAppearance from 'components/CSettingsAppearance.vue'
import { mapGetters } from 'vuex'
import CSettingsUpdater from 'components/CSettingsUpdater.vue'

export default {
  name: 'SystemManagement',
  components: { CSettingsUpdater, CSettingsAppearance, CQuestion },
  async beforeRouteEnter (to, from, next) {
    const defaultFilter = await SystemService.getDefaultFilter()
    next(vm => {
      vm.defaultFilter.data = defaultFilter
    })
  },
  data: () => {
    return {
      shutdown: {
        dialog: false
      },
      defaultFilter: {
        saving: false,
        data: {
          enable: false,
          filter: {
            fabricable: ''
          }
        }
      }
    }
  },
  setup () {
    return {
      v: useVuelidate()
    }
  },
  created () {
    this.mdiPower = mdiPower
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    })
  },
  methods: {
    doReboot () {
      SystemService.doShutdown(true)
        .finally(() => {
          this.shutdown.dialog = false
        })
    },
    doShutdown () {
      SystemService.doShutdown(false)
        .finally(() => {
          this.shutdown.dialog = false
        })
    },
    onClickSaveDefaultFilter () {
      this.defaultFilter.saving = true
      SystemService.setDefaultFilter(this.defaultFilter.data)
        .then((x) => {
          this.defaultFilter.data = x
          this.$q.notify({
            type: 'positive',
            message: this.$t('page.system_mgmt.default_filter.notifications.settings_updated')
          })
        })
        .finally(x => {
          this.defaultFilter.saving = false
        })
    }
  },
  validations () {
    return {
      defaultFilter: {
        data: {
          enable: {
            required
          },
          filter: {
            fabricable: {}
          }
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
