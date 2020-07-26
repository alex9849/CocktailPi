<template>
  <draggable
    :value="ingredients"
    @input="updateOneElementProductionStepList"
    @start="ingredientDrag = true"
    @end="ingredientDrag = false"
    :disabled="!editable"
    draggable=".dragItem"
    group="ingredients"
    tag="div"
    class="rounded-borders q-list q-list--bordered q-list--separator"
    :animation="200"
  >
    <q-item
      v-for="(productionStep, index) in ingredients"
      :key="index"
      class="dragItem"
    >
      <q-item-section avatar>
        <q-avatar color="grey">{{ index + 1}}.</q-avatar>
      </q-item-section>
      <q-item-section v-if="productionStep.length === 1 && !ingredientDrag">
        {{ productionStep[0].amount }}ml {{ productionStep[0].ingredient.name }}
      </q-item-section>
      <q-item-section v-if="productionStep.length === 1 && !ingredientDrag && editable" side>
        <q-btn
          :icon="mdiPencilOutline"
          @click="showIngredientEditor(productionStep, productionStep[0])"
          dense
          flat
          rounded
        />
      </q-item-section>
      <q-item-section v-if="productionStep.length === 1 && !ingredientDrag && editable" side>
        <q-btn
          :icon="mdiDelete"
          @click="removeIngredient(productionStep, productionStep[0])"
          dense
          flat
          rounded
        />
      </q-item-section>

      <q-item-section v-else-if="productionStep.length !== 1 || ingredientDrag">
        <draggable
          :value="productionStep"
          @input="updateProductionStepList(productionStep, $event)"
          @start="ingredientDrag = true"
          @end="ingredientDrag = false"
          :disabled="!editable"
          draggable=".dragItem"
          group="ingredients"
          tag="div"
          class="q-list q-list--bordered q-list--separator"
          :animation="200"
        >
          <q-item v-for="(ingredient, index) in productionStep" :key="index" class="dragItem">
            <q-item-section>
              {{ ingredient.amount }}ml {{ ingredient.ingredient.name }}
            </q-item-section>
            <q-item-section side v-if="editable">
              <q-btn
                :icon="mdiPencilOutline"
                @click="showIngredientEditor(productionStep, ingredient)"
                dense
                flat
                rounded
              />
            </q-item-section>
            <q-item-section side v-if="editable">
              <q-btn
                :icon="mdiDelete"
                @click="removeIngredient(productionStep, ingredient)"
                dense
                flat
                rounded
              />
            </q-item-section>
          </q-item>
        </draggable>
      </q-item-section>
    </q-item>
    <q-item slot="header">
      <q-item-section>
        <q-item-label header>Ingredients</q-item-label>
      </q-item-section>

      <q-item-section side v-if="editable">
        <q-btn
          :icon="mdiPlusCircleOutline"
          @click="showIngredientEditor(null)"
          dense
          flat
          rounded
        />
      </q-item-section>

      <q-dialog
        v-model="showIngredientEditorDialog"
        @hide="closeIngredientEditor"
      >
        <q-card style="width: 400px">
          <q-card-section class="text-center innerpadding">
            <h5 style="margin: 5px">{{ addIngredient?"Add Ingredient":"Edit Ingredient" }}</h5>
            <ingredient-form
              v-model="editIngredient"
              :headline="addIngredient?'Add Ingredient':'Edit Ingredient'"
              @valid="ingridientValid = true"
              @invalid="ingridientValid = false"
              @submit="saveEditIngredient"
            >
              <template slot="below">
                <div class="q-pa-md q-gutter-sm">
                  <q-btn
                    style="width: 100px"
                    color="negative"
                    label="Abort"
                    no-caps
                    @click="closeIngredientEditor"
                  />
                  <q-btn
                    type="submit"
                    style="width: 100px"
                    color="positive"
                    label="Save"
                    no-caps
                    :disable="!ingridientValid"
                  />
                </div>
              </template>
            </ingredient-form>
          </q-card-section>
        </q-card>
      </q-dialog>

    </q-item>
  </draggable>
</template>

<script>
  import {mdiDelete, mdiPencilOutline, mdiPlusCircleOutline} from "@quasar/extras/mdi-v5";
  import IngredientForm from "./IngredientForm";
  import draggable from 'vuedraggable';

  const cloneDeep = require('lodash/clonedeep');

  export default {
    name: "IngredientList",
    components: {IngredientForm, draggable},
    props: {
      value: {
        type: Array,
        required: true,
        default: () => []
      },
      editable: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        ingredients: [],
        showIngredientEditorDialog: false,
        addIngredient: false,
        ingridientValid: false,
        valid: false,
        ingredientDrag: false,
        newIngredient: {
          amount: '',
          ingredient: null
        },
        editIngredient: {
          amount: '',
          ingredient: null
        },
        editIngredientGroupIndex: -1,
        editIngredientIndex: -1,
        disableNextUpdateProductionStepList: false,
      }
    },
    watch: {
      value: {
        deep: true,
        handler() {
          this.ingredients = cloneDeep(this.value);
        }
      }
    },
    methods: {
      log(el) {
        console.log(el);
      },
      updateProductionStepList(group, updated) {
        if (updated.length === 0) {
          this.ingredients = this.ingredients.filter(x => x != group);
        } else {
          //Merge complete productionsteps into others
          for (let updatedListItem of updated) {
            if (Array.isArray(updatedListItem)) {
              let updatedListItemIndex = updated.indexOf(updatedListItem);
              updated.splice(updatedListItemIndex, 1, ...updatedListItem);
              //This is needed, because the updateOneElementProductionStepList()-Method
              // would otherwise reset your ingredients, because it works with old data
              this.disableNextUpdateProductionStepList = true;
              this.ingredients = this.ingredients.filter(x => x != updatedListItem);
            }
          }
          //Search and merge duplicates
          let iterationIndex = 0;
          let ingredientIdsToIndex = new Map();
          for (let updatedListItem of updated) {
            if(ingredientIdsToIndex.has(updatedListItem.ingredient.id)) {
              let index = ingredientIdsToIndex.get(updatedListItem.ingredient.id);
              updated[index].amount += updatedListItem.amount;
              updated.splice(iterationIndex, 1);
            } else {
              ingredientIdsToIndex.set(updatedListItem.ingredient.id, iterationIndex)
            }
            iterationIndex++;
          }
          let groupIndex = this.ingredients.indexOf(group);
          let newIngredients = Object.assign([], this.ingredients);
          newIngredients[groupIndex] = updated;
          this.ingredients = newIngredients;
        }
        this.emitChange();
      },
      updateOneElementProductionStepList(updated) {
        if(this.disableNextUpdateProductionStepList) {
          this.disableNextUpdateProductionStepList = false
          return;
        }
        for (let updatedListItem of updated) {
          if (!Array.isArray(updatedListItem)) {
            let updatedListItemIndex = updated.indexOf(updatedListItem);
            updated.splice(updatedListItemIndex, 1, [updatedListItem]);
          }
        }
        this.ingredients = updated;
        this.emitChange();
      },
      emitChange() {
        this.$emit('input', this.ingredients);
      },
      showIngredientEditor() {
        this.showIngredientEditor(null)
      },
      showIngredientEditor(group, ingredient) {
        this.addIngredient = true;
        if (ingredient) {
          this.editIngredient = Object.assign({}, ingredient);
          this.addIngredient = false;
          this.editIngredientGroupIndex = this.ingredients.indexOf(group);
          this.editIngredientIndex = this.ingredients[this.editIngredientGroupIndex].indexOf(ingredient);
        }
        this.showIngredientEditorDialog = true;
      },
      removeIngredient(group, ingredient) {
        let groupIndex = this.ingredients.indexOf(group);
        let newGroup = group.filter(x => x !== ingredient);
        if (newGroup.length === 0) {
          this.ingredients = this.ingredients.filter(x => x !== group)
        } else {
          let newIngredients = Object.assign([], this.ingredients);
          newIngredients[groupIndex] = newGroup;
          this.ingredients = newIngredients;
        }
        this.emitChange();
      },
      closeIngredientEditor() {
        this.editIngredient = JSON.parse(JSON.stringify(this.newIngredient));
        this.showIngredientEditorDialog = false;
        this.editIngredientIndex = -1;
        this.editIngredientGroupIndex = -1;
      },
      saveEditIngredient() {
        if (this.editIngredientIndex < 0) {
          this.ingredients.push([this.editIngredient]);
        } else {
          this.ingredients[this.editIngredientGroupIndex][this.editIngredientIndex] = this.editIngredient;
        }
        this.closeIngredientEditor();
        this.emitChange();
      }
    },
    created() {
      this.mdiPlusCircleOutline = mdiPlusCircleOutline;
      this.mdiPencilOutline = mdiPencilOutline;
      this.mdiDelete = mdiDelete;
    }
  }
</script>

<style scoped>

</style>
