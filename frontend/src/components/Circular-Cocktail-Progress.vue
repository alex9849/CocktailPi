<template>
  <div style="display: inline">
    <q-circular-progress
      show-value
      style="margin: 5px; position: marker"
      class="text-white cursor-pointer actionable"
      :value="60"
      :thickness="0.13"
      color="positive"
      center-color="info"
      track-color="transparent"
      size="lg"
      @click="showDialog = !showDialog"
    >
      <q-icon :name="mdiTimerSandEmpty" size="20px"/>
    </q-circular-progress>
    <q-dialog
      v-model="showDialog"
    >
      <q-card q-card-section class="text-center" style="width: 500px">
        <q-card-section
          style="padding-bottom: 0"
        >
          <h5>Current prepared cocktail</h5>
          <q-splitter
            horizontal
            :value="10"
            style="padding-bottom: 10px"
          />
          <div v-if="hasCurrenlyPreparedCocktail">

          </div>
          <div v-else>
            <p>Currently no cocktail gets prepared!</p>
            <p>Go to "My recipes" or "Public recipes" to put one in order.</p>
          </div>
        </q-card-section>
        <q-card-actions
          style="padding-top: 0; padding-bottom: 16px"
          align="around"
        >
          <q-btn
            color="grey"
            @click="showDialog = false"
            style="width: 100px"
          >
            Close
          </q-btn>
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script>
  import {mdiTimerSandEmpty} from "@quasar/extras/mdi-v5";

  export default {
    name: "Circular-Cocktail-Progress",
    data() {
      return {
        showDialog: false
      }
    },
    created() {
      this.mdiTimerSandEmpty = mdiTimerSandEmpty;
    },
    computed: {
      hasCurrenlyPreparedCocktail() {
        return false;
      }
    }
  }
</script>

<style scoped>
  .actionable {
    border-radius: 4px;
  }
  .actionable:hover {
    box-shadow: inset 0 0 100px 100px rgba(255, 255, 255, 0.1);
  }
</style>
