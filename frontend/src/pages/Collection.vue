<template>
  <q-page class="page-content" padding>
    <h5>{{ collection.name }}</h5>
    <top-button-arranger>
      <q-btn
        v-if="!editRecipeMode.active"
        @click="editRecipeMode.active = true"
        color="grey"
        label="Modify recipes"
        no-caps
      />
      <q-btn
        v-if="editRecipeMode.active"
        @click="editRecipeMode.active = false"
        color="warning"
        label="Stop modifying recipes"
        no-caps
      />
      <q-btn
        color="negative"
        label="Delete collection"
        no-caps
        @click="showDeleteCollectionDialog = true"
      />
    </top-button-arranger>
    <div class="row q-col-gutter-md q-py-md">
      <div class="col-12 col-md-8 col-lg-9">
        <c-recipe-search-list
          ref="recipeSearchList"
          :collection-id="collection.id"
        >
          <template v-slot:firstItem>
            <div class="col-12"
                 v-if="editRecipeMode.active"
            >
              <c-recipe-selector
                :loading="editRecipeMode.addingRecipe"
                label="Add recipe"
                class="bg-grey-2"
                @update:selection="!!$event ? onAddRecipe($event.id) : ''"
              >
                <template v-slot:prepend>
                  <q-icon :name="mdiPlusCircleOutline" />
                </template>
              </c-recipe-selector>
            </div>
          </template>
          <template v-slot:recipeHeadline="{recipe}" >
            <div class="flex content-center">
              <b>{{ recipe.name }}</b>
              <q-btn
                round
                flat
                dense
                v-if="editRecipeMode.active"
                :loading="editRecipeMode.deletingRecipIds.some(x => x === recipe.id)"
                @click.prevent="onClickDeleteRecipe(recipe.id)"
                class="text-red"
                :icon="mdiDelete"
              >
              </q-btn>
            </div>
          </template>
        </c-recipe-search-list>
      </div>
      <div class="col-12 col-md-4 col-lg-3">
        <q-card class="rounded-borders"
                bordered style="background-color: #f3f3fa"
        >
          <q-card-section class="col">
            <q-form @submit.prevent="() => {}"
                    class="q-gutter-md"
            >
              <div>
                <q-img v-if="collection.hasImage"
                       class="col rounded-borders"
                       style="max-height: 200px"
                       :src="$store.getters['auth/getFormattedServerAddress'] + '/api/collection/' + collection.id + '/image?timestamp=' + collection.lastUpdate.getMilliseconds()"
                />
              </div>
              <q-input label="Name"
                       outlined
                       :disable="!editData.editMode || editData.saving"
                       v-model:model-value="editData.collection.name"
                       hide-bottom-space
                       :rules="[
                        val => !v.editData.collection.name.required.$invalid || 'Required',
                        val => !v.editData.collection.name.maxLength.$invalid || 'Maximal length 20',
                        val => !v.editData.collection.name.minLength.$invalid || 'Minimal length 3']"
              >

              </q-input>
              <div class="row"
                   v-if="editData.editMode"
                   :class="{'rounded-borders q-card--bordered q-card--flat no-shadow q-pa-xs': collection.hasImage && !editData.newImage && !editData.removeImage}"
              >
                <div class="col">
                  <q-toggle label="remove existing image"
                            color="red"
                            :disable="editData.saving"
                            v-if="collection.hasImage && !editData.newImage"
                            v-model:model-value="editData.removeImage"
                            @change="onChangeRemoveImage($event)"
                  />
                  <q-file label="Image"
                          outlined
                          bottom-slots
                          :disable="editData.saving"
                          v-if="!editData.removeImage"
                          v-model:model-value="editData.newImage"
                          @change="onImageSelect($event)"
                          max-file-size="20971520"
                          accept="image/*"
                          clearable
                          hide-bottom-space
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
              <q-checkbox label="complete"
                          :disable="!editData.editMode || editData.saving"
                          v-model:model-value="editData.collection.completed"
              />
              <q-input label="description"
                       outlined
                       autogrow
                       hide-bottom-space
                       type="textarea"
                       :disable="!editData.editMode || editData.saving"
                       v-model:model-value="editData.collection.description"
                       :rules="[
                        val => !v.editData.collection.description.required.$invalid || 'Required',
                        val => !v.editData.collection.description.maxLength.$invalid || 'Maximal length 2000']"
              >
              </q-input>
              <q-btn
                v-if="!editData.editMode"
                @click="editData.editMode = true"
                color="grey"
                label="Edit"
                no-caps
              />
              <q-btn
                v-if="editData.editMode"
                :disable="editData.saving"
                @click="onAbortEdit()"
                color="negative"
                label="Abort"
                no-caps
              />
              <q-btn
                v-if="editData.editMode"
                type="submit"
                color="positive"
                :loading="editData.saving"
                label="Save"
                @click="onClickSafe()"
                no-caps
              />
            </q-form>
          </q-card-section>
        </q-card>
      </div>
    </div>
    <c-question
      ok-button-text="Delete"
      :loading="deletingCollection"
      ok-color="red"
      @clickAbort="showDeleteCollectionDialog = false"
      @clickOk="onDeleteCollection"
      :question="'Delete collection \'' + collection.name + '\'?'"
      v-model:show="showDeleteCollectionDialog"
    />
    <c-edit-dialog
      title="Add recipe"
      v-model:show="editData.showAddRecipeDialog"
    >
      <c-recipe-selector/>
    </c-edit-dialog>
  </q-page>
</template>

<script>
import { maxLength, minLength, required } from '@vuelidate/validators'
import CollectionService from 'src/services/collection.service'
import { mdiDelete, mdiPlusCircleOutline } from '@quasar/extras/mdi-v5'
import TopButtonArranger from 'components/TopButtonArranger'
import CQuestion from 'components/CQuestion'
import CEditDialog from 'components/CEditDialog'
import CRecipeSelector from 'components/CRecipeSelector'
import CRecipeSearchList from 'components/CRecipeSearchList'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'Collection',
  components: {
    CRecipeSearchList,
    CRecipeSelector,
    CEditDialog,
    CQuestion,
    TopButtonArranger
  },
  async beforeRouteEnter (to, from, next) {
    const collection = await CollectionService.getCollection(to.params.id)
    next(vm => {
      vm.collection = collection
      vm.editData.collection = Object.assign({}, collection)
    })
  },
  data () {
    return {
      editRecipes: false,
      editRecipeMode: {
        active: false,
        deletingRecipIds: [],
        addingRecipe: false
      },
      editData: {
        showAddRecipeDialog: false,
        saving: false,
        editMode: false,
        removeImage: false,
        newImage: null,
        collection: {
          name: '',
          description: '',
          hasImage: false,
          completed: false
        }
      },
      collection: {
        name: 'Test',
        description: '',
        hasImage: false,
        complete: false
      },
      showDeleteCollectionDialog: false,
      deletingCollection: false
    }
  },
  setup () {
    return {
      v: useVuelidate(),
      mdiDelete: mdiDelete,
      mdiPlusCircleOutline: mdiPlusCircleOutline
    }
  },
  watch: {
    collection: {
      handler (newValue) {
        this.resetEditData()
      }
    }
  },
  methods: {
    onDeleteCollection () {
      this.deletingCollection = true
      CollectionService.deleteCollection(this.collection.id)
        .then(() => {
          this.$router.push({ name: 'mycollections' })
        })
        .finally(() => {
          this.deletingCollection = false
        })
    },
    onClickDeleteRecipe (recipeId) {
      this.editRecipeMode.deletingRecipIds.push(recipeId)
      CollectionService.removeRecipeFromCollection(this.collection.id, recipeId)
        .then(() => {
          this.$q.notify({
            type: 'positive',
            message: 'Recipe removed successfully'
          })
        })
        .finally(() => {
          this.$refs.recipeSearchList.updateRecipes(false).finally(() => {
            this.editRecipeMode.deletingRecipIds = this.editRecipeMode.deletingRecipIds.filter(x => x !== recipeId)
          })
        })
    },
    onAddRecipe (recipeId) {
      this.editRecipeMode.addingRecipe = true
      CollectionService.addRecipeToCollection(this.collection.id, recipeId)
        .then(() => {
          this.$q.notify({
            type: 'positive',
            message: 'Recipe added successfully'
          })
        })
        .finally(() => {
          this.editRecipeMode.addingRecipe = false
          this.$refs.recipeSearchList.updateRecipes(false)
        })
    },
    onImageSelect (image) {
      if (image) {
        this.editData.removeImage = false
      }
    },
    onChangeRemoveImage (isRemove) {
      if (isRemove) {
        this.editData.newImage = null
      }
    },
    onAbortEdit () {
      this.resetEditData()
      this.editData.editMode = false
    },
    refreshCollection () {
      return CollectionService.getCollection(this.$route.params.id)
        .then(collection => {
          this.collection = collection
          return this.collection
        })
    },
    onClickSafe () {
      this.editData.saving = true
      CollectionService.updateCollection(this.editData.collection, this.collection.id, this.editData.newImage, this.editData.removeImage)
        .then(() => {
          this.refreshCollection()
          this.editData.editMode = false
        }).finally(() => {
          this.editData.saving = false
        })
    },
    resetEditData () {
      this.editData.collection = Object.assign({}, this.collection)
      this.editData.newImage = null
      this.editData.removeImage = false
    }
  },
  validations () {
    return {
      editData: {
        collection: {
          name: {
            required,
            minLength: minLength(3),
            maxLength: maxLength(20)
          },
          description: {
            required,
            maxLength: maxLength(2000)
          },
          complete: {}
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
