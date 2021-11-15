<template>
  <q-card flat bordered class="bg-grey-1">
    <q-card-section horizontal>
      <q-card-section class="col-4 q-pa-sm">
        <q-img :src="$store.getters['auth/getFormattedServerAddress'] + '/api/collection/' + value.id + '/image?timestamp=' + value.lastUpdate.getMilliseconds()"
               placeholder-src="../assets/cocktail-solid.png"
               :ratio="1"
               class="rounded-borders"
        />
      </q-card-section>
      <q-card-section class="col-8 q-pa-sm">
        <div class="text-h5" style="margin-bottom: 0">{{ value.name }}</div>
        <div class="text-caption text-grey">
          {{ value.size }} Cocktail(s) / {{ value.completed? 'complete' : 'incomplete'}}
        </div>
        <div class="text-caption text-grey dotted-overflow" style="">
          {{ value.description }}
        </div>
      </q-card-section>
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: 'CCollectionCard',
  props: {
    value: {
      type: Object,
      required: true
    }
  }
}
</script>

<style scoped>
.dotted-overflow {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
