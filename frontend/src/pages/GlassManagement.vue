<template>
  <q-page
    padding
    class="page-content"
  >
    <h5>{{ $t('page.glass_mgmt.headline') }}
      <q-btn
        :icon="mdiInformation"
        dense
        flat
        round
        color="background-info-icon"
        @click="showHelp = true"
      >
      </q-btn>
    </h5>
    <TopButtonArranger>
      <q-btn
        color="positive"
        :label="$t('page.glass_mgmt.add_glass_btn_label')"
        :disable="loading"
        @click="showEditDialog(null)"
        no-caps
      />
      <q-btn
        color="info"
        :label="$t('page.glass_mgmt.refresh_btn_label')"
        :disable="loading"
        :loading="loading"
        @click="onRefresh"
        no-caps
      />
    </TopButtonArranger>
    <div class="q-py-md">
      <q-table
        :dark="color.cardBodyDark"
        :columns="columns"
        :loading="loading"
        :pagination="{rowsPerPage: 0, sortBy: 'name'}"
        hide-bottom
        :rows="glasses"
      >
        <template
          v-slot:loading
        >
          <q-inner-loading
            :dark="color.cardBodyDark"
            showing
            color="info"
          />
        </template>
        <template v-slot:body-cell-actions="props">
          <q-td
            key="actions"
            :props="props"
            class="q-pa-md q-gutter-x-sm"
          >
            <q-btn
              :icon="mdiPencilOutline"
              :style="{backgroundColor: '#31ccec'}"
              dense
              rounded
              text-color="white"
              @click="showEditDialog(props.row)"
            >
              <q-tooltip>
                {{ $t('page.glass_mgmt.glass_table.edit_btn_tooltip') }}
              </q-tooltip>
            </q-btn>
            <q-btn
              :icon="mdiDelete"
              color="red"
              dense
              rounded
              @click="$refs.deleteDialog.openForItems([props.row])"
            >
              <q-tooltip>
                {{ $t('page.glass_mgmt.glass_table.delete_btn_tooltip') }}
              </q-tooltip>
            </q-btn>
          </q-td>
        </template>
        <template v-slot:body-cell-size="props">
          <q-td :props="props"
                key="size"
          >
            {{ props.row.size }} ml
          </q-td>
        </template>
        <template v-slot:body-cell-default="props">
          <q-td :props="props"
                key="default"
          >
            <q-icon
              v-if="props.row.default"
              size="sm"
              :name="mdiCheckCircle"
            />
            <q-icon
              v-else
              size="sm"
              :name="mdiCheckboxBlankCircleOutline"
            />
          </q-td>
        </template>
        <template v-slot:body-cell-useForSingleIngredients="props">
          <q-td :props="props"
                key="useForSingleIngredients"
          >
            <q-icon
              v-if="props.row.useForSingleIngredients"
              size="sm"
              :name="mdiCheckCircle"
            />
            <q-icon
              v-else
              size="sm"
              :name="mdiCheckboxBlankCircleOutline"
            />
          </q-td>
        </template>
        <template
          v-slot:bottom-row
        >
          <td>
            {{ $t('page.glass_mgmt.glass_table.nr_glasses', {nr: glasses.length}) }}
          </td>
          <td rowspan="5"/>
        </template>
      </q-table>
      <c-delete-warning
        ref="deleteDialog"
        headline="Delete glass?"
        :delete-method="deleteGlass"
        :list-point-method="x => x.name"
        item-name-plural="glasses"
        item-name-singular="glass"
        @deleteFailure="onRefresh"
        @deleteSuccess="onDeleteSuccess"
      />
    </div>
    <c-edit-dialog
      v-model:show="editOptions.editDialog"
      :error-message="editOptions.editErrorMessage"
      :saving="editOptions.saving"
      :title="editOptions.editDialogHeadline"
      :valid="editOptions.valid"
      @clickAbort="closeEditDialog"
      @clickSave="onClickEditDialogSave"
    >
      <c-glass-form
        v-model:model-value="editOptions.editGlass"
        @invalid="editOptions.valid = !$event"
      />
    </c-edit-dialog>
    <q-dialog
      v-model:model-value="showHelp"
    >
      <q-card>
        <q-card-section class="q-gutter-y-sm">
          <h6>{{ $t('page.glass_mgmt.help_dialog.headline') }}</h6>
          <p style="white-space: pre-line">
            {{ $t('page.glass_mgmt.help_dialog.text') }}
          </p>
        </q-card-section>
        <q-card-section class="row justify-center">
          <q-btn
            class="col-auto"
            color="grey"
            @click="showHelp = false"
          >
            Close
          </q-btn>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script>

import TopButtonArranger from 'components/TopButtonArranger.vue'
import { mdiCheckboxBlankCircleOutline, mdiCheckCircle, mdiDelete, mdiPencilOutline, mdiInformation } from '@quasar/extras/mdi-v5'
import CDeleteWarning from 'components/CDeleteWarning.vue'
import GlassService from 'src/services/glass.service'
import CEditDialog from 'components/CEditDialog.vue'
import CGlassForm from 'components/CGlassForm.vue'
import { mapGetters } from 'vuex'

export default {
  name: 'GlassManagement',
  components: { CGlassForm, CEditDialog, CDeleteWarning, TopButtonArranger },
  async beforeRouteEnter (to, from, next) {
    const glasses = await GlassService.getAllGlasses()
    next(vm => {
      vm.glasses = glasses
    })
  },
  data () {
    return {
      glasses: [],
      showHelp: false,
      columns: [
        { name: 'name', label: this.$t('page.glass_mgmt.glass_table.columns.name'), field: 'name', align: 'center' },
        { name: 'size', label: this.$t('page.glass_mgmt.glass_table.columns.size'), field: 'size', align: 'center' },
        { name: 'default', label: this.$t('page.glass_mgmt.glass_table.columns.default'), field: 'default', align: 'center' },
        {
          name: 'useForSingleIngredients',
          label: this.$t('page.glass_mgmt.glass_table.columns.for_single_ingredients'),
          field: 'useForSingleIngredients',
          align: 'center'
        },
        { name: 'actions', label: this.$t('page.glass_mgmt.glass_table.columns.actions'), field: '', align: 'center' }
      ],
      loading: false,
      editOptions: {
        valid: false,
        editDialogHeadline: '',
        editErrorMessage: '',
        saving: false,
        editDialog: false,
        editGlass: {},
        newGlass: {
          id: -1,
          name: '',
          size: '',
          useForSingleIngredients: false,
          default: false
        }
      }
    }
  },
  created () {
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiCheckboxBlankCircleOutline = mdiCheckboxBlankCircleOutline
    this.mdiCheckCircle = mdiCheckCircle
    this.mdiInformation = mdiInformation
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    })
  },
  methods: {
    showEditDialog (glass) {
      if (glass) {
        this.editOptions.editGlass = Object.assign({}, glass)
        this.editOptions.editDialogHeadline = this.$t('page.glass_mgmt.edit_dialog.headline_edit')
      } else {
        this.editOptions.editGlass = Object.assign({}, this.editOptions.newGlass)
        this.editOptions.editDialogHeadline = this.$t('page.glass_mgmt.edit_dialog.headline_create')
      }
      this.editOptions.editDialog = true
    },
    closeEditDialog () {
      this.editOptions.editIngredient = Object.assign({}, this.editOptions.newGlass)
      this.editOptions.editDialog = false
      this.editOptions.editErrorMessage = ''
    },
    onClickEditDialogSave () {
      const isNew = this.editOptions.editGlass.id === -1
      this.editOptions.saving = true
      let promise
      if (isNew) {
        promise = GlassService.createGlass(this.editOptions.editGlass)
      } else {
        promise = GlassService.updateGlass(this.editOptions.editGlass)
      }
      promise = promise.then((recipe) => {
        let msg
        if (this.isNew) {
          msg = this.$t('page.glass_mgmt.notifications.glass_created')
        } else {
          msg = this.$t('page.glass_mgmt.notifications.glass_updated')
        }
        this.$q.notify({
          type: 'positive',
          message: msg
        })
        this.closeEditDialog()
        this.onRefresh()
      })
      promise.finally(() => {
        this.editOptions.saving = false
      })
    },
    onRefresh () {
      this.loading = true
      setTimeout(() => {
        GlassService.getAllGlasses()
          .then(x => {
            this.glasses = x
          })
          .finally(() => {
            this.loading = false
          })
      }, 500)
    },
    onDeleteSuccess () {
      this.onRefresh()
    },
    deleteGlass (id) {
      return GlassService.deleteGlass(id)
    }
  }
}
</script>

<style scoped>

</style>
