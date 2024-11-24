<template>
  <q-card class="bg-card-body text-card-body">
    <q-card-section class="q-pa-none">
      <q-expansion-item
        :label="$t('component.pump_status.headline')"
        header-class="bg-card-header text-card-header text-weight-medium"
        :default-opened="!$q.platform.is.mobile"
      >
        <template v-slot:default>
          <q-separator/>
          <q-card-section>
            <div class="row q-col-gutter-sm justify-center">
              <div class="col-12 col-md-4 col-lg-12">
                <q-card
                  flat
                  bordered
                  class="bg-card-item-group text-card-item-group full-height"
                >
                  <q-card-section class="q-py-xs bg-card-header text-card-header">
                    <p class="text-weight-medium">
                      {{ $t('component.pump_status.pumps.headline') }}
                    </p>
                  </q-card-section>
                  <q-separator/>
                  <q-card-section
                    class="q-py-sm"
                  >
                    <table class="table-no-stripes">
                      <tbody>
                      <tr>
                        <td>{{ $t('component.pump_status.pumps.pumps_installed') }}</td>
                        <td>
                          <q-badge>{{ nrPumps }}</q-badge>
                        </td>
                      </tr>
                      <tr>
                        <td>{{ $t('component.pump_status.pumps.ingredients_installed') }}</td>
                        <td>
                          <q-badge>{{ nrIngredientsInstalled }}</q-badge>
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </q-card-section>
                </q-card>
              </div>
              <div class="col-12 col-md-4 col-lg-12">
                <q-card
                  flat
                  bordered
                  class="bg-card-item-group text-card-item-group full-height"
                >
                  <q-card-section
                    class="q-py-xs q-pr-xs bg-cyan-1 row items-center bg-card-header text-card-header"
                  >
                    <div class="col">
                      <p class="text-weight-medium">
                        {{ $t('component.pump_status.reverse_pumping.headline') }}
                      </p>
                    </div>
                    <div class="col-shrink">
                      <q-btn
                        color="info"
                        :label="$t('component.pump_status.configure_btn')"
                        :icon="mdiPencilOutline"
                        @click="$router.push({name: 'reversepumpsettings'})"
                        dense
                        no-caps
                        size="sm"
                      />
                    </div>
                  </q-card-section>
                  <q-separator/>
                  <q-card-section
                    class="q-py-sm"
                  >
                    <table class="table-no-stripes">
                      <tbody>
                      <tr>
                        <td>{{ $t('component.pump_status.reverse_pumping.status') }}</td>
                        <td>
                          <q-badge
                            :class="{'bg-negative': !reversePumpSettings.enable , 'bg-positive': reversePumpSettings.enable}"
                          >
                            {{ reversePumpingStatus }}
                          </q-badge>
                        </td>
                      </tr>
                      <tr v-if="reversePumpSettings.enable">
                        <td>{{ $t('component.pump_status.reverse_pumping.overshoot') }}</td>
                        <td>
                          <q-badge>{{ reversePumpSettings?.settings?.overshoot }}%</q-badge>
                        </td>
                      </tr>
                      <tr v-if="reversePumpSettings.enable">
                        <td>{{ $t('component.pump_status.reverse_pumping.timer') }}</td>
                        <td>
                          <q-badge>{{ reversePumpSettings?.settings?.autoPumpBackTimer }} min</q-badge>
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </q-card-section>
                </q-card>
              </div>
              <div class="col-12 col-md-4 col-lg-12">
                <q-card
                  flat
                  bordered
                  class="bg-card-item-group text-card-item-group full-height"
                >
                  <q-card-section
                    class="q-py-xs q-pr-xs bg-cyan-1 row items-center bg-card-header text-card-header"
                  >
                    <div class="col">
                      <p class="text-weight-medium">
                        {{ $t('component.pump_status.load_cell.headline') }}
                      </p>
                    </div>
                    <div class="col-shrink">
                      <q-btn
                        color="info"
                        :label="$t('component.pump_status.configure_btn')"
                        :icon="mdiPencilOutline"
                        @click="$router.push({name: 'loadcellsettings'})"
                        dense
                        no-caps
                        size="sm"
                      />
                    </div>
                  </q-card-section>
                  <q-separator/>
                  <q-card-section
                    class="q-py-sm"
                  >
                    <table class="table-no-stripes">
                      <tbody>
                      <tr>
                        <td>{{ $t('component.pump_status.load_cell.status') }}</td>
                        <td>
                          <q-badge
                            :class="{'bg-negative': !loadCellSettings.enable , 'bg-positive': loadCellSettings.enable}"
                          >
                            {{ loadCellStatus }}
                          </q-badge>
                        </td>
                      </tr>
                      <tr v-if="loadCellSettings.enable">
                        <td>{{ $t('component.pump_status.load_cell.calibrated') }}</td>
                        <td>
                          <q-badge
                            :class="{'bg-negative': !loadCellSettings?.calibrated , 'bg-positive': loadCellSettings?.calibrated}"
                          >
                            {{ loadCellCalibrated }}
                          </q-badge>
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </q-card-section>
                </q-card>
              </div>
            </div>
          </q-card-section>
        </template>
      </q-expansion-item>
    </q-card-section>
  </q-card>
</template>

<script>
import { mdiPencilOutline } from '@quasar/extras/mdi-v5'
import PumpSettingsService from 'src/services/pumpsettings.service'
import { mapGetters } from 'vuex'

export default {
  name: 'CPumpStatus',
  props: {
    classed: {
      type: String,
      default: ''
    }
  },
  data: () => {
    return {
      reversePumpSettings: {

      },
      loadCellSettings: {
        enable: false,
        clkPin: null,
        dtPin: null,
        calibrated: false
      }
    }
  },
  created () {
    this.mdiPencilOutline = mdiPencilOutline
    this.fetchLoadCell()
    this.fetchReversePumpSettings()
  },
  methods: {
    fetchLoadCell () {
      PumpSettingsService.getLoadCell().then(data => {
        this.loadCellSettings = data
        this.loadCellSettings = Object.assign(this.loadCellSettings, data)
        this.loadCellSettings.calibrated = !!(data?.calibrated)
        this.loadCellSettings.enable = !!data
      })
    },
    fetchReversePumpSettings () {
      PumpSettingsService.getReversePumpSettings().then(data => {
        this.reversePumpSettings = data
      })
    }
  },
  computed: {
    ...mapGetters({
      pumps: 'pumpLayout/getLayout'
    }),
    nrPumps () {
      return this.pumps.length
    },
    nrIngredientsInstalled () {
      return this.pumps.filter(x => !!x.currentIngredient).length
    },
    reversePumpingStatus () {
      if (this.reversePumpSettings?.enable) {
        return this.$t('component.pump_status.reverse_pumping.status_enabled')
      } else {
        return this.$t('component.pump_status.reverse_pumping.status_disabled')
      }
    },
    loadCellStatus () {
      if (this.loadCellSettings?.enable) {
        return this.$t('component.pump_status.load_cell.status_enabled')
      } else {
        return this.$t('component.pump_status.load_cell.status_disabled')
      }
    },
    loadCellCalibrated () {
      if (this.loadCellSettings?.calibrated) {
        return this.$t('component.pump_status.load_cell.calibrated_yes')
      } else {
        return this.$t('component.pump_status.load_cell.calibrated_no')
      }
    }
  }
}
</script>

<style scoped>

</style>
