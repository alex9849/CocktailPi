<template>
  <q-card flat
          bordered
          class="bg-warning"
          v-if="unassignedIngredients.length !== 0"
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
  </q-card>
</template>

<script>
import { mdiAlertOutline } from '@quasar/extras/mdi-v5'

export default {
  name: 'CMakeCocktailDialogMissingIngredients',
  props: {
    unassignedIngredients: {
      type: Array,
      reqiured: true
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
