<template>
  <TopButtonArranger>
    <q-btn
      :disable="loading"
      color="positive"
      :label="$t('component.ingredient_mgmt.create_btn_label')"
      no-caps
      @click="showEditDialog(null)"
    />
    <q-btn
      :disable="loading"
      :loading="loading"
      color="info"
      :label="$t('component.ingredient_mgmt.refresh_btn_label')"
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
      :rows="ingredients"
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
          :placeholder="$t('component.ingredient_mgmt.ingredient_table.search_field_label')"
        >
          <template v-slot:append>
            <q-icon name="search"/>
          </template>
        </q-input>
      </template>
      <template v-slot:body-cell-alcoholContent="props">
        <q-td
          key="alcoholContent"
          :props="props"
        >
          {{ props.row.alcoholContent }}%
        </q-td>
      </template>
      <template v-slot:body-cell-normalName>
      </template>
      <template v-slot:header-cell-normalName>
      </template>
      <template v-slot:body-cell-bottleSize="props">
        <q-td
          key="bottleSize"
          :props="props"
        >
          <p v-if="props.row.type === 'automated'">
            {{ props.row.bottleSize }} {{ props.row.unit }}
          </p>
        </q-td>
      </template>
      <template v-slot:body-cell-hasImage="props">
        <q-td
          key="hasImage"
          :props="props"
        >
          <q-btn
            v-if="props.row.hasImage"
            @click="onClickShowImgDialog(props.row)"
            dense
            outline
            no-caps
          >
            {{ $t('component.ingredient_mgmt.ingredient_table.has_img_col.yes_btn_label') }}
          </q-btn>
          <p v-else>
            {{ $t('component.ingredient_mgmt.ingredient_table.has_img_col.no_label') }}
          </p>
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
              {{ $t('component.ingredient_mgmt.ingredient_table.edit_btn_tooltip') }}
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
              {{ $t('component.ingredient_mgmt.ingredient_table.delete_btn_tooltip') }}
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
  </div>
  <c-delete-warning
    ref="deleteDialog"
    :delete-method="deleteIngredient"
    :list-point-method="x => x.name"
    :headline="$t('component.ingredient_mgmt.delete_dialog.headline')"
    :banner-warning="$t('component.ingredient_mgmt.delete_dialog.warning')"
    @deleteFailure="fetchAll"
    @deleteSuccess="onDeleteSuccess"
  />
  <c-edit-dialog
    v-model:show="editOptions.editDialog"
    :error-message="editOptions.editErrorMessage"
    :saving="editOptions.editIngredientSaving"
    :title="editDialogHeadline"
    :valid="editOptions.valid"
    @clickAbort="closeEditDialog"
    @clickSave="onClickSaveIngredient"
  >
    <ingredient-form
      v-model:model-value="editOptions.editIngredient"
      :disable="editOptions.editIngredientSaving"
      v-model:new-image="editOptions.newImage"
      v-model:remove-image="editOptions.removeImage"
      @invalid="editOptions.valid = false"
      @valid="editOptions.valid = true"
    />
  </c-edit-dialog>
  <q-dialog
    :model-value="showImgDialog.show"
    @update:modelValue="closeShowImgDialog()"
  >
    <q-card
      style="width: 500px; max-width: 1000px"
    >
      <q-card-section>
        <q-img
          :src="this.$store.getters['auth/getFormattedServerAddress'] + '/api/ingredient/' + showImgDialog.ingredient.id + '/image?timestamp=' + showImgDialog.ingredient.lastUpdate.getTime()"
        />
      </q-card-section>
      <q-card-actions align="center">
        <q-btn
          @click="closeShowImgDialog"
        >
          {{ $t('component.show_img_dialog.close_btn_label') }}
        </q-btn>
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script>
import { mdiCheckboxBlankCircleOutline, mdiCheckCircle, mdiDelete, mdiPencilOutline } from '@quasar/extras/mdi-v5'
import IngredientService, { ingredientDtoMapper } from '../services/ingredient.service'
import CEditDialog from 'components/CEditDialog'
import TopButtonArranger from 'components/TopButtonArranger'
import IngredientForm from 'components/IngredientForm'
import CDeleteWarning from 'components/CDeleteWarning'
import { mapGetters } from 'vuex'

export default {
  name: 'CIngredientManagement',
  components: { CDeleteWarning, IngredientForm, CEditDialog, TopButtonArranger },
  data () {
    return {
      filter: '',
      columns: [
        {
          name: 'name',
          label: this.$t('component.ingredient_mgmt.ingredient_table.columns.ingredient'),
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
          name: 'type',
          label: this.$t('component.ingredient_mgmt.ingredient_table.columns.type'),
          field: 'type',
          align: 'center'
        },
        {
          name: 'alcoholContent',
          label: this.$t('component.ingredient_mgmt.ingredient_table.columns.alc_content'),
          field: 'alcoholContent',
          align: 'center'
        },
        {
          name: 'bottleSize',
          label: this.$t('component.ingredient_mgmt.ingredient_table.columns.bottle_size'),
          field: 'bottleSize',
          align: 'center'
        },
        {
          name: 'unit',
          label: this.$t('component.ingredient_mgmt.ingredient_table.columns.unit'),
          field: 'unit',
          align: 'center'
        },
        {
          name: 'pumpTimeMultiplier',
          label: this.$t('component.ingredient_mgmt.ingredient_table.columns.pump_time_multiplier'),
          field: 'pumpTimeMultiplier',
          align: 'center'
        },
        {
          name: 'parentGroup',
          label: this.$t('component.ingredient_mgmt.ingredient_table.columns.parent_group'),
          field: 'parentGroupName',
          align: 'center'
        },
        {
          name: 'hasImage',
          label: this.$t('component.ingredient_mgmt.ingredient_table.columns.has_image'),
          field: 'hasImage',
          align: 'center'
        },
        {
          name: 'actions',
          label: this.$t('component.ingredient_mgmt.ingredient_table.columns.actions'),
          field: '',
          align: 'center'
        }
      ],
      showImgDialog: {
        show: false,
        ingredient: {
          id: '',
          lastUpdate: ''
        }
      },
      ingredients: [],
      loading: false,
      editOptions: {
        editErrorMessage: '',
        editIngredientSaving: false,
        editDialog: false,
        valid: false,
        removeImage: false,
        newImage: null,
        editIngredient: {
          id: -1,
          name: '',
          pumpTimeMultiplier: 1.0,
          alcoholContent: 0,
          hasImage: false,
          type: 'automated',
          unit: null
        },
        newIngredient: {
          id: -1,
          name: '',
          pumpTimeMultiplier: 1.0,
          alcoholContent: 0,
          hasImage: false,
          type: 'automated',
          unit: null
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
    deleteIngredient (id) {
      IngredientService.deleteIngredient(id)
    },
    showEditDialog (ingredient) {
      if (ingredient) {
        this.editOptions.editIngredient = Object.assign({}, this.editOptions.newIngredient)
        this.editOptions.editIngredient = Object.assign(this.editOptions.editIngredient, ingredient)
      }
      this.editOptions.editDialog = true
    },
    closeEditDialog () {
      this.editOptions.editIngredient = Object.assign({}, this.editOptions.newIngredient)
      this.editOptions.newImage = null
      this.editOptions.removeImage = false
      this.editOptions.editDialog = false
      this.editOptions.editErrorMessage = ''
    },
    onClickSaveIngredient () {
      this.editOptions.editIngredientSaving = true
      const vm = this
      const onSuccess = function () {
        vm.editOptions.editIngredientSaving = false
        vm.editOptions.editErrorMessage = ''
        let msg
        if (vm.isEditIngredientNew) {
          msg = vm.$t('component.ingredient_mgmt.notifications.ingredient_created')
        } else {
          msg = vm.$t('component.ingredient_mgmt.notifications.ingredient_updated')
        }
        vm.$q.notify({
          type: 'positive',
          message: msg
        })
        vm.closeEditDialog()
        vm.fetchAll()
      }

      const onError = function (error) {
        vm.editOptions.editIngredientSaving = false
        vm.editOptions.editErrorMessage = error.response.data.message
      }
      const dtoIngredient = ingredientDtoMapper.toIngredientCreateDto(this.editOptions.editIngredient)
      if (this.isEditIngredientNew) {
        IngredientService.createIngredient(dtoIngredient, this.editOptions.newImage)
          .then(onSuccess, error => onError(error))
      } else {
        IngredientService.updateIngredient(this.editOptions.editIngredient.id, dtoIngredient, this.editOptions.newImage, this.editOptions.removeImage)
          .then(onSuccess, error => onError(error))
      }
    },
    onDeleteSuccess () {
      this.fetchAll()
    },
    fetchAll () {
      this.loading = true
      IngredientService.getIngredientsFilter(null, null,
        null, true, null, null, null, null)
        .then(ingredients => {
          this.loading = false
          this.ingredients = ingredients
        })
    },
    onClickShowImgDialog (ingredient) {
      this.showImgDialog.show = true
      if (ingredient.hasImage) {
        this.showImgDialog.ingredient = ingredient
      }
    },
    closeShowImgDialog () {
      this.showImgDialog.show = false
    }
  },
  setup () {
    return {
      mdiDelete,
      mdiPencilOutline,
      mdiCheckCircle,
      mdiCheckboxBlankCircleOutline
    }
  },
  created () {
    this.fetchAll()
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    }),
    isEditIngredientNew () {
      return this.editOptions.editIngredient.id === -1
    },
    editDialogHeadline () {
      if (this.isEditIngredientNew) {
        return this.$t('component.ingredient_mgmt.edit_dialog.headline_create')
      }
      return this.$t('component.ingredient_mgmt.edit_dialog.headline_edit')
    }
  }
}
</script>

<style scoped>
</style>
