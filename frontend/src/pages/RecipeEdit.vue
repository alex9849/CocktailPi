<template>
  <q-page class="page-content" padding>
    <h5>{{ headline }}</h5>
    <q-banner v-if="error !== ''" rounded dense class="text-white bg-red-5" style="margin-bottom: 20px">
      {{ error }}
    </q-banner>
    <q-card
      flat
      bordered
      class="bg-card-body text-card-body"
    >
      <q-card-section>
        <q-form
          class="q-gutter-y-md"
          greedy
          @submit.prevent="onClickSave"
        >
          <q-card
            flat
            bordered
            class="bg-card-item-group text-card-item-group"
          >
            <q-card-section
              class="q-gutter-y-md"
            >
              <q-input
                :dark="color.cardItemGroupDark"
                outlined
                :disable="disabled"
                hide-bottom-space
                v-model:model-value="v.editRecipe.recipe.name.$model"
                :error-message="v.editRecipe.recipe.name.$errors[0]?.$message"
                :error="v.editRecipe.recipe.name.$errors.length > 0"
                :label="$t('page.recipe_edit.form.name')"
              />
              <q-select
                :dark="color.cardItemGroupDark"
                :disable="disabled"
                :label="$t('page.recipe_edit.form.categories')"
                v-model:model-value="v.editRecipe.recipe.categories.$model"
                :error-message="v.editRecipe.recipe.categories.$errors[0]?.$message"
                :error="v.editRecipe.recipe.categories.$errors.length > 0"
                :options="categories"
                option-label="name"
                hide-bottom-space
                multiple
                clearable
                outlined
              />
              <div
                :style="{borderColor: color.cardItemGroupDark ? 'lightgrey' : 'grey'}"
                :class="{'rounded-borders q-card--bordered q-card--flat no-shadow q-pa-xs': editRecipe.recipe.hasImage && !editRecipe.image && !editRecipe.removeImage}"
                class="row"
              >
                <div class="col">
                  <q-toggle v-if="editRecipe.recipe.hasImage && !editRecipe.image"
                            :dark="color.cardItemGroupDark"
                            :disable="disabled"
                            v-model:model-value="v.editRecipe.removeImage.$model"
                            color="red"
                            :label="$t('page.recipe_edit.form.remove_image')"
                  />
                  <q-file v-if="!editRecipe.removeImage"
                          :dark="color.cardItemGroupDark"
                          :disable="disabled"
                          v-model:model-value="v.editRecipe.image.$model"
                          accept="image/*"
                          bottom-slots
                          clearable
                          hide-bottom-space
                          :label="$t('page.recipe_edit.form.image')"
                          max-file-size="20971520"
                          outlined
                  >
                    <template v-slot:prepend>
                      <q-icon
                        name="cloud_upload"
                        @click.stop
                      />
                    </template>
                  </q-file>
                </div>
              </div>
              <q-input
                outlined
                hide-bottom-space
                :disable="disabled"
                :dark="color.cardItemGroupDark"
                v-model:model-value="v.editRecipe.recipe.description.$model"
                :error-message="v.editRecipe.recipe.description.$errors[0]?.$message"
                :error="v.editRecipe.recipe.description.$errors.length > 0"
                type="textarea"
                :label="$t('page.recipe_edit.form.desc')"
                counter
                maxlength="2000"
              />
            </q-card-section>
          </q-card>
          <q-card
            flat
            bordered
            class="bg-card-item-group text-card-item-group"
          >
            <q-card-section
              class="q-gutter-y-md"
            >
              <q-select
                :disable="disabled"
                :dark="color.cardItemGroupDark"
                :label="$t('page.recipe_edit.form.default_glass')"
                v-model:model-value="v.editRecipe.recipe.defaultGlass.$model"
                :error-message="v.editRecipe.recipe.defaultGlass.$errors[0]?.$message"
                :error="v.editRecipe.recipe.defaultGlass.$errors.length > 0"
                :options="glasses"
                :option-label="x => x.name + ' (' + x.size + 'ml)'"
                hide-bottom-space
                outlined
                clearable
              />
            </q-card-section>
          </q-card>
          <q-card
            flat
            bordered
            class="bg-card-item-group text-card-item-group"
          >
            <q-card-section
              class="q-gutter-y-md"
            >
              <IngredientList
                v-model:model-value="v.editRecipe.recipe.productionSteps.$model"
                editable
                :background-color="color.cardItemGroup"
                :disable="disabled"
              />
            </q-card-section>
          </q-card>
          <div class="q-gutter-sm">
            <q-btn
              type="submit"
              style="width: 100px"
              color="positive"
              :label="saveBtnLabel"
              no-caps
              :disable="v.editRecipe.$invalid"
              :loading="sending"
            />
            <q-btn
              style="width: 100px"
              color="negative"
              :label="$t('page.recipe_edit.abort_btn_label')"
              no-caps
              @click="onClickAbort"
            />
          </div>
        </q-form>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script>
import RecipeService, { recipeDtoMapper } from '../services/recipe.service'
import IngredientList from 'components/IngredientList.vue'
import useVuelidate from '@vuelidate/core'
import { maxLength, minLength, required } from '@vuelidate/validators'
import { mapGetters } from 'vuex'
import Recipe from 'src/models/Recipe'
import GlassService from 'src/services/glass.service'

export default {
  name: 'RecipeEdit',
  components: { IngredientList },
  data () {
    return {
      editRecipe: {
        recipe: {},
        image: null,
        removeImage: false
      },
      isNew: true,
      error: '',
      sending: false,
      glasses: []
    }
  },
  async beforeRouteEnter (to, from, next) {
    const isNew = to.name === 'recipeadd'
    let recipe
    if (isNew) {
      recipe = new Recipe(0, '', {}, '', [], [], null)
    } else {
      recipe = await RecipeService.getRecipe(to.params.id)
    }
    const glasses = await GlassService.getAllGlasses()
    next(vm => {
      vm.editRecipe.recipe = recipe
      vm.isNew = isNew
      glasses.sort((a, b) => a.size - b.size)
      vm.glasses = glasses
    })
  },
  setup () {
    return { v: useVuelidate() }
  },
  methods: {
    onClickAbort () {
      if (!this.isNew) {
        this.$router.push({ name: 'recipedetails', params: { id: this.$route.params.id } })
        return
      }
      if (this.lastRecipeListRoute) {
        this.$router.push(this.lastRecipeListRoute)
        return
      }
      this.$router.push({ name: 'publicrecipes' })
    },
    onClickSave () {
      if (this.v.editRecipe.$invalid) {
        return
      }
      this.sending = true
      const dto = recipeDtoMapper.toRecipeCreateDto(this.editRecipe.recipe)

      let promise
      if (this.isNew) {
        dto.ownerId = this.user.id
        promise = RecipeService.createRecipe(dto, this.editRecipe.image)
      } else {
        promise = RecipeService.updateRecipe(this.editRecipe.recipe.id, dto, this.editRecipe.image, this.editRecipe.removeImage)
      }
      promise = promise.then((recipe) => {
        const msg = 'Recipe ' + (this.isNew ? 'created' : 'updated') + ' successfully'
        this.$q.notify({
          type: 'positive',
          message: msg
        })
        this.$router.push({ name: 'recipedetails', params: { id: recipe.id } })
      })
      promise.finally(() => {
        this.sending = false
      })
    }
  },
  computed: {
    ...mapGetters({
      categories: 'category/getCategories',
      lastRecipeListRoute: 'common/getLastRecipeListRoute',
      user: 'auth/getUser',
      color: 'appearance/getNormalColors'
    }),
    disabled () {
      return this.sending
    },
    saveBtnLabel () {
      if (this.isNew) {
        return this.$t('page.recipe_edit.save_btn_label')
      } else {
        return this.$t('page.recipe_edit.update_btn_label')
      }
    },
    headline () {
      if (this.isNew) {
        return this.$t('page.recipe_edit.headline_new')
      } else {
        return this.$t('page.recipe_edit.headline_edit')
      }
    }
  },
  validations () {
    return {
      editRecipe: {
        recipe: {
          name: {
            required,
            minLength: minLength(3),
            maxLength: maxLength(50)
          },
          description: {
            maxLength: maxLength(2000)
          },
          productionSteps: {
            required,
            minLength: minLength(1)
          },
          defaultGlass: {},
          categories: {}
        },
        image: {},
        removeImage: {}
      }
    }
  }
}
</script>

<style scoped>

</style>
