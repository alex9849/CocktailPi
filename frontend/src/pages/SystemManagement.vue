<template>
  <q-page class="page-content" padding>
    <h5>System Management</h5>
    <q-card
      flat
      bordered
      class="bg-card-primary"
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
              Shutdown system
            </q-btn>
          </div>
        </div>
      </q-card-section>
    </q-card>
    <h5>Default filter</h5>
    <q-card
      flat
      bordered
      class="bg-card-primary q-pa-md"
    >
      <q-form class="q-col-gutter-md">
        <div class="row">
          <q-card class="col" flat bordered>
            <q-toggle
              label="Enable default filter"
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
              class="row items-center justify-start"
              flat
              bordered
            >
              <div class="col-shrink">
                <p class="q-px-sm text-grey-7">Fabricable:</p>
              </div>
              <div class="col-grow">
                <div class="row">
                  <q-radio
                    :disable="defaultFilter.saving"
                    label="Show all"
                    v-model:model-value="v.defaultFilter.data.filter.fabricable.$model"
                    val=""
                    class="col-12 col-sm-4"
                  />
                  <q-radio
                    :disable="defaultFilter.saving"
                    label="Fabricable with owned ingredients"
                    v-model:model-value="v.defaultFilter.data.filter.fabricable.$model"
                    val="manual"
                    class="col-12 col-sm-4"
                  />
                  <q-radio
                    :disable="defaultFilter.saving"
                    label="Fabricable fully automatic"
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
            label="Save"
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
      question="Shut down?"
      @clickAbort="shutdown.dialog = false"
      @clickOk="doShutdown"
      ok-color="red"
      ok-button-text="Shutdown"
    />
  </q-page>
</template>

<script>

import { mdiPower } from '@quasar/extras/mdi-v5'
import CQuestion from 'components/CQuestion'
import SystemService from 'src/services/system.service'
import useVuelidate from '@vuelidate/core'
import { required } from '@vuelidate/validators'

export default {
  name: 'SystemManagement',
  components: { CQuestion },
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
  methods: {
    doShutdown () {
      SystemService.doShutdown()
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
            message: 'Settings updated!'
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
