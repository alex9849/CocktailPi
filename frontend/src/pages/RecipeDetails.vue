<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="Recipe Details"/>
    </q-breadcrumbs>
    <div class="row innerpadding" style="display: inline">
      <div class="col vcenter">
        <h5 style="margin-bottom: 0">{{ recipe.name }}</h5>
      </div>
    </div>
    <div class="row innerpadding">
      <div class="col q-gutter-sm" style="display: flex; justify-content: end">
        <q-btn
          color="grey"
          :to="{name: 'recipeedit', params: {id: $route.params.id}}"
        >
          Edit
        </q-btn>
        <q-btn
          color="green"
        >
          Make cocktail
        </q-btn>
      </div>
    </div>
    <div class="row innerpadding">
      <div class="col">
        <q-card flat bordered>
          <q-card-section>
            Short description: {{ recipe.description }}
          </q-card-section>
        </q-card>
      </div>
    </div>
    <div class="row innerpadding">
      <div class="col" style="min-width: max-content">
        <q-img
          src="../assets/cocktail-solid.png"
          :ratio="16/9"
          class="rounded-borders"
          style="min-width: 250px"
        />
      </div>
      <div style="min-width: 200px" class="col">
        <q-list class="rounded-borders" bordered separator>
          <q-item-label header>Ingridients</q-item-label>
          <q-separator />
          <q-item v-for="(ingridient, index) in recipe.recipeIngredients">
            <q-item-section avatar>
              <q-avatar color="grey">{{ index + 1}}.</q-avatar>
            </q-item-section>
            <q-item-section>
              {{ ingridient.amount }}ml {{ ingridient.ingredient.name }}
            </q-item-section>
          </q-item>
        </q-list>
      </div>
    </div>
    <div class="row innerpadding">
      <div class="col">
        <q-card flat bordered style="min-height: 100px">
          <q-card-section>
            <b>Description:</b>
            <div style="min-width: 200px" class="col">
              {{ recipe.description }}
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>

  </q-page>
</template>

<script>
  import recipeService from '../services/recipe.service'

  export default {
    name: "RecipeDetails",
    data() {
      return {
        recipe: null
      }
    },
    created() {
      recipeService.getRecipe(this.$route.params.id)
        .then(recipe => this.recipe = recipe);
    }
  }
</script>

<style scoped>
  .innerpadding > * {
    padding: 10px;
  }
  .vcenter {
    display: inline-block;
    vertical-align: middle;
    float: none;
  }
</style>
