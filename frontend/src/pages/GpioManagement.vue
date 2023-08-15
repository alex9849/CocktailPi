<template>
  <q-page class="page-content" padding>
    <h5>GPIO</h5>

    <div class="row q-col-gutter-md reverse">
      <div class="col-12 col-md-4 col-lg-3">
        <q-card class="bg-card-primary">
          <q-card-section class="q-py-sm bg-card-secondary">
            <p class="text-weight-medium">Status</p>
          </q-card-section>
          <q-separator :value="50" />
          <q-card-section class="q-py-md">
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-6 col-md-12">
                <q-card flat bordered class="bg-grey-2">
                  <q-card-section class="q-py-xs bg-cyan-1">
                    <p class="text-weight-medium">Pin usage</p>
                  </q-card-section>
                  <q-separator :value="10" />
                  <q-card-section class="row q-py-sm q-col-gutter-xs">
                    <p class="col-12 col-sm-6 col-md-12">Pins usage: <q-badge>1/36</q-badge></p>
                    <p class="col-12 col-sm-6 col-md-12">Expansion cards: <q-badge>1</q-badge></p>
                  </q-card-section>
                </q-card>
              </div>
              <div class="col-12 col-sm-6 col-md-12">
                <q-card flat bordered class="bg-grey-2">
                  <q-card-section class="q-py-xs q-pr-xs bg-cyan-1 row items-center">
                    <div class="col">
                      <p class="text-weight-medium">I2C</p>
                    </div>
                    <div class="col-shrink">
                      <q-btn
                        color="info"
                        label="Configure"
                        :icon="mdiPencilOutline"
                        @click="$router.push({name: 'i2cmanagement'})"
                        dense
                        no-caps
                        size="sm"
                      />
                    </div>
                  </q-card-section>
                  <q-separator :value="10" />
                  <q-card-section class="row q-py-sm q-col-gutter-xs">
                    <p class="col-12 col-sm-6 col-md-12">Status: <q-badge class="bg-negative">disabled</q-badge></p>
                  </q-card-section>
                  <q-separator :value="10" />
                  <q-card-section class="row q-py-sm q-col-gutter-xs">
                    <div class="col-12">
                      <p class="text-weight-medium">Bus 1</p>
                      <p>Pins: 1, 2</p>
                    </div>
                  </q-card-section>
                </q-card>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-md-8 col-lg-9 q-gutter-y-md">
        <div class="row">
          <div class="col-12">
            <q-card class="bg-card-primary">
              <q-card-section class="q-py-sm bg-card-secondary">
                <p class="text-weight-medium">Local GPIOs:</p>
              </q-card-section>
              <q-separator :value="10" />
              <q-card-section class="">
                <div class="row q-col-gutter-sm">
                  <div class="col-12">
                    <c-gpio-expander-expansion-item
                      non-editable
                      pin-prefix="BCM "
                    />
                  </div>
                </div>
              </q-card-section>
            </q-card>
          </div>
        </div>
        <div class="row">
          <div class="col-12">
            <q-card class="bg-card-primary">
              <q-card-section class="q-py-sm bg-card-secondary">
                <div class="row items-center">
                  <p class="col text-weight-medium">I2C GPIO Expanders:</p>
                  <div class="col-shrink">
                    <q-btn
                      label="Add"
                      color="positive"
                      @click="$router.push({name: 'gpioexpanderadd'})"
                      :icon="mdiPlusCircleOutline"
                      no-caps
                      disable
                    />
                  </div>
                </div>
              </q-card-section>
              <q-separator :value="10" />
              <q-card-section class="">
                <div class="row q-col-gutter-sm">
                  <div class="col-12">
                    <q-card
                      flat
                      bordered
                    >
                      <q-card-section>
                        <p>
                          <q-icon
                            :name="mdiAlert"
                            size="sm"
                          /> I2C disabled!
                        </p>
                      </q-card-section>
                    </q-card>
                  </div>
                  <div class="col-12">
                    <q-card
                      flat
                      bordered
                    >
                      <q-card-section>
                        <p>
                          <q-icon
                            :name="mdiAlert"
                            size="sm"
                          /> No expanders found!
                        </p>
                      </q-card-section>
                    </q-card>
                  </div>
                  <div class="col-12">
                    <c-gpio-expander-expansion-item />
                  </div>
                  <div class="col-12">
                    <c-gpio-expander-expansion-item />
                  </div>
                </div>
              </q-card-section>
            </q-card>
          </div>
        </div>
      </div>
    </div>
  </q-page>

</template>

<script>
import { mdiAlert, mdiDelete, mdiPencilOutline, mdiPlusCircleOutline } from '@quasar/extras/mdi-v5'
import CGpioExpanderExpansionItem from 'components/gpiosetup/CGpioExpanderExpansionItem.vue'
export default {
  name: 'GpioManagement',
  methods: {
  },
  components: { CGpioExpanderExpansionItem },
  created () {
    this.mdiAlert = mdiAlert
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiPlusCircleOutline = mdiPlusCircleOutline
  }
}

</script>

<style scoped>

</style>
