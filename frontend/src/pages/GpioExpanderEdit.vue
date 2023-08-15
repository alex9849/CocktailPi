<template>
  <q-page class="page-content" padding>
    <h5>{{ isNew ? 'Add' : 'Edit' }} I2C GPIO Expander</h5>
    <q-card
      flat
      bordered
      class="q-pa-md bg-card-primary"
    >
      <q-form
        class="q-col-gutter-md"
      >
        <div class="col-12">
          <q-card
            flat
            bordered
            class="q-pa-md"
          >
            <c-assistant-container>
              <template v-slot:fields>
                <q-input
                  label="Board Identifier"
                  outlined
                  hide-bottom-space
                />
                <q-input
                  :disable="!isNew"
                  label="Select board"
                  outlined
                  hide-bottom-space
                />
              </template>
            </c-assistant-container>
          </q-card>
        </div>
        <div class="col-12">
          <q-card
            flat
            bordered
            class="q-pa-md"
          >
            <c-assistant-container>
              <template v-slot:fields>
                <q-card bordered flat>
                  <q-card-section class="q-gutter-sm">
                    <div
                      class="row justify-end items-center"
                    >
                      <div class="col-grow">
                        <p class="text-weight-medium text-body1">Connected I2C Devices / Address</p>
                      </div>
                      <div class="col-shrink">
                        <q-btn
                          class="bg-info text-white"
                          :icon="mdiSync"
                          dense
                          no-caps
                          disable
                          label="Re-Check"
                        />
                      </div>
                    </div>
                    <q-card flat bordered class="bg-grey-1">
                      <q-card-section class="q-gutter-md">
                        <div class="row justify-center">
                            <q-spinner class="col" size="xl" />
                        </div>
                        <div class="row justify-center">
                          Probing addresses...
                        </div>
                      </q-card-section>
                    </q-card>
                    <q-list
                      bordered
                      separator
                      class="rounded-borders"
                    >
                      <q-item
                        v-for="i in 5"
                        :key="i"
                        clickable
                        :v-ripple="false"
                        disable
                      >
                        <q-item-section avatar>
                          <q-radio
                            v-model:model-value="expander.address"
                            :val="i"
                            label="0x27"
                            dense
                            disable
                          />
                        </q-item-section>
                        <q-item-section>
                          <q-item-label caption>
                            In use by dummy
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                    </q-list>
                    <q-card
                      flat
                      bordered
                      class="bg-grey-1"
                    >
                      <q-card-section
                        class="flex justify-center items-center"
                      >
                        <p>
                          <q-icon
                            size="md"
                            :name="mdiAlert"
                          />
                          No devices connected to I2C-bus!
                        </p>
                      </q-card-section>
                    </q-card>
                  </q-card-section>
                </q-card>
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
import { mdiAlert, mdiSync } from '@quasar/extras/mdi-v5'

export default {
  name: 'GpioExpanderEdit',
  created () {
    this.mdiAlert = mdiAlert
    this.mdiSync = mdiSync
  },
  components: { CAssistantContainer },
  data: () => {
    return {
      i2cDetector: {},
      expander: {
        address: ''
      }
    }
  },
  computed: {
    isNew () {
      return this.$route.name === 'gpioexpanderadd'
    },
    columns () {
      const columns = []
      for (let i = 0; i < 16; i++) {
        columns.push({ name: i, label: i, field: i, align: 'center', sortable: false })
      }
      return columns
    }
  }
}
</script>

<style scoped>

</style>
