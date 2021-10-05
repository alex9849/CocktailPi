<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el :to="{name: 'mycollections'}" label="My collections"/>
      <q-breadcrumbs-el label="Collection name"/>
    </q-breadcrumbs>
  </q-page>
</template>

<script>
export default {
  name: "Collection"
}
</script>

<style scoped>

</style>
