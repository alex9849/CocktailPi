<template>
  <draggable
    :value="ingredients"
    @input="updateOneElementProductionStepList"
    :disabled="!editable"
    :options="{delay: 400, delayOnTouchOnly: true, touchStartThreshold: 10}"
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
      :style="'background-color: ' + ((index % 2 === 0)? row1Color:row2Color)"
    >
      <q-item-section avatar>
        <q-avatar color="grey">{{ index + 1}}.</q-avatar>
      </q-item-section>

      <q-item-section>
        <draggable
          :value="productionStep"
          @input="updateProductionStepList(productionStep, $event)"
          :disabled="!editable"
          :options="{delay: 400, delayOnTouchOnly: true, touchStartThreshold: 10}"
          draggable=".dragItem"
          group="ingredients"
          tag="div"
          class="q-list q-list--bordered q-list--separator"
          :animation="200"
        >
          <q-item v-for="(ingredient, index) in productionStep" :key="index" class="dragItem">
            <q-item-section>
              {{ ingredient.amount }}ml {{ ingredient.ingredient.name }}
              <q-item-label v-if="ingredient.ingredient.alcoholContent !== 0" caption>
                {{ ingredient.ingredient.alcoholContent }}% alcohol content
              </q-item-label>
            </q-item-section>
            <q-item-section side v-if="editable">
              <q-btn
                :icon="mdiPencilOutline"
                @click.native="showIngredientEditor(productionStep, ingredient)"
                dense
                flat
                rounded
              />
            </q-item-section>
            <q-item-section side v-if="editable">
              <q-btn
                :icon="mdiDelete"
                @click.native="removeIngredient(productionStep, ingredient)"
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
        <q-item-label header style="padding: 0" class="text-black">
          <b v-if="big">
            Ingredients
          </b>
          <div v-else>
            Ingredients
          </div>
        </q-item-label>
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

      <c-edit-dialog
        v-model="showIngredientEditorDialog"
        :title="addIngredient?'Add Ingredient':'Edit Ingredient'"
        :valid="ingridientValid"
        @clickAbort="closeIngredientEditor"
        @clickSave="saveEditIngredient"
      >
        <add-ingredient-form
          v-model="editIngredient"
          @valid="ingridientValid = true"
          @invalid="ingridientValid = false"
        />
      </c-edit-dialog>

    </q-item>
  </draggable>
</template>

<script>
import {mdiDelete, mdiPencilOutline, mdiPlusCircleOutline} from "@quasar/extras/mdi-v5";
import AddIngredientForm from "./AddIngredientForm";
import draggable from 'vuedraggable';
import cloneDeep from 'lodash/cloneDeep'
import CEditDialog from "components/CEditDialog";

export default {
    name: "IngredientList",
    components: {CEditDialog, AddIngredientForm, draggable},
    props: {
      value: {
        type: Array,
        required: true,
        default: () => []
      },
      editable: {
        type: Boolean,
        default: false
      },
      big: {
        type: Boolean,
        default: false
      },
      row1Color: {
        type: String,
        default: "#FFFFFF"
      },
      row2Color: {
        type: String,
        default: "#FFFFFF"
      }
    },
    data() {
      return {
        ingredients: [],
        showIngredientEditorDialog: false,
        addIngredient: false,
        ingridientValid: false,
        valid: false,
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
