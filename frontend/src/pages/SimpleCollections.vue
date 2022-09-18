<template>
  <q-page class="page-content" padding>
    <q-page padding>
      <div class="row justify-evenly">
        <div class="col-3 col-md-2 q-ma-lg q-pa-md rounded-borders text-center"
             v-for="i in 20"
             :class="[recipeState()]"
             style="cursor: grabbing"
        >
          Recipe {{ i }}
        </div>
      </div>
    </q-page>
  </q-page>
</template>

<script>
export default {
  name: 'SimpleCollections',
  methods: {
    recipeState () {
      const random = Math.random()
      if (random < 0.3) {
        return 'bg-green'
      } else if (random < 0.6) {
        return 'bg-warning'
      } else {
        return 'bg-grey'
      }
    }
  }
}
</script>

<style scoped>

</style>
