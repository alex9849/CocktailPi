<template>
  <q-page class="page-content" padding>
    <h5>I2C configuration</h5>
    <q-card
      flat
      bordered
    >
      <q-card-section>
        <q-toggle
          v-model:model-value="v.config.enable.$model"
          label="Enable I2C"
          color="positive"
        />
        <q-separator
          v-if="config.enable"
          horizontal
          class="q-mb-md"
          :value="10"
        />
        <c-assistant-container
          v-if="config.enable"
        >
          <template v-slot:explanations>
            When enabling I2C two GPIO-pins on the local board get used up for the SDA and SCL pin of the I2C-bus.
            Some boards provide more then one I2C bus. The cocktailmaker software only supports one I2C bus a the time.<br>
            This supported bus is the device at <q-badge outline class="text-black">/sys/bus/i2c/devices/i2c-1</q-badge>
            on the underlying linux filesystem.<br>
            On normal Raspberry PIs the pins used for SDL and SCL are on normally <b>2 for SDA</b> and <b>3 for SDL</b>.<br>
            <b>The fields for the SDL and SCL pin don't influence the selected bus.</b> They only decide on which pins get
            blocked from being assigned to a pump or different connected hardware within the cocktailmaker software.
          </template>
          <template v-slot:fields>
            <q-input
              v-model:model-value="v.config.sdaPin.$model"
              :error-message="v.config.sdaPin.$errors[0]?.$message"
              :error="v.config.sdaPin.$errors.length > 0"
              outlined
              hide-bottom-space
              type="number"
              filled
              label="SDA BCM-Pin"
            />
            <q-input
              v-model:model-value="v.config.sclPin.$model"
              :error-message="v.config.sclPin.$errors[0]?.$message"
              :error="v.config.sclPin.$errors.length > 0"
              outlined
              hide-bottom-space
              type="number"
              filled
              label="SCL BCM-Pin"
            />
          </template>
        </c-assistant-container>
      </q-card-section>
      <q-card-actions class="q-ma-sm">
        <q-btn
          style="width: 100px"
          color="positive"
          label="Save"
          :disable="v.config.$invalid"
          @click="$router.push({name: 'gpiomanagement'})"
        />
        <q-btn
          style="width: 100px"
          color="negative"
          label="Abort"
          @click="$router.push({name: 'gpiomanagement'})"
        />
      </q-card-actions>
    </q-card>
  </q-page>

</template>

<script>
import CAssistantContainer from 'components/CAssistantContainer.vue'
import { required, requiredIf } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'I2CManagement',
  components: { CAssistantContainer },
  data: () => {
    return {
      config: {
        enable: false,
        sdaPin: null,
        sclPin: null
      }
    }
  },
  setup () {
    return {
      v: useVuelidate()
    }
  },
  validations () {
    return {
      config: {
        enable: {
          required
        },
        sdaPin: {
          required: requiredIf(() => this.config.enable)
        },
        sclPin: {
          required: requiredIf(() => this.config.enable)
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
