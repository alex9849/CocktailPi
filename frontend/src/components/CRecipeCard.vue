<template>
  <q-card
    :style="'background-color: ' + backgroundColor"
  >
    <q-card-section
      style="padding: 10px"
    >
      <div
        class="row q-gutter-sm"
        style="position: relative"
      >
        <div
          style="position: absolute; z-index: 1;"
        >
          <slot name="beforePicture"/>
        </div>
        <q-img
          :src="'/api/recipe/' + recipe.id + '/image?timestamp=' + recipe.lastUpdate.getMilliseconds()"
          placeholder-src="../assets/cocktail-solid.png"
          :ratio="16/9"
          class="col rounded-borders max-picture-size"
        />
        <div class="col" style="min-width: 200px;">
          <div class="row">
            <div class="col">
              <h5
                style="margin: 0; padding-bottom: 10px;"
              >
                <b>{{ recipe.name}}</b>
              </h5>
            </div>
            <div class="col/"/>
            <div class="col" style="display: contents; max-width: max-content">
              <slot name="headlineRight"/>
            </div>
            <div class="row">
              <div class="col"/>
              <div
                class="col"
                style="display: contents; max-width: max-content;">
                <slot name="topRight"/>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col">
              {{ shortenedDescription }}
            </div>
          </div>
          <div class="row" style="margin-top: 10px">
            <q-chip
              v-for="(ingredient) in uniqueIngredientNames(recipe.recipeIngredients)"
              dense
              style="margin-left: 0; margin-right: 5px"
              square
              outline
              :color="hasPumpLayoutIngredient(ingredient.id)? 'green':'red'"
            >
              <q-tooltip
                v-if="!hasPumpLayoutIngredient(ingredient.id)"
              >
                {{ "Ingredient missing" }}
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

  import {mapGetters} from "vuex";

  export default {
    name: "CRecipeCard",
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
        getPumpIngredients: 'pumpLayout/getPumpIngredients'
      }),
      shortenedDescription() {
        let sDesc = this.recipe.description.substring(0, Math.min(80, this.recipe.description.length));
        if(sDesc.length < this.recipe.description.length) {
          sDesc += " ...";
        }
        return sDesc;
      }
    },
    methods: {
      getIngredientColor(ingredientId) {

      },
      hasPumpLayoutIngredient(ingredientId) {
        return this.getPumpIngredients.some(x => x.id === ingredientId);
      },
      uniqueIngredientNames(productionSteps) {
        if(!this.showIngredients) {
          return [];
        }
        let unique = new Map();
        for (let productionStep of productionSteps) {
          for (let ingredient of productionStep) {
            unique.set(ingredient.ingredient.id, ingredient.ingredient);
          }
        }
        return Array.from(unique.values());
      }
    }
  }
</script>

<style scoped>

  .max-picture-size {
    min-width: 200px
  }

  @media screen and (min-width: 500px) {
    .max-picture-size {
      max-width: 250px;
    }
  }

</style>
