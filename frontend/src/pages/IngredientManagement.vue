<template>
  <q-page class="page-content" padding>
    <h5>Ingredient Management
      <q-btn
        :icon="mdiInformation"
        dense
        flat
        round
        color="grey"
        @click="showHelp = true"
      >
      </q-btn>
    </h5>
    <h6>Ingredient groups</h6>
    <c-ingredient-group-management/>
    <h6>Ingredients</h6>
    <c-ingredient-management/>
    <q-dialog
      v-model:model-value="showHelp"
    >
      <q-card>
        <q-card-section>
          <h6>Ingredients:</h6>
          <p>
            Ingredients are concrete ingredients that can be directly added to a glass and can
            be bought from the supermarket. Ingredients can usually be assigned to a pump.
          </p>
          <h6>Ingredient groups:</h6>
          <p>
            Ingredient groups are groups that consist of multiple "child ingredients" or other ingredient groups.
            Recipes can contain ingredient groups. Before ordering such an recipe, the user has to replace that
            ingredient group with an ingredient that belongs to that group. The software automatically does that for the user,
            if such an ingredient was assigned to a pump or is marked as being in the 'Bar'. Ingredients on pumps get preferred.
          </p>
          <p>
            <b>Example:</b> You have a specific ingredient called 'Whisky 08/15'.
            This whisky is categorized under the ingredient group 'Bourbon'. The group 'Bourbon', in turn,
            falls under the broader category 'Whisky'. There is an additional child ingredient
            group related to 'Whisky' known as 'Scotch'.
          </p>
          <p>
            You've included 'Whisky 08/15' in your bar's inventory. Due to the hierarchical structure,
            CocktailMaker now recognizes that 'Whisky 08/15' can be utilized not only in recipes that
            specifically require this ingredient, but also in various recipes that call for any type of
            'Bourbon' or 'Whisky'. However, it's important to note that 'Whisky 08/15' cannot substitute
            for recipes that specifically demand 'Scotch' or other distinct ingredients.
          </p>
        </q-card-section>
        <q-card-section class="row justify-center">
          <q-btn
            class="col-auto"
            color="grey"
            @click="showHelp = false"
          >
            Close
          </q-btn>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script>
import CIngredientManagement from 'components/CIngredientManagement'
import CIngredientGroupManagement from 'components/CIngredientGroupManagement'
import { mdiInformation } from '@mdi/js'

export default {
  name: 'IngredientManagement',
  components: { CIngredientGroupManagement, CIngredientManagement },
  data: () => {
    return {
      showHelp: false
    }
  },
  created () {
    this.mdiInformation = mdiInformation
  }
}
</script>

<style scoped>
</style>
