<template>
  <q-card flat
          bordered
          :class="cardClass"
  >
    <q-card-section
      horizontal
      v-if="unassignedIngredients.length !== 0"
    >
      <div class="flex items-center col-auto"
           v-if="!$q.platform.is.mobile"
      >
        <q-icon :name="mdiAlertOutline"
                size="xl"
                class="text-orange-14 q-pa-xs"
        ></q-icon>
      </div>
      <q-separator vertical
                   v-if="!$q.platform.is.mobile"
      />
      <div class="col">
        <div class="q-pa-sm">
          The following ingredients have to get added manually or are not assigned to pumps. You will be asked to
          add them during the production progress:
        </div>
        <q-separator></q-separator>
        <ul style="text-align: start">
          <li v-for="ingredient in unassignedIngredients" :key="ingredient.id">
            {{ ingredient.name }}
            <q-chip :color="ingredient.inBar? 'green-4' : 'red-4'"
                    dense
                    square
                    :ripple="false"
            >
              <div v-if="ingredient.inBar">in bar</div>
              <div v-else>not in bar</div>
            </q-chip>
          </li>
        </ul>
      </div>
    </q-card-section>
    <q-card-section
      horizontal
      v-else
    >
      <div class="flex items-center col-auto bg-light-green-14"
      >
        <q-icon :name="mdiCheck"
                size="xl"
                class="text-white q-pa-xs"
        ></q-icon>
      </div>
      <q-separator vertical
      />
      <div class="col flex justify-center items-center">
        <p class="q-pa-sm" style="text-wrap: normal">All ingredients assigned to pumps! Cocktail can be produced fully automatic!</p>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import { mdiAlertOutline, mdiCheckOutline, mdiCheck } from '@quasar/extras/mdi-v5'

export default {
  name: 'CMakeCocktailDialogIngredientsToAddManually',
  props: {
    unassignedIngredients: {
      type: Array,
      reqiured: true
    }
  },
  setup () {
    return {
      mdiAlertOutline: mdiAlertOutline,
      mdiCheckOutline: mdiCheckOutline,
      mdiCheck: mdiCheck
    }
  },
  computed: {
    cardClass () {
      return {
        'bg-warning': this.unassignedIngredients.length !== 0,
        'bg-info': this.unassignedIngredients.length === 0
      }
    }
  }
}
</script>

<style scoped>

</style>
