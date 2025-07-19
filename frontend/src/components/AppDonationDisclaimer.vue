<template>
  <q-dialog
    :model-value="show"
    persistent
    maximized
  >
    <q-card
      :dark="dark"
    >
      <q-card-section>
        <q-toolbar>
          <q-toolbar-title class="text-center">
            <p class="text-h3" style="white-space: initial">
              {{ $t('component.donation_disclaimer.headline') }}
            </p>
            <p class="text-weight-thin">
              {{ $t('component.donation_disclaimer.headline_caption') }}
              <q-icon :name="mdiChevronDoubleDown"/>
            </p>
          </q-toolbar-title>
          <q-btn flat
                 round
                 size="xl"
                 dense
                 icon="close"
                 @click="closeDialog"
          />
        </q-toolbar>
      </q-card-section>
      <q-separator
        :dark="dark"
      />
      <q-card-section style="font-size: 15px; max-width: 800px" class="page-content">
        <q-card
          flat
          bordered
          :dark="dark"
        >
          <q-card-section>
            <h6 class="text-center">
              {{ $t('component.donation_disclaimer.headline_2') }}
            </h6>
            <p>
              {{ $t('component.donation_disclaimer.introduction') }}
            </p>
            <div class="q-pa-md q-gutter-sm">
              <div class="row justify-center">
                <q-btn
                  style="width: 300px"
                  color="info"
                  :icon="mdiPaypal"
                  @click="clickDonatePaypal"
                >
                  {{ $t('component.donation_disclaimer.donate_paypal_btn_label') }}
                </q-btn>
              </div>
            </div>
            <div v-html="$t('component.donation_disclaimer.main_text')"/>
          </q-card-section>
          <q-card-section>
            <div class="q-gutter-sm">
              <div class="row justify-center">
                <q-btn
                  style="width: 300px"
                  color="info"
                  @click="clickDonatePaypal"
                  :icon="mdiPaypal"
                >
                  {{ $t('component.donation_disclaimer.donate_paypal_btn_label') }}
                </q-btn>
              </div>
            </div>
          </q-card-section>
          <q-separator
            :dark="dark"
          />
          <q-card-section>
            <div
              v-if="donationSettings.donated"
            >
              <div class="text-center">
                <p class="text-h5">
                  {{ $t('component.donation_disclaimer.action_box.donated.headline') }}
                </p>
                <p class="text-caption">
                  {{ $t('component.donation_disclaimer.action_box.donated.caption') }}
                </p>
              </div>
              <q-card-section>
                <div class="q-gutter-sm">
                  <div class="row justify-center">
                    <q-btn
                      color="positive"
                      style="width: 500px"
                      no-caps
                      :disable="loadingDonationModify"
                      @click="closeDialog"
                    >
                      {{ $t('component.donation_disclaimer.action_box.donated.close_btn') }}
                    </q-btn>
                  </div>
                  <div class="row justify-center">
                    <q-btn
                      color="negative"
                      style="width: 500px"
                      no-caps
                      :loading="loadingDonationModify"
                      @click="clickUndonate()"
                    >
                      {{ $t('component.donation_disclaimer.action_box.donated.revert_btn') }}
                    </q-btn>
                  </div>
                </div>
              </q-card-section>
            </div>
            <div
              v-else-if="!clickedDonated"
            >
              <p class="text-center text-h5">
                {{ $t('component.donation_disclaimer.action_box.not_donated.headline') }}
              </p>
              <q-card-section>
                <div class="q-gutter-sm">
                  <div class="row justify-center">
                    <q-btn
                      color="positive"
                      style="width: 500px"
                      no-caps
                      @click="clickedDonated = true"
                    >
                      {{ $t('component.donation_disclaimer.action_box.not_donated.donated_btn') }}
                    </q-btn>
                  </div>
                  <div class="row justify-center">
                    <q-btn
                      color="negative"
                      style="width: 500px"
                      no-caps
                      @click="closeDialog"
                    >
                      {{ $t('component.donation_disclaimer.action_box.not_donated.close_btn') }}
                    </q-btn>
                  </div>
                </div>
              </q-card-section>
            </div>
            <div v-else>
              <p class="text-center text-h5">
                {{ $t('component.donation_disclaimer.action_box.lying_is_no_nice.headline') }}
              </p>
              <q-card-section>
                <div class="q-gutter-sm">
                  <div class="row justify-center">
                    <q-checkbox
                      :dark="dark"
                      :label="$t('component.donation_disclaimer.action_box.lying_is_no_nice.checkbox')"
                      v-model:model-value="notLieCheckBox"
                    />
                  </div>
                  <div class="row justify-center">
                    <q-btn
                      color="positive"
                      style="width: 500px"
                      :disable="!notLieCheckBox"
                      :loading="loadingDonationModify"
                      no-caps
                      @click="clickConfirmDonate"
                    >
                      {{ $t('component.donation_disclaimer.action_box.lying_is_no_nice.confirm_btn') }}
                    </q-btn>
                  </div>
                  <div class="row justify-center">
                    <q-btn
                      color="grey"
                      :disable="loadingDonationModify"
                      style="width: 500px"
                      no-caps
                      @click="() => {clickedDonated = false; notLieCheckBox = false;}"
                    >
                      {{ $t('component.donation_disclaimer.action_box.lying_is_no_nice.go_back') }}
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
import { mapActions, mapGetters, mapMutations } from 'vuex'
import SystemService from 'src/services/system.service'

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
      firstOpen: true,
      clickedDonated: false,
      notLieCheckBox: false,
      loadingDonationModify: false,
      timerTask: null
    }
  },
  computed: {
    ...mapGetters({
      show: 'common/isShowDonateDialog',
      donationSettings: 'common/getDonationSettings'
    }),
    dark () {
      return this.$route.path.includes('/simple/')
    }
  },
  watch: {
    show (val) {
      if (val) {
        this.firstOpen = false
        SystemService.setOpenedDonationDisclaimer()
      }
    },
    donationSettings: {
      immediate: true,
      handler (val) {
        if (this.timerTask !== null) {
          clearTimeout(this.timerTask)
          this.timerTask = null
        }
        if (val.showDisclaimer) {
          this.timerTask = setTimeout(() => {
            if (this.firstOpen) {
              this.setShowDonateDialog(true)
            }
          }, val.disclaimerDelay)
        }
      }
    }
  },
  methods: {
    ...mapMutations({
      setShowDonateDialog: 'common/setShowDonateDialog',
      openExternalLink: 'common/openExternalLink'
    }),
    ...mapActions({
      fetchGlobalSettings: 'common/fetchGlobalSettings'
    }),
    closeDialog () {
      this.clickedDonated = false
      this.setShowDonateDialog(false)
    },
    clickDonatePaypal () {
      this.openExternalLink('https://www.paypal.com/donate/?hosted_button_id=FQUPSE8DCXFMC')
    },
    clickConfirmDonate () {
      this.setDonated(true)
      this.notLieCheckBox = false
    },
    clickUndonate () {
      this.setDonated(false)
        .then(() => {
          this.clickedDonated = false
        })
    },
    setDonated (donated) {
      this.loadingDonationModify = true
      return SystemService.setDonated(donated)
        .then(() => {
          this.fetchGlobalSettings()
        })
        .finally(() => {
          this.loadingDonationModify = false
        })
    }
  }
}

</script>

<style scoped>

</style>
