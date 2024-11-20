<template>
  <q-card class="bg-card-body text-card-body">
    <q-card-section class="q-pa-none">
      <q-expansion-item
        label="Status"
        header-class="bg-card-header text-card-header text-weight-medium"
        :default-opened="!$q.platform.is.mobile"
      >
        <template v-slot:default>
          <q-separator/>
          <q-card-section>
            <div class="row q-col-gutter-sm justify-center">
              <div class="col-12 col-md-4 col-lg-12">
                <q-card
                  flat
                  bordered
                  class="bg-card-item-group text-card-item-group full-height"
                >
                  <q-card-section class="q-py-xs bg-card-header text-card-header">
                    <p class="text-weight-medium">Pumps</p>
                  </q-card-section>
                  <q-separator/>
                  <q-card-section
                    class="q-py-sm"
                  >
                    <table class="table-no-stripes">
                      <tbody>
                      <tr>
                        <td>Pumps installed:</td>
                        <td>
                          <q-badge>0</q-badge>
                        </td>
                      </tr>
                      <tr>
                        <td>Ingredients installed:</td>
                        <td>
                          <q-badge>0</q-badge>
                        </td>
                      </tr>
                      <tr>
                        <td>Pumps running:</td>
                        <td>
                          <q-badge>0</q-badge>
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </q-card-section>
                </q-card>
              </div>
              <div class="col-12 col-md-4 col-lg-12">
                <q-card
                  flat
                  bordered
                  class="bg-card-item-group text-card-item-group full-height"
                >
                  <q-card-section
                    class="q-py-xs q-pr-xs bg-cyan-1 row items-center bg-card-header text-card-header"
                  >
                    <div class="col">
                      <p class="text-weight-medium">Reverse pumping</p>
                    </div>
                    <div class="col-shrink">
                      <q-btn
                        color="info"
                        label="Configure"
                        :icon="mdiPencilOutline"
                        @click="$router.push({name: 'reversepumpsettings'})"
                        dense
                        no-caps
                        size="sm"
                      />
                    </div>
                  </q-card-section>
                  <q-separator/>
                  <q-card-section
                    class="q-py-sm"
                  >
                    <table class="table-no-stripes">
                      <tbody>
                      <tr>
                        <td>Status:</td>
                        <td>
                          <q-badge>enabled</q-badge>
                        </td>
                      </tr>
                      <tr>
                        <td>Dir-Pin:</td>
                        <td>
                          <q-badge>Local board / BCM 0</q-badge>
                        </td>
                      </tr>
                      <tr>
                        <td>Overshoot:</td>
                        <td>
                          <q-badge>10%</q-badge>
                        </td>
                      </tr>
                      <tr>
                        <td>Timer:</td>
                        <td>
                          <q-badge>5 min</q-badge>
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </q-card-section>
                </q-card>
              </div>
              <div class="col-12 col-md-4 col-lg-12">
                <q-card
                  flat
                  bordered
                  class="bg-card-item-group text-card-item-group full-height"
                >
                  <q-card-section
                    class="q-py-xs q-pr-xs bg-cyan-1 row items-center bg-card-header text-card-header"
                  >
                    <div class="col">
                      <p class="text-weight-medium">Load cell</p>
                    </div>
                    <div class="col-shrink">
                      <q-btn
                        color="info"
                        label="Configure"
                        :icon="mdiPencilOutline"
                        @click="$router.push({name: 'loadcellsettings'})"
                        dense
                        no-caps
                        size="sm"
                      />
                    </div>
                  </q-card-section>
                  <q-separator/>
                  <q-card-section
                    class="q-py-sm"
                  >
                    <table class="table-no-stripes">
                      <tbody>
                      <tr>
                        <td>Status:</td>
                        <td>
                          <q-badge>enabled</q-badge>
                        </td>
                      </tr>
                      <tr>
                        <td>CLK-Pin:</td>
                        <td>
                          <q-badge>Local board / BCM 0</q-badge>
                        </td>
                      </tr>
                      <tr>
                        <td>DT-Pin:</td>
                        <td>
                          <q-badge>Local board / BCM 1</q-badge>
                        </td>
                      </tr>
                      <tr>
                        <td>Calibrated:</td>
                        <td>
                          <q-badge>false</q-badge>
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </q-card-section>
                </q-card>
              </div>
            </div>
          </q-card-section>
        </template>
      </q-expansion-item>
    </q-card-section>
  </q-card>
</template>

<script>
import { mdiPencilOutline } from '@quasar/extras/mdi-v5'

export default {
  name: 'CPumpStatus',
  props: {
    classed: {
      type: String,
      default: ''
    }
  },
  created () {
    this.mdiPencilOutline = mdiPencilOutline
  }
}
</script>

<style scoped>

</style>
