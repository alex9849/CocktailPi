<template>
  <q-card
    :style="{ backgroundColor: backgroundColor, color: textColor }"
    class="full-height"
    :dark="isDark"
  >
    <q-card-section class="full-height q-pa-sm">
      <div class="row full-height">
        <div class="col-12 row">
          <div class="col-12 col-xsm-4 col-sm-4 col-md-4 col-lg-3 flex">
            <div class="relative-position full-height full-width">
              <div class="absolute-top-left" style="z-index: 1">
                <q-badge class="q-ma-xs">
                  {{ alcoholPercentageDisplay }}
                </q-badge>
              </div>
              <q-img
                :src="imageUrl + '&width=500'"
                v-if="recipe.hasImage"
                placeholder-src="~assets/cocktail-solid.png"
                :ratio="16 / 9"
                class="rounded-borders full-height"
              />
              <q-img
                v-else
                :ratio="16 / 9"
                class="rounded-borders full-height"
                placeholder-src="~assets/cocktail-solid.png"
                src="~assets/cocktail-solid.png"
              />
            </div>
          </div>
          <div class="col-12 col-xsm-8 col-sm-8 col-md-8 col-lg-9 q-pl-xsm-sm q-pt-xsm-none q-pt-sm flex column">
            <div class="row">
              <div class="col-12 row justify-around">
                <div class="col">
                  <p class="text-h5 dotted-overflow-1">
                    <slot name="headline">
                      <b>{{ recipe.name }}</b>
                    </slot>
                  </p>
                </div>
                <div class="col-grow" />
                <div class="col-shrink">
                  <slot name="topRight">
                    <c-recipe-fabricable-icon :ingredients="recipe.ingredients" />
                  </slot>
                </div>
              </div>

              <div class="col-12 row" v-if="recipe.description">
                <div class="col dotted-overflow-2">
                  {{ recipe.description }}
                </div>
              </div>

              <div class="col-12 dotted-overflow-2 row" v-if="showIngredients" style="margin-top: 10px">
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
                  <q-tooltip v-if="ingredientChipTooltip(ingredient)">
                    {{ ingredientChipTooltip(ingredient) }}
                  </q-tooltip>
                  {{ ingredient.name }}
                </q-chip>
              </div>
            </div>

            <div style="flex-grow: 1" />
            <div class="row justify-end">
              <p :style="{ color: ownerColor }">
                {{ $t('component.recipe_card.owner_name', { name: recipe.ownerName }) }}
              </p>
            </div>
          </div>
        </div>
      </div>
      <slot name="bottom" />
    </q-card-section>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useI18n } from 'vue-i18n'
import CRecipeFabricableIcon from 'components/CRecipeFabricableIcon'
import { calcTextColor, complementColor, isDark as isDarkUtil } from 'src/mixins/utils'

// Props
const props = defineProps({
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
})

const store = useStore()
const { t } = useI18n()

// Computed
const isDark = computed(() => isDarkUtil(props.backgroundColor))
const textColor = computed(() => calcTextColor(props.backgroundColor))
const ownerColor = computed(() => complementColor(props.backgroundColor, 50))
const imageUrl = computed(() => {
  let url = store.getters['auth/getFormattedServerAddress'] + '/api/recipe/' + props.recipe.id + '/image?timestamp=' + props.recipe.lastUpdate.getTime()
  if (props.recipe.type === 'ingredientrecipe') {
    url += '&isIngredient=true'
  }
  return url
})

const alcoholPercentageDisplay = computed(() => {
  if (props.recipe.minAlcoholContent === props.recipe.maxAlcoholContent) {
    if (props.recipe.maxAlcoholContent === 0) {
      return 'No Alc.'
    }
    return String(props.recipe.maxAlcoholContent) + '% Alc.'
  }
  return String(props.recipe.minAlcoholContent) + '-' + String(props.recipe.maxAlcoholContent) + '% Alc.'
})

/*
const alcoholPercentageColor = computed(() => {
  const maxRed = 40
  let percentage = Math.min(maxRed, props.recipe.maxAlcoholContent)
  percentage = percentage / maxRed * 100
  const colorModifier = (255 * percentage) / 100
  const red = Math.round(colorModifier)
  const green = Math.round(255 - colorModifier)
  return `rgb(${red}, ${green}, 0)`
})
 */

// Methods
function ingredientChipColor (ingredient) {
  if (ingredient.onPump) {
    return 'positive'
  }
  if (ingredient.inBar) {
    return 'orange-6'
  }
  return 'negative'
}

function ingredientChipTooltip (ingredient) {
  if (ingredient.onPump) {
    return ''
  }
  if (ingredient.inBar) {
    return t('component.recipe_card.ingredient_add_manually_tooltip')
  }
  return t('component.recipe_card.ingredient_not_owned_tooltip')
}
</script>

<style scoped>
</style>

<style scoped>
</style>
