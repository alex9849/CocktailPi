<template>
  <q-page class="page-content" padding>
    <h5>I2C</h5>
    <q-card
      flat
      bordered
    >
      <q-card-section>
        <q-toggle
          v-model:model-value="config.enable"
          label="Enable I2C"
          color="positive"
        />
        <c-assistant-container>
          <template v-slot:explanations></template>
          <template v-slot:fields>
            <q-input
              v-model:model-value="config.sdaPin"
              outlined
              type="number"
              filled
              label="SCL BCM-Pin"
            />
            <q-input
              v-model:model-value="config.sclPin"
              outlined
              type="number"
              filled
              label="SCL BCM-Pin"
            />
          </template>
        </c-assistant-container>
      </q-card-section>
    </q-card>
  </q-page>

</template>

<script>
import CAssistantContainer from 'components/CAssistantContainer.vue'

export default {
  name: 'I2CManagement',
  components: { CAssistantContainer },
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
