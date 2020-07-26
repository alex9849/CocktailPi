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
        v-model="value.shortDescription"
        @input="() => {$emit('input', value); $v.value.shortDescription.$touch();}"
        type="textarea"
        input-style="height: 40px"
        label="Short description"
        counter
        maxlength="100"
        :rules="[
        val => $v.value.shortDescription.maxLength || 'Maximal length 100']"
      />
      <q-input
        outlined
        :loading="loading"
        :disable="loading || disabled"
        v-model="value.description"
        @input="() => {$emit('input', value); $v.value.description.$touch();}"
        type="textarea"
        label="Description"
        counter
        maxlength="2000"
        :rules="[
        val => $v.value.description.maxLength || 'Maximal length 2000']"
      />
      <draggable
        v-model="value.recipeIngredients"
        @start="ingredientDrag = true"
        @end="ingredientDrag = false"
        tag="div"
        class="rounded-borders q-list q-list--bordered q-list--separator"
        :animation="200"
      >
          <q-item
            v-for="(productionStep, index) in value.recipeIngredients"
            :key="index"
          >
            <q-item-section avatar>
              <q-avatar color="grey">{{ index + 1}}.</q-avatar>
            </q-item-section>
            <q-item-section v-if="productionStep.length === 1">
              {{ productionStep[0].amount }}ml {{ productionStep[0].ingredient.name }}
            </q-item-section>
            <q-item-section v-if="productionStep.length === 1" side>
              <q-btn
                :icon="mdiPencilOutline"
                @click="showIngredientEditor(productionStep, productionStep[0])"
                dense
                flat
                rounded
              />
            </q-item-section>
            <q-item-section v-if="productionStep.length === 1" side>
              <q-btn
                :icon="mdiDelete"
                @click="removeIngredient(productionStep, productionStep[0])"
                dense
                flat
                rounded
              />
            </q-item-section>

            <q-item-section v-else>
              <q-list bordered separator>
                <q-item v-for="(ingredient, index) in productionStep" :key="index">
                  <q-item-section>
                    {{ ingredient.amount }}ml {{ ingredient.ingredient.name }}
                  </q-item-section>
                  <q-item-section side>
                    <q-btn
                      :icon="mdiPencilOutline"
                      @click="showIngredientEditor(productionStep, ingredient)"
                      dense
                      flat
                      rounded
                    />
                  </q-item-section>
                  <q-item-section side>
                    <q-btn
                      :icon="mdiDelete"
                      @click="removeIngredient(productionStep, ingredient)"
                      dense
                      flat
                      rounded
                    />
                  </q-item-section>
                </q-item>
              </q-list>
            </q-item-section>
          </q-item>
        <q-item slot="header">
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
      </draggable>
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
        editIngredientGroupIndex: -1,
        editIngredientIndex: -1
      }
    },
    methods: {
      showIngredientEditor() {
        this.showIngredientEditor(null)
      },
      showIngredientEditor(group, ingredient) {
        this.addIngredient = true;
        if (ingredient) {
          this.editIngredient = Object.assign({}, ingredient);
          this.addIngredient = false;
          this.editIngredientGroupIndex = this.value.recipeIngredients.indexOf(group);
          this.editIngredientIndex = this.value.recipeIngredients[this.editIngredientGroupIndex].indexOf(ingredient);
        }
        this.showIngredientEditorDialog = true;
      },
      removeIngredient(group, ingredient) {
        let groupIndex = this.value.recipeIngredients.indexOf(group);
        let newGroup = group.filter(x => x !== ingredient);
        if(newGroup.length === 0) {
          this.value.recipeIngredients = this.value.recipeIngredients.filter(x => x !== group)
        } else {
          let newIngredients = Object.assign([], this.value.recipeIngredients);
          newIngredients[groupIndex] = newGroup;
          this.value.recipeIngredients = newIngredients;
        }
      },
      closeIngredientEditor() {
        this.editIngredient = JSON.parse(JSON.stringify(this.newIngredient));
        this.showIngredientEditorDialog = false;
        this.editIngredientIndex = -1;
        this.editIngredientGroupIndex = -1;
      },
      saveEditIngredient() {
        if (this.editIngredientIndex < 0) {
          this.value.recipeIngredients.push([this.editIngredient]);
        } else {
          this.value.recipeIngredients[this.editIngredientGroupIndex][this.editIngredientIndex] = this.editIngredient;
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
          shortDescription: {
            maxLength: maxLength(100)
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
    computed: {}
  }
</script>

<style scoped>

</style>
