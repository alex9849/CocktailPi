<template>
  <q-card
    flat
    bordered
  >
    <q-expansion-item
      header-class="q-pa-sm"
      @show="fetchPins"
    >
      <template v-if="true" v-slot:header>
        <q-item class="full-width">
          <q-item-section>
            <q-item-label
              class="text-weight-bold"
            >
              {{ board.name }}
            </q-item-label>
            <q-item-label caption>
              {{ caption }}
            </q-item-label>
          </q-item-section>
          <q-item-section
            side
            v-if="!nonEditable"
          >
            <div class="row justify-end q-col-gutter-sm">
              <div>
                <q-btn
                  @click="$router.push({name: 'gpioexpandereditor', params: {id: board.id}})"
                  :icon="mdiPencilOutline"
                  text-color="white"
                  color="info"
                  dense
                  rounded
                />
              </div>
              <div>
                <q-btn
                  :icon="mdiDelete"
                  text-color="white"
                  color="negative"
                  @click.stop="$emit('clickDelete')"
                  dense
                  rounded
                />
              </div>
            </div>
          </q-item-section>
        </q-item>
      </template>
      <template v-slot:default>
        <q-separator />
        <div class="row justify-center bg-grey-2">
          <q-spinner
            v-if="pins.loading"
            class="text-info q-ma-md"
            size="xl"
          />
          <div
            v-else
            class="col-12 col-lg-8"
          >
            <q-list separator class="bg-white">
              <q-item :key="pin.nr" v-for="pin in pins.pins">
                <q-item-section>
                  <div class="row items-center q-col-gutter-md">
                    <div class="col-auto">
                      {{ pinPrefix }}{{ pin.nr }}
                    </div>
                    <div class="col">
                      <q-icon
                        :name="pin.inUse ? 'check' : 'close'"
                        size="sm"
                      />
                    </div>
                  </div>
                </q-item-section>
                <q-item-section
                  v-if="!!pin.pinResource"
                  side
                >
                  {{ pin.pinResource.name }}
                </q-item-section>
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
import GpioService from 'src/services/gpio.service'
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
    },
    board: {
      type: Object,
      required: true
    }
  },
  emits: ['clickDelete'],
  data: () => {
    return {
      pins: {
        loading: false,
        pins: []
      }
    }
  },
  created () {
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
  },
  methods: {
    fetchPins () {
      this.pins.loading = true
      GpioService.getBoardPins(this.board.id)
        .then(x => {
          this.pins.pins = x
        })
        .finally(() => {
          this.pins.loading = false
        })
    }
  },
  computed: {
    caption () {
      if (this.board.type === 'local') {
        return 'Board: Local, Usage: ' + this.board.usedPinCount + '/' + this.board.pinCount
      } else if (this.board.type === 'i2c') {
        return 'Address: 0x27, Board: MCP23017, Usage: ' + this.board.usedPinCount + '/' + this.board.pinCount
      }
      return ''
    }
  }
}
</script>

<style scoped>

</style>
