<template>
  <draggable
    :value="productionSteps"
    :disabled="!editable"
    :delay="400"
    :delayOnTouchOnly="true"
    :touchStartThreshold="10"
    draggable=".dragItem"
    group="ingredients"
    tag="div"
    class="rounded-borders q-list q-list--bordered q-list--separator"
    :animation="200"
  >
    <q-item
      v-for="(productionStep, index) in productionSteps"
      :key="index"
      class="dragItem"
      :class="{'alternateGrey': alternateRowColors}"
    >
      <q-item-section avatar>
        <q-avatar color="grey">{{ index + 1}}.</q-avatar>
      </q-item-section>
      <q-item-section v-if="productionStep.type === 'writtenInstruction'">
        <q-list bordered>
          <q-item>
            <q-item-section>
              {{ productionStep.message }}
            </q-item-section>
          </q-item>
        </q-list>
      </q-item-section>
      <q-item-section v-if="productionStep.type === 'addIngredients'">
        <draggable
          :value="productionStep.stepIngredients"
          :disabled="!editable"
          :delay="400"
          :delayOnTouchOnly="true"
          :touchStartThreshold="10"
          draggable=".dragItem"
          group="ingredients"
          tag="div"
          class="q-list q-list--bordered q-list--separator"
          :animation="200"
        >
          <q-item v-for="(ingredient, index) in productionStep.stepIngredients" :key="index" class="dragItem">
            <q-item-section>
              <div style="display: ruby">
                {{ ingredient.amount }} {{ ingredient.ingredient.unit }} {{ ingredient.ingredient.name }}
                <q-item-label v-if="!ingredient.scale" caption>
                  (Unscaled)
                </q-item-label>
              </div>
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
            Production-Steps
          </b>
          <div v-else>
            Production-Steps
          </div>
        </q-item-label>
      </q-item-section>

      <q-item-section side v-if="editable">
        <q-btn
          :icon="mdiPlusCircleOutline"
          @click="showIngredientEditor(null, null)"
          dense
          flat
          rounded
        />
      </q-item-section>

      <c-edit-dialog
        v-model="editor.visible"
        :title="editor.isCreatingNew? 'Add Production-Step':'Edit Production-Step'"
        :valid="editor.valid"
        @clickAbort="closeIngredientEditor"
        @clickSave="saveEditIngredient"
      >
        <production-step-list-editor
          @valid="editor.valid = true"
          @invalid="editor.valid = false"
        />
      </c-edit-dialog>

    </q-item>
  </draggable>
</template>

<script>
import {mdiDelete, mdiPencilOutline, mdiPlusCircleOutline} from "@quasar/extras/mdi-v5";
import draggable from 'vuedraggable';
import cloneDeep from 'lodash/cloneDeep'
import CEditDialog from "components/CEditDialog";
import ProductionStepListEditor from "components/ProductionStepListEditor";

export default {
    name: "IngredientList",
    components: {ProductionStepListEditor, CEditDialog, draggable},
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
      alternateRowColors: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        productionSteps: [],
        editor: {
          isCreatingNew: true,
          visible: false,
          valid: false,
          productionStepIndex: -1,
          ingredientIndex: -1
        },
      }
    },
    watch: {
      value: {
        deep: true,
        handler() {
          this.productionSteps = cloneDeep(this.value);
        }
      }
    },
    methods: {
      log(el) {
        console.log(el);
      },
      emitChange() {
        this.$emit('input', this.productionSteps);
      },
      showIngredientEditor(productionStep, ingredient) {
        this.editor.isCreatingNew = !productionStep;
        this.editor.visible = true;
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
        this.editor.visible = false;
        this.editor.productionStepIndex = -1;
        this.editor.ingredientIndex = -1;
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
