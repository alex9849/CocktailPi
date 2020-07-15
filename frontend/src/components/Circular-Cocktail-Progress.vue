<template>
    <q-circular-progress
      show-value
      style="margin: 5px"
      class="text-white"
      :value="60"
      :thickness="0.13"
      color="positive"
      center-color="info"
      track-color="transparent"
      size="lg"
    >
      <q-icon :name="mdiTimerSandEmpty" size="20px"/>
    </q-circular-progress>
</template>

<script>
  import {mdiTimerSandEmpty} from "@quasar/extras/mdi-v5";

  export default {
        name: "Circular-Cocktail-Progress",
      created() {
          this.mdiTimerSandEmpty = mdiTimerSandEmpty;
      }
    }
</script>

<style scoped>

</style>
