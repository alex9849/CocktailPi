<template>
  <q-page class="page-content" padding>
    <h5>My collections</h5>
    <top-button-arranger>
      <q-btn
        color="positive"
        label="Create collection"
        no-caps
        @click="onClickCreateCollectionMenu"
      />
      <q-btn
        color="info"
        label="Refresh"
        :disable="isLoading"
        :loading="isLoading"
        @click="refreshCollections"
        no-caps
      />
    </top-button-arranger>
    <div class="q-py-md row q-col-gutter-sm">
      <div v-for="collection of collections"
           :key="collection.id"
           class="col-12 col-sm-6 col-lg-4 d-flex items-stretch"
      >
        <router-link
          :to="{name: 'collection', params: {id: collection.id}}"
        >
          <c-collection-card
            :model-value="collection"
            class="full-height"
            style="max-height: 300px"
          />
        </router-link>
      </div>
    </div>
    <div
    >
    </div>
    <q-card
      v-if="collections.length === 0"
      flat bordered
      class="bg-card-secondary"
    >
      <div class="row q-pa-md items-center q-gutter-sm">
        <q-icon size="sm" :name="mdiAlert" />
        <p class="">No collections found!</p>
      </div>
    </q-card>
    <c-edit-dialog
      v-model:show="createCollection.menuOpen"
      :saving="createCollection.saving"
      title="Create collection"
      :valid="!v.createCollection.$invalid"
      :error-message="createCollection.errorMessage"
      @clickAbort="onCloseCreateCollectionMenu"
      @clickSave="onClickSafeNewCollection"
    >
      <q-input
        label="Name"
        v-model:model-value="v.createCollection.name.$model"
        outlined
        :rules="[val => !v.createCollection.name.required.$invalid || 'Required',
              val => !v.createCollection.name.minLength.$invalid || 'Min 3 letters',
              val => !v.createCollection.name.maxLength.$invalid || 'Max 20 letters']"
      />
    </c-edit-dialog>
  </q-page>
</template>

<script>
import CCollectionCard from 'components/CCollectionCard'
import CollectionService from '../services/collection.service'
import { mdiAlert, mdiPlusCircleOutline } from '@quasar/extras/mdi-v5'
import CEditDialog from 'components/CEditDialog'
import { mapGetters } from 'vuex'
import { maxLength, minLength, required } from '@vuelidate/validators'
import TopButtonArranger from 'components/TopButtonArranger'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'MyCollections',
  components: { TopButtonArranger, CEditDialog, CCollectionCard },
  async beforeRouteEnter (to, from, next) {
    const collections = await CollectionService.getCollections()
    next(vm => {
      vm.collections = collections
    })
  },
  data () {
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
  setup () {
    return {
      v: useVuelidate(),
      mdiPlusCircleOutline: mdiPlusCircleOutline
    }
  },
  created () {
    this.mdiAlert = mdiAlert
  },
  computed: {
    ...mapGetters({
      currentUser: 'auth/getUser'
    })
  },
  methods: {
    onClickCreateCollectionMenu () {
      this.createCollection.menuOpen = true
    },
    onCloseCreateCollectionMenu () {
      this.createCollection.menuOpen = false
      this.createCollection.name = ''
    },
    refreshCollections () {
      this.isLoading = true
      const vm = this
      setTimeout(() => {
        CollectionService.getCollections()
          .then(collections => {
            vm.collections = collections
          })
          .finally(() => {
            vm.isLoading = false
          })
      }, 500)
    },
    onClickSafeNewCollection () {
      CollectionService.createCollection(this.createCollection.name)
        .then(() => {
          this.createCollection.errorMessage = ''
          this.refreshCollections()
          this.onCloseCreateCollectionMenu()
        }, err => {
          this.createCollection.errorMessage = err.response.data.message
        })
        .finally(() => {
          this.createCollection.saving = false
        })
    }
  },
  validations () {
    return {
      createCollection: {
        name: {
          required,
          minLength: minLength(3),
          maxLength: maxLength(20)
        }
      }
    }
  }
}
</script>

<style scoped>
a {
  text-decoration: none;
  color: inherit;
}
</style>
