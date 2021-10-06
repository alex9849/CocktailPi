<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el :to="{name: 'mycollections'}" label="My collections"/>
      <q-breadcrumbs-el :label="collection.name"/>
    </q-breadcrumbs>
    <h5 style="margin-bottom: 15px">{{ collection.name }}</h5>
    <div class="row q-col-gutter-sm">
      <div class="col-12 col-md-8 col-lg-9">
        <c-recipe-list
          list-body-color="transparent"
          flat
          :recipes="[]"
          class="rounded-borders q-card q-card--bordered q-card--flat no-shadow"
        />
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
                       v-model="editData.collection.name"
                       hide-bottom-space
                       :rules="[
                        val => $v.editData.collection.name.required || 'Required',
                        val => $v.editData.collection.name.maxLength || 'Maximal length 20',
                        val => $v.editData.collection.name.minLength || 'Minimal length 3']"
              >

              </q-input>
              <q-input label="description"
                       outlined
                       autogrow
                       hide-bottom-space
                       type="textarea"
                       :disable="!editData.editMode || editData.saving"
                       v-model="editData.collection.description"
                       :rules="[
                        val => $v.editData.collection.description.required || 'Required',
                        val => $v.editData.collection.description.maxLength || 'Maximal length 2000']"
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
                            v-model="editData.removeImage"
                            @change="onChangeRemoveImage($event)"
                  />
                  <q-file label="Image"
                          outlined
                          bottom-slots
                          :disable="editData.saving"
                          v-if="!editData.removeImage"
                          v-model="editData.newImage"
                          @change="onImageSelect($event)"
                          max-file-size="20971520"
                          accept="image/*"
                          clearable
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
                          v-model="editData.collection.completed"
              />
              <div class="q-gutter-x-sm">
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
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script>
import CRecipeList from "components/CRecipeList";
import {maxLength, minLength, required} from "vuelidate/lib/validators";
import CollectionService from "src/services/collection.service";

export default {
  name: "Collection",
  components: {CRecipeList},
  async beforeRouteEnter(to, from, next) {
    const collection = await CollectionService.getCollection(to.params.id);
    next(vm => {
      vm.collection = collection;
      vm.editData.collection = Object.assign({}, collection);
    })
  },
  data() {
    return {
      editData: {
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
      }
    }
  },
  watch: {
    collection: {
      handler(newValue) {
        this.resetEditData();
      }
    }
  },
  methods: {
    onImageSelect(image) {
      if (!!image) {
        this.editData.removeImage = false;
      }
    },
    onChangeRemoveImage(isRemove) {
      if (isRemove) {
        this.editData.newImage = null;
      }
    },
    onAbortEdit() {
      this.resetEditData();
      this.editData.editMode = false;
    },
    refreshCollection() {
      return CollectionService.getCollection(this.$route.params.id)
        .then(collection => {
          this.collection = collection
          return this.collection;
        });
    },
    onClickSafe() {
      this.editData.saving = true;
      CollectionService.updateCollection(this.editData.collection, this.editData.newImage, this.editData.removeImage)
        .then(() => {
          this.refreshCollection();
          this.editData.editMode = false;
        }).finally(() => {
        this.editData.saving = false;
      })
    },
    resetEditData() {
      this.editData.collection = Object.assign({}, this.collection)
      this.editData.newImage = null;
      this.editData.removeImage = false;
    }
  },
  validations() {
    let validations = {
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
    };
    return validations;
  }
}
</script>

<style scoped>

</style>
