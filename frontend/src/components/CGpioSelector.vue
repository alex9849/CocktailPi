<template>
  <div>
    <q-card
      class="overflow-hidden bg-transparent"
      flat
    >
      <q-card-section class="q-px-none q-py-xs row">
        <q-select
          :dark="dark"
          class="col-6 field-left"
          label="Board"
          :model-value="selection.board"
          @update:modelValue="onSelectBoard"
          :options="boards"
          option-label="name"
          option-value="id"
          :error="error"
          :disable="loading || disable"
          outlined
          no-error-icon
          hide-bottom-space
        >
          <template
            v-slot:no-option
          >
            <q-linear-progress
              v-if="loadingBoards"
              query
              reverse
              size="xs"
              color="info"
            />
            <q-item v-if="loadingBoards">
              <q-item-section>
                <i>Loading Boards...</i>
              </q-item-section>
            </q-item>
          </template>
        </q-select>
        <q-select
          style="border-left: solid #D5D5D5FF 1px"
          :dark="dark"
          class="col-6 field-right"
          :label="label ? label : 'SCL-Pin'"
          :model-value="selection.pin"
          ref="pinSelect"
          @update:modelValue="onSelectPin"
          @popupShow="onOpenPinSelectPopup"
          :option-label="x => x.pinName"
          :error="error"
          :disable="loading || disable"
          :loading="loading"
          :options="pins"
          outlined
          hide-bottom-space
        >
          <template
            v-slot:no-option
          >
            <q-item
              v-if="!selection.board"
              :dark="dark"
            >
              <q-item-section>
                <i>Select a board first.</i>
              </q-item-section>
            </q-item>
            <q-linear-progress
              :dark="dark"
              v-if="loadingPins"
              query
              reverse
              size="xs"
              color="info"
            />
            <q-item
              :dark="dark"
              v-if="loadingPins"
            >
              <q-item-section>
                <i>Loading Pins...</i>
              </q-item-section>
            </q-item>
          </template>
          <template v-slot:append>
            <q-btn
              v-if="this.selection.board && clearable"
              :disable="loading || disable"
              :icon="mdiCloseCircle"
              flat
              dense
              round
              @click.stop="onClear()"
            />
          </template>
          <template v-slot:option="{opt, toggleOption}">
            <q-item
              :dark="dark"
              clickable
              @click="toggleOption(opt)"
            >
              <q-item-section avatar>
                <q-item-label>
                  {{ opt.pinName }}
                </q-item-label>
              </q-item-section>
              <q-item-section
                v-if="opt.inUse"
                class="text-italic text-grey-7"
              >
                (In use)
              </q-item-section>
            </q-item>
          </template>
        </q-select>
      </q-card-section>
    </q-card>
    <div
      v-if="errorMessage"
      class="q-field__bottom row items-start q-field__bottom--stale text-negative"
    >
      <div class="q-field__messages col">
        <div role="alert">
          {{ errorMessage }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import GpioService from 'src/services/gpio.service'
import { mdiCloseCircle } from '@mdi/js'

export default {
  name: 'CGpioSelector',
  props: {
    error: {
      type: Boolean,
      default: false
    },
    errorMessage: {
      type: String
    },
    modelValue: {
      type: Object
    },
    label: {
      type: String
    },
    disallowExpanderPins: {
      type: Boolean,
      default: false
    },
    allowedInUsePins: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    clearable: {
      type: Boolean,
      default: false
    },
    disable: {
      type: Boolean,
      default: false
    },
    dark: {
      type: Boolean,
      default: false
    }
  },
  data: () => {
    return {
      selection: {
        board: '',
        pin: ''
      },
      boards: [],
      pins: [],
      pinsFetched: false,
      loadingBoards: true,
      loadingPins: false
    }
  },
  emits: ['update:model-value'],
  created () {
    this.mdiCloseCircle = mdiCloseCircle
    this.fetchBoards()
  },
  watch: {
    modelValue: {
      immediate: true,
      handler (val, oldVal) {
        if (!val) {
          return
        }
        this.setSelectionFromModelValue()
      }
    }
  },
  methods: {
    onSelectBoard (board) {
      if (this.selection.board?.id === board?.id) {
        return
      }
      this.selection.board = board
      this.resetFetchedPins()
      if (this.selection.pin) {
        this.selection.pin = null
        this.emitPin()
      }
    },
    onSelectPin (pin) {
      this.selection.pin = pin
      this.emitPin()
      this.$refs.pinSelect.blur()
    },
    onOpenPinSelectPopup () {
      if (!this.pinsFetched && this.selection.board) {
        this.fetchPins(this.selection.board.id)
      }
    },
    onClear () {
      this.onSelectBoard(null)
      this.$refs.pinSelect.blur()
    },
    resetFetchedPins () {
      this.pinsFetched = false
      this.pins = []
    },
    setSelectionFromModelValue () {
      if (this.loadingBoards || !this.modelValue) {
        return
      }
      for (const board of this.boards) {
        if (this.modelValue.boardId === board.id) {
          this.selection.board = board
          this.selection.pin = this.modelValue
          return
        }
      }
    },
    fetchBoards () {
      this.loadingBoards = true
      let promise
      if (this.disallowExpanderPins) {
        promise = GpioService.getBoardsByType('local')
      } else {
        promise = GpioService.getBoards()
      }
      promise
        .then(x => {
          this.boards = x
        })
        .finally(() => {
          this.loadingBoards = false
          this.setSelectionFromModelValue()
        })
    },
    fetchPins (boardId) {
      this.loadingPins = true
      GpioService.getBoardPins(boardId)
        .then(x => {
          this.pins = x
          this.pinsFetched = true
        })
        .finally(() => {
          this.loadingPins = false
        })
    },
    emitPin () {
      this.$emit('update:model-value', this.selection.pin)
    }
  }
}
</script>

<style scoped>
:deep(.field-left .q-field__control::before) {
  border-top-right-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
}

:deep(.field-right .q-field__control::before) {
  border-left: none !important;
  border-top-left-radius: 0 !important;
  border-bottom-left-radius: 0 !important;
}

</style>
