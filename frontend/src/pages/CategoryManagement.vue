<template>
  <q-page class="page-content" padding>
    <h5>{{ $t('page.category_mgmt.headline') }}</h5>
    <TopButtonArranger>
      <q-btn
        color="positive"
        :label="$t('page.category_mgmt.create_btn_label')"
        :disable="loading"
        @click="showEditDialog(null)"
        no-caps
      />
      <q-btn
        color="info"
        :label="$t('page.category_mgmt.refresh_btn_label')"
        :disable="loading"
        :loading="loading"
        @click="onRefresh"
        no-caps
      />
    </TopButtonArranger>
    <div class="q-py-md">
      <q-table
        :columns="columns"
        :rows="categories"
        :loading="loading"
        hide-bottom
        :pagination="{rowsPerPage: 0, sortBy: 'name'}"
        :no-data-label="$t('page.category_mgmt.no_data_msg')"
      >
        <template v-slot:body="props">
          <q-tr
            :props="props"
          >
            <q-td
              key="name"
              :props="props"
            >
              {{ props.row.name }}
            </q-td>
            <q-td
              key="actions"
              class="q-pa-md q-gutter-x-sm"
              :props="props"
            >
              <q-btn
                :icon="mdiPencilOutline"
                text-color="white"
                color="info"
                @click="showEditDialog(props.row)"
                dense
                rounded
              >
                <q-tooltip>
                  {{ $t('page.category_mgmt.category_table.edit_btn_tooltip') }}
                </q-tooltip>
              </q-btn>
              <q-btn
                :icon="mdiDelete"
                color="red"
                @click="() => {deleteOptions.deleteCategory.push(props.row); openDeleteDialog(false);}"
                dense
                rounded
              >
                <q-tooltip>
                  {{ $t('page.category_mgmt.category_table.delete_btn_tooltip') }}
                </q-tooltip>
              </q-btn>
            </q-td>
          </q-tr>
        </template>
        <template
          v-slot:bottom-row
        >
          <td
            style="color: #b5b5b5"
          >
            {{ $t('page.category_mgmt.category_table.nr_categories', {nr: categories.length}) }}
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
    </div>
    <c-question
      :question="$t('page.category_mgmt.delete_dialog.headline')"
      ok-color="red"
      :ok-button-text="$t('page.category_mgmt.delete_dialog.ok_btn_label')"
      :abort-button-text="$t('page.category_mgmt.delete_dialog.abort_btn_label')"
      :loading="deleteOptions.deleteLoading"
      v-model:show="deleteOptions.deleteDialog"
      @clickOk="deleteSelected"
      @clickAbort="closeDeleteDialog"
    >
      <template v-slot:error-area>
        <div>
          <q-banner v-if="deleteOptions.deleteErrorMessage !== ''" rounded dense class="text-white bg-red-5" style="margin: 10px">
            {{ deleteOptions.deleteErrorMessage }}
          </q-banner>
        </div>
      </template>
      <template v-slot:buttons>
        <q-btn
          v-if="deleteOptions.deleteCategory.length === 0"
          color="grey"
          style="width: 150px"
          @click="closeDeleteDialog"
        >
          Ok
        </q-btn>
      </template>
      <template v-slot:default>
        <ul style="padding: 0; list-style: none">
          <li
            :key="index"
            v-for="(category, index) in deleteOptions.deleteCategory"
          >
            {{category.name}}
          </li>
        </ul>
      </template>
    </c-question>
    <c-edit-dialog
      v-model:show="editOptions.editDialog"
      :error-message="editOptions.editErrorMessage"
      :title="editDialogHeadline"
      :saving="editOptions.editCategorySaving"
      :valid="editOptions.valid"
      :save-btn-label="$t('page.category_mgmt.create_dialog.save_btn_label')"
      :abort-btn-label="$t('page.category_mgmt.create_dialog.abort_btn_label')"
      @clickAbort="closeEditDialog"
      @clickSave="onClickSaveCategory"
    >
      <q-input
        :label="$t('page.category_mgmt.create_dialog.name_field_label')"
        outlined
        :disable="editOptions.editCategorySaving"
        v-model:model-value="v.editOptions.editCategory.name.$model"
        filled
        :rules="[
                val => !v.editOptions.editCategory.name.required.$invalid || $t('errors.field_required'),
                val => !v.editOptions.editCategory.name.maxLength.$invalid || $t('errors.max_letters', {nr: 15})
              ]"
      />
    </c-edit-dialog>
  </q-page>
</template>

<script>
import { mdiDelete, mdiPencilOutline } from '@quasar/extras/mdi-v5'
import CQuestion from '../components/CQuestion'
import { maxLength, required } from '@vuelidate/validators'
import CEditDialog from 'components/CEditDialog'
import TopButtonArranger from 'components/TopButtonArranger'
import { mapActions, mapGetters } from 'vuex'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'CategoryManagement',
  components: { TopButtonArranger, CEditDialog, CQuestion },
  data () {
    return {
      columns: [
        { name: 'name', label: this.$t('page.category_mgmt.category_table.columns.category'), field: 'name', align: 'center' },
        { name: 'actions', label: this.$t('page.category_mgmt.category_table.columns.actions'), field: '', align: 'center' }
      ],
      selected: [],
      loading: false,
      deleteOptions: {
        deleteCategory: [],
        deleteErrorMessage: '',
        deleteLoading: false,
        deleteDialog: false
      },
      editOptions: {
        editErrorMessage: '',
        editCategorySaving: false,
        editDialog: false,
        valid: false,
        editCategory: {
          id: -1,
          name: '',
          alcoholContent: 0
        },
        newCategory: {
          id: -1,
          name: '',
          alcoholContent: 0
        }
      }
    }
  },
  methods: {
    ...mapActions({
      createCategory: 'category/createCategory',
      updateCategory: 'category/updateCategory',
      fetchCategories: 'category/fetchCategories',
      deleteCategories: 'category/deleteCategories'
    }),
    onRefresh () {
      this.loading = true
      const vm = this
      setTimeout(() => {
        vm.fetchCategories()
          .finally(() => {
            vm.loading = false
          })
      }, 500)
    },
    showEditDialog (category) {
      if (category) {
        this.editOptions.editCategory = Object.assign({}, category)
      }
      this.editOptions.editDialog = true
    },
    closeEditDialog () {
      this.editOptions.editCategory = Object.assign({}, this.editOptions.newCategory)
      this.editOptions.editDialog = false
      this.editOptions.editErrorMessage = ''
    },
    onClickSaveCategory () {
      this.editOptions.editCategorySaving = true
      const vm = this
      const onSuccess = function () {
        vm.editOptions.editCategorySaving = false
        vm.editOptions.editErrorMessage = ''
        let msg
        if (vm.iseditCategoryNew) {
          msg = vm.$t('page.category_mgmt.notifications.category_created')
        } else {
          msg = vm.$t('page.category_mgmt.notifications.category_updated')
        }
        vm.$q.notify({
          type: 'positive',
          message: msg
        })
        vm.closeEditDialog()
      }

      const onError = function (error) {
        vm.editOptions.editCategorySaving = false
        vm.editOptions.editErrorMessage = error.response.data.message
        vm.$q.notify({
          type: 'negative',
          message: error.response.data.message
        })
      }

      if (this.iseditCategoryNew) {
        this.createCategory(this.editOptions.editCategory.name)
          .then(onSuccess, error => onError(error))
      } else {
        this.updateCategory(this.editOptions.editCategory)
          .then(onSuccess, error => onError(error))
      }
    },
    deleteSelected () {
      const vm = this
      const toDeleteIds = this.deleteOptions.deleteCategory.map(x => x.id)
      this.deleteOptions.deleteLoading = true
      this.deleteCategories(toDeleteIds)
        .then(() => {
          vm.closeDeleteDialog()
          vm.selected.splice(0, vm.selected.length)
          vm.deleteOptions.deleteLoading = false
          vm.deleteOptions.deleteErrorMessage = ''
          vm.$q.notify({
            type: 'positive',
            message: vm.$t('page.category_mgmt.notifications.category_deleted')
          })
        }, err => {
          vm.deleteOptions.deleteLoading = false
          vm.selected.splice(0, vm.selected.length)
          vm.deleteOptions.deleteErrorMessage = err.response.data.message
          vm.$q.notify({
            type: 'negative',
            message: err.response.data.message
          })
        })
    },
    openDeleteDialog (forSelected) {
      if (forSelected) {
        this.deleteOptions.deleteCategory.push(...this.selected)
      }
      this.deleteOptions.deleteDialog = true
    },
    closeDeleteDialog () {
      this.deleteOptions.deleteCategory.splice(0, this.deleteOptions.deleteCategory.length)
      this.deleteOptions.deleteDialog = false
      this.deleteOptions.deleteErrorMessage = ''
    }
  },
  setup () {
    return {
      v: useVuelidate(),
      mdiDelete: mdiDelete,
      mdiPencilOutline: mdiPencilOutline
    }
  },
  validations () {
    const validations = {
      editOptions: {
        editCategory: {
          name: {
            required,
            maxLength: maxLength(15)
          }
        }
      }
    }
    return validations
  },
  watch: {
    'v.editOptions.editCategory.$invalid': {
      handler (value) {
        this.editOptions.valid = !value
      }
    }
  },
  computed: {
    ...mapGetters({
      categories: 'category/getCategories'
    }),
    iseditCategoryNew () {
      return this.editOptions.editCategory.id === -1
    },
    editDialogHeadline () {
      if (this.iseditCategoryNew) {
        return this.$t('page.category_mgmt.create_dialog.headline_create')
      }
      return this.$t('page.category_mgmt.create_dialog.headline_edit')
    }
  }
}
</script>

<style scoped>
</style>
