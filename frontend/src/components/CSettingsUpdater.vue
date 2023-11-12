<template>
  <q-card
    bordered
    class="bg-card-body text-card-body"
  >
    <q-card-section
      v-if="!foundUpdate"
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
            :loading="checking"
          >
          </q-btn>
        </div>
      </div>
    </q-card-section>
    <q-card-section
      v-else
    >
      <q-card
        class="bg-card-item-group text-card-item-group full-height"
        :dark="color.cardItemGroupDark"
        flat
        bordered
      >
        <q-card-section
          class="text-center row justify-center"
        >
          <div class="col-12 col-sm-10 col-md-8 col-lg-6">
            <div class="row items-center justify-center q-col-gutter-md">
              <div class="col-12 col-md-6">
                <h6>Update available:</h6>
                <p
                  class="text-subtitle2"
                >
                  v1.0.0 -> v1.2.0
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

export default {
  name: 'CSettingsUpdater',
  data () {
    return {
      checking: false,
      foundUpdate: null
    }
  },
  methods: {
    mdiUpdate () {
      return mdiUpdate
    },
    reset () {
      this.foundUpdate = null
    },
    checkUpdate () {
      this.checkComplete = true
      this.foundUpdate = {
        currentVersion: '1.0.0',
        update: {
          version: '1.5.1'
        }
      }
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
