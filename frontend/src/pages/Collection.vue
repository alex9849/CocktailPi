<template>
  <q-page class="page-content" padding>
    <h5>{{ collection.name }}</h5>
    <q-breadcrumbs separator="/" class="text-orange" active-color="secondary">
      <q-breadcrumbs-el label="Collections" :to="{ name: 'collections' }" />
      <q-breadcrumbs-el :label="collection.name" />
    </q-breadcrumbs>
    <top-button-arranger
      v-if="isCanEdit"
    >
      <q-btn
        v-if="!editRecipeMode.active"
        @click="editRecipeMode.active = true"
        color="grey"
        :label="$t('page.collection.modify_recipes')"
        no-caps
      />
      <q-btn
        v-if="editRecipeMode.active"
        @click="editRecipeMode.active = false"
        color="warning"
        :label="$t('page.collection.stop_modify_recipes')"
        no-caps
      />
      <q-btn
        color="negative"
        :label="$t('page.collection.delete')"
        no-caps
        @click="showDeleteCollectionDialog = true"
      />
    </top-button-arranger>
    <div class="row q-col-gutter-md q-py-md">
      <div class="col-12 col-md-8 col-lg-9">
        <c-recipe-search-list
          ref="recipeSearchList"
          v-if="collectionLoaded"
          :collection-id="collection.id"
        >
          <template v-slot:firstItem>
            <div class="col-12"
                 v-if="editRecipeMode.active"
            >
              <c-recipe-selector
                :loading="editRecipeMode.addingRecipe"
                :label="$t('page.collection.add_recipe')"
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
        <q-card
          class="rounded-borders bg-card-body text-card-body"
          bordered
        >
          <q-card-section class="col">
            <q-form @submit.prevent="onClickSafe"
                    class="q-gutter-md"
            >
              <div>
                <q-img v-if="collection.hasImage"
                       class="col rounded-borders"
                       style="max-height: 200px"
                       :src="$store.getters['auth/getFormattedServerAddress'] + '/api/collection/' + collection.id + '/image?timestamp=' + collection.lastUpdate.getTime()"
                />
              </div>
              <q-input :label="$t('page.collection.form.name')"
                       :dark="color.cardBodyDark"
                       outlined
                       :disable="!editData.editMode || editData.saving"
                       v-model:model-value="editData.collection.name"
                       hide-bottom-space
                       :rules="[
                        val => !v.editData.collection.name.required.$invalid || $t('errors.field_required'),
                        val => !v.editData.collection.name.maxLength.$invalid || $t('errors.max_letters', {nr: 20}),
                        val => !v.editData.collection.name.minLength.$invalid || $t('errors.min_letters', {nr: 3})]"
              >

              </q-input>
              <div class="row"
                   v-if="editData.editMode"
                   :style="{borderColor: color.cardBodyDark ? 'lightgrey' : 'grey'}"
                   :class="{'rounded-borders q-card--bordered q-card--flat no-shadow q-pa-xs': collection.hasImage && !editData.newImage && !editData.removeImage}"
              >
                <div class="col">
                  <q-toggle :label="$t('page.collection.form.remove_img')"
                            color="red"
                            :dark="color.cardBodyDark"
                            :disable="editData.saving"
                            v-if="collection.hasImage && !editData.newImage"
                            v-model:model-value="editData.removeImage"
                            @change="onChangeRemoveImage($event)"
                  />
                  <q-file :label="$t('page.collection.form.image')"
                          outlined
                          :dark="color.cardBodyDark"
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
              <q-input :label="$t('page.collection.form.desc')"
                       outlined
                       :dark="color.cardBodyDark"
                       autogrow
                       hide-bottom-space
                       type="textarea"
                       :disable="!editData.editMode || editData.saving"
                       v-model:model-value="editData.collection.description"
                       :rules="[
                        val => !v.editData.collection.description.required.$invalid || $t('errors.field_required'),
                        val => !v.editData.collection.description.maxLength.$invalid || $t('errors.max_letters', {nr: 2000})]"
              >
              </q-input>
              <q-card-actions class="q-pa-none">
                <q-btn
                  style="width: 100px"
                  v-if="!editData.editMode && isCanEdit"
                  @click="editData.editMode = true"
                  color="grey"
                  :label="$t('page.collection.form.edit_btn_label')"
                  no-caps
                />
                <q-btn
                  style="width: 100px"
                  v-if="editData.editMode"
                  type="submit"
                  color="positive"
                  :loading="editData.saving"
                  :label="$t('page.collection.form.save_btn_label')"
                  no-caps
                />
                <q-btn
                  style="width: 100px"
                  v-if="editData.editMode"
                  :disable="editData.saving"
                  @click="onAbortEdit()"
                  color="negative"
                  :label="$t('page.collection.form.abort_btn_label')"
                  no-caps
                />
              </q-card-actions>
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
      :question="$t('page.collection.delete_dialog.headline', {name: collection.name})"
      v-model:show="showDeleteCollectionDialog"
    />
    <c-edit-dialog
      :title="$t('page.collection.add_recipe')"
      v-model:show="editData.showAddRecipeDialog"
    >
      <c-recipe-selector/>
    </c-edit-dialog>
  </q-page>
</template>

<script>
import { maxLength, minLength, required } from '@vuelidate/validators'
import CollectionService, { collectionDtoMapper } from 'src/services/collection.service'
import { mdiDelete, mdiPlusCircleOutline } from '@quasar/extras/mdi-v5'
import TopButtonArranger from 'components/TopButtonArranger'
import CQuestion from 'components/CQuestion'
import CEditDialog from 'components/CEditDialog'
import CRecipeSelector from 'components/CRecipeSelector'
import CRecipeSearchList from 'components/CRecipeSearchList'
import useVuelidate from '@vuelidate/core'
import { mapGetters } from 'vuex'

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
    let collection
    try {
      collection = await CollectionService.getCollection(to.params.id)
    } catch (e) {
      if (e.response.status === 404) {
        next({ name: '404Page' })
        return
      }
    }
    next(vm => {
      vm.collection = collection
      vm.editData.collection = Object.assign({}, collection)
      vm.collectionLoaded = true
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
          hasImage: false
        }
      },
      collection: {
        id: -1,
        name: 'Test',
        description: '',
        hasImage: false
      },
      collectionLoaded: false,
      showDeleteCollectionDialog: false,
      deletingCollection: false
    }
  },
  setup () {
    return {
      v: useVuelidate(),
      mdiDelete,
      mdiPlusCircleOutline
    }
  },
  watch: {
    collection: {
      handler () {
        this.resetEditData()
      }
    }
  },
  computed: {
    ...mapGetters({
      getUser: 'auth/getUser',
      color: 'appearance/getNormalColors'
    }),
    isCanEdit () {
      return this.getUser.adminLevel >= 3 || this.getUser.id === this.collection.ownerId
    }
  },
  methods: {
    onDeleteCollection () {
      this.deletingCollection = true
      CollectionService.deleteCollection(this.collection.id)
        .then(() => {
          this.$router.push({ name: 'collections' })
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
            message: this.$t('page.collection.notifications.recipe_removed')
          })
        })
        .finally(() => {
          this.$refs.recipeSearchList.onClickSearch()
          this.editRecipeMode.deletingRecipIds = this.editRecipeMode.deletingRecipIds.filter(x => x !== recipeId)
        })
    },
    onAddRecipe (recipeId) {
      this.editRecipeMode.addingRecipe = true
      CollectionService.addRecipeToCollection(this.collection.id, recipeId)
        .then(() => {
          this.$q.notify({
            type: 'positive',
            message: this.$t('page.collection.notifications.recipe_added')
          })
        })
        .finally(() => {
          this.editRecipeMode.addingRecipe = false
          this.$refs.recipeSearchList.onClickSearch()
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
      const dto = collectionDtoMapper.toCreateCollectionDto(this.editData.collection)
      CollectionService.updateCollection(dto, this.collection.id, this.editData.newImage, this.editData.removeImage)
        .then(() => {
          this.refreshCollection()
          this.editData.editMode = false
          this.$q.notify({
            type: 'positive',
            message: this.$t('page.collection.notifications.collection_updated')
          })
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
          }
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
