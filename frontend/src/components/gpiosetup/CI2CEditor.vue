<template>
  <q-card>
    <q-card-section>
      <q-toggle
        v-model:model-value="config.enable"
        label="Enable I2C"
      />
      <q-input
        v-model:model-value="config.sdaPin"
        outlined
        type="number"
        filled
        label="SCL-BCM-Pin"
      />
      <q-input
        v-model:model-value="config.sclPin"
        outlined
        type="number"
        filled
        label="SCL-BCM-Pin"
      />
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: 'CI2CEditor',
  data: () => {
    return {
      config: {
        enable: false,
        sdaPin: '',
        sclPin: ''
      }
    }
  }
}
</script>

<style scoped>

</style>
