<template>
  <q-card
    flat
    bordered
  >
    <q-expansion-item
      header-class="q-pa-sm"
    >
      <template v-if="true" v-slot:header>
        <q-item class="full-width">
          <q-item-section>
            <q-item-label
              class="text-weight-bold"
            >
              Expander 1
            </q-item-label>
            <q-item-label caption>
              Address: 0x27, Usage 5/16
            </q-item-label>
          </q-item-section>
          <q-item-section
            side
            v-if="!nonEditable"
          >
            <div class="row justify-end q-col-gutter-sm">
              <div>
                <q-btn
                  @click="$router.push({name: 'gpioexpandereditor', params: {id: 1}})"
                  :icon="mdiPencilOutline"
                  text-color="white"
                  color="info"
                  @click.stop=""
                  dense
                  rounded
                />
              </div>
              <div>
                <q-btn
                  :icon="mdiDelete"
                  text-color="white"
                  color="negative"
                  @click.stop=""
                  dense
                  rounded
                />
              </div>
            </div>
          </q-item-section>
        </q-item>
      </template>
      <template v-slot:default>
        <div class="row justify-center bg-grey-2">
          <div class="col-12 col-lg-8">
            <q-list separator class="bg-white">
              <q-item :key="idx" v-for="idx in 36">
                <q-item-section>
                  <div class="row items-center q-col-gutter-md">
                    <div class="col-auto">
                      {{ pinPrefix }}{{ idx }}
                    </div>
                    <div class="col">
                      <q-icon
                        name="check"
                        size="sm"
                      />
                    </div>
                  </div>
                </q-item-section>
                <q-item-section side>Pump4</q-item-section>
              </q-item>
            </q-list>
          </div>
        </div>
      </template>
    </q-expansion-item>
  </q-card>
</template>

<script>
import { mdiDelete, mdiPencilOutline } from '@quasar/extras/mdi-v5'
export default {
  name: 'CGpioExpanderExpansionItem',
  props: {
    nonEditable: {
      type: Boolean,
      default: false
    },
    pinPrefix: {
      type: String,
      default: () => 'GPIO '
    }
  },
  created () {
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
  }
}
</script>

<style scoped>

</style>
