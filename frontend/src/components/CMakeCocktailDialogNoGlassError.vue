<template>
  <c-q-headlined-card
    :headline="$t('component.make_cocktail_no_glass.not_fulfilled_msg')"
    card-class="bg-warning"
    :icon="mdiClose"
    icon-background-class="bg-warning"
    icon-class="text-negative"
  />
</template>

<script>
import { defineComponent } from 'vue'
import CQHeadlinedCard from 'components/CQHeadlinedCard.vue'
import { mdiClose } from '@quasar/extras/mdi-v5'

export default defineComponent({
  name: 'CMakeCocktailDialogNoGlassError',
  components: { CQHeadlinedCard },
  created () {
    this.mdiClose = mdiClose
  }
})
</script>

<style scoped>

</style>
