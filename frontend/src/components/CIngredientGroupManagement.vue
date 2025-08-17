<template>
  <TopButtonArranger>
    <q-btn
      :disable="loading"
      color="positive"
      :label="$t('component.ingredient_group_mgmt.create_btn_label')"
      no-caps
      @click="showEditDialog(null)"
    />
    <q-btn
      :disable="loading"
      :loading="loading"
      color="info"
      :label="$t('component.ingredient_group_mgmt.refresh_btn_label')"
      no-caps
      @click="onRefresh"
    />
  </TopButtonArranger>
  <div class="q-py-md">
    <q-table
      :dark="color.cardBodyDark"
      :columns="columns"
      :loading="loading"
      :pagination="{rowsPerPage: 10, sortBy: 'name'}"
      :rows="groups"
      :filter="filter"
      hide-no-data
    >
      <template v-slot:top-right>
        <q-input
          :dark="color.cardBodyDark"
          outlined
          dense
          debounce="300"
          v-model="filter"
          :placeholder="$t('component.ingredient_group_mgmt.group_table.search_field_label')"
        >
          <template v-slot:append>
            <q-icon name="search"/>
          </template>
        </q-input>
      </template>
      <template v-slot:body-cell-normalName>
      </template>
      <template v-slot:header-cell-normalName>
      </template>
      <template v-slot:body-cell-alcoholContent="props">
        <q-td
          key="alcoholContent"
          :props="props"
        >
          {{ getAlcoholContentString(props.row.minAlcoholContent, props.row.maxAlcoholContent) }}
        </q-td>
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
              {{ $t('component.ingredient_group_mgmt.group_table.edit_btn_tooltip') }}
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
              {{ $t('component.ingredient_group_mgmt.group_table.delete_btn_tooltip') }}
            </q-tooltip>
          </q-btn>
        </q-td>
      </template>
      <template
        v-slot:loading
      >
        <q-inner-loading
          :dark="color.cardBodyDark"
          color="info"
          showing
        />
      </template>
    </q-table>
    <c-delete-warning
      ref="deleteDialog"
      :delete-method="deleteGroup"
      :headline="$t('component.ingredient_group_mgmt.group_table.delete_dialog.headline')"
      :list-point-method="x => x.name"
      :banner-warning="$t('component.ingredient_group_mgmt.group_table.delete_dialog.warning')"
      @deleteFailure="fetchAll"
      @deleteSuccess="onDeleteSuccess"
    />
    <c-edit-dialog
      v-model:show="editOptions.show"
      :error-message="editOptions.editErrorMessage"
      :saving="editOptions.saving"
      :title="editDialogHeadline"
      :valid="editOptions.valid"
      @clickAbort="closeEditDialog"
      @clickSave="onClickSaveGroup"
    >
      <c-ingredient-group-form
        v-model:model-value="editOptions.editGroup"
        :disable="editOptions.saving"
        @invalid="editOptions.valid = false"
        @valid="editOptions.valid = true"
      />
    </c-edit-dialog>
  </div>
</template>

<script>
import { mdiDelete, mdiPencilOutline } from '@quasar/extras/mdi-v5'
import TopButtonArranger from 'components/TopButtonArranger'
import IngredientService, { ingredientDtoMapper } from 'src/services/ingredient.service'
import CDeleteWarning from 'components/CDeleteWarning'
import CEditDialog from 'components/CEditDialog'
import CIngredientGroupForm from 'components/CIngredientGroupForm'
import { mapGetters } from 'vuex'

export default {
  name: 'CIngredientGroupManagement',
  components: { CIngredientGroupForm, TopButtonArranger, CDeleteWarning, CEditDialog },
  data () {
    return {
      filter: '',
      groups: [],
      columns: [
        {
          name: 'name',
          label: this.$t('component.ingredient_group_mgmt.group_table.columns.group'),
          field: 'name',
          align: 'center'
        },
        {
          name: 'normalName',
          label: 'normalName',
          field: 'normalName',
          align: 'center'
        },
        {
          name: 'alcoholContent',
          label: this.$t('component.ingredient_group_mgmt.group_table.columns.alc_content'),
          field: 'alcoholContent',
          align: 'center'
        },
        {
          name: 'parentGroup',
          label: this.$t('component.ingredient_group_mgmt.group_table.columns.parent_group'),
          field: 'parentGroupName',
          align: 'center'
        },
        {
          name: 'actions',
          label: this.$t('component.ingredient_group_mgmt.group_table.columns.actions'),
          field: '',
          align: 'center'
        }
      ],
      loading: false,
      editOptions: {
        show: false,
        editErrorMessage: '',
        valid: false,
        saving: false,
        editGroup: {
          id: -1,
          name: '',
          type: 'group',
          parentGroupId: null
        },
        newGroup: {
          id: -1,
          name: '',
          type: 'group',
          parentGroupId: null
        }
      }
    }
  },
  methods: {
    onRefresh () {
      this.loading = true
      const vm = this
      setTimeout(vm.fetchAll, 500)
    },
    fetchAll () {
      this.loading = true
      IngredientService.getIngredientsFilter(null, true,
        true, null, null, false)
        .then(groups => {
          this.loading = false
          this.groups = groups
        })
    },
    onClickSaveGroup () {
      this.editOptions.saving = true
      const vm = this
      const onSuccess = function () {
        vm.editOptions.saving = false
        vm.editOptions.editErrorMessage = ''
        let msg
        if (vm.isEditGroupNew) {
          msg = vm.$t('component.ingredient_group_mgmt.notifications.group_created')
        } else {
          msg = vm.$t('component.ingredient_group_mgmt.notifications.group_updated')
        }
        vm.$q.notify({
          type: 'positive',
          message: msg
        })
        vm.closeEditDialog()
        vm.fetchAll()
      }

      const onError = function (error) {
        vm.editOptions.saving = false
        vm.editOptions.editErrorMessage = error.response.data.message
      }

      const dto = ingredientDtoMapper.toIngredientCreateDto(this.editOptions.editGroup)
      if (this.isEditGroupNew) {
        IngredientService.createIngredient(dto)
          .then(onSuccess, error => onError(error))
      } else {
        IngredientService.updateIngredient(this.editOptions.editGroup.id, dto, null)
          .then(onSuccess, error => onError(error))
      }
    },
    getAlcoholContentString (min, max) {
      if (min == null) {
        return '-'
      }
      if (min === max) {
        return min + '%'
      }
      return min + ' - ' + max + '%'
    },
    onDeleteSuccess () {
      this.fetchAll()
    },
    deleteGroup (id) {
      IngredientService.deleteIngredient(id)
    },
    showEditDialog (group) {
      if (group) {
        this.editOptions.editGroup = Object.assign({}, this.editOptions.newGroup)
        this.editOptions.editGroup = Object.assign(this.editOptions.editGroup, group)
      }
      this.editOptions.show = true
    },
    closeEditDialog () {
      this.editOptions.editGroup = Object.assign({}, this.editOptions.newGroup)
      this.editOptions.show = false
      this.editOptions.editErrorMessage = ''
    }
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    }),
    isEditGroupNew () {
      return this.editOptions.editGroup.id === -1
    },
    editDialogHeadline () {
      if (this.isEditGroupNew) {
        return this.$t('component.ingredient_group_mgmt.create_headline')
      }
      return this.$t('component.ingredient_group_mgmt.edit_headline')
    }
  },
  setup () {
    return {
      mdiDelete,
      mdiPencilOutline
    }
  },
  created () {
    this.fetchAll()
  }
}
</script>

<style scoped>

</style>
