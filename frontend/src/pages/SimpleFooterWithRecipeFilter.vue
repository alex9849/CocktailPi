<template>
  <simple-footer>
    <template v-slot:top>
      <transition-group
        appear
        enter-active-class="animated fadeInUp"
        leave-active-class="animated fadeOutDown"
      >
        <div class="row bg-red">hi</div>
      </transition-group>
    </template>
  </simple-footer>
</template>

<script>
import SimpleFooter from 'pages/SimpleFooter'
export default {
  name: 'SimpleFooterWithRecipeFilter',
  components: { SimpleFooter }
}
</script>

<style scoped>

</style>
