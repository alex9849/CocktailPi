<template>
  <q-card
    flat
    bordered
    class="bg-card-item-group text-card-item-group"
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
            <q-item-label
              :style="{'color': captionColor}"
              caption
            >
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
                  :icon="mdiRestart"
                  :loading="restarting"
                  @click.stop="onClickRestart()"
                  text-color="white"
                  color="info"
                  dense
                  rounded
                />
              </div>
              <div>
                <q-btn
                  @click.stop="$router.push({name: 'gpioexpandereditor', params: {id: board.id}})"
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
                  <div class="row items-center q-col-gutter-md text-black">
                    <div class="col-auto">
                      {{ pin.pinName }}
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
import { mdiDelete, mdiRestart, mdiPencilOutline } from '@quasar/extras/mdi-v5'
import GpioService from 'src/services/gpio.service'
import { mapGetters } from 'vuex'
import { complementColor } from 'src/mixins/utils'
import { i2cExpanderBoardTypes } from 'src/mixins/constants'
export default {
  name: 'CGpioExpanderExpansionItem',
  mixins: [i2cExpanderBoardTypes],
  props: {
    nonEditable: {
      type: Boolean,
      default: false
    },
    board: {
      type: Object,
      required: true
    }
  },
  emits: ['clickDelete', 'clickRestart'],
  data: () => {
    return {
      pins: {
        loading: false,
        pins: []
      },
      restarting: false
    }
  },
  created () {
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiRestart = mdiRestart
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
    },
    onClickRestart () {
      this.restarting = true
      GpioService.restartGpioBoard(this.board.id)
        .finally(() => {
          this.restarting = false
        })
    }
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    }),
    captionColor () {
      return complementColor(this.color.cardItemGroup, 70)
    },
    caption () {
      if (this.board.type === 'local') {
        return this.$t('component.gpio_expander_expansion_item.caption_local',
          { pinsUsed: this.board.usedPinCount, pinsMax: this.board.pinCount }
        )
      } else if (this.board.type === 'i2c') {
        return this.$t('component.gpio_expander_expansion_item.caption_i2c',
          {
            pinsUsed: this.board.usedPinCount,
            pinsMax: this.board.pinCount,
            addr: '0x' + this.board.address.toString(16),
            board: this.i2cExpanderBoardTypeName(this.board.boardModel)
          }
        )
      }
      return ''
    }
  }
}
</script>

<style scoped>

</style>
