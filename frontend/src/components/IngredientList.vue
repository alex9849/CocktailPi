<template>
  <draggable
    :itemKey="step => productionSteps.indexOf(step)"
    :model-value="productionSteps"
    @update:model-value="onProductionStepListDrag($event)"
    :disabled="!editable || disable || !showPStepDragIcon"
    :delay="400"
    :delayOnTouchOnly="true"
    :touchStartThreshold="10"
    draggable=".dragItem"
    group="productionSteps"
    tag="div"
    :class="{'q-list--dark': compColors.dark}"
    class="rounded-borders q-list q-list--bordered q-list--separator"
    :style="{'backgroundColor': backgroundColor, 'color': compColors.text}"
    :animation="200"
  >
    <template #item="{element, index}">
      <q-item
        :class="{'alternateGrey': alternateRowColors}"
        :dark="compColors.dark"
        class="dragItem"
      >
        <q-item-section
          v-if="showPStepDragIcon"
          side
        >
          <q-icon
            style="cursor: grab"
            :style="{'color': compColors.icon}"
            :name="mdiDrag"
          />
        </q-item-section>
        <q-item-section
          avatar
        >
          <q-avatar
            :style="{'backgroundColor': compColors.pstep, 'color': compColors.pstepText}"
          >
            {{ index + 1}}.
          </q-avatar>
        </q-item-section>
        <q-item-section v-if="element.type === 'writtenInstruction'">
          <q-list
            :dark="compColors.dark"
          >
            <q-item
              :dark="compColors.dark"
            >
              <q-item-section>
                <q-item-label
                  :style="{'color': compColors.caption}"
                  caption
                >
                  {{ $t('component.ingredient_list.manual_instruction') }}
                </q-item-label>
                {{ element.message }}
              </q-item-section>
              <q-item-section v-if="editable" side>
                <q-btn
                  :icon="mdiPencilOutline"
                  dense
                  flat
                  rounded
                  :style="{'color': compColors.icon}"
                  @click="showManualInstructionEditor(element)"
                />
              </q-item-section>
              <q-item-section v-if="editable" side>
                <q-btn
                  :icon="mdiDelete"
                  dense
                  flat
                  rounded
                  :style="{'color': compColors.icon}"
                  @click="removeManualInstruction(element)"
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
            :disabled="!editable || disable || !showIngredientDragIcon"
            :itemKey="step => productionSteps.indexOf(step)"
            :model-value="element.stepIngredients"
            :touchStartThreshold="10"
            :class="{'q-list--dark': compColors.dark}"
            class="q-list q-list--bordered q-list--separator"
            draggable=".dragItem"
            group="ingredients"
            tag="div"
            @update:model-value="onStepIngredientsListDrag(element, $event)"
          >
            <template #item="sublist">
              <q-item
                class="dragItem"
              >
                <q-item-section
                  v-if="showIngredientDragIcon"
                  side
                >
                  <q-icon
                    style="cursor: grab"
                    :name="mdiDrag"
                    :style="{'color': compColors.icon}"
                  />
                </q-item-section>
                <q-item-section>
                  <div style="display: ruby">
                    {{ sublist.element.amount }} {{ sublist.element.ingredient.unit }} {{ sublist.element.ingredient.name }}
                    <q-item-label
                      v-if="getStepIngredientAnnotationString(sublist.element)"
                      :style="{'color': compColors.caption}"
                      caption
                    >
                      {{ getStepIngredientAnnotationString(sublist.element) }}
                    </q-item-label>
                  </div>
                  <q-item-label
                    v-if="getAlcoholContentString(sublist.element.ingredient)"
                    :style="{'color': compColors.caption}"
                    caption
                  >
                    {{ getAlcoholContentString(sublist.element.ingredient) }}
                  </q-item-label>
                </q-item-section>
                <q-item-section v-if="editable" side>
                  <q-btn
                    v-if="element.stepIngredients.length > 1"
                    :icon="mdiCallSplit"
                    dense
                    flat
                    rounded
                    :style="{'color': compColors.icon}"
                    @click="splitUpStepIngredient(element, sublist.element)"
                  />
                </q-item-section>
                <q-item-section v-if="editable" side>
                  <q-btn
                    :icon="mdiPencilOutline"
                    dense
                    flat
                    rounded
                    :style="{'color': compColors.icon}"
                    @click="showStepIngredientEditor(element, sublist.element)"
                  />
                </q-item-section>
                <q-item-section v-if="editable" side>
                  <q-btn
                    :icon="mdiDelete"
                    dense
                    flat
                    rounded
                    :style="{'color': compColors.icon}"
                    @click="removeStepIngredient(element, sublist.element)"
                  />
                </q-item-section>
              </q-item>
            </template>
          </draggable>
        </q-item-section>
      </q-item>
    </template>
    <template #header>
      <q-item
        :dark="compColors.dark"
      >
        <q-item-section>
          <q-item-label
            header
            style="padding: 0"
            :style="{'color': compColors.text}"
          >
            <b v-if="big">
              {{ $t('component.ingredient_list.headline') }}
            </b>
            <div v-else>
              {{ $t('component.ingredient_list.headline') }}
            </div>
          </q-item-label>
        </q-item-section>

        <q-item-section v-if="editable" side>
          <q-btn
            :icon="mdiPlusCircleOutline"
            dense
            flat
            rounded
            :style="{'color': compColors.icon}"
            @click="showAddProductionStepEditor()"
          />
        </q-item-section>

        <c-edit-dialog
          v-model:show="editor.visible"
          :title="editDialogHeadline"
          :valid="editor.valid"
          :save-btn-label="$t('component.ingredient_list.edit_dialog.save_btn_label')"
          :abort-btn-label="$t('component.ingredient_list.edit_dialog.abort_btn_label')"
          @clickAbort="closeProductionStepEditor"
          @clickSave="saveEditProductionStep"
        >
          <q-separator
            :dark="compColors.dark"
          />
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
      <q-item
        :dark="compColors.dark"
      >
        <q-item-section class="text-center">
          {{ $t('component.ingredient_list.no_steps_added') }}
        </q-item-section>
      </q-item>
    </template>
  </draggable>
</template>

<script>
import { mdiCallSplit, mdiDelete, mdiPencilOutline, mdiPlusCircleOutline, mdiDrag } from '@quasar/extras/mdi-v5'
import draggable from 'vuedraggable'
import cloneDeep from 'lodash/cloneDeep'
import CEditDialog from 'components/CEditDialog'
import ProductionStepListEditor from 'components/ProductionStepListEditor'
import { calcTextColor, complementColor, isDark } from 'src/mixins/utils'

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
    disable: {
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
    },
    backgroundColor: {
      type: String,
      default: '#FFFFFF'
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
  computed: {
    compColors () {
      const pstep = complementColor(this.backgroundColor, 30)
      const dark = isDark(this.backgroundColor)
      return {
        bg: this.backgroundColor,
        dark: dark,
        text: calcTextColor(this.backgroundColor),
        caption: complementColor(this.backgroundColor, 60),
        pstep: pstep,
        pstepText: calcTextColor(pstep),
        icon: complementColor(this.backgroundColor, 60)
      }
    },
    showPStepDragIcon () {
      return this.editable && this.productionSteps.length > 1
    },
    showIngredientDragIcon () {
      return this.editable && this.productionSteps
        .filter(x => x.type === 'addIngredients').length > 1
    },
    editDialogHeadline () {
      if (this.editor.isCreatingNew) {
        return this.$t('component.ingredient_list.edit_dialog.add_headline')
      } else {
        return this.$t('component.ingredient_list.edit_dialog.add_headline')
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
    getAlcoholContentString (ingredient) {
      if (ingredient.type === 'group') {
        if (ingredient.minAlcoholContent === ingredient.maxAlcoholContent) {
          if (!ingredient.minAlcoholContent) {
            return null
          }
          return this.$t('component.ingredient_list.alc_content', { nr: ingredient.minAlcoholContent })
        }
        return this.$t('component.ingredient_list.alc_content_range', { min: ingredient.minAlcoholContent, max: ingredient.maxAlcoholContent })
      }
      if (ingredient.alcoholContent === 0) {
        return null
      }
      return this.$t('component.ingredient_list.alc_content', { nr: ingredient.alcoholContent })
    },
    getStepIngredientAnnotationString (stepIngredient) {
      const annotations = []
      if (!stepIngredient.scale) {
        annotations.push(this.$t('component.ingredient_list.tag_unscaled'))
      }
      if (stepIngredient.boostable) {
        annotations.push(this.$t('component.ingredient_list.tag_boostable'))
      }
      if (annotations.length === 0) {
        return ''
      }
      return '(' + annotations.join(', ') + ')'
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
          let added = false
          for (const iStep of this.productionSteps.slice().reverse()) {
            if (iStep.type === 'addIngredients') {
              const existingIngredient = iStep.stepIngredients
                .find(x => x.ingredient.id === this.editor.editingObject.ingredient.id)
              if (existingIngredient) {
                existingIngredient.amount += this.editor.editingObject.amount
              } else {
                iStep.stepIngredients.push(this.editor.editingObject)
              }
              added = true
              break
            }
          }
          if (!added) {
            this.productionSteps.push({
              type: 'addIngredients',
              stepIngredients: [this.editor.editingObject]
            })
          }
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
      mdiCallSplit: mdiCallSplit,
      mdiDrag: mdiDrag
    }
  }
}
</script>

<style scoped>

</style>
