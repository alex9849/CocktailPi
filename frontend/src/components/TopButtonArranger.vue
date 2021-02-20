<template>
  <div
    class="row"
    v-bind:class="{ 'justify-center': $q.platform.is.mobile, 'justify-end': !$q.platform.is.mobile }"
  >
    <div
      class="q-gutter-sm" style="display: contents; max-width: max-content"
    >
      <slot/>
    </div>
  </div>
</template>

<script>
export default {
  name: "TopButtonArranger"
}
</script>

<style scoped>

</style>
