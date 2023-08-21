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
                  <div class="col-12">
                    <q-card
                      class="row items-center justify-start"
                      flat
                      bordered
                    >
                      <div class="col-shrink">
                        <p class="q-px-sm text-grey-7">Fabricable:</p>
                      </div>
                      <div class="col-grow">
                        <div class="row">
                          <q-radio
                            label="Show all"
                            :model-value="filter.fabricable"
                            @update:model-value="$emit('update:filter', {...filter, fabricable: $event})"
                            val=""
                            class="col-12 col-sm-4"
                          />
                          <q-radio
                            label="Fabricable with owned ingredients"
                            :model-value="filter.fabricable"
                            @update:model-value="$emit('update:filter', {...filter, fabricable: $event})"
                            val="manual"
                            class="col-12 col-sm-4"
                          />
                          <q-radio
                            label="Fabricable fully automatic"
                            :model-value="filter.fabricable"
                            @update:model-value="$emit('update:filter', {...filter, fabricable: $event})"
                            val="auto"
                            class="col-12 col-sm-4"
                          />
                        </div>
                      </div>
                    </q-card>
                  </div>
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
                    :options="recipeOrderByOptions"
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
import { recipeOrderOptions } from '../mixins/constants'

export default {
  name: 'CRecipeSearchFilterCard',
  components: { CIngredientSelector },
  mixins: [recipeOrderOptions],
  props: {
    filter: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      isFilterExpanded: false
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
        fabricable: '',
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
