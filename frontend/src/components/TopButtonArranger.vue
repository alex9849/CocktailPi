<template>
  <div
    class="row q-gutter-sm"
    v-bind:class="{ 'justify-center': $q.platform.is.mobile, 'justify-end': !$q.platform.is.mobile }"
  >
    <slot/>
  </div>
</template>

<script>
export default {
  name: 'TopButtonArranger'
}
</script>

<style scoped>

</style>
