<template>
  <q-page class="page-content" padding>
    <h5>{{ $t('page.ingredient_mgmt.headline') }}
      <q-btn
        :icon="mdiInformation"
        dense
        flat
        round
        color="grey"
        @click="showHelp = true"
      >
      </q-btn>
    </h5>
    <h6>{{ $t('page.ingredient_mgmt.ingredient_groups.headline') }}</h6>
    <c-ingredient-group-management/>
    <h6>{{ $t('page.ingredient_mgmt.ingredients.headline') }}</h6>
    <c-ingredient-management/>
    <q-dialog
      v-model:model-value="showHelp"
    >
      <q-card>
        <q-card-section>
          <h6>{{ $t('page.ingredient_mgmt.help_dialog.ingredients.headline') }}</h6>
          <p style="white-space: pre-line">
            {{ $t('page.ingredient_mgmt.help_dialog.ingredients.text') }}
          </p>
          <h6>{{ $t('page.ingredient_mgmt.help_dialog.groups.headline') }}</h6>
          <p style="white-space: pre-line">
            {{ $t('page.ingredient_mgmt.help_dialog.groups.text') }}
          </p>
        </q-card-section>
        <q-card-section class="row justify-center">
          <q-btn
            class="col-auto"
            color="grey"
            @click="showHelp = false"
          >
            {{ $t('page.ingredient_mgmt.help_dialog.close_btn_label') }}
          </q-btn>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script>
import CIngredientManagement from 'components/CIngredientManagement'
import CIngredientGroupManagement from 'components/CIngredientGroupManagement'
import { mdiInformation } from '@mdi/js'

export default {
  name: 'IngredientManagement',
  components: { CIngredientGroupManagement, CIngredientManagement },
  data: () => {
    return {
      showHelp: false
    }
  },
  created () {
    this.mdiInformation = mdiInformation
  }
}
</script>

<style scoped>
</style>
