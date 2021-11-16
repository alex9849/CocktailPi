<template>
  <q-card>
    <q-card-section>
      <div class="q-gutter-md">
        <div>
          <b>Search-options</b>
        </div>
        <div>
          <q-card
            flat bordered
          >
            <q-card-section class="q-pa-none">
              <q-expansion-item
                label="Expert-Search"
                v-model="isFilterExpanded"
                :header-class="isFilterExpanded? 'bg-grey-2' : ''"
              >
                <div
                  class="row justify-evenly q-col-gutter-sm q-pa-sm"
                >
                  <q-checkbox
                    :model-value="filter.fabricableWithOwnedIngredients"
                    @update:model-value="$emit('update:filter', {...filter, fabricableWithOwnedIngredients: $event})"
                    class="col-12 col-sm-6"
                  >
                    Fabricable with owned ingredients
                  </q-checkbox>
                  <q-checkbox
                    :model-value="filter.automaticallyFabricable"
                    @update:model-value="$emit('update:filter', {...filter, automaticallyFabricable: $event})"
                    class="col-12 col-sm-6"
                  >
                    Can be fabricated fully automatic
                  </q-checkbox>
                  <c-ingredient-selector
                    :model-value="filter.containsIngredients"
                    @update:model-value="$emit('update:filter', {...filter, containsIngredients: $event})"
                    class="col-12 col-sm-8"
                    dense
                    label="Contains ingredients"
                    multiple
                    emit-value
                    map-options
                    use-chips
                  />
                  <q-select
                    :model-value="filter.orderBy"
                    @update:model-value="$emit('update:filter', {...filter, orderBy: $event})"
                    class="col-12 col-sm-4"
                    label="Order by"
                    emit-value
                    map-options
                    round
                    outlined
                    dense
                    :options="orderByOptions"
                  />
                  <div
                    class="col-12 flex justify-center"
                  >
                    <q-btn
                      color="red"
                      label="Reset filters"
                      @click="resetFilter()"
                    />
                  </div>
                </div>
              </q-expansion-item>
            </q-card-section>
          </q-card>
        </div>
        <div class="block">
          <q-input
            :model-value="filter.query"
            @update:model-value="$emit('update:filter', {...filter, query: $event})"
            outlined
            label="Search"
            dense
            bg-color="white"
            @keypress.enter="onSearch"
          >
            <template v-slot:after>
              <q-btn
                text-color="black"
                color="white"
                :icon="mdiMagnify"
                @click="onSearch"
              />
            </template>
          </q-input>
        </div>
      </div>
    </q-card-section>
  </q-card>

</template>

<script>
import { mdiMagnify } from '@quasar/extras/mdi-v5'
import CIngredientSelector from 'components/CIngredientSelector'

export default {
  name: 'CRecipeSearchFilterCard',
  components: { CIngredientSelector },
  props: {
    filter: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      isFilterExpanded: false,
      orderByOptions: [{
        label: 'Name asc',
        value: 'nameAsc'
      }, {
        label: 'Name desc',
        value: 'nameDesc'
      }, {
        label: 'Last update',
        value: 'lastUpdateAsc'
      }, {
        label: 'Least update',
        value: 'lastUpdateDesc'
      }]
    }
  },
  emits: ['clickSearch', 'update:filter'],
  setup () {
    return {
      mdiMagnify: mdiMagnify
    }
  },
  methods: {
    defaultFilter () {
      return {
        query: '',
        automaticallyFabricable: false,
        fabricableWithOwnedIngredients: false,
        containsIngredients: [],
        orderBy: null
      }
    },
    onSearch () {
      this.$emit('clickSearch')
    },
    resetFilter () {
      this.$emit('update:filter', this.defaultFilter())
    }
  }
}
</script>

<style scoped>

</style>
