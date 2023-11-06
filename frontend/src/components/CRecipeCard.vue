<template>
  <q-card
    :style="'background-color: ' + backgroundColor"
  >
    <q-card-section
      style="padding: 10px"
    >
      <div
        class="row q-col-gutter-md"
      >
        <div class="col-12 col-sm-4 col-md-4 col-lg-3 flex">
          <q-img
            :src="$store.getters['auth/getFormattedServerAddress'] + '/api/recipe/' + recipe.id + '/image?timestamp=' + recipe.lastUpdate.getTime()"
            v-if="recipe.hasImage"
            placeholder-src="~assets/cocktail-solid.png"
            :ratio="16/9"
            style="flex-grow: 1"
            class="rounded-borders"
          />
          <q-img
            v-else
            :ratio="16/9"
            style="flex-grow: 1"
            class="rounded-borders"
            placeholder-src="~assets/cocktail-solid.png"
            src="~assets/cocktail-solid.png"
          />
        </div>
        <div class="col-12 col-sm-8 col-md-8 col-lg-9" style="display: flex; flex-direction: column">
          <div class="row">
            <div class="col">
              <p class="text-h5">
                <slot name="headline">
                  <b>{{ recipe.name }}</b>
                </slot>
              </p>
            </div>
            <div class="col/"/>
            <div class="row">
              <div class="col"/>
              <div
                class="col"
                style="display: contents; max-width: max-content;">
                <slot name="topRight">
                  <c-recipe-fabricable-icon
                    :ingredients="recipe.ingredients"
                  />
                </slot>
              </div>
            </div>
          </div>
          <div class="row" v-if="recipe.description">
            <div class="col dotted-overflow">
              {{ recipe.description }}
            </div>
          </div>
          <div v-if="showIngredients"
               class="row"
               style="margin-top: 10px"
          >
            <q-chip
              v-for="ingredient in recipe.ingredients"
              :key="ingredient.id"
              dense
              style="margin-left: 0; margin-right: 5px"
              square
              outline
              :ripple="false"
              :color="ingredientChipColor(ingredient)"
            >
              <q-tooltip
                v-if="!!ingredientChipTooltip(ingredient)"
              >
                {{ ingredientChipTooltip(ingredient) }}
              </q-tooltip>
              {{ ingredient.name }}
            </q-chip>
          </div>
          <div style="flex-grow: 1"/>
          <div class="row justify-end">
            <p class="text-grey">
              {{ $t('component.recipe_card.owner_name', {name: recipe.ownerName}) }}
            </p>
          </div>
        </div>
      </div>
      <slot name="bottom"/>
    </q-card-section>
  </q-card>
</template>

<script>

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
      default: true
    },
    backgroundColor: {
      type: String,
      default: '#fafafa'
    }
  },
  methods: {
    ingredientChipColor (ingredient) {
      if (ingredient.onPump) {
        return 'green'
      }
      if (ingredient.inBar) {
        return 'orange-6'
      }
      return 'red'
    },
    ingredientChipTooltip (ingredient) {
      if (ingredient.onPump) {
        return ''
      }
      if (ingredient.inBar) {
        return this.$t('component.recipe_card.ingredient_add_manually_tooltip')
      }
      return this.$t('component.recipe_card.ingredient_not_owned_tooltip')
    }
  }
}
</script>

<style scoped>
</style>
