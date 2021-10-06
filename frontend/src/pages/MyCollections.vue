<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="My collections"/>
    </q-breadcrumbs>
    <h5>My collections</h5>
    <top-button-arranger>
      <q-btn
        color="info"
        label="Refresh"
        :disable="isLoading"
        :loading="isLoading"
        @click="refreshCollections"
        no-caps
      />
    </top-button-arranger>
    <div class="q-pa-md row q-col-gutter-sm">
      <router-link v-for="collection of collections"
                   :key="collection.id"
                   class="col-12 col-md-6 col-lg-4 d-flex items-stretch"
                   :to="{name: 'collection', params: {id: collection.id}}"
      >
        <c-collection-card
          :value="collection"
          class="full-height"
          style="max-height: 300px"
        />
      </router-link>
      <div class="col-12 col-md-6 col-lg-4 d-flex items-stretch">
        <q-card
          style="cursor: pointer"
          @click="onClickCreateCollectionMenu()"
          class="full-height bg-grey-3"
          flat
          bordered
        >
          <q-card-actions align="center" class="full-height">
            <q-icon class="full-height" size="100px" :name="mdiPlusCircleOutline"></q-icon>
          </q-card-actions>
        </q-card>
      </div>
    </div>
    <c-edit-dialog
      v-model="createCollection.menuOpen"
      :saving="createCollection.saving"
      title="Create collection"
      :valid="!$v.createCollection.$invalid"
      :error-message="createCollection.errorMessage"
      @clickAbort="onCloseCreateCollectionMenu"
      @clickSave="onClickSafeNewCollection"
    >
      <q-input
        label="Name"
        v-model="createCollection.name"
        outlined
        @input="$v.createCollection.name.$touch()"
        :rules="[val => $v.createCollection.name.required || 'Required',
              val => $v.createCollection.name.minLength || 'Min 3 letters',
              val => $v.createCollection.name.maxLength || 'Max 20 letters']"
      />
    </c-edit-dialog>
  </q-page>
</template>

<script>
import CCollectionCard from "components/CCollectionCard";
import CollectionService from "../services/collection.service";
import {store} from '../store';
import {mdiPlusCircleOutline} from "@quasar/extras/mdi-v5";
import CEditDialog from "components/CEditDialog";
import {mapGetters} from "vuex";
import {maxLength, minLength, required} from "vuelidate/lib/validators";
import TopButtonArranger from "components/TopButtonArranger";

export default {
  name: "MyCollections",
  components: {TopButtonArranger, CEditDialog, CCollectionCard},
  async beforeRouteEnter(to, from, next) {
    let userId = store.getters['auth/getUser'].id;
    let collections = await CollectionService.getCollectionsByUser(userId);
    next(vm => {
      vm.collections = collections;
    })
  },
  data() {
    return {
      isLoading: false,
      collections: [],
      createCollection: {
        menuOpen: false,
        errorMessage: '',
        name: '',
        saving: false
      }
    }
  },
  created() {
    this.mdiPlusCircleOutline = mdiPlusCircleOutline;
  },
  computed: {
    ...mapGetters({
      currentUser: 'auth/getUser'
    })
  },
  methods: {
    onClickCreateCollectionMenu() {
      this.createCollection.menuOpen = true;
    },
    onCloseCreateCollectionMenu() {
      this.createCollection.menuOpen = false;
      this.createCollection.name = ''
    },
    refreshCollections() {
      this.isLoading = true;
      let vm = this;
      setTimeout(() => {
        CollectionService.getCollectionsByUser(this.currentUser.id)
          .then(collections => {
            vm.collections = collections
          })
          .finally(() => vm.isLoading = false);
      }, 500)
    },
    onClickSafeNewCollection() {
      CollectionService.createCollection(this.createCollection.name)
        .then(() => {
          this.createCollection.errorMessage = '';
          this.refreshCollections();
          this.onCloseCreateCollectionMenu();
        }, err => {
          this.createCollection.errorMessage = err.response.data.message;
        })
        .finally(() => {
          this.createCollection.saving = false;
        })
    }
  },
  validations() {
    let validations = {
      createCollection: {
        name: {
          required,
          minLength: minLength(3),
          maxLength: maxLength(20)
        }
      }
    };
    return validations;
  }
}
</script>

<style scoped>
a {
  text-decoration: none;
  color: inherit;
}
</style>
