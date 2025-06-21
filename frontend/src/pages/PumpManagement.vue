<template>
  <q-page class="page-content" padding>
    <h5>{{ $t('component.pump_mgmt.headline') }}</h5>
    <div class="q-gutter-sm">
      <TopButtonArranger>
        <q-btn
          color="positive"
          :label="$t('component.pump_mgmt.add_btn_label')"
          @click="showAddDialog = true"
          :icon="mdiPlusCircleOutline"
          no-caps
        />
        <q-btn
          color="positive"
          :label="$t('component.pump_mgmt.start_all_btn_label')"
          @click="onClickTurnOnAllPumps()"
          :icon="mdiPlay"
          no-caps
        />
        <q-btn
          color="negative"
          :label="$t('component.pump_mgmt.stop_all_btn_label')"
          @click="onClickTurnOffAllPumps()"
          :icon="mdiStop"
          no-caps
        />
      </TopButtonArranger>
      <TopButtonArranger>
        <q-btn
          color="info"
          :label="showPumpDetails ? 'Hide details' : 'Show details'"
          @click="showPumpDetails = !showPumpDetails"
          :icon="showPumpDetails ? mdiChevronDoubleUp : mdiChevronDoubleDown"
          no-caps
        />
      </TopButtonArranger>
    </div>
    <div class="row q-col-gutter-lg q-mt-md reverse">
      <div class="col-lg-3 col-12">
        <c-pump-status />
      </div>
      <div class="col-lg-9 col-12">
        <div class="row q-col-gutter-sm">
          <div
            class="col-12 col-sm-6 col-lg-4"
            v-bind:key="pump.id"
            v-for="pump in pumps"
          >
            <c-pump-card
              style="height: 100%"
              :pump="pump"
              :show-detailed="showPumpDetails"
            />
          </div>
          <div
            class="col-12"
            v-if="pumps.length === 0"
          >
            <q-card flat bordered class="bg-card-header text-card-header">
              <div class="row q-pa-md items-center q-gutter-sm">
                <q-icon size="sm" :name="mdiAlert" />
                <p class="">{{ $t('component.pump_mgmt.no_pumps_found') }}</p>
              </div>
            </q-card>
          </div>
        </div>
      </div>
    </div>
    <c-pump-setup-type-selector
      v-model:show="showAddDialog"
    />
  </q-page>
</template>

<script>

import TopButtonArranger from 'components/TopButtonArranger.vue'
import CPumpCard from 'components/CPumpCard.vue'
import CPumpSetupTypeSelector from 'components/pumpsetup/CPumpSetupTypeSelector.vue'
import { mdiChevronDoubleUp, mdiChevronDoubleDown, mdiAlert, mdiDelete, mdiPencilOutline, mdiPlay, mdiPlusCircleOutline, mdiStop } from '@quasar/extras/mdi-v5'
import PumpService from 'src/services/pump.service'
import { mapGetters } from 'vuex'
import CPumpStatus from 'components/CPumpStatus.vue'

export default {
  name: 'PumpManagement',
  components: { CPumpStatus, CPumpSetupTypeSelector, CPumpCard, TopButtonArranger },
  created () {
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiPlay = mdiPlay
    this.mdiStop = mdiStop
    this.mdiAlert = mdiAlert
    this.mdiChevronDoubleDown = mdiChevronDoubleDown
    this.mdiChevronDoubleUp = mdiChevronDoubleUp
    this.mdiPlusCircleOutline = mdiPlusCircleOutline
  },
  data () {
    return {
      showAddDialog: false,
      showPumpDetails: false
    }
  },
  methods: {
    onClickTurnOnAllPumps () {
      const vm = this
      PumpService.startPump(null).then(() => {
        vm.$q.notify({
          type: 'positive',
          message: this.$t('component.pump_mgmt.notifications.all_started')
        })
      })
    },
    onClickTurnOffAllPumps () {
      const vm = this
      PumpService.stopPump(null).then(() => {
        vm.$q.notify({
          type: 'positive',
          message: this.$t('component.pump_mgmt.notifications.all_stopped')
        })
      })
    }
  },
  computed: {
    ...mapGetters({
      pumps: 'pumpLayout/getLayout',
      isAllowReversePumping: 'common/isAllowReversePumping'
    })
  }
}
</script>

<style scoped>
</style>
