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
          v-if="recipe.hasImage"
          placeholder-src="~assets/cocktail-solid.png"
          :ratio="16/9"
          class="col-12 col-sm-6 col-md-3 rounded-borders q-px-xs"
        />
        <q-img
          v-else
          :ratio="16/9"
          class="col-12 col-sm-6 col-md-3 rounded-borders q-px-xs"
          placeholder-src="~assets/cocktail-solid.png"
          src="~assets/cocktail-solid.png"
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
                    :ingredients="recipe.ingredients"
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
          <div class="row">
            <div class="col"/>
            <div class="col" style="display: contents; max-width: max-content">
              by {{ recipe.ownerName }}
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
        return 'add manually'
      }
      return 'not owned'
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
