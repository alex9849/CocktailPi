<template>
  <q-card
    :style="{'backgroundColor': backgroundColor, 'color': textColor}"
    class="full-height"
    :dark="isDark"
  >
    <q-card-section
      class="full-height q-pa-sm"
    >
      <div
        class="row full-height"
      >
        <div class="col-12 row">
          <div class="col-12 col-xsm-4 col-sm-4 col-md-4 col-lg-3 flex">
            <q-img
              :src="imageUrl + '&width=500'"
              v-if="recipe.hasImage"
              placeholder-src="~assets/cocktail-solid.png"
              :ratio="16/9"
              class="rounded-borders"
            />
            <q-img
              v-else
              :ratio="16/9"
              class="rounded-borders"
              placeholder-src="~assets/cocktail-solid.png"
              src="~assets/cocktail-solid.png"
            />
          </div>
          <div class="col-12 col-xsm-8 col-sm-8 col-md-8 col-lg-9 q-pl-xsm-sm q-pt-xsm-none q-pt-sm flex column">
            <div class="row">
              <div class="col-12 row justify-around">
                <div class="col">
                  <p class="text-h5 dotted-overflow-2">
                    <slot name="headline">
                      <b>{{ recipe.name }}</b>
                    </slot>
                  </p>
                </div>
                <div class="col-grow" />
                <div class="col-shrink">
                  <slot name="topRight">
                    <c-recipe-fabricable-icon
                      :ingredients="recipe.ingredients"
                    />
                  </slot>
                </div>
              </div>
              <div class="col-12 row" v-if="recipe.description">
                <div class="col dotted-overflow-2">
                  {{ recipe.description }}
                </div>
              </div>
              <div class="col-12 dotted-overflow-2 row" v-if="showIngredients"
                   style="margin-top: 10px"
              >
                <q-chip
                  v-for="ingredient in recipe.ingredients"
                  :key="ingredient.id"
                  dense
                  style="margin-left: 0; margin-right: 5px"
                  square
                  text-color="white"
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
            </div>

            <div style="flex-grow: 1"/>
            <div class="row justify-end">
              <p
                :style="{'color': ownerColor}"
              >
                {{ $t('component.recipe_card.owner_name', {name: recipe.ownerName}) }}
              </p>
            </div>
          </div>
        </div>
      </div>
      <slot name="bottom"/>
    </q-card-section>
  </q-card>
</template>

<script>

import CRecipeFabricableIcon from 'components/CRecipeFabricableIcon'
import { calcTextColor, complementColor, isDark } from 'src/mixins/utils'

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
        return 'positive'
      }
      if (ingredient.inBar) {
        return 'orange-6'
      }
      return 'negative'
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
  },
  computed: {
    isDark () {
      return isDark(this.backgroundColor)
    },
    textColor () {
      return calcTextColor(this.backgroundColor)
    },
    ownerColor () {
      return complementColor(this.backgroundColor, 50)
    },
    imageUrl () {
      let url = this.$store.getters['auth/getFormattedServerAddress'] + '/api/recipe/' + this.recipe.id + '/image?timestamp=' + this.recipe.lastUpdate.getTime()
      if (this.recipe.type === 'ingredientrecipe') {
        url += '&isIngredient=true'
      }
      return url
    }
  }
}
</script>

<style scoped>
</style>
