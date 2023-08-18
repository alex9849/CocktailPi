<template>
  <q-page class="page-content" padding>
    <h5>I2C configuration</h5>
    <q-card
      flat
      bordered
      class="q-pa-md bg-card-primary"
    >
      <q-form
        class="q-col-gutter-md"
      >
        <div
          class="col-12"
        >
          <q-card
            flat
            bordered
          >
            <q-toggle
              v-model:model-value="v.config.enable.$model"
              label="Enable I2C"
              color="positive"
            />
          </q-card>
        </div>
        <div
          class="col-12"
          v-if="config.enable"
        >
          <q-card
            flat
            bordered
            class="q-pa-md"
          >
            <c-assistant-container>
              <template v-slot:explanations>
                When enabling I2C two GPIO-pins on the local board get used up for the SDA and SCL pin of the I2C-bus.
                Some boards provide more then one I2C bus. The cocktailmaker software only supports one I2C bus a the time.<br>
                This supported bus is the device at <q-badge outline class="text-black">/sys/bus/i2c/devices/i2c-1</q-badge>
                on the underlying linux filesystem.<br>
                On normal Raspberry PIs the pins used for SDL and SCL are on normally <b>2 for SDA</b> and <b>3 for SDL</b>.
                <p>If you can't see the pins that you want to use here, make sure that you don't have them assigned to something already.</p>
                <br>
                <b>The fields for the SDL and SCL pin don't influence the selected bus.</b> They only decide on which pins get
                marked as inUse.
              </template>
              <template v-slot:fields>
                <c-gpio-selector
                  disallow-expander-pins
                  v-model:model-value="v.config.sdaPin.$model"
                  :error-message="v.config.sdaPin.$errors[0]?.$message"
                  :error="v.config.sdaPin.$errors.length > 0"
                  label="SDA Pin"
                  error
                  error-message="Test"
                />
                <c-gpio-selector
                  disallow-expander-pins
                  v-model:model-value="v.config.sclPin.$model"
                  label="SCL Pin"
                  :error-message="v.config.sclPin.$errors[0]?.$message"
                  :error="v.config.sclPin.$errors.length > 0"
                />
              </template>
            </c-assistant-container>
          </q-card>
        </div>
        <div class="col">
          <q-card-actions class="q-pa-none">
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
        </div>
      </q-form>
    </q-card>
  </q-page>

</template>

<script>
import CAssistantContainer from 'components/CAssistantContainer.vue'
import { required, requiredIf } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'
import CGpioSelector from 'components/CGpioSelector.vue'

export default {
  name: 'I2CManagement',
  components: { CGpioSelector, CAssistantContainer },
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
