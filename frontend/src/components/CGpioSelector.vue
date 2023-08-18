<template>
  <div>
    <q-card class="overflow-hidden" flat bordered>
      <div class="row">
        <q-select
          class="col-6"
          label="Board"
          bg-color="white"
          v-model:model-value="selection.board"
          :options="boards"
          option-label="name"
          option-value="id"
          :loading="loadingBoards"
          :disable="loadingBoards"
          :error="error"
          square
          filled
          no-error-icon
          hide-bottom-space
        />
        <q-select
          style="border-left: solid #D5D5D5FF 1px"
          class="col-6"
          bg-color="white"
          :label="label ? label : 'SCL-Pin'"
          v-model:model-value="selection.pin"
          @update:modelValue="onPinSelect"
          :disable="loadingPins"
          :loading="loadingPins"
          :option-label="x => x ? pinIdPrefix + x.nr : null"
          :error="error"
          option-value="nr"
          square
          :options="selectablePins"
          filled
          hide-bottom-space
        >
          <template
            v-if="!selection.board"
            v-slot:no-option
          >
            <q-item>
              <q-item-section>
                <i>Select a board first.</i>
              </q-item-section>
            </q-item>
          </template>
        </q-select>
      </div>
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
    }
  },
  data: () => {
    return {
      selection: {
        board: '',
        pin: ''
      },
      boards: [{
        value: 1,
        label: 'Local'
      }],
      pins: [],
      loadingBoards: true,
      loadingPins: false
    }
  },
  emits: ['update:model-value'],
  created () {
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
    },
    'selection.board': {
      immediate: true,
      handler (val, oldval) {
        if (!val) {
          this.pins = []
          if (this.selection.pin) {
            this.onPinSelect(null)
            this.selection.pin = ''
          }
          return
        }
        if (val.id === oldval?.id) {
          return
        }
        this.selection.pin = ''
        this.onPinSelect(null)
        this.fetchPins(val.id)
      }
    }
  },
  methods: {
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
        })
        .finally(() => {
          this.loadingPins = false
        })
    },
    onPinSelect (pin) {
      this.$emit('update:model-value', pin)
    }
  },
  computed: {
    pinIdPrefix () {
      if (this.selection.board?.type === 'local') {
        return 'BCM '
      }
      return 'GPIO'
    },
    selectablePins () {
      const pins = []
      const allowedPinIds = new Set(
        this.allowedInUsePins
          .filter(x => x.boardId === this.selection.board?.id)
          .map(x => x.nr)
      )
      if (this.modelValue && this.modelValue.boardId === this.selection.board?.id) {
        allowedPinIds.add(this.modelValue.nr)
      }
      for (const pin of this.pins) {
        if (pin.inUse && !allowedPinIds.has(pin.nr)) {
          continue
        }
        pins.push(pin)
      }
      return pins
    }
  }
}
</script>

<style scoped>

</style>
