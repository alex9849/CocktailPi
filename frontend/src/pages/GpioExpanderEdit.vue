<template>
  <q-page class="page-content" padding>
    <h5>{{ isNew ? 'Add' : 'Edit' }} I2C GPIO Expander</h5>
    <q-card
      flat
      bordered
    >
      <q-card-section>
        <q-input
          name="Board Identifier"
        />
        <q-separator :value="10" />
        <q-input
          name="Select board"
        />
        <q-separator :value="10" />
        <q-table
          title="I2C Address"
          :columns="columns"
          :rows="[{0: 0, 1: 1, 2: 2, 3: 3, 4: 4, 5: 5, 6: 6, 7: 7, 8: 8, 9: 9, 10: 10, 11: 11, 12: 12, 13: 13, 14: 14, 15: 15}]"
          hide-bottom
        >
          <template v-slot:body-cell="props">
            <q-td
              :props="props"
            >
              <q-radio
                disable
                v-model:model-value="expander.address"
                :label="props.rowIndex * 16 + props.col.field"
                :val="props.rowIndex * 16 + props.col.field"
              />
            </q-td>
          </template>
        </q-table>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script>

export default {
  name: 'GpioExpanderEdit',
  data: () => {
    return {
      i2cDetector: {
      },
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
