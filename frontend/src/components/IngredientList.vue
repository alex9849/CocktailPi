<template>
  <draggable
    :itemKey="step => productionSteps.indexOf(step)"
    :model-value="productionSteps"
    @update:model-value="onProductionStepListDrag($event)"
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
    <template #item="{element, index}">
      <q-item
        :class="{'alternateGrey': alternateRowColors}"
        class="dragItem"
      >
        <q-item-section avatar>
          <q-avatar color="grey">{{ index + 1}}.</q-avatar>
        </q-item-section>
        <q-item-section v-if="element.type === 'writtenInstruction'">
          <q-list>
            <q-item>
              <q-item-section>
                <q-item-label caption>
                  Manual instruction:
                </q-item-label>
                {{ element.message }}
              </q-item-section>
              <q-item-section v-if="editable" side>
                <q-btn
                  :icon="mdiPencilOutline"
                  dense
                  flat
                  rounded
                  @click.native="showManualInstructionEditor(element)"
                />
              </q-item-section>
              <q-item-section v-if="editable" side>
                <q-btn
                  :icon="mdiDelete"
                  dense
                  flat
                  rounded
                  @click.native="removeManualInstruction(element)"
                />
              </q-item-section>
            </q-item>
          </q-list>
        </q-item-section>
        <q-item-section v-if="element.type === 'addIngredients'">
          <draggable
            :animation="200"
            :delay="400"
            :delayOnTouchOnly="true"
            :disabled="!editable"
            :itemKey="step => productionSteps.indexOf(step)"
            :model-value="element.stepIngredients"
            :touchStartThreshold="10"
            class="q-list q-list--bordered q-list--separator"
            draggable=".dragItem"
            group="ingredients"
            tag="div"
            @update:model-value="onStepIngredientsListDrag(element, $event)"
          >
            <template #item="sublist">
              <q-item class="dragItem">
                <q-item-section>
                  <div style="display: ruby">
                    {{ sublist.element.amount }} {{ sublist.element.ingredient.unit }} {{ sublist.element.ingredient.name }}
                    <q-item-label v-if="!element.scale" caption>
                      (Unscaled)
                    </q-item-label>
                  </div>
                  <q-item-label v-if="sublist.element.ingredient.alcoholContent !== 0" caption>
                    {{ sublist.element.ingredient.alcoholContent }}% alcohol content
                  </q-item-label>
                </q-item-section>
                <q-item-section v-if="editable" side>
                  <q-btn
                    v-if="element.stepIngredients.length > 1"
                    :icon="mdiCallSplit"
                    dense
                    flat
                    rounded
                    @click.native="splitUpStepIngredient(element, sublist.element)"
                  />
                </q-item-section>
                <q-item-section v-if="editable" side>
                  <q-btn
                    :icon="mdiPencilOutline"
                    dense
                    flat
                    rounded
                    @click.native="showStepIngredientEditor(element, sublist.element)"
                  />
                </q-item-section>
                <q-item-section v-if="editable" side>
                  <q-btn
                    :icon="mdiDelete"
                    dense
                    flat
                    rounded
                    @click.native="removeStepIngredient(element, sublist.element)"
                  />
                </q-item-section>
              </q-item>
            </template>
          </draggable>
        </q-item-section>
      </q-item>
    </template>
    <template #header>
      <q-item>
        <q-item-section>
          <q-item-label class="text-black" header style="padding: 0">
            <b v-if="big">
              Production-Steps
            </b>
            <div v-else>
              Production-Steps
            </div>
          </q-item-label>
        </q-item-section>

        <q-item-section v-if="editable" side>
          <q-btn
            :icon="mdiPlusCircleOutline"
            dense
            flat
            rounded
            @click="showAddProductionStepEditor()"
          />
        </q-item-section>

        <c-edit-dialog
          v-model:show="editor.visible"
          :title="editor.isCreatingNew? 'Add Production-Step':'Edit Production-Step'"
          :valid="editor.valid"
          @clickAbort="closeProductionStepEditor"
          @clickSave="saveEditProductionStep"
        >
          <q-splitter :model-value="10" horizontal />
          <production-step-list-editor
            v-model:model-value="editor.editingObject"
            :new-production-step="editor.isCreatingNew"
            @invalid="editor.valid = false"
            @valid="editor.valid = true"
          />
        </c-edit-dialog>

      </q-item>
    </template>
    <template v-if="productionSteps.length === 0"
              #footer
    >
      <q-item>
        <q-item-section class="text-center">
          No Production-Steps added!
        </q-item-section>
      </q-item>
    </template>
  </draggable>
</template>

<script>
import { mdiCallSplit, mdiDelete, mdiPencilOutline, mdiPlusCircleOutline } from '@quasar/extras/mdi-v5'
import draggable from 'vuedraggable'
import cloneDeep from 'lodash/cloneDeep'
import CEditDialog from 'components/CEditDialog'
import ProductionStepListEditor from 'components/ProductionStepListEditor'

export default {
  name: 'IngredientList',
  components: { ProductionStepListEditor, CEditDialog, draggable },
  props: {
    modelValue: {
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
  emits: ['update:modelValue'],
  data () {
    return {
      productionSteps: [],
      editor: {
        isCreatingNew: true,
        visible: false,
        valid: false,
        productionStepIndex: -1,
        ingredientIndex: -1,
        editingObject: null
      }
    }
  },
  watch: {
    modelValue: {
      immediate: true,
      deep: true,
      handler () {
        this.productionSteps = cloneDeep(this.modelValue)
      }
    }
  },
  methods: {
    log (el) {
      console.log(el)
    },
    emitChange () {
      this.$emit('update:modelValue', this.productionSteps)
    },
    showAddProductionStepEditor () {
      this.editor.isCreatingNew = true
      this.editor.visible = true
    },
    showManualInstructionEditor (productionStep) {
      this.editor.productionStepIndex = this.productionSteps.indexOf(productionStep)
      this.editor.editingObject = productionStep
      this.editor.isCreatingNew = false
      this.editor.visible = true
    },
    removeManualInstruction (productionStep) {
      this.productionSteps = this.productionSteps.filter(x => x !== productionStep)
      this.emitChange()
    },
    showStepIngredientEditor (productionStep, stepIngredient) {
      this.editor.productionStepIndex = this.productionSteps.indexOf(productionStep)
      this.editor.ingredientIndex = productionStep.stepIngredients.indexOf(stepIngredient)
      this.editor.editingObject = stepIngredient
      this.editor.isCreatingNew = false
      this.editor.visible = true
    },
    removeStepIngredient (productionStep, stepIngredient) {
      productionStep.stepIngredients = productionStep.stepIngredients.filter(x => x !== stepIngredient)
      if (productionStep.stepIngredients.length === 0) {
        this.productionSteps = this.productionSteps.filter(x => x !== productionStep)
      }
      this.emitChange()
    },
    closeProductionStepEditor () {
      this.editor.visible = false
      this.editor.productionStepIndex = -1
      this.editor.ingredientIndex = -1
      this.editor.editingObject = null
    },
    saveEditProductionStep () {
      if (this.editor.productionStepIndex >= 0) {
        // Edit
        if (this.editor.ingredientIndex >= 0) {
          // Ingredient
          this.productionSteps[this.editor.productionStepIndex]
            .stepIngredients[this.editor.ingredientIndex] = this.editor.editingObject
        } else {
          // Instruction
          this.productionSteps[this.editor.productionStepIndex] = this.editor.editingObject
        }
      } else {
        // Add new ProductionStep
        if (this.editor.editingObject.type === 'writtenInstruction') {
          // Instruction
          this.productionSteps.push(this.editor.editingObject)
        } else {
          // Ingredient
          this.productionSteps.push({
            type: 'addIngredients',
            stepIngredients: [this.editor.editingObject]
          })
        }
      }
      this.closeProductionStepEditor()
      this.emitChange()
    },
    splitUpStepIngredient (productionStep, stepIngredient) {
      const prodStepIndex = this.productionSteps.indexOf(productionStep)
      productionStep.stepIngredients = productionStep.stepIngredients
        .filter(x => x !== stepIngredient)
      this.productionSteps.splice(prodStepIndex + 1, 0, {
        type: 'addIngredients',
        stepIngredients: [stepIngredient]
      })
      this.emitChange()
    },
    onProductionStepListDrag (value) {
      this.productionSteps = value
      this.emitChange()
    },
    onStepIngredientsListDrag (productionStep, value) {
      productionStep.stepIngredients = value
      this.productionSteps = this.productionSteps.filter(x => {
        if (x.type !== 'addIngredients') {
          return true
        }
        return x.stepIngredients.length !== 0
      })
      const ingredientIdToStepIndex = new Map()
      for (let index = 0; index < productionStep.stepIngredients.length; index++) {
        const si = productionStep.stepIngredients[index]
        if (ingredientIdToStepIndex.has(si.ingredient.id)) {
          const otherSIIndex = ingredientIdToStepIndex.get(si.ingredient.id)
          productionStep.stepIngredients[otherSIIndex].amount += si.amount
          productionStep.stepIngredients.splice(index, 1)
          index--
        } else {
          ingredientIdToStepIndex.set(si.ingredient.id, index)
        }
      }
      this.emitChange()
    }
  },
  setup () {
    return {
      mdiPlusCircleOutline: mdiPlusCircleOutline,
      mdiPencilOutline: mdiPencilOutline,
      mdiDelete: mdiDelete,
      mdiCallSplit: mdiCallSplit
    }
  }
}
</script>

<style scoped>

</style>
