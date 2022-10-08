<template>
  <q-page class="page-content" padding>
    <h5>System Management</h5>
    <q-card flat bordered>
      <q-card-section>
        <div class="row justify-center">
          <div class="col-auto">
            <q-btn
              size="xl"
              :icon="mdiPower"
              no-caps
              dense
              color="red"
              @click="shutdown.dialog = true"
            >
              Shutdown system
            </q-btn>
          </div>
        </div>
      </q-card-section>
    </q-card>
    <c-question
      v-model:show="shutdown.dialog"
      question="Shut down?"
      @clickAbort="shutdown.dialog = false"
      @clickOk="doShutdown"
      ok-color="red"
      ok-button-text="Shutdown"
    />
  </q-page>
</template>

<script>

import { mdiPower } from '@quasar/extras/mdi-v5'
import CQuestion from 'components/CQuestion'
import SystemService from 'src/services/system.service'

export default {
  name: 'SystemManagement',
  components: { CQuestion },
  data: () => {
    return {
      shutdown: {
        dialog: false
      }
    }
  },
  created () {
    this.mdiPower = mdiPower
  },
  methods: {
    doShutdown () {
      SystemService.doShutdown()
        .finally(() => {
          this.shutdown.dialog = false
        })
    }
  }
}
</script>

<style scoped>

</style>
