<template>
  <c-q-headlined-card
    :headline="headline"
    :card-class="cardClass"
    :icon="icon"
    :icon-background-class="iconBackgroundClass"
    :icon-class="iconClass"
  >
    <template v-slot:content v-if="ingredientGroupReplacements.length !== 0">
      <div class="row justify-center items-center q-pa-sm">
        <q-table
          :columns="columns"
          :rows="tableRows"
          hide-bottom
          hide-header
          dense
          flat
          grid
          class="full-width"
          :class="cardClass"
          card-container-class="justify-center"
        >
          <template v-slot:item="{ row }">
            <div
              class="q-pa-xs col-xs-12 col-sm-9 col-md-6 col-lg-3 grid-style-transition"
            >
            <q-card :class="{'bg-green-4': !!row.replacement, 'bg-deep-orange-3': !row.replacement }">
              <q-card-section class="q-pa-sm">
                <div class="q-gutter-xs">
                  <q-input
                    outlined
                    disable
                    label="Production step"
                    readonly
                    :model-value="row.productionStep"
                    dense
                  >
                    <template v-slot:label>
                      <p class="text-grey-10 text-weight-medium">Production step</p>
                    </template>
                  </q-input>
                  <q-input
                    outlined
                    disable
                    label="Ingredient group"
                    :model-value="row.ingredientGroup.name"
                    dense
                  >
                    <template v-slot:label>
                      <p class="text-grey-10 text-weight-medium">Ingredient group</p>
                    </template>
                  </q-input>
                  <c-ingredient-selector
                    dense
                    outlined
                    :only-group-leafs="row.ingredientGroup.id"
                    label="Replacement"
                    :selected="row.replacement"
                    @update:selected="onReplacementUpdate(row.productionStep, row.ingredientGroup.id, $event)"
                  >
                    <template v-slot:label>
                      <p class="text-grey-10 text-weight-medium">Replacement</p>
                    </template>
                  </c-ingredient-selector>
                </div>
              </q-card-section>
            </q-card>
            </div>
          </template>
        </q-table>
      </div>
    </template>
  </c-q-headlined-card>
</template>

<script>
import CQHeadlinedCard from 'components/CQHeadlinedCard'
import { mdiAlertOutline, mdiCheck } from '@quasar/extras/mdi-v5'
import CIngredientSelector from 'components/CIngredientSelector'
export default {
  name: 'CMakeCocktailDialogIngredientGroupReplacements',
  components: { CIngredientSelector, CQHeadlinedCard },
  props: {
    ingredientGroupReplacements: Array,
    allIngredientGroupsReplaced: Boolean
  },
  emits: ['ReplacementUpdate'],
  data () {
    return {
      columns: [
        { name: 'productionStep', label: 'Production step', field: 'productionStep' },
        { name: 'ingredientGroup', label: 'Ingredient group', field: 'ingredientGroup' },
        { name: 'replacement', label: 'Replacement', field: 'replacement' }
      ]
    }
  },
  methods: {
    onReplacementUpdate (prodStepNr, toReplaceId, replacement) {
      this.$emit('ReplacementUpdate', { prodStepNr: prodStepNr - 1, toReplaceId, replacement })
    }
  },
  computed: {
    tableRows () {
      const data = []
      let prodStepNr = 0
      for (const prodStep of this.ingredientGroupReplacements) {
        prodStepNr++
        for (const ingredientGroupReplacement of prodStep) {
          data.push({
            productionStep: prodStepNr,
            ingredientGroup: ingredientGroupReplacement.ingredientGroup,
            replacement: ingredientGroupReplacement.selectedReplacement
          })
        }
      }
      return data
    },
    isFulfilled () {
      return this.allIngredientGroupsReplaced
    },
    cardClass () {
      return {
        'bg-warning': !this.isFulfilled,
        'bg-info': this.isFulfilled
      }
    },
    headline () {
      if (this.isFulfilled) {
        return 'All ingredient-groups have been replaced with concrete ingredients!'
      } else {
        return 'The following ingredient-groups have to get real existing ingredients assigned:'
      }
    },
    iconClass () {
      return this.isFulfilled ? 'text-white' : 'text-orange-14'
    },
    iconBackgroundClass () {
      return this.isFulfilled ? 'bg-light-green-14' : 'bg-warning'
    },
    icon () {
      return this.isFulfilled ? mdiCheck : mdiAlertOutline
    }
  }
}
</script>

<style scoped>
</style>
