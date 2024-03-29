<template>
  <q-card
    class="bg-card-body text-card-body"
  >
    <q-card-section>
      <div class="q-gutter-md">
        <div>
          <b>{{ $t('component.recipe_search_filter_card.headline') }}</b>
        </div>
        <div>
          <q-card
            flat
            bordered
            class="bg-card-header text-card-header"
          >
            <q-card-section class="q-pa-none">
              <q-expansion-item
                :label="$t('component.recipe_search_filter_card.expert_search_label')"
                v-model="isFilterExpanded"
                :header-class="{'bg-card-header': isFilterExpanded, 'text-card-header': isFilterExpanded,
                 'bg-card-item-group': !isFilterExpanded, 'text-card-item-group': !isFilterExpanded}"
                class="bg-card-item-group text-card-item-group"
              >
                <div
                  class="row justify-evenly q-col-gutter-sm q-pa-sm"
                >
                  <div class="col-12">
                    <q-card
                      class="row items-center bg-transparent justify-start"
                      flat
                      bordered
                    >
                      <div class="col-shrink">
                        <p class="q-px-sm" :style="{'color': fabricableLabelColor}">
                          {{ $t('component.recipe_search_filter_card.fabricable_box.headline') }}
                        </p>
                      </div>
                      <div class="col-grow">
                        <div class="row">
                          <q-radio
                            :dark="color.cardItemGroupDark"
                            v-for="option in fabricable_options"
                            :key="option.val"
                            :label="option.label"
                            :model-value="filter.fabricable"
                            @update:model-value="$emit('update:filter', {...filter, fabricable: $event})"
                            :val="option.val"
                            class="col-12 col-sm-4"
                          />
                        </div>
                      </div>
                    </q-card>
                  </div>
                  <c-ingredient-selector
                    :dark="color.cardItemGroupDark"
                    :model-value="filter.containsIngredients"
                    @update:model-value="$emit('update:filter', {...filter, containsIngredients: $event})"
                    class="col-12 col-sm-8"
                    dense
                    :label="$t('component.recipe_search_filter_card.contains_ingredient_field_label')"
                    multiple
                    emit-value
                    map-options
                    use-chips
                  />
                  <q-select
                    :dark="color.cardItemGroupDark"
                    :model-value="filter.orderBy"
                    @update:model-value="$emit('update:filter', {...filter, orderBy: $event})"
                    class="col-12 col-sm-4"
                    :label="$t('component.recipe_search_filter_card.order_by_selector_label')"
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
                      :label="$t('component.recipe_search_filter_card.reset_btn_label')"
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
            :dark="color.cardItemGroupDark"
            :model-value="filter.query"
            @update:model-value="$emit('update:filter', {...filter, query: $event})"
            outlined
            :label="$t('component.recipe_search_filter_card.cocktail_name_field_label')"
            dense
            bg-color="card-item-group"
            @keypress.enter="onSearch"
          >
            <template v-slot:after>
              <q-btn
                text-color="card-item-group"
                color="card-item-group"
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
import { mapGetters } from 'vuex'
import { complementColor } from 'src/mixins/utils'

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
      isFilterExpanded: false,
      fabricable_options: [{
        val: '',
        label: this.$t('component.recipe_search_filter_card.fabricable_box.show_all')
      }, {
        val: 'manual',
        label: this.$t('component.recipe_search_filter_card.fabricable_box.fabricable_owned')
      }, {
        val: 'auto',
        label: this.$t('component.recipe_search_filter_card.fabricable_box.fabricable_auto')
      }]
    }
  },
  emits: ['clickSearch', 'update:filter'],
  setup () {
    return {
      mdiMagnify
    }
  },
  computed: {
    fabricableLabelColor () {
      return complementColor(this.color.cardItemGroup, 70)
    },
    ...mapGetters({
      color: 'appearance/getNormalColors'
    })
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
