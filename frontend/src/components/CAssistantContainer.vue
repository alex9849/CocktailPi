<template>
  <div class="row justify-center items-center reverse q-col-gutter-md">
    <div class="col-12 col-lg-6">
      <div class="q-pa-md">
        <slot name="explanations" />
      </div>
    </div>
    <div class="col-12 col-sm-10 col-md-7 col-lg-6 q-gutter-md">
      <slot name="fields"/>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CAssistantContainer',
  data () {
    return {
      showHints: true
    }
  }
}
</script>

<style scoped>

</style>
