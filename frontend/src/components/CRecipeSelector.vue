<template>
  <q-select
    outlined
    behavior="menu"
    :loading="loading"
    :dense="dense"
    :model-value="selection"
    :bg-color="bgColor"
    @update:model-value="$emit('update:selection', $event)"
    use-input
    :clearable="clearable"
    :disable="disable"
    hide-dropdown-icon
    :label="label"
    :options="fetchedOptions"
    :emit-value="emitValue"
    option-label="name"
    option-value="id"
    input-debounce="0"
    :map-options="mapOptions"
    @filter="filterRecipes"
    @filter-abort="abortFilterRecipes"
    :rules="rules"
  >
    <template v-slot:prepend>
      <slot name="prepend"/>
    </template>
  </q-select>
</template>

<script>
import RecipeService from '../services/recipe.service'
import { mapGetters } from 'vuex'

export default {
  name: 'CRecipeSelector',
  props: {
    selection: {
      type: [Object, Array]
    },
    label: {
      type: String,
      default: 'Recipe'
    },
    dense: {
      type: Boolean,
      default: false
    },
    loading: {
      type: Boolean,
      default: false
    },
    disable: {
      type: Boolean,
      default: false
    },
    clearable: {
      type: Boolean,
      default: false
    },
    emitValue: {
      type: Boolean,
      default: false
    },
    mapOptions: {
      type: Boolean,
      default: false
    },
    rules: {
      type: Array,
      default: () => []
    },
    bgColor: {
      type: String,
      default: undefined
    },
    noInputOptions: {
      type: Array,
      default: () => []
    }
  },
  emits: ['update:selection'],
  data () {
    return {
      fetchedOptions: []
    }
  },
  computed: {
    ...mapGetters({
      user: 'auth/getUser'
    })
  },
  methods: {
    filterRecipes (val, update, abort) {
      this.stringInput = val
      if (val.length < 2) {
        update()
        return
      }
      RecipeService.getRecipes(0, null, null, null, null, val, null, null)
        .then(page => {
          update(() => {
            this.fetchedOptions = page.content
          })
        }, () => abort)
    },
    abortFilterRecipes () {

    }
  }
}
</script>

<style scoped>

</style>
