<template>
  <q-dialog
    :model-value="show"
    persistent
    maximized
  >
    <q-card>
      <q-card-section>
        <q-toolbar>
          <q-toolbar-title>
            <p class="text-h3 text-center">Your support is needed!</p>
          </q-toolbar-title>
        </q-toolbar>
      </q-card-section>
      <q-separator />
      <q-card-section style="font-size: 15px" class="page-content">
        <q-card
          flat
          bordered
        >
          <q-card-section>
            <p>
              This software is free, but still it took and takes a lot time to develop in maintain it.
              The developer of CocktailMaker (me) develops it in his free time. I'm also a student who doesn't have a real income yet.
            </p>
            <p>Here are some more reasons by you should donate:</p>
            <ul>
              <li>
                Users of this software usually spent large amounts of money on hardware (a Raspberry Pi, Pumps, a case, ...).
                Nevertheless this hardware wouldn't function if the CocktailMaker software wouldn't exist.
              </li>
              <li>
                Before adding this disclaimer I got around 50€ of donations in two years. You might think that that someone else will donate, but sadly they think that too.
              </li>
              <li>
                I usually don't get any feedback for the software. It doesn't collect any data. I have no idea how many people are out there using it.
                A donation and also "stars" on Github give me positive feedback and motivate me to continue working on CocktailMaker.</li>
              <li>
                Developing this software causes costs. I as a developer often buy hardware, just to test
                if it would function with the device and make sense.
              </li>
            </ul>
            <p>
              Donating to CocktailMaker is entirely voluntary. While the software remains free to use, your contribution
              demonstrates your appreciation for the developer's hard work and dedication, motivating them to continue refining and expanding the software's capabilities.
            </p>
            <p>
              You can donate using Github Sponsors or Paypal. You can pick any amount that you think that the software is worth to you.
              You can also do monthly donations if you want to support me and my work over a period of time.
            </p>
          </q-card-section>
          <q-card-section>
            <div class="q-gutter-sm">
              <div class="row justify-center">
                <q-btn
                  style="width: 300px"
                  :icon="mdiGithub"
                  color="black"
                >
                  Donate via Github sponsors
                </q-btn>
              </div>
              <div class="row justify-center">
                <q-btn
                  style="width: 300px"
                  color="info"
                  :icon="mdiPaypal"
                >
                  Donate via PayPal
                </q-btn>
              </div>
            </div>
          </q-card-section>
          <q-card-section>
            <q-card
              v-if="!clickedDonated"
              flat
              bordered
            >
              <q-card-section class="q-pa-sm text-center text-h5">
                Thank you very much!
              </q-card-section>
              <q-card-section>
                <div class="q-gutter-sm">
                  <div class="row justify-center">
                    <q-btn
                      color="positive"
                      style="width: 500px"
                      no-caps
                      :icon-right="mdiEmoticon"
                      @click="clickedDonated = true"
                    >
                      I made a donation
                    </q-btn>
                  </div>
                  <div class="row justify-center">
                    <q-btn
                      color="negative"
                      style="width: 500px"
                      no-caps
                      @click="closeDialog"
                    >
                      No, remind me later
                    </q-btn>
                  </div>
                </div>
              </q-card-section>
            </q-card>
            <q-card
              v-else
              flat
              bordered
            >
              <q-card-section class="q-pa-sm text-center text-h5">
                Please note that lying is not nice.
              </q-card-section>
              <q-card-section>
                <div class="q-gutter-sm">
                  <div class="row justify-center">
                    <q-btn
                      color="positive"
                      style="width: 500px"
                      no-caps
                      :icon-right="mdiEmoticonExcited"
                      @click="closeDialog"
                    >
                      I didn't lie
                    </q-btn>
                  </div>
                  <div class="row justify-center">
                    <q-btn
                      color="negative"
                      style="width: 500px"
                      no-caps
                      @click="clickedDonated = false"
                    >
                      I lied
                    </q-btn>
                  </div>
                </div>
              </q-card-section>
            </q-card>
          </q-card-section>
        </q-card>
      </q-card-section>
    </q-card>
  </q-dialog>

</template>

<script>

import { mdiGithub } from '@quasar/extras/mdi-v5'
import { mdiEmoticon, mdiEmoticonExcited, mdiPaypal } from '@mdi/js'
import { mapGetters, mapMutations } from 'vuex'

export default {
  name: 'AppDonationDisclaimer',
  created () {
    this.mdiGithub = mdiGithub
    this.mdiPaypal = mdiPaypal
    this.mdiEmoticon = mdiEmoticon
    this.mdiEmoticonExcited = mdiEmoticonExcited
  },
  data: () => {
    return {
      clickedDonated: false
    }
  },
  computed: {
    ...mapGetters({
      show: 'common/isShowDonateDialog'
    })
  },
  methods: {
    ...mapMutations({
      setShowDonateDialog: 'common/setShowDonateDialog'
    }),
    closeDialog () {
      this.clickedDonated = false
      this.setShowDonateDialog(false)
    }
  }
}

</script>

<style scoped>

</style>
