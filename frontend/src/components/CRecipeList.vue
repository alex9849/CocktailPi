<template>
  <div>
    <div class="row q-col-gutter-md">
      <slot name="firstItem"/>
      <div v-if="showNoData"
           class="col-12"
      >
        <q-card
          flat
          bordered
          class="bg-card-header text-card-header"
        >
          <q-card-section class="text-center">
            {{ $t('component.recipe_list.no_recipes_found') }}
          </q-card-section>
        </q-card>
      </div>
      <div
        v-for="recipe of recipes"
        :key="recipe.id"
        class="col-12 col-lg-6"
      >
        <router-link
          class="no-link-format"
          :to="{name: 'recipedetails', params: {id: recipe.id}}"
        >
          <c-recipe-card
            :recipe="recipe"
            show-ingredients
            style="height: 160px"
            class="q-card--bordered q-card--flat no-shadow"
            :background-color="color.cardBody"
          >
            <template v-slot:headline>
              <slot
                v-if="!!$slots.recipeHeadline"
                :recipe="recipe"
                name="recipeHeadline"
              />
            </template>
            <template v-slot:topRight>
              <slot
                v-if="!!$slots.recipeTopRight"
                :recipe="recipe"
                name="recipeTopRight"
              />
            </template>
          </c-recipe-card>
        </router-link>
      </div>
      <slot name="lastItem"/>
    </div>
  </div>
</template>

<script>
import CRecipeCard from 'components/CRecipeCard'
import { mapGetters, mapMutations } from 'vuex'

export default {
  name: 'CRecipeList',
  components: { CRecipeCard },
  props: {
    recipes: {
      type: Array,
      required: true
    },
    showNoData: {
      type: Boolean,
      default: false
    }
  },
  created () {
    this.setLastRecipeListRoute(this.$route)
  },
  methods: {
    ...mapMutations({
      setLastRecipeListRoute: 'common/setLastRecipeListRoute'
    })
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    })
  },
  watch: {
    $route: {
      deep: true,
      handler (newValue, oldValue) {
        if (oldValue.name !== newValue.name) {
          return
        }
        this.setLastRecipeListRoute(newValue)
      }
    }
  }
}
</script>

<style scoped>
.no-link-format {
  text-decoration: none;
  color: inherit;
}
</style>
