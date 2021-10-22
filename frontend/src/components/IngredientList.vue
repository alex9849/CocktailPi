<template>
  <draggable
    :value="productionSteps"
    @input="onProductionStepListDrag($event)"
    :disabled="!editable"
    :delay="400"
    :delayOnTouchOnly="true"
    :touchStartThreshold="10"
    draggable=".dragItem"
    group="productionSteps"
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
        <q-list>
          <q-item>
            <q-item-section>
              <q-item-label caption>
                Manual instruction:
              </q-item-label>
              {{ productionStep.message }}
            </q-item-section>
            <q-item-section side v-if="editable">
              <q-btn
                :icon="mdiPencilOutline"
                @click.native="showManualInstructionEditor(productionStep)"
                dense
                flat
                rounded
              />
            </q-item-section>
            <q-item-section side v-if="editable">
              <q-btn
                :icon="mdiDelete"
                @click.native="removeManualInstruction(productionStep)"
                dense
                flat
                rounded
              />
            </q-item-section>
          </q-item>
        </q-list>
      </q-item-section>
      <q-item-section v-if="productionStep.type === 'addIngredients'">
        <draggable
          :value="productionStep.stepIngredients"
          @input="onStepIngredientsListDrag(productionStep, $event)"
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
          <q-item v-for="(stepIngredient, index) in productionStep.stepIngredients" :key="index" class="dragItem">
            <q-item-section>
              <div style="display: ruby">
                {{ stepIngredient.amount }} {{ stepIngredient.ingredient.unit }} {{ stepIngredient.ingredient.name }}
                <q-item-label v-if="!stepIngredient.scale" caption>
                  (Unscaled)
                </q-item-label>
              </div>
              <q-item-label v-if="stepIngredient.ingredient.alcoholContent !== 0" caption>
                {{ stepIngredient.ingredient.alcoholContent }}% alcohol content
              </q-item-label>
            </q-item-section>
            <q-item-section side v-if="editable">
              <q-btn
                v-if="productionStep.stepIngredients.length > 1"
                :icon="mdiCallSplit"
                @click.native="splitUpStepIngredient(productionStep, stepIngredient)"
                dense
                flat
                rounded
              />
            </q-item-section>
            <q-item-section side v-if="editable">
              <q-btn
                :icon="mdiPencilOutline"
                @click.native="showStepIngredientEditor(productionStep, stepIngredient)"
                dense
                flat
                rounded
              />
            </q-item-section>
            <q-item-section side v-if="editable">
              <q-btn
                :icon="mdiDelete"
                @click.native="removeStepIngredient(productionStep, stepIngredient)"
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
          @click="showAddProductionStepEditor()"
          dense
          flat
          rounded
        />
      </q-item-section>

      <c-edit-dialog
        v-model="editor.visible"
        :title="editor.isCreatingNew? 'Add Production-Step':'Edit Production-Step'"
        :valid="editor.valid"
        @clickAbort="closeProductionStepEditor"
        @clickSave="saveEditProductionStep"
      >
        <q-splitter horizontal :value="10" />
        <production-step-list-editor
          @valid="editor.valid = true"
          @invalid="editor.valid = false"
          v-model="editor.editingObject"
          :new-production-step="editor.isCreatingNew"
        />
      </c-edit-dialog>

    </q-item>
  </draggable>
</template>

<script>
import {mdiCallSplit, mdiDelete, mdiPencilOutline, mdiPlusCircleOutline} from "@quasar/extras/mdi-v5";
import draggable from 'vuedraggable';
import cloneDeep from 'lodash/cloneDeep'
import CEditDialog from "components/CEditDialog";
import ProductionStepListEditor from "components/ProductionStepListEditor";
import Vue from "vue";

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
          ingredientIndex: -1,
          editingObject: null
        },
      }
    },
    watch: {
      value: {
        immediate: true,
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
      showAddProductionStepEditor() {
        this.editor.isCreatingNew = true;
        this.editor.visible = true;
      },
      showManualInstructionEditor(productionStep) {
        this.editor.productionStepIndex = this.productionSteps.indexOf(productionStep);
        Vue.set(this.editor, 'editingObject', productionStep);
        this.editor.isCreatingNew = false;
        this.editor.visible = true;
      },
      removeManualInstruction(productionStep) {
        this.productionSteps = this.productionSteps.filter(x => x !== productionStep);
        this.emitChange();
      },
      showStepIngredientEditor(productionStep, stepIngredient) {
        this.editor.productionStepIndex = this.productionSteps.indexOf(productionStep);
        this.editor.ingredientIndex = productionStep.stepIngredients.indexOf(stepIngredient);
        Vue.set(this.editor, 'editingObject', stepIngredient);
        this.editor.isCreatingNew = false;
        this.editor.visible = true;
      },
      removeStepIngredient(productionStep, stepIngredient) {
        productionStep.stepIngredients = productionStep.stepIngredients.filter(x => x !== stepIngredient);
        if(productionStep.stepIngredients.length === 0) {
          this.productionSteps = this.productionSteps.filter(x => x !== productionStep);
        }
        this.emitChange();
      },
      closeProductionStepEditor() {
        this.editor.visible = false;
        this.editor.productionStepIndex = -1;
        this.editor.ingredientIndex = -1;
        Vue.set(this.editor, 'editingObject', null);
      },
      saveEditProductionStep() {
        if(this.editor.productionStepIndex >= 0) {
          //Edit
          if(this.editor.ingredientIndex >= 0) {
            //Ingredient
            this.productionSteps[this.editor.productionStepIndex]
              .stepIngredients[this.editor.ingredientIndex] = this.editor.editingObject
          } else {
            //Instruction
            this.productionSteps[this.editor.productionStepIndex] = this.editor.editingObject
          }
        } else {
          //Add new ProductionStep
          if(this.editor.editingObject.type === 'writtenInstruction') {
            //Instruction
            this.productionSteps.push(this.editor.editingObject)
          } else {
            //Ingredient
            this.productionSteps.push({
              type: 'addIngredients',
              stepIngredients: [this.editor.editingObject]
            })
          }
        }
        this.closeProductionStepEditor();
        this.emitChange();
      },
      splitUpStepIngredient(productionStep, stepIngredient) {
        let prodStepIndex = this.productionSteps.indexOf(productionStep);
        productionStep.stepIngredients = productionStep.stepIngredients
          .filter(x => x !== stepIngredient)
        this.productionSteps.splice(prodStepIndex + 1, 0, {
          type: 'addIngredients',
          stepIngredients: [stepIngredient]
        })
        this.emitChange()
      },
      onProductionStepListDrag(value) {
        this.productionSteps = value;
        this.emitChange()
      },
      onStepIngredientsListDrag(productionStep, value) {
        productionStep.stepIngredients = value;
        this.productionSteps = this.productionSteps.filter(x => {
          if(x.type !== 'addIngredients') {
            return true;
          }
          return x.stepIngredients.length !== 0;
        })
        this.emitChange();
      },
    },
    created() {
      this.mdiPlusCircleOutline = mdiPlusCircleOutline;
      this.mdiPencilOutline = mdiPencilOutline;
      this.mdiDelete = mdiDelete;
      this.mdiCallSplit = mdiCallSplit;
    }
  }
</script>

<style scoped>

</style>
