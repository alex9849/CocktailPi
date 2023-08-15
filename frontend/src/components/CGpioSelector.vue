<template>
  <q-card class="row overflow-hidden" flat bordered>
    <q-select
      class="col-6"
      label="Board"
      bg-color="white"
      v-model:model-value="selection.board"
      :options="boards"

      square
      filled
      hide-bottom-space
    />
    <q-select
      style="border-left: solid #D5D5D5FF 1px"
      class="col-6"
      bg-color="white"
      :label="label ? label : 'SCL-Pin'"
      v-model:model-value="selection.pin"
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
  </q-card>
</template>

<script>

export default {
  name: 'CGpioSelector',
  props: {
    label: {
      type: String
    },
    disallowExpanderPins: {
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
      boards: [{
        value: 1,
        label: 'Local'
      }],
      pins: []
    }
  },
  methods: {
    onBoardSelect () {

    }
  },
  computed: {
    selectablePins () {
      const pins = []
      if (!this.selection.board) {
        return pins
      }
      for (let i = 0; i < 36; i++) {
        pins.push({
          value: i,
          label: 'BCM ' + i
        })
      }
      return pins
    }
  }
}
</script>

<style scoped>

</style>
