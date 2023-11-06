<template>
  <c-q-headlined-card
    :headline="headline"
    :card-class="cardClass"
    :icon="icon"
    :icon-background-class="iconBackgroundClass"
    :icon-class="iconClass"
    :hide-content-slot="tableRows.length === 0"
  >
    <template v-slot:content>
      <div class="row justify-center items-center q-pa-sm">
        <div
          v-for="(row, idx) in tableRows"
          :key="idx"
          class="q-pa-xs col-xs-12 col-sm-9 col-md-6 col-lg-3 grid-style-transition"
        >
          <c-make-cocktail-dialog-ingredient-group-replacements-card
            :class="{'bg-green-4': !!row.replacement, 'bg-deep-orange-3': !row.replacement }"
            @ReplacementUpdate="onReplacementUpdate(row.productionStep, row.ingredientGroup.id, $event)"
            :replacement-entry="row"
          />
        </div>
      </div>
    </template>
  </c-q-headlined-card>
</template>

<script>
import CQHeadlinedCard from 'components/CQHeadlinedCard'
import { mdiClose, mdiCheck } from '@quasar/extras/mdi-v5'
import CMakeCocktailDialogIngredientGroupReplacementsCard
  from 'components/CMakeCocktailDialogIngredientGroupReplacementsCard'
export default {
  name: 'CMakeCocktailDialogIngredientGroupReplacements',
  components: { CMakeCocktailDialogIngredientGroupReplacementsCard, CQHeadlinedCard },
  props: {
    ingredientGroupReplacements: Array,
    allIngredientGroupsReplaced: Boolean
  },
  emits: ['ReplacementUpdate'],
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
        'bg-light-blue-3': this.isFulfilled
      }
    },
    headline () {
      if (this.isFulfilled) {
        return this.$t('component.make_cocktail_group_replacements.fulfilled_msg')
      } else {
        return this.$t('component.make_cocktail_group_replacements.not_fulfilled_msg')
      }
    },
    iconClass () {
      return this.isFulfilled ? 'text-white' : 'text-negative'
    },
    iconBackgroundClass () {
      return this.isFulfilled ? 'bg-light-green-14' : 'bg-warning'
    },
    icon () {
      return this.isFulfilled ? mdiCheck : mdiClose
    }
  }
}
</script>

<style scoped>
</style>
