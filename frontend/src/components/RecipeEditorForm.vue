<template>
  <div>
    <q-form
      class="q-gutter-y-md"
      greedy
      @submit.prevent="$emit('submit')"
    >
      <q-input
        outlined
        :loading="loading"
        :disable="loading || disabled"
        v-model="value.name"
        @input="() => {$emit('input', value); $v.value.name.$touch();}"
        label="Recipe-Name"
        :rules="[
        val => $v.value.name.required || 'Required',
        val => $v.value.name.minLength || 'Minimal length 3',
        val => $v.value.name.maxLength || 'Maximal length 20']"
      />
      <q-input
        outlined
        :loading="loading"
        :disable="loading || disabled"
        v-model="value.description"
        @input="() => {$emit('input', value); $v.value.name.$touch();}"
        type="textarea"
        label="Description"
        counter
        maxlength="2000"
        :rules="[
        val => $v.value.description.maxLength || 'Maximal length 2000']"
      />
      <q-list class="rounded-borders" bordered separator>
        <q-item>
          <q-item-section>
            <q-item-label header>Ingredients</q-item-label>
          </q-item-section>

          <q-item-section side>
            <q-btn
              :icon="mdiPlusCircleOutline"
              @click="showIngredientEditor(null)"
              dense
              flat
              rounded
            />
          </q-item-section>
        </q-item>
        <q-separator/>
        <draggable
          v-model="value.recipeIngredients"
          @start="ingredientDrag = true"
          @end="ingredientDrag = false"
          v-bind="dragOptions"
        >
          <transition-group
            type="transition"
            :name="!ingredientDrag ? 'flip-list' : null"
          >
            <q-item
              v-for="(ingredient, index) in value.recipeIngredients"
              class="list-group-item"
              :key="index"
            >
              <q-item-section avatar>
                <q-avatar color="grey">{{ index + 1}}.</q-avatar>
              </q-item-section>
              <q-item-section>
                {{ ingredient.amount }}ml {{ ingredient.ingredient.name }}
              </q-item-section>
              <q-item-section side>
                <q-btn
                  :icon="mdiPencilOutline"
                  @click="showIngredientEditor(ingredient)"
                  dense
                  flat
                  rounded
                />
              </q-item-section>
              <q-item-section side>
                <q-btn
                  :icon="mdiDelete"
                  @click="value.recipeIngredients = value.recipeIngredients.filter(x => x !== ingredient)"
                  dense
                  flat
                  rounded
                />
              </q-item-section>
            </q-item>
          </transition-group>
        </draggable>
      </q-list>
      <q-checkbox
        v-model="value.inPublic"
        :disable="loading || disabled"
        label="Public recipe"
        @input="$emit('input', value)"
      />
      <slot name="below"/>
    </q-form>


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
  </div>
</template>

<script>
  import {maxLength, minLength, required} from "vuelidate/lib/validators";
  import {mdiDelete, mdiPencilOutline, mdiPlusCircleOutline} from '@quasar/extras/mdi-v5';
  import IngredientForm from "./IngredientForm";
  import draggable from 'vuedraggable';

  export default {
    name: "RecipeEditorForm",
    components: {IngredientForm, draggable},
    props: {
      value: {
        type: Object,
        required: true
      },
      loading: {
        type: Boolean,
        default: false
      },
      disabled: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
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
        editIngredientIndex: -1
      }
    },
    methods: {
      showIngredientEditor() {
        this.showIngredientEditor(null)
      },
      showIngredientEditor(ingredient) {
        this.addIngredient = true;
        if (ingredient) {
          this.editIngredient = Object.assign({}, ingredient);
          this.addIngredient = false;
          this.editIngredientIndex = this.value.recipeIngredients.indexOf(ingredient);
        }
        this.showIngredientEditorDialog = true;
      },
      closeIngredientEditor() {
        this.editIngredient = JSON.parse(JSON.stringify(this.newIngredient));
        this.showIngredientEditorDialog = false;
        this.editIngredientIndex = -1;
      },
      saveEditIngredient() {
        if (this.editIngredientIndex < 0) {
          this.value.recipeIngredients.push(this.editIngredient);
        } else {
          this.value.recipeIngredients[this.editIngredientIndex] = this.editIngredient;
        }
        this.closeIngredientEditor();
      }
    },
    validations() {
      let validations = {
        value: {
          name: {
            required,
            minLength: minLength(3),
            maxLength: maxLength(20)
          },
          description: {
            maxLength: maxLength(2000)
          }
        }
      };
      return validations;
    },
    watch: {
      '$v.value.$invalid': function _watch$vValue$invalid(value) {
        if (!value) {
          this.$emit('valid');
        } else {
          this.$emit('invalid');
        }
      }
    },
    created() {
      this.mdiPlusCircleOutline = mdiPlusCircleOutline;
      this.mdiPencilOutline = mdiPencilOutline;
      this.mdiDelete = mdiDelete;
    },
    computed: {
      dragOptions() {
        return {
          animation: 200,
          group: "description",
          disabled: false,
          ghostClass: "ghost"
        };
      }
    }
  }
</script>

<style scoped>
  .flip-list-move {
    transition: transform 0.5s;
  }
  .no-move {
    transition: transform 0s;
  }
</style>
