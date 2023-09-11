<template>
  <div
    class="row q-gutter-sm"
    v-bind:class="{ 'justify-center': centerButtons, 'justify-end': !centerButtons }"
  >
    <slot/>
  </div>
</template>

<script>

export default {
  name: 'TopButtonArranger',
  created () {
    window.addEventListener('resize', this.handleResize)
    this.handleResize()
  },
  data: () => {
    return {
      centerButtons: false
    }
  },
  methods: {
    handleResize () {
      this.centerButtons = window.innerWidth < 500
    }
  }
}
</script>

<style scoped>

</style>
