<template>
  <div>
    <div class="row q-col-gutter-md">
      <slot name="firstItem" />

      <div v-if="showNoData" class="col-12">
        <q-card flat bordered class="bg-card-header text-card-header">
          <q-card-section class="text-center">
            {{ $t('component.recipe_list.no_recipes_found') }}
          </q-card-section>
        </q-card>
      </div>

      <div
        v-for="index in inView.length"
        :key="recipes[index - 1].id"
        :data-id="index - 1"
        class="col-recipe-list-card card-height"
        v-intersection="onIntersection"
      >
        <router-link
          class="no-link-format"
          :to="{ name: 'recipedetails', params: { id: recipes[index - 1].id } }"
        >
          <c-recipe-card
            v-if="inView[index - 1]"
            :recipe="recipes[index - 1]"
            show-ingredients
            class="q-card--bordered q-card--flat no-shadow"
            :background-color="color.cardBody"
          >
            <template v-slot:headline>
              <slot
                v-if="$slots.recipeHeadline"
                :recipe="recipes[index - 1]"
                name="recipeHeadline"
              />
            </template>
            <template v-slot:topRight>
              <slot
                v-if="$slots.recipeTopRight"
                :recipe="recipes[index - 1]"
                name="recipeTopRight"
              />
            </template>
          </c-recipe-card>
        </router-link>
      </div>

      <slot name="lastItem" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRoute } from 'vue-router'
import CRecipeCard from 'components/CRecipeCard'

// Props
const props = defineProps({
  recipes: {
    type: Array,
    required: true
  },
  showNoData: {
    type: Boolean,
    default: false
  }
})

// Store & Route
const store = useStore()
const route = useRoute()

const color = computed(() => store.getters['appearance/getNormalColors'])

const setLastRecipeListRoute = (route) => {
  store.commit('common/setLastRecipeListRoute', route)
}

// Reactive State
const inView = ref([])

// Lifecycle
onMounted(() => {
  setLastRecipeListRoute(route)
})

// Watchers
watch(route, (newVal) => {
  setLastRecipeListRoute(newVal)
}, { deep: true })

watch(() => props.recipes, (newValue) => {
  const currLen = inView.value.length
  if (newValue.length > currLen) {
    inView.value.push(...Array(newValue.length - currLen).fill(false))
  } else {
    inView.value.splice(newValue.length, currLen - newValue.length)
  }
}, { immediate: true, deep: true })

// Intersection handling
function onIntersection (entry) {
  const index = parseInt(entry.target.dataset.id, 10)
  inView.value.splice(index, 1, entry.isIntersecting)
}

// (Optional) Sliding render window helper — not currently used
/*
function renderCard(index) {
  const start = Math.max(0, index - 3)
  const end = Math.min(inView.value.length - 1, index + 3)
  return inView.value.slice(start, end + 1).some(Boolean)
} */
</script>

<style scoped>
.no-link-format {
  text-decoration: none;
  color: inherit;
}

.card-height {
  height: unset;
  @media (min-width: 401px) {
    height: 200px;
  }
}
</style>
