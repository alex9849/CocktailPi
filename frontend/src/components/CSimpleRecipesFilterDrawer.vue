<template>
  <q-drawer
    overlay
    persistent
    behavior="desktop"
    :width="250"
    v-model="leftDrawerOpen"
    class="bg-sv-sidebar text-sv-sidebar shadow-5"
  >
    <div class="absolute" style="top: 25px; right: -17px">
      <q-btn
        dense
        round
        unelevated
        @click="leftDrawerOpen = false"
        color="sv-btn-primary"
        text-color="sv-btn-primary"
        icon="chevron_left"
      />
    </div>
    <h6 class="text-center">{{$t('component.simple_recipes_filter_drawer.headline')}}</h6>
    <q-separator
      :dark="colors.sidebarDark"
      size="1"
    />
    <q-list padding>
      <q-item dark>
        <q-item-section>
          <q-input
            dense
            :dark="colors.sidebarDark"
            outlined
            :label="$t('component.simple_recipes_filter_drawer.name_field_label')"
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
            :dark="colors.sidebarDark"
            multiple
            emit-value
            map-options
            use-chips

            hide-bottom-space
            :label="$t('component.simple_recipes_filter_drawer.contains_ingredients_field_label')"
            :model-value="filter.containsIngredients"
            @update:model-value="$emit('update:filter', {...filter, containsIngredients: $event})"
          />
        </q-item-section>
      </q-item>
      <q-item>
        <q-card
          flat
          :dark="colors.sidebarDark"
          bordered
          style="border-color: #c0c0c0"
          class="bg-transparent"
        >
          <p class="q-px-sm">{{ $t('component.simple_recipes_filter_drawer.fabricable_box.headline') }}</p>
          <q-separator
            :dark="colors.sidebarDark"
          />
          <q-list
            :dark="colors.sidebarDark"
          >
            <q-item
              :key="option.val"
              v-for="option in fabricable_options"
              tag="label"
              class="q-px-xs"
              :dark="colors.sidebarDark"
              v-ripple
            >
              <q-item-section side top>
                <q-radio
                  keep-color
                  :model-value="filter.fabricable"
                  @update:model-value="$emit('update:filter', {...filter, fabricable: $event})"
                  :val="option.val"
                ></q-radio>
              </q-item-section>

              <q-item-section>
                <q-item-label>{{ option.label }}</q-item-label>
                <q-item-label caption>
                  {{ option.desc }}
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
            :dark="colors.sidebarDark"
            emit-value
            map-options
            :label="$t('component.simple_recipes_filter_drawer.order_by_selector_label')"
            :options="recipeOrderByOptions"
            :model-value="filter.orderBy"
            @update:model-value="$emit('update:filter', {...filter, orderBy: $event})"
          />
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-btn
            :label="$t('component.simple_recipes_filter_drawer.search_btn_label')"
            color="info"
            :icon="mdiMagnify"
            @click="onSearch"
          />
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-btn
            :label="$t('component.simple_recipes_filter_drawer.reset_btn_label')"
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
        color="sv-btn-primary"
        text-color="sv-btn-primary"
        :label="$t('component.simple_recipes_filter_drawer.open_btn_label')"
        icon="chevron_right"
      />
    </transition>
  </div>
</template>

<script>
import { mdiMagnify } from '@quasar/extras/mdi-v5'
import { recipeOrderOptions } from 'src/mixins/constants'
import CIngredientSelector from 'components/CIngredientSelector'
import { mapGetters } from 'vuex'

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
  data () {
    return {
      leftDrawerOpen: false,
      fabricable_options: [{
        val: '',
        label: this.$t('component.simple_recipes_filter_drawer.fabricable_box.show_all'),
        desc: this.$t('component.simple_recipes_filter_drawer.fabricable_box.show_all_desc')
      }, {
        val: 'manual',
        label: this.$t('component.simple_recipes_filter_drawer.fabricable_box.fabricable_owned'),
        desc: this.$t('component.simple_recipes_filter_drawer.fabricable_box.fabricable_owned_desc')
      }, {
        val: 'auto',
        label: this.$t('component.simple_recipes_filter_drawer.fabricable_box.fabricable_auto'),
        desc: this.$t('component.simple_recipes_filter_drawer.fabricable_box.fabricable_auto_desc')
      }]
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
  },
  computed: {
    ...mapGetters({
      colors: 'appearance/getSvColors'
    })
  }
}
</script>

<style scoped>
</style>
