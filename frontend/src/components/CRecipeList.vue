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
        v-for="(recipe, index) in recipes"
        :key="recipe.id"
        :data-id="index"
        :class="cardClass"
        v-intersection="onIntersection"
        v-memo="[isInView(index)]"
      >
        <router-link
          v-if="isInView(index)"
          class="no-link-format"
          :to="{ name: 'recipedetails', params: { id: recipe.id } }"
        >
          <c-recipe-card
            :recipe="recipe"
            show-ingredients
            :img-no-spinner="!firstRender[index]"
            class="q-card--bordered q-card--flat no-shadow"
            :background-color="color.cardBody"
          >
            <template v-slot:headline>
              <slot
                v-if="$slots.recipeHeadline"
                :recipe="recipe"
                name="recipeHeadline"
              />
            </template>
            <template v-slot:topRight>
              <slot
                v-if="$slots.recipeTopRight"
                :recipe="recipe"
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
  },
  cardClass: {
    type: String,
    default: () => 'col-recipe-list-card recipe-card-height'
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
const firstRender = ref([])

// Lifecycle
onMounted(() => {
  setLastRecipeListRoute({
    path: route.path,
    name: route.name,
    fullPath: route.fullPath,
    params: { ...route.params },
    query: { ...route.query }
  })
})

// Watchers
watch(route, (oldVal, newVal) => {
  if (oldVal.name !== newVal.name) {
    return
  }
  setLastRecipeListRoute({
    path: newVal.path,
    name: newVal.name,
    fullPath: newVal.fullPath,
    params: { ...newVal.params },
    query: { ...newVal.query }
  })
}, { deep: true })

watch(() => props.recipes.length, (newValue) => {
  const currLen = inView.value.length
  if (newValue > currLen) {
    inView.value.push(...Array(newValue - currLen).fill(false))
    firstRender.value.push(...Array(newValue - currLen).fill(true))
  }
}, { immediate: true })

// Intersection handling
function onIntersection (entry) {
  const index = parseInt(entry.target.dataset.id, 10)
  if (inView.value[index] && !entry.isIntersecting) {
    firstRender.value[index] = false
  }
  inView.value[index] = entry.isIntersecting
}

function isInView (index) {
  const minIdx = Math.max(0, index - 10)
  const maxIdx = Math.min(inView.value.length - 1, index + 10)
  for (let i = minIdx; i <= maxIdx; i++) {
    if (inView.value[i]) {
      return true
    }
  }
  return false
}

</script>

<style scoped>
</style>
