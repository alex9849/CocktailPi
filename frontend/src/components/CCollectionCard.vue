<template>
  <q-card flat bordered class="bg-grey-1">
    <q-card-section horizontal>
      <q-card-section class="col-4 q-pa-sm">
        <q-img v-if="modelValue.hasImage"
               :src="this.$store.getters['auth/getFormattedServerAddress']
                  + '/api/collection/' + this.modelValue.id + '/image?timestamp='
                  + this.modelValue.lastUpdate.getTime()"
               placeholder-src="~assets/cocktail-solid.png"
               :ratio="1"
               class="rounded-borders"
        />
        <q-img v-else
               :ratio="1"
               class="rounded-borders"
               placeholder-src="~assets/cocktail-solid.png"
               src="~assets/cocktail-solid.png"
        />
      </q-card-section>
      <q-card-section class="col-8 q-pa-sm flex column">
        <div class="text-h5" style="margin-bottom: 0">{{ $t('component.simple_collection_card.name', { name: modelValue.name }) }}</div>
        <div class="text-caption text-grey">
          {{ $t('component.simple_collection_card.nr_cocktails', { nr: modelValue.size }) }}
        </div>
        <div class="text-caption text-grey dotted-overflow" style="">
          {{ $t('component.simple_collection_card.desc', { desc: modelValue.description }) }}
        </div>
        <div style="flex-grow: 1" />
        <div class="row justify-end">
          <p class="text-caption text-grey">
            {{ $t('component.simple_collection_card.owner', { owner: modelValue.ownerName }) }}
          </p>
        </div>
      </q-card-section>
    </q-card-section>
  </q-card>
</template>

<script>

export default {
  name: 'CCollectionCard',
  props: {
    modelValue: {
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
