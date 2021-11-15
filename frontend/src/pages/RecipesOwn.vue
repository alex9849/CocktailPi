<template>
  <q-page class="page-content" padding>
    <h5>My Recipes</h5>
    <top-button-arranger>
      <q-btn
        v-if="isRecipeCreatorRole"
        color="positive"
        label="Create recipe"
        no-caps
        :to="{name: 'recipeadd'}"
      />
      <q-btn
        color="info"
        label="Refresh"
        no-caps
        :disable="refreshing"
        @click="onRefreshButton"
      />
    </top-button-arranger>
    <div class="q-py-md">
      <c-recipe-search-list
        :only-own-recipes="true"
        ref="recipeSearchList"
      />
    </div>
  </q-page>
</template>

<script>
import CRecipeSearchList from '../components/CRecipeSearchList'
import TopButtonArranger from 'components/TopButtonArranger'
import { mapGetters } from 'vuex'

export default {
  name: 'OwnRecipes',
  components: { TopButtonArranger, CRecipeSearchList },
  data () {
    return {
      refreshing: false
    }
  },
  computed: {
    ...mapGetters({
      isRecipeCreatorRole: 'auth/isRecipeCreator'
    })
  },
  methods: {
    onRefreshButton () {
      this.refreshing = true
      this.$refs.recipeSearchList.updateRecipes()
        .finally(() => this.refreshing = false)
    }
  }
}
</script>

<style scoped>

</style>
