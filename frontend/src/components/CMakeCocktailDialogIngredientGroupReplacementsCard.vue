<template>
  <q-card>
    <q-card-section class="q-pa-sm">
      <div class="q-gutter-xs">
        <q-input
          outlined
          disable
          :label="$t('component.make_cocktail_group_replacements.card.prod_step_label')"
          readonly
          :model-value="replacementEntry.productionStep"
          dense
        >
          <template v-slot:label>
            <p class="text-grey-10 text-weight-medium">Production step</p>
          </template>
        </q-input>
        <q-input
          outlined
          disable
          :label="$t('component.make_cocktail_group_replacements.card.ingredient_group_label')"
          :model-value="replacementEntry.ingredientGroup.name"
          dense
        >
          <template v-slot:label>
            <p class="text-grey-10 text-weight-medium">Ingredient group</p>
          </template>
        </q-input>
        <c-ingredient-selector
          dense
          outlined
          :only-group-children="replacementEntry.ingredientGroup.id"
          filter-ingredient-groups
          fetch-instantly
          :loading="noInputReplacementOptionsLoading"
          :label="$t('component.make_cocktail_group_replacements.card.replacement_label')"
          hide-bottom-space
          :selected="replacementEntry.replacement"
          :no-input-options="noInputReplacementOptions"
          @update:selected="$emit('replacementUpdate', $event)"
        >
          <template v-slot:afterIngredientName="{scope}">
            <q-item-label
              v-if="scope.opt.onPump"
              caption
              class="text-green"
            >
              {{ $t('component.make_cocktail_group_replacements.card.tags.automatically_addable') }}
            </q-item-label>
            <q-item-label
              v-else-if="scope.opt.inBar"
              caption
              class="text-warning"
            >
              {{ $t('component.make_cocktail_group_replacements.card.tags.in_bar') }}
            </q-item-label>
            <q-item-label
              v-else
              caption
              class="text-negative"
            >
              {{ $t('component.make_cocktail_group_replacements.card.tags.not_in_bar') }}
            </q-item-label>
          </template>
          <template v-slot:label>
            <p class="text-grey-10 text-weight-medium">
              {{ $t('component.make_cocktail_group_replacements.card.replacement_label') }}
            </p>
          </template>
        </c-ingredient-selector>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import CIngredientSelector from 'components/CIngredientSelector'
import IngredientService from 'src/services/ingredient.service'
export default {
  name: 'CMakeCocktailDialogIngredientGroupReplacementsCard',
  components: { CIngredientSelector },
  props: {
    replacementEntry: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      noInputReplacementOptions: [],
      noInputReplacementOptionsLoading: false
    }
  },
  emits: ['replacementUpdate'],
  watch: {
    ingredientGroupId: {
      immediate: true,
      handler (newValue) {
        this.noInputReplacementOptionsLoading = true
        IngredientService.getIngredientsFilter(null, null, null,
          null, newValue, null, null, true)
          .then(ingredients => {
            this.noInputReplacementOptions.slice(0, this.noInputReplacementOptions.length)
            this.noInputReplacementOptions.push(...ingredients)
          })
          .finally(() => {
            this.noInputReplacementOptionsLoading = false
          })
      }
    }
  },
  computed: {
    ingredientGroupId () {
      return this.replacementEntry.ingredientGroup.id
    }
  }
}
</script>

<style scoped>

</style>
