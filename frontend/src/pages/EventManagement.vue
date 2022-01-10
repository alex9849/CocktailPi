<template>
  <q-page class="page-content" padding>
    <h5>Events</h5>
    <TopButtonArranger>
      <q-btn
        color="negative"
        label="Delete selected actions"
        no-caps
      />
      <q-btn
        color="positive"
        label="Add action"
        no-caps
      />
      <q-btn
        color="info"
        label="Refresh"
        no-caps
      />
    </TopButtonArranger>
    <div class="q-py-md">
      <q-table
        v-model:selected="selected"
        :columns="columns"
        :loading="isLoading"
        :pagination="{rowsPerPage: 0, sortBy: 'trigger'}"
        :rows="actions"
        :table-style="{margin: '15px'}"
        hide-bottom
        selection="multiple"
        style="background-color: #f3f3fa"
      >
        <template
          v-slot:loading
        >
          <q-inner-loading
            color="info"
            showing
          />
        </template>
      </q-table>
    </div>
  </q-page>
</template>

<script>

import TopButtonArranger from 'components/TopButtonArranger'

export default {
  name: 'EventManagement',
  components: { TopButtonArranger },
  data () {
    return {
      selected: [],
      actions: [],
      isLoading: false,
      columns: [
        { name: 'trigger', label: 'Trigger', field: 'trigger', align: 'center' },
        { name: 'action', label: 'Action', field: 'action', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ]
    }
  }
}
</script>

<style scoped>

</style>
