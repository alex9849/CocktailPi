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
                    v-model="value.fabricableWithOwnedIngredients"
                    class="col-12 col-sm-6"
                  >
                    Fabricable with owned ingredients
                  </q-checkbox>
                  <q-checkbox
                    v-model="value.automaticallyFabricable"
                    class="col-12 col-sm-6"
                  >
                    Can be fabricated fully automatic
                  </q-checkbox>
                  <c-ingredient-selector
                    v-model="value.containsIngredients"
                    class="col-12 col-sm-8"
                    dense
                    label="Contains ingredients"
                    multiple
                    emit-value
                    map-options
                    use-chips
                  />
                  <q-select
                    v-model="value.orderBy"
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
            v-model="value.query"
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
    value: {
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
  emits: ['clickSearch', 'input'],
  created () {
    this.mdiMagnify = mdiMagnify
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
      this.$emit('input', this.defaultFilter())
    }
  }
}
</script>

<style scoped>

</style>
