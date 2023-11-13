<template>
  <q-card
    bordered
    class="bg-card-body text-card-body"
  >
    <q-card-section
      class="q-col-gutter-md"
      v-if="!updateCandidate"
    >
      <div class="row justify-center">
        <div class="col-auto">
          <q-btn
            size="xl"
            no-caps
            dense
            color="info"
            :icon="mdiUpdate()"
            label="Check for update"
            @click="checkUpdate"
            :loading="searchUpdate"
          />
        </div>
      </div>
      <div
        v-if="errorMsg"
        class="row justify-center"
      >
        <q-banner
          rounded
          class="bg-negative text-white text-weight-bold"
        >
          {{ errorMsg }}
        </q-banner>
      </div>
    </q-card-section>
    <q-card-section
      v-if="updateCandidate"
    >
      <q-card
        class="bg-card-item-group text-card-item-group full-height text-center"
        :dark="color.cardItemGroupDark"
        flat
        bordered
      >
        <q-card-section
          v-if="updateCandidate.updateAvailable"
          class="row justify-center"
        >
          <div class="col-12 col-sm-10 col-md-8 col-lg-6">
            <div class="row items-center justify-center q-col-gutter-md">
              <div class="col-12 col-md-6">
                <h6>Update available:</h6>
                <p
                  class="text-subtitle2"
                >
                  v{{ updateCandidate.currentVersion }} -> v{{ updateCandidate.newestVersion }}
                </p>
              </div>
              <div class="col-12 col-md-6">
                <q-btn
                  size="xl"
                  no-caps
                  dense
                  color="info"
                  :icon="mdiUpdate()"
                  label="Perform update"
                  @click="reset"
                />
              </div>
            </div>
          </div>
        </q-card-section>
        <q-card-section
          v-else
        >
          <p
            class="text-weight-medium"
          >
            No update available! You are using the newest version. :)
          </p>
        </q-card-section>
      </q-card>
    </q-card-section>
    <q-card-section
    >
      <q-card
        class="bg-card-item-group text-card-item-group full-height"
        :dark="color.cardItemGroupDark"
        flat
        bordered
      >
        <q-card-section
          class="row q-col-gutter-md"
        >
          <div class="col-all text-center q-gutter-sm">
            <p class="text-h5">
              Performing update!
            </p>
            <q-badge
              color="warning"
              class="text-overline"
              style="font-size: 15px"
            >
              Do not power down your system!
            </q-badge>
            <p class="text-body1 text-italic">
              Resources locked! UI reloads automatically!
            </p>
          </div>
          <div class="col-all">
            <q-linear-progress
              :dark="color.cardItemGroupDark"
              query
              reverse
              rounded
              stripe
              color="info"
              size="xl"
            />
          </div>
        </q-card-section>
      </q-card>
    </q-card-section>
  </q-card>
</template>

<script>

import { mapGetters } from 'vuex'
import { mdiUpdate } from '@mdi/js'
import SystemService from 'src/services/system.service'

export default {
  name: 'CSettingsUpdater',
  data () {
    return {
      searchUpdate: false,
      updateCandidate: '',
      errorMsg: ''
    }
  },
  methods: {
    mdiUpdate () {
      return mdiUpdate
    },
    checkUpdate () {
      this.searchUpdate = true
      this.errorMsg = ''
      this.updateCandidate = ''
      SystemService.getCheckUpdate()
        .then(data => {
          this.updateCandidate = data
        })
        .catch(err => {
          this.errorMsg = err?.response?.data?.message
        })
        .finally(() => {
          this.searchUpdate = false
        })
    }
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    })
  }
}
</script>

<style scoped>

</style>
