<template>
  <q-card
    :style="'background-color: ' + backgroundColor"
  >
    <q-card-section
      style="padding: 10px"
    >
      <div
        class="row q-gutter-y-sm"
        style="position: relative"
      >
        <div
          style="position: absolute; z-index: 1;"
        >
          <slot name="beforePicture"/>
        </div>
        <q-img
          :src="$store.getters['auth/getFormattedServerAddress'] + '/api/recipe/' + recipe.id + '/image?timestamp=' + recipe.lastUpdate.getMilliseconds()"
          placeholder-src="../assets/cocktail-solid.png"
          :ratio="16/9"
          class="col-12 col-sm-6 col-md-3 rounded-borders q-px-xs"
        />
        <div class="col-12 col-sm-6 col-md-9 q-px-xs">
          <div class="row">
            <div class="col">
              <h5
                style="margin: 0; padding-bottom: 10px;"
              >
                <slot name="headline">
                  <b>{{ recipe.name }}</b>
                </slot>
              </h5>
            </div>
            <div class="col/"/>
            <div class="row">
              <div class="col"/>
              <div
                class="col"
                style="display: contents; max-width: max-content;">
                <slot name="topRight">
                  <c-recipe-fabricable-icon
                    :recipe="recipe"
                  />
                </slot>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col dotted-overflow">
              {{ recipe.description }}
            </div>
          </div>
          <div class="row" style="margin-top: 10px">
            <q-chip
              v-for="(ingredient) in uniqueIngredientNames(recipe.productionSteps)"
              dense
              style="margin-left: 0; margin-right: 5px"
              square
              outline
              :color="ingredientChipColor(ingredient.id)"
            >
              <q-tooltip
                v-if="!!ingredientChipTooltip(ingredient.id)"
              >
                {{ ingredientChipTooltip(ingredient.id) }}
              </q-tooltip>
              {{ ingredient.name }}
            </q-chip>
          </div>
          <div class="row">
            <div class="col"/>
            <div class="col" style="display: contents; max-width: max-content">
              by {{ recipe.owner.username }}
            </div>
          </div>
        </div>
      </div>
      <slot name="bottom"/>
    </q-card-section>
  </q-card>
</template>

<script>

import {mapGetters} from 'vuex'
import CRecipeFabricableIcon from 'components/CRecipeFabricableIcon'

export default {
  name: 'CRecipeCard',
  components: { CRecipeFabricableIcon },
  props: {
    recipe: {
      type: Object,
      required: true
    },
    showIngredients: {
      type: Boolean,
      default: false
    },
    backgroundColor: {
      type: String,
      default: '#fafafa'
    }
  },
  computed: {
    ...mapGetters({
      getPumpIngredients: 'pumpLayout/getPumpIngredients',
      ownedIngredients: 'bar/getOwnedIngredients'
    })
  },
  methods: {
    hasPumpLayoutIngredient (ingredientId) {
      return this.getPumpIngredients.some(x => x.id === ingredientId)
    },
    ingredientChipColor (ingredientId) {
      if (this.getPumpIngredients.some(x => x.id === ingredientId)) {
        return 'green'
      }
      if (this.ownedIngredients.some(x => x.id === ingredientId)) {
        return 'orange-6'
      }
      return 'red'
    },
    ingredientChipTooltip (ingredientId) {
      if (this.getPumpIngredients.some(x => x.id === ingredientId)) {
        return ''
      }
      if (this.ownedIngredients.some(x => x.id === ingredientId)) {
        return 'add manually'
      }
      return 'not owned'
    },
    uniqueIngredientNames (productionSteps) {
      if (!this.showIngredients) {
        return []
      }
      const unique = new Map()
      for (const productionStep of productionSteps) {
        if (productionStep.type !== 'addIngredients') {
          continue
        }
        for (const ingredient of productionStep.stepIngredients) {
          unique.set(ingredient.ingredient.id, ingredient.ingredient)
        }
      }
      return Array.from(unique.values())
    }
  }
}
</script>

<style scoped>
.dotted-overflow {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
