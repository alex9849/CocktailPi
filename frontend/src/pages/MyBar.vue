<template>
  <q-page class="page-content" padding>
    <h5>Ingredients you own</h5>
    <TopButtonArranger>
      <q-btn
        color="positive"
        label="Add ingredient"
        :disable="loading"
        @click="editOptions.editDialog = true"
        no-caps
      />
      <q-btn
        color="info"
        label="Refresh"
        :disable="loading"
        :loading="loading"
        @click="onRefresh"
        no-caps
      />
    </TopButtonArranger>
    <div class="q-py-md">
      <q-table
        :columns="columns"
        :rows="ownedIngredients"
        :loading="loading"
        hide-bottom
        :pagination="{rowsPerPage: 0, sortBy: 'name'}"
        no-data-label="No ingredients found"
      >
        <template v-slot:body-cell-actions="props">
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
                Delete
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
            {{ ownedIngredients.length }} ingredient(s) in total
          </td>
          <td rowspan="5"/>
        </template>
        <template
          v-slot:loading
        >
          <q-inner-loading
            showing
            color="info"
          />
        </template>
      </q-table>
      <h5

        style="padding-top: 15px; padding-bottom: 15px"
      >
        Ingredients you should buy
      </h5>
      <q-card flat class="bg-grey-4">
        <q-card-section class="text-center">
          Feature not implemented yet!
        </q-card-section>
      </q-card>
    </div>
    <c-edit-dialog
      v-model:show="editOptions.editDialog"
      :error-message="editOptions.editErrorMessage"
      title="Add Ingredient"
      :saving="editOptions.saving"
      :valid="editOptions.valid"
      @clickAbort="closeEditDialog"
      @clickSave="onAddOwnedIngredient"
    >
      <c-ingredient-selector
        label="Ingredient"
        :disable="editOptions.saving"
        v-model:selected="v.editOptions.addIngredient.$model"
        :rules="[() => !v.editOptions.addIngredient.$error || 'Required']"
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
import { mapActions, mapGetters } from 'vuex'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'MyBar',
  components: { TopButtonArranger, CEditDialog, CIngredientSelector },
  data () {
    return {
      columns: [
        { name: 'name', label: 'Ingredient', field: 'name', align: 'center' },
        { name: 'alcoholContent', label: 'Alcohol content', field: 'alcoholContent', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ],
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
  setup () {
    return {
      v: useVuelidate(),
      mdiDelete: mdiDelete
    }
  },
  computed: {
    ...mapGetters({
      ownedIngredients: 'bar/getOwnedIngredients'
    })
  },
  methods: {
    ...mapActions({
      fetchIngredientsAction: 'bar/fetchIngredients',
      addOwnedIngredientAction: 'bar/addIngredient',
      removeOwnedIngredientAction: 'bar/removeIngredient'
    }),
    onRefresh () {
      this.loading = true
      setTimeout(() => {
        this.fetchIngredientsAction()
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
        vm.editOptions.editErrorMessage = ''
        vm.$q.notify({
          type: 'positive',
          message: 'Ingredient added successfully'
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
      this.addOwnedIngredientAction(this.editOptions.addIngredient.id)
        .then(onSuccess, onError)
        .finally(() => {
          this.editOptions.saving = false
        })
    },
    onRemoveOwnedIngredient (id) {
      this.removeOwnedIngredientAction(id)
        .then(() => {
          this.$q.notify({
            type: 'positive',
            message: 'Ingredient removed successfully'
          })
        }, err => {
          this.$q.notify({
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
