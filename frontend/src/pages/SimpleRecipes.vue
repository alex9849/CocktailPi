<template>
  <q-page padding class="page-content column flex">
    <h4 class="text-center">{{ $t('page.simple_recipes.headline') }}</h4>
    <simple-recipes-search-list
      @empty="showNoData = $event"
    />
    <div class="row items-center"
         style="flex-grow: 1"
         v-if="showNoData"
    >
      <div
        class="col-12 text-h5"
      >
        <div class="row items-center justify-center">
          <q-icon :name="mdiAlert" size="lg"/>
          <p>
            {{ $t('page.simple_recipes.no_data_msg') }}
          </p>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script>
import SimpleRecipesSearchList from 'components/SimpleRecipesSearchList'
import { mdiAlert } from '@quasar/extras/mdi-v5'
import store from '../store'

export default {
  name: 'SimpleRecipes',
  components: { SimpleRecipesSearchList },
  async beforeRouteEnter (to, from, next) {
    await store().dispatch('common/fetchDefaultFilter')
    next()
  },
  data () {
    return {
      showNoData: false
    }
  },
  created () {
    this.mdiAlert = mdiAlert
  }
}
</script>

<style scoped>

</style>
