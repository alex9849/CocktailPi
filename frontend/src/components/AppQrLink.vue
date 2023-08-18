<template>
  <q-dialog
    v-model:model-value="show"
  >
    <q-card
      style="width: 400px"
    >
      <q-card-section class="q-pb-none row justify-center">
        <div
          class="col-12"
          v-if="qrCode"
          v-html="qrCode"
          :style="{'max-width': ($q.screen.height - 100) + 'px'}"
        />
        <div
          v-else
          class="row justify-center q-pa-xl col-12"
        >
          <q-spinner-tail size="9em" />
        </div>
        <p class="text-center col-12">{{ destination }}</p>
      </q-card-section>
      <q-card-section>
        <div class="row justify-center">
          <q-btn
            @click="close"
            color="grey"
          >
            Close
          </q-btn>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>

</template>

<script>

import { mapGetters } from 'vuex'
import { openURL } from 'quasar'
import QRCode from 'qrcode'

export default {
  name: 'AppQrLink',
  computed: {
    ...mapGetters({
      asQrCode: 'common/showExternalLinksAsQrCode',
      destination: 'common/getExternalLink',
      trigger: 'common/getTrigger'
    })
  },
  data: () => {
    return {
      qrCode: null,
      show: false
    }
  },
  methods: {
    close () {
      this.show = false
      this.qrCode = null
    }
  },
  watch: {
    trigger: {
      immediate: true,
      handler () {
        if (!this.destination) {
          return
        }
        if (!this.asQrCode) {
          openURL(this.destination)
        } else {
          QRCode.toString(this.destination, {
            errorCorrectionLevel: 'H',
            type: 'svg',
            margin: 0
          })
            .then(x => {
              this.qrCode = x
            })
          this.show = true
        }
      }
    }
  }

}
</script>

<style scoped>

</style>
