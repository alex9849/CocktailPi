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
        <q-card
          @click="$router.push({name: 'recipedetails', params: {id: props.row.id}})"
          :style="'cursor: pointer; margin: 10px; background-color: ' + ((props.rowIndex % 2 === 0)? listItem1Color : listItem2Color)"
        >
          <q-card-section
            style="padding: 10px"
          >
            <div
              class="row"
              style="position: relative"
            >
              <q-checkbox
                v-if="selectable"
                v-model="props.selected"
                keep-color
                style="position: absolute; z-index: 1;"
              />
              <q-img
                :src="'/api/recipe/' + props.row.id + '/image?nocache=' + new Date().getTime()"
                placeholder-src="../assets/cocktail-solid.png"
                :ratio="16/9"
                class="col rounded-borders"
                style="max-width: 225px; max-height: 180px"
              />
              <div class="col" style="padding-left: 10px; position: relative">
                <h5
                  style="margin: 0; padding-bottom: 10px;"
                >
                  <b>{{ props.row.name}}</b>
                </h5>
                <div>
                  {{ props.row.shortDescription }}
                </div>

                <div class="row" style="position: absolute; bottom: 0; left: 0; right: 0; padding-inline: 10px">
                  <div class="col" style="overflow: hidden; max-height: 36px">
                    Ingredients:
                    <q-chip v-if="index < 4" v-for="(name, index) in uniqueIngredientNames(props.row.recipeIngredients)">
                      {{ index !== 3?name:'...' }}
                    </q-chip>
                  </div>
                  <div class="col" style="text-align: right; max-width: max-content">
                    by {{ props.row.owner.username }}
                  </div>
                </div>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </template>
    </q-table>
  </div>
</template>

<script>
  export default {
    name: "CRecipeList",
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
    methods: {
      uniqueIngredientNames(productionSteps) {
        let unique = new Set();
        for(let productionStep of productionSteps) {
          for(let ingredient of productionStep) {
            unique.add(ingredient.ingredient.name);
          }
        }
        return Array.from(unique.values());
      }
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
</style>
