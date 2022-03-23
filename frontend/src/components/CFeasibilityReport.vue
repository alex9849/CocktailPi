<template>
  <q-card flat
          bordered
          class="bg-red-5"
  >
    <q-card-section horizontal>
      <div class="flex items-center col-auto"
           v-if="!$q.platform.is.mobile"
      >
        <q-icon :name="mdiAlertOutline"
                size="lg"
                class="text-orange-14 q-pa-sm"
        ></q-icon>
      </div>
      <q-separator vertical
                   v-if="!$q.platform.is.mobile"
      />
      <div class="col">
        <div class="q-pa-sm">
          Can't make cocktail! Some pumps don't have enough liquid left:
        </div>
        <q-separator></q-separator>
        <ul style="text-align: start">
          <li v-for="insufficientIngredient in feasibilityReport.insufficientIngredients" :key="insufficientIngredient.ingredient.id">
            {{ insufficientIngredient.ingredient.name }}:
            <strong>{{ insufficientIngredient.amountNeeded }} ml</strong> required
          </li>
        </ul>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import { mdiAlertOutline } from '@quasar/extras/mdi-v5'

export default {
  name: 'CFeasibilityReport',
  props: {
    feasibilityReport: {
      type: Object,
      required: true
    }
  },
  setup () {
    return {
      mdiAlertOutline: mdiAlertOutline
    }
  }
}
</script>

<style scoped>

</style>
