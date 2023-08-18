<template>
  <q-dialog
    :model-value="show"
    persistent
    maximized
  >
    <q-card>
      <q-card-section>
        <q-toolbar>
          <q-toolbar-title class="text-center">
            <p class="text-h3" style="white-space: initial">Your support is needed!</p>
            <p class="text-weight-thin">Scroll down
              <q-icon :name="mdiChevronDoubleDown"/>
            </p>

          </q-toolbar-title>
        </q-toolbar>
      </q-card-section>
      <q-separator/>
      <q-card-section style="font-size: 15px; max-width: 800px" class="page-content">
        <q-card
          flat
          bordered
        >
          <q-card-section>
            <p>
              This software is free, but still it took and takes a lot time to develop and maintain it.
              The developer of CocktailMaker (me) develops it in his free time. I'm also a student who doesn't have a
              real income yet.
            </p>
            <div class="q-pa-md q-gutter-sm">
              <div class="row justify-center">
                <q-btn
                  style="width: 300px"
                  :icon="mdiGithub"
                  color="black"
                  @click="clickDonateGitHub"
                >
                  Donate via GitHub sponsors
                </q-btn>
              </div>
              <div class="row justify-center">
                <q-btn
                  style="width: 300px"
                  color="info"
                  :icon="mdiPaypal"
                  @click="clickDonatePaypal"
                >
                  Donate via PayPal
                </q-btn>
              </div>
            </div>
            <p>Here are some more reasons why you should donate:</p>
            <ul>
              <li>
                Users of this software usually spent large amounts of money on hardware (a Raspberry Pi, Pumps, a case,
                ...).
                Nevertheless this hardware wouldn't function if the CocktailMaker software wouldn't exist.
              </li>
              <li>
                You might think that that someone else will donate, but sadly they think that too. Before adding this
                disclaimer I got around 50€ of donations in two years
              </li>
              <li>
                I usually don't get any feedback for the software. It doesn't collect any data. I have no idea how many
                people are out there using it.
                A donation and also "stars" on GitHub give me positive feedback and motivate me to continue working on
                CocktailMaker.
              </li>
              <li>
                Developing this software causes costs. I as a developer often buy hardware, just to test
                if it would function with the device and make sense.
              </li>
            </ul>
            <p>
              Donating to CocktailMaker is entirely voluntary. While the software remains free to use, your contribution
              demonstrates your appreciation for the developer's hard work and dedication, motivating them to continue
              refining and expanding the software's capabilities.
            </p>
            <p>
              You can donate using GitHub Sponsors or Paypal. You can pick any amount that you think that the software
              is worth to you.
              You can also do monthly donations if you want to support me and my work over a period of time.
            </p>
          </q-card-section>
          <q-card-section>
            <div class="q-gutter-sm">
              <div class="row justify-center">
                <q-btn
                  style="width: 300px"
                  :icon="mdiGithub"
                  @click="clickDonateGitHub"
                  color="black"
                >
                  Donate via GitHub sponsors
                </q-btn>
              </div>
              <div class="row justify-center">
                <q-btn
                  style="width: 300px"
                  color="info"
                  @click="clickDonatePaypal"
                  :icon="mdiPaypal"
                >
                  Donate via PayPal
                </q-btn>
              </div>
            </div>
          </q-card-section>
          <q-separator/>
          <q-card-section>
            <div
              v-if="!clickedDonated"
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
            </div>
            <div v-else>
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
            </div>
          </q-card-section>
        </q-card>
      </q-card-section>
    </q-card>
  </q-dialog>

</template>

<script>

import { mdiGithub } from '@quasar/extras/mdi-v5'
import { mdiChevronDoubleDown, mdiEmoticon, mdiEmoticonExcited, mdiPaypal } from '@mdi/js'
import { mapGetters, mapMutations } from 'vuex'

export default {
  name: 'AppDonationDisclaimer',
  created () {
    this.mdiGithub = mdiGithub
    this.mdiPaypal = mdiPaypal
    this.mdiEmoticon = mdiEmoticon
    this.mdiEmoticonExcited = mdiEmoticonExcited
    this.mdiChevronDoubleDown = mdiChevronDoubleDown
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
      setShowDonateDialog: 'common/setShowDonateDialog',
      openExternalLink: 'common/openExternalLink'
    }),
    closeDialog () {
      this.clickedDonated = false
      this.setShowDonateDialog(false)
    },
    clickDonatePaypal () {
      this.openExternalLink('https://www.paypal.com/donate/?cmd=_s-xclick&hosted_button_id=B5YNFG7WH4D3S')
    },
    clickDonateGitHub () {
      this.openExternalLink('https://github.com/sponsors/alex9849')
    }
  }
}

</script>

<style scoped>

</style>
