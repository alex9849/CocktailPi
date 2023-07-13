<template>
  <h5>Pumps</h5>
  <div class="q-gutter-sm">
    <TopButtonArranger>
      <q-btn
        color="negative"
        label="Delete selected pumps"
        no-caps
        @click="$refs.deleteDialog.openForItems(selected)"
      />
      <q-btn
        color="positive"
        label="Add pump"
        @click="showAddDialog = true"
        no-caps
      />
    </TopButtonArranger>
    <TopButtonArranger>
      <q-btn
        color="positive"
        label="Start all pumps"
        @click="onClickTurnOnAllPumps()"
        :icon="mdiPlay"
        no-caps
      />
      <q-btn
        color="negative"
        label="Stop all pumps"
        @click="onClickTurnOffAllPumps()"
        :icon="mdiStop"
        no-caps
      />
    </TopButtonArranger>
  </div>
  <div class="row q-col-gutter-md q-mt-md">
    <div
      class="col-12 col-sm-6 col-lg-4"
      v-bind:key="pump.id"
      v-for="pump in pumps"
    >
      <c-pump-card
        style="height: 100%"
        :pump="pump"
        show-detailed
      />
    </div>
    <div
      class="col-12"
      v-if="pumps.length === 0"
    >
      <q-card flat bordered class="bg-card-container">
        <div class="row q-pa-md items-center q-gutter-sm">
          <q-icon size="sm" :name="mdiAlert" />
          <p class="">No pumps found!</p>
        </div>
      </q-card>
    </div>
  </div>
  <c-pump-setup-type-selector
    v-model:show="showAddDialog"
    @clickType="onClickAddPump($event)"
  />
  <c-delete-warning
    ref="deleteDialog"
    :delete-method="deletePump"
    :list-point-method="x => 'Pump #' + x.id"
    item-name-plural="pumps"
    item-name-singular="pump"
    @deleteSuccess="onDeleteSuccess"
  />
</template>

<script>
import TopButtonArranger from 'components/TopButtonArranger'
import CEditDialog from 'components/CEditDialog'
import PumpEditorForm from 'components/PumpEditorForm'
import CDeleteWarning from 'components/CDeleteWarning'
import { mdiDelete, mdiPencilOutline, mdiPlay, mdiStop, mdiAlert } from '@quasar/extras/mdi-v5'
import PumpService from 'src/services/pump.service'
import { mapGetters } from 'vuex'
import CPumpCard from 'components/CPumpCard.vue'
import CPumpSetupTypeSelector from 'components/pumpsetup/CPumpSetupTypeSelector.vue'

export default {
  name: 'CPumpManagement',
  components: { CPumpSetupTypeSelector, CPumpCard, TopButtonArranger, CEditDialog, PumpEditorForm, CDeleteWarning },
  created () {
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiPlay = mdiPlay
    this.mdiStop = mdiStop
    this.mdiAlert = mdiAlert
  },
  data () {
    return {
      showAddDialog: false
    }
  },
  methods: {
    onClickAddPump (type) {
      const newPump = {
        type: type
      }
      PumpService.createPump(newPump)
        .then(response => {
          this.$router.push({
            name: 'editpump',
            params: {
              pumpId: response.data.id
            }
          })
        })
    },
    onClickTurnOnAllPumps () {
      const vm = this
      PumpService.startPump(null).then(() => {
        vm.$q.notify({
          type: 'positive',
          message: 'All pumps started!'
        })
      })
    },
    onClickTurnOffAllPumps () {
      const vm = this
      PumpService.stopPump(null).then(() => {
        vm.$q.notify({
          type: 'positive',
          message: 'All pumps stopped!'
        })
      })
    },
    deletePump (id) {
      return PumpService.deletePump(id)
    },
    onDeleteSuccess () {
      this.selected.splice(0, this.selected.length)
    }
  },
  computed: {
    ...mapGetters({
      getPumpOccupation: 'pumpLayout/getPumpOccupation',
      pumps: 'pumpLayout/getLayout',
      isAllowReversePumping: 'common/isAllowReversePumping'
    }),
    deleteQuestionMessage () {
      if (this.deletePumps.length === 0) {
        return 'No pumps selected!'
      }
      if (this.deletePumps.length === 1) {
        return 'The following pump will be deleted:'
      }
      return 'The following pumps will be deleted:'
    }
  }
}
</script>

<style scoped>

</style>
