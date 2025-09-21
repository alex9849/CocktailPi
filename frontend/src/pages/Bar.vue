<template>
  <q-page class="page-content" padding>
    <h5>{{ $t('page.bar.headline') }}</h5>
    <TopButtonArranger>
      <q-btn
        color="positive"
        :label="$t('page.bar.add_btn_label')"
        :disable="loading"
        @click="editOptions.editDialog = true"
        no-caps
        v-if="getAdminLevel >= 2"
      />
      <q-btn
        color="info"
        :label="$t('page.bar.refresh_btn_label')"
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
        :rows="ownedIngredients"
        :loading="loading"
        hide-bottom
        :pagination="{rowsPerPage: 0, sortBy: 'name'}"
        :no-data-label="$t('page.bar.owned_table.no_data_msg')"
      >
        <template v-if="getAdminLevel >= 2"
                  v-slot:body-cell-actions="props"
        >
          <q-td :props="props"
                key="actions"
                class="q-pa-md q-gutter-x-sm"
          >
            <q-btn
              :icon="mdiDelete"
              color="red"
              @click="onRemoveOwnedIngredient(props.row.id)"
              dense
              rounded
            >
              <q-tooltip>
                {{ $t('page.bar.owned_table.delete_btn_tooltip') }}
              </q-tooltip>
            </q-btn>
          </q-td>
        </template>
        <template v-slot:body-cell-alcoholContent="props">
          <q-td :props="props"
                key="alcoholContent"
          >
            {{ props.row.alcoholContent }}%
          </q-td>
        </template>
        <template
          v-slot:bottom-row
        >
          <td
            style="color: #b5b5b5"
          >
            {{ $t('page.bar.owned_table.nr_ingredients_owned', {nrIngredients: ownedIngredients.length}) }}
          </td>
          <td rowspan="5"/>
        </template>
        <template
          v-slot:loading
        >
          <q-inner-loading
            :dark="color.cardBodyDark"
            showing
            color="info"
          />
        </template>
      </q-table>
    </div>
    <c-edit-dialog
      v-model:show="editOptions.editDialog"
      :error-message="editOptions.editErrorMessage"
      :title="$t('page.bar.add_dialog.headline')"
      :saving="editOptions.saving"
      :valid="editOptions.valid"
      :save-btn-label="$t('page.bar.add_dialog.save_btn_label')"
      :abort-btn-label="$t('page.bar.add_dialog.abort_btn_label')"
      @clickAbort="closeEditDialog"
      @clickSave="onAddOwnedIngredient"
    >
      <c-ingredient-selector
        outlined
        :label="$t('page.bar.add_dialog.ingredient_selector_label')"
        :disable="editOptions.saving"
        filter-ingredient-groups
        v-model:selected="v.editOptions.addIngredient.$model"
        :rules="[() => !v.editOptions.addIngredient.$error || $t('page.bar.add_dialog.required_error')]"
      />
    </c-edit-dialog>
  </q-page>
</template>

<script>
import CIngredientSelector from 'components/CIngredientSelector'
import { required } from '@vuelidate/validators'
import CEditDialog from 'components/CEditDialog'
import { mdiDelete } from '@quasar/extras/mdi-v5'
import TopButtonArranger from 'components/TopButtonArranger'
import { mapGetters } from 'vuex'
import useVuelidate from '@vuelidate/core'
import IngredientService from '../services/ingredient.service'

export default {
  name: 'Bar',
  components: { TopButtonArranger, CEditDialog, CIngredientSelector },
  data () {
    return {
      ownedIngredients: [],
      loading: false,
      editOptions: {
        editErrorMessage: '',
        saving: false,
        editDialog: false,
        valid: false,
        addIngredient: null
      }
    }
  },
  async beforeRouteEnter (to, from, next) {
    const ownedIngredients = await IngredientService
      .getIngredientsFilter(null, null, null, null, null, true, null, null)
    next(vm => {
      vm.ownedIngredients = ownedIngredients
    })
  },
  setup () {
    return {
      v: useVuelidate(),
      mdiDelete
    }
  },
  computed: {
    ...mapGetters({
      getAdminLevel: 'auth/getAdminLevel',
      color: 'appearance/getNormalColors'
    }),
    columns () {
      const columns = [
        { name: 'name', label: this.$t('page.bar.owned_table.columns.ingredient'), field: 'name', align: 'center' },
        { name: 'alcoholContent', label: this.$t('page.bar.owned_table.columns.alc_content'), field: 'alcoholContent', align: 'center' }
      ]
      if (this.getAdminLevel >= 2) {
        columns.push({ name: 'actions', label: this.$t('page.bar.owned_table.columns.actions'), field: '', align: 'center' })
      }
      return columns
    }
  },
  methods: {
    onRefresh () {
      this.loading = true
      setTimeout(() => {
        IngredientService
          .getIngredientsFilter(null, null, null, null, null, true, null, null)
          .then(data => {
            this.ownedIngredients = data
          })
          .finally(() => {
            this.loading = false
          })
      }, 500)
    },
    closeEditDialog () {
      this.editOptions.editDialog = false
      this.editOptions.addIngredient = null
    },
    onAddOwnedIngredient () {
      const vm = this
      const onSuccess = function () {
        vm.onRefresh()
        vm.editOptions.editErrorMessage = ''
        vm.$q.notify({
          type: 'positive',
          message: vm.$t('page.bar.notifications.ingredient_added')
        })
        vm.closeEditDialog()
      }

      const onError = function (error) {
        vm.editOptions.editErrorMessage = error.response.data.message
        vm.$q.notify({
          type: 'negative',
          message: error.response.data.message
        })
      }

      this.editOptions.saving = true
      IngredientService.addToBar(this.editOptions.addIngredient.id)
        .then(onSuccess, onError)
        .finally(() => {
          this.editOptions.saving = false
        })
    },
    onRemoveOwnedIngredient (id) {
      const vm = this
      IngredientService.removeFromBar(id)
        .then(() => {
          vm.onRefresh()
          vm.$q.notify({
            type: 'positive',
            message: vm.$t('page.bar.notifications.ingredient_removed')
          })
        }, err => {
          vm.$q.notify({
            type: 'negative',
            message: err.response.data.message
          })
        })
    }
  },
  watch: {
    'v.editOptions.$invalid': function _watch$vEditOptionsEditIngredient$invalid (value) {
      this.editOptions.valid = !value
    }
  },
  validations () {
    return {
      editOptions: {
        addIngredient: {
          required
        }
      }
    }
  }
}
</script>

<style scoped>
</style>
