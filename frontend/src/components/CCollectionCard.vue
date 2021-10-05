<template>
  <q-card flat bordered>
    <q-card-section horizontal>
      <q-card-section class="col-auto">
        <q-avatar size="8rem">
          <img src="https://cdn.quasar.dev/img/parallax2.jpg"
          />
        </q-avatar>
      </q-card-section>
      <q-card-section class="q-pt-xs">
        <div class="text-h5 q-mt-sm q-mb-xs" style="margin-bottom: 0">{{ value.name }}</div>
        <div class="text-caption text-grey">
          {{ value.size }} Cocktail(s) / {{ value.complete? 'complete' : 'incomplete'}}
        </div>
        <div class="text-caption text-grey">
          {{ value.description }}
        </div>
      </q-card-section>
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: "CCollectionCard",
  props: {
    value: {
      type: Object,
      required: true
    }
  }
}
</script>

<style scoped>

</style>
