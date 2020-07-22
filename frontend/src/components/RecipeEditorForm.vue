<template>
  <div>
    <q-form
      class="innerpadding q-gutter-y-md"
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
        <q-item v-for="(ingredient, index) in value.recipeIngredients">
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
    >
      <q-card style="width: 400px">
        <q-card-section class="text-center innerpadding">
          <h5 style="margin: 5px">{{ addIngredient?"Add Ingredient":"Edit Ingredient" }}</h5>
          <q-select
            outlined
            name="Ingredient"

          />
          <q-input
            label="Amount (in ml)"
            type="number"
            outlined
            v-model="editIngredient.amount"
          />
          <div class="q-pa-md q-gutter-sm">
            <q-btn
              style="width: 100px"
              color="negative"
              label="Abort"
              no-caps
              :to="{name: 'recipedetails', params: {id: $route.params.id}}"
            />
            <q-btn
              type="submit"
              style="width: 100px"
              color="positive"
              label="Save"
              no-caps
              :disable="loading || !isValid"
              @click=""
            />
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>
  </div>
</template>

<script>
  import {maxLength, minLength, required} from "vuelidate/lib/validators";
  import {mdiDelete, mdiPencilOutline, mdiPlusCircleOutline} from '@quasar/extras/mdi-v5';

  export default {
    name: "RecipeEditorForm",
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
        newIngredient: {
          amount: '',
          ingredient: {
            id: '',
            name: ''
          }
        },
        editIngredient: {
          amount: '',
          ingredient: {
            id: '',
            name: ''
          }
        }
      }
    },
    methods: {
      showIngredientEditor() {
        this.showIngredientEditor(null)
      },
      showIngredientEditor(ingredient) {
        this.addIngredient = true;
        if (ingredient) {
          this.editIngredient = ingredient;
          this.addIngredient = false;
        }
        this.showIngredientEditorDialog = true;
      },
      closeIngredientEditor() {
        this.editIngredient = JSON.parse(JSON.stringify(this.newIngredient));
        this.showIngredientEditorDialog = false;
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
    }
  }
</script>

<style scoped>
  .innerpadding > * {
    padding: 10px;
  }
</style>
