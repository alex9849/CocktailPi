<template>
  <div>
    <slot name="top"/>
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
      :flat="flat"
      no-data-label="No cocktails found :("
      :style="'background-color: ' + listBodyColor"
      :table-style="{margin: '0px'}"
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
              <c-recipe-fabricable-icon
                :recipe="props.row"
              />
            </template>
          </c-recipe-card>
        </router-link>
      </template>
    </q-table>
  </div>
</template>

<script>
import CRecipeCard from "./CRecipeCard";
import CRecipeFabricableIcon from "components/CRecipeFabricableIcon";

export default {
  name: "CRecipeList",
  components: {CRecipeFabricableIcon, CRecipeCard},
  props: {
    recipes: {
      type: Array,
      required: true
    },
    flat: {
      type: Boolean,
      default: false
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
        {
          name: 'shortDescription',
          align: 'center',
          label: 'Short description',
          field: 'shortDescription',
          sortable: true
        },
        {name: 'owner', label: 'Owner', field: 'owner.username', sortable: true}
      ]
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

<style >
a {
  text-decoration: none;
  color: inherit;
}
.q-table__bottom--nodata {
  border: 0;
}
</style>
