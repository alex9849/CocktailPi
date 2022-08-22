<template>
  <router-view/>
</template>

<style scoped>
</style>

<script>

import { store } from '../store'

export default {
  name: 'LoggedInLayout',
  beforeRouteEnter (to, from, next) {
    Promise.all([
      store.dispatch('category/fetchCategories'),
      store.dispatch('auth/fetchCurrentUser'),
      store.dispatch('globalSettings/fetchGlobalSettings')
    ]).then(() => next())
      .catch(() => {
        store.dispatch('auth/logout')
          .finally(() => next({
            name: 'login',
            query: {
              redirectTo: to.fullPath
            }
          }))
      })
  }
}
</script>
