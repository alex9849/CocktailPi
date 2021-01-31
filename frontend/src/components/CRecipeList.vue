<template>
  <div>
    <slot name="top" />
    <q-table
      :data="recipes"
      :columns="columns"
      row-key="name"
      :loading="loading"
      :visible-columns="['name']"
      :pagination="{rowsPerPage: 0}"
      hide-pagination
      hide-header
      selection="multiple"
      :selected.sync="selected"
      no-data-label="No cocktails found :("
      :style="'background-color: ' + listBodyColor"
    >
      <template
        v-slot:loading
      >
        <q-inner-loading
          showing
          color="info"
        />
      </template>
      <template v-slot:body="props">
        <router-link
          :to="{name: 'recipedetails', params: {id: props.row.id}}"
          class=""
        >
          <c-recipe-card
            style="margin: 10px"
            :recipe="props.row"
            show-ingredients
            :background-color="(props.rowIndex % 2 === 0)? listItem1Color : listItem2Color"
          >
            <template slot="beforePicture">
              <q-checkbox
                v-if="selectable"
                v-model="props.selected"
                keep-color
              />
            </template>
            <template slot="topRight">
              <q-icon
                v-if="doPumpsHaveAllIngredients(props.row)"
                :name="mdiCheckBold"
                size="md"
                color="positive">
                <q-tooltip>
                  Cocktail can be ordered!
                </q-tooltip>
              </q-icon>
              <q-icon
                v-else-if="props.row.ingredientsInBar && areEnoughPumpsAvailable(props.row)"
                :name="mdiAlert"
                size="md"
                color="warning">
                <q-tooltip>
                  Ingredients in bar, but no pumps assigned!
                </q-tooltip>
              </q-icon>
              <q-icon
                v-else
                :name="mdiClose"
                size="md"
                color="negative">
                <q-tooltip>
                  Missing ingredients or not enough pumps!
                </q-tooltip>
              </q-icon>
            </template>
          </c-recipe-card>
        </router-link>
      </template>
    </q-table>
  </div>
</template>

<script>
  import CRecipeCard from "./CRecipeCard";
  import {mdiAlert, mdiCheckBold, mdiClose} from "@quasar/extras/mdi-v5";
  import {mapGetters} from "vuex";

  export default {
    name: "CRecipeList",
    components: {CRecipeCard},
    props: {
      recipes: {
        type: Array,
        required: true
      },
      selectable: {
        type: Boolean,
        default: false
      },
      loading: {
        type: Boolean,
        default: false
      },
      listBodyColor: {
        type: String,
        default: '#f3f3fa'
      },
      listItem1Color: {
        type: String,
        default: '#f3f3fa'
      },
      listItem2Color: {
        type: String,
        default: '#fafafa'
      }
    },
    data() {
      return {
        selected: [],
        columns: [
          {
            name: 'name',
            label: 'Name',
            align: 'left',
            field: row => row.name,
            format: val => `${val}`,
            sortable: true
          },
          { name: 'shortDescription', align: 'center', label: 'Short description', field: 'shortDescription', sortable: true },
          { name: 'owner', label: 'Owner', field: 'owner.username', sortable: true }
        ]
      }
    },
    created() {
      this.mdiCheckBold = mdiCheckBold;
      this.mdiAlert = mdiAlert;
      this.mdiClose = mdiClose;
    },
    computed: {
      ...mapGetters({
        doPumpsHaveAllIngredients: 'pumpLayout/doPumpsHaveAllIngredientsForRecipe',
        areEnoughPumpsAvailable: 'pumpLayout/areEnoughPumpsAvailable'
      })
    },
    watch: {
      selected(newValue) {
        this.$emit('selectionChange', newValue);
      },
      recipes() {
        this.selected = [];
      }
    }
  }
</script>

<style scoped>
  a {
    text-decoration: none;
    color: inherit;
  }
</style>
