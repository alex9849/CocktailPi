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
          :src="'/api/recipe/' + recipe.id + '/image?nocache=' + new Date().getTime()"
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
              {{ recipe.shortDescription }}
            </div>
          </div>
          <div class="row"/>
          <div class="row">
            <div class="col">
              Ingredients:
              <q-chip v-if="index < 4" v-for="(name, index) in uniqueIngredientNames(recipe.recipeIngredients)">
                {{ index !== 3?name:'...' }}
              </q-chip>
            </div>
            <div class="col"/>
            <div class="col" style="display: contents; max-width: max-content">
              by {{ recipe.owner.username }}
            </div>
          </div>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>

  export default {
    name: "CRecipeCard",
    props: {
      recipe: {
        type: Object,
        required: true
      },
      backgroundColor: {
        type: String,
        default: '#fafafa'
      }
    },
    methods: {
      uniqueIngredientNames(productionSteps) {
        let unique = new Set();
        for (let productionStep of productionSteps) {
          for (let ingredient of productionStep) {
            unique.add(ingredient.ingredient.name);
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
