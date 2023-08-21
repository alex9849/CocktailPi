<template>
  <q-drawer
    overlay
    persistent
    behavior="desktop"
    :width="250"
    v-model="leftDrawerOpen"
    class="bg-grey-8 shadow-5"
  >
    <div class="absolute" style="top: 25px; right: -17px">
      <q-btn
        dense
        round
        unelevated
        @click="leftDrawerOpen = false"
        color="accent"
        icon="chevron_left"
      />
    </div>
    <h6 class="text-center text-white">Filter</h6>
    <q-separator size="1" color="darkgrey" />
    <q-list padding>
      <q-item dark>
        <q-item-section>
          <q-input
            dense
            dark
            outlined
            label="Name"
            :model-value="filter.query"
            @update:model-value="$emit('update:filter', {...filter, query: $event})"
            @keypress.enter="onSearch"
          />
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <c-ingredient-selector
            outlined
            dense
            dark
            multiple
            emit-value
            map-options
            use-chips
            hide-bottom-space
            label="Contains ingredients"
            :model-value="filter.containsIngredients"
            @update:model-value="$emit('update:filter', {...filter, containsIngredients: $event})"
          />
        </q-item-section>
      </q-item>
      <q-item>
        <q-card
          flat
          bordered
          style="border-color: #c0c0c0"
          class="bg-transparent"
        >
          <p class="text-grey-4 q-px-sm">Fabricable</p>
          <q-separator dark />
          <q-list>
            <q-item
              tag="label"
              class="q-px-xs"
              dark
              v-ripple
            >
              <q-item-section side top>
                <q-radio
                  color="white"
                  keep-color
                  :model-value="filter.fabricable"
                  @update:model-value="$emit('update:filter', {...filter, fabricable: $event})"
                  val=""
                ></q-radio>
              </q-item-section>

              <q-item-section>
                <q-item-label>All</q-item-label>
                <q-item-label caption>
                  Shows all recips
                </q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              tag="label"
              class="q-px-xs"
              dark
              v-ripple
            >
              <q-item-section side top>
                <q-radio
                  color="white"
                  keep-color
                  :model-value="filter.fabricable"
                  @update:model-value="$emit('update:filter', {...filter, fabricable: $event})"
                  val="manual"
                />
              </q-item-section>

              <q-item-section>
                <q-item-label>Fabricable</q-item-label>
                <q-item-label caption>
                  Only shows recipes that can be produced with owned ingredients
                </q-item-label>
              </q-item-section>
            </q-item>
            <q-item
              tag="label"
              class="q-px-xs"
              dark
              v-ripple
            >
              <q-item-section side top>
                <q-radio
                  color="white"
                  keep-color
                  :model-value="filter.fabricable"
                  @update:model-value="$emit('update:filter', {...filter, fabricable: $event})"
                  val="auto"
                />
              </q-item-section>

              <q-item-section>
                <q-item-label>Automatically fabricable</q-item-label>
                <q-item-label caption>
                  Only shows recipes that can be produced fully automatic
                </q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-card>
      </q-item>
      <q-item>
        <q-item-section>
          <q-select
            outlined
            dense
            dark
            emit-value
            map-options
            label="Order by"
            :options="recipeOrderByOptions"
            :model-value="filter.orderBy"
            @update:model-value="$emit('update:filter', {...filter, orderBy: $event})"
          />
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-btn
            label="Search"
            color="info"
            :icon="mdiMagnify"
            @click="onSearch"
          />
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-btn
            label="Reset"
            color="red"
            @click="resetFilter"
          />
        </q-item-section>
      </q-item>
    </q-list>
  </q-drawer>
  <div class="fixed" style="top: 75px; left: 0; z-index: 1000">
    <transition
      appear
      enter-active-class="animated fadeInLeft"
      leave-active-class="animated fadeOutRight"
    >
      <q-btn
        dense
        no-caps
        unelevated
        v-if="!leftDrawerOpen"
        @click="leftDrawerOpen = true"
        color="accent"
        label="Filter"
        icon="chevron_right"
      />
    </transition>
  </div>
</template>

<script>
import { mdiMagnify } from '@quasar/extras/mdi-v5'
import { recipeOrderOptions } from 'src/mixins/constants'
import CIngredientSelector from 'components/CIngredientSelector'

export default {
  name: 'CSimpleRecipesFilterDrawer',
  components: { CIngredientSelector },
  mixins: [recipeOrderOptions],
  props: {
    filter: {
      type: Object,
      required: true
    }
  },
  emits: ['clickSearch', 'update:filter'],
  setup () {
    return {
      mdiMagnify: mdiMagnify
    }
  },
  data: () => {
    return {
      leftDrawerOpen: false
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
      this.leftDrawerOpen = false
    },
    resetFilter () {
      this.$emit('update:filter', this.defaultFilter())
    }
  }
}
</script>

<style scoped>
</style>
