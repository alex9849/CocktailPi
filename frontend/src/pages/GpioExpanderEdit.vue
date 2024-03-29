<template>
  <q-page class="page-content" padding>
    <h5>{{ isNew ? 'Add' : 'Edit' }} I2C GPIO Expander</h5>
    <q-card
      flat
      bordered
      class="q-pa-md bg-card-body text-card-body"
    >
      <q-form
        class="q-col-gutter-md"
        @submit.prevent="saveGpioBoard"
      >
        <div class="col-12">
          <q-card
            flat
            bordered
            class="q-pa-md bg-card-item-group text-card-item-group"
          >
            <c-assistant-container>
              <template v-slot:fields>
                <q-input
                  :dark="color.cardItemGroupDark"
                  label="Board Name"
                  outlined
                  hide-bottom-space
                  v-model:model-value="v.expander.name.$model"
                  :disable="saving"
                />
                <q-select
                  :dark="color.cardItemGroupDark"
                  :disable="!isNew || saving"
                  label="Select board type"
                  v-model:model-value="v.expander.boardModel.$model"
                  :options="i2cExpanderBoardTypes"
                  emit-value
                  map-options
                  outlined
                  hide-bottom-space
                />
              </template>
            </c-assistant-container>
          </q-card>
        </div>
        <div class="col-12">
          <q-card
            flat
            bordered
            class="q-pa-md bg-card-item-group text-card-item-group"
          >
            <c-assistant-container>
              <template v-slot:fields>
                <q-card
                  :dark="color.cardItemGroupDark"
                  class="bg-card-item-group text-card-item-group"
                  bordered
                  flat
                >
                  <q-card-section class="q-gutter-sm">
                    <div
                      class="row justify-end items-center"
                    >
                      <div class="col-grow">
                        <p class="text-weight-medium text-body1">Reachable I2C Devices / Address</p>
                      </div>
                      <div class="col-shrink">
                        <q-btn
                          class="bg-info text-white"
                          :icon="mdiSync"
                          @click="fetchI2cProbe"
                          :disable="i2cProber.loading"
                          dense
                          no-caps
                          label="Re-Check"
                        />
                      </div>
                    </div>
                    <q-card
                      v-if="i2cProber.loading"
                      flat
                      bordered
                      :dark="color.cardItemGroupDark"
                      class="bg-card-item-group text-card-item-group"
                    >
                      <q-card-section class="q-gutter-md">
                        <div class="row justify-center">
                            <q-spinner class="col" size="xl" />
                        </div>
                        <div class="row justify-center">
                          Probing addresses...
                        </div>
                      </q-card-section>
                    </q-card>
                    <q-list
                      :dark="color.cardItemGroupDark"
                      v-if="!i2cProber.loading && i2cProber.probe.length !== 0"
                      bordered
                      separator
                      class="rounded-borders"
                    >
                      <q-item
                        :dark="color.cardItemGroupDark"
                        v-for="i2cDevice in i2cProber.probe"
                        :key="i2cDevice.address"
                        clickable
                        @click="() => {v.expander.address.$model = i2cDevice.address}"
                        :v-ripple="false"
                        :disable="i2cDevice.inUse && i2cDevice.resource.id !== expander.id"
                      >
                        <q-item-section avatar>
                          <q-radio
                            :dark="color.cardItemGroupDark"
                            :model-value="v.expander.address.$model"
                            @update:modelValue="v.expander.address.$model = $event"
                            :val="i2cDevice.address"
                            dense
                            :disable="i2cDevice.inUse && i2cDevice.resource.id !== expander.id"
                          >
                            <template v-slot:default>
                              Address {{ i2cDevice.address }} (0x{{ i2cDevice.address.toString(16) }})
                            </template>
                          </q-radio>
                        </q-item-section>
                        <q-item-section
                          v-if="i2cDevice.inUse && i2cDevice.resource.id !== expander.id"
                          class="text-italic text-grey-7"
                        >
                          (In use)
                        </q-item-section>
                        <q-item-section
                          v-if="i2cDevice.isUse"
                        >
                          <q-item-label caption>
                            In use by {{ i2cDevice.resource.name }}
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                    </q-list>
                    <q-card
                      v-if="!i2cProber.loading && i2cProber.probe.length === 0"
                      flat
                      bordered
                      :dark="color.cardItemGroupDark"
                      class="bg-card-item-group text-card-item-group"
                    >
                      <q-card-section
                        class="flex justify-center items-center"
                      >
                        <p>
                          <q-icon
                            size="md"
                            :name="mdiAlert"
                          />
                          No devices connected to I2C-bus!
                        </p>
                      </q-card-section>
                    </q-card>
                  </q-card-section>
                </q-card>
              </template>
            </c-assistant-container>
          </q-card>
        </div>
        <div class="col">
          <q-card-actions class="q-pa-none">
            <q-btn
              style="width: 100px"
              color="positive"
              label="Save"
              :loading="saving"
              :disable="v.expander.$invalid"
              @click="saveGpioBoard"
            />
            <q-btn
              style="width: 100px"
              color="negative"
              label="Abort"
              :disable="saving"
              @click="$router.push({name: 'gpiomanagement'})"
            />
          </q-card-actions>
        </div>
      </q-form>
    </q-card>
  </q-page>
</template>

<script>

import CAssistantContainer from 'components/CAssistantContainer.vue'
import { mdiAlert, mdiSync } from '@quasar/extras/mdi-v5'
import SystemService from 'src/services/system.service'
import { i2cExpanderBoardTypes } from 'src/mixins/constants'
import GpioService, { gpioBoardDtoMapper } from 'src/services/gpio.service'
import { required } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'
import { mapGetters } from 'vuex'

export default {
  name: 'GpioExpanderEdit',
  created () {
    this.mdiAlert = mdiAlert
    this.mdiSync = mdiSync
    this.fetchI2cProbe()
  },
  async beforeRouteEnter (to, from, next) {
    if (to.name === 'gpioexpandereditor') {
      let board
      try {
        board = await GpioService.getBoard(to.params.id)
      } catch (e) {
        if (e.response.status === 404) {
          next({ name: '404Page' })
          return
        }
      }
      next(vm => {
        vm.expander = board
      })
    }
    next()
  },
  components: { CAssistantContainer },
  mixins: [i2cExpanderBoardTypes],
  data: () => {
    return {
      i2cProber: {
        loading: true,
        probe: []
      },
      saving: false,
      expander: {
        id: -1,
        name: '',
        address: '',
        boardModel: '',
        type: 'i2c'
      }
    }
  },
  setup () {
    return {
      v: useVuelidate()
    }
  },
  methods: {
    saveGpioBoard () {
      if (this.v.expander.$invalid) {
        return
      }
      let promise
      this.saving = true
      if (this.isNew) {
        promise = GpioService.createGpioBoard(gpioBoardDtoMapper.toGpioBoardDto(this.expander))
      } else {
        promise = GpioService.updateGpioBoard(this.expander.id, gpioBoardDtoMapper.toGpioBoardDto(this.expander))
      }
      promise
        .then(() => {
          this.$router.push({ name: 'gpiomanagement' })
        })
        .finally(() => {
          this.saving = false
        })
    },
    fetchI2cProbe () {
      this.i2cProber.loading = true
      return SystemService.getI2CProbe()
        .then(x => {
          this.i2cProber.probe = x
        })
        .finally(() => {
          this.i2cProber.loading = false
        })
    }
  },
  computed: {
    isNew () {
      return this.$route.name === 'gpioexpanderadd'
    },
    ...mapGetters({
      color: 'appearance/getNormalColors'
    })
  },
  validations () {
    return {
      expander: {
        name: {
          required
        },
        address: {
          required
        },
        boardModel: {
          required
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
