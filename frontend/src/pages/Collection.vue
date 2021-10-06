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
                <q-img v-if="collection.image"
                       class="col rounded-borders"
                       style="max-height: 200px"
                       src="https://cdn.quasar.dev/img/parallax2.jpg"
                />
              </div>
              <q-input label="Name"
                       outlined
                       :disable="!editData.editMode"
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
                       :disable="!editData.editMode"
                       v-model="editData.collection.description"
                       :rules="[
                        val => $v.editData.collection.description.required || 'Required',
                        val => $v.editData.collection.description.maxLength || 'Maximal length 2000']"
              >

              </q-input>
              <div class="row"
                   v-if="editData.editMode"
                   :class="{'rounded-borders q-card--bordered q-card--flat no-shadow q-pa-xs': editData.collection.image}"
              >
                <div class="col">
                  <q-toggle label="remove existing image"
                            color="red"
                            v-model="editData.removeImage"
                  />
                  <q-file label="Image"
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
              <q-checkbox label="complete"
                          :disable="!editData.editMode"
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
                  @click="onAbortEdit()"
                  color="negative"
                  label="Abort"
                  no-caps
                />
                <q-btn
                  v-if="editData.editMode"
                  type="submit"
                  color="positive"
                  label="Save"
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
    let collection = await CollectionService.getCollection(to.params.id);
    next(vm => {
      vm.collection = collection;
      vm.editData.collection = Object.assign({}, collection);
    })
  },
  data() {
    return {
      editData: {
        editMode: false,
        removeImage: false,
        collection: {
          name: '',
          description: '',
          image: true,
          completed: false
        }
      },
      collection: {
        name: 'Test',
        description: '',
        image: true,
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
    onAbortEdit() {
      this.resetEditData();
      this.editData.editMode = false;
    },
    resetEditData() {
      this.editData.collection = Object.assign({}, this.collection)
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
