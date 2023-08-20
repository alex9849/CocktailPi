<template>
  <q-page class="page-content" padding>
    <h5>GPIO</h5>

    <div class="row q-col-gutter-md reverse">
      <div class="col-12 col-md-4 col-lg-3">
        <q-card class="bg-card-primary">
          <q-card-section class="q-py-sm bg-card-secondary">
            <p class="text-weight-medium">Status</p>
          </q-card-section>
          <q-separator :value="50" />
          <q-card-section class="q-py-md">
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-6 col-md-12">
                <q-card
                  flat bordered class="bg-grey-2"
                >
                  <q-card-section class="q-py-xs bg-cyan-1">
                    <p class="text-weight-medium">Pin usage</p>
                  </q-card-section>
                  <q-separator />
                  <q-card-section
                    v-if="gpioStatus.loading"
                    class="row q-py-sm q-col-gutter-xs"
                  >
                    <q-skeleton class="col-12 col-sm-6 col-md-12" type="text" />
                    <q-skeleton class="col-12 col-sm-6 col-md-12" type="text" />
                  </q-card-section>
                  <q-card-section
                    v-else
                    class="row q-py-sm q-col-gutter-xs"
                  >
                    <p class="col-12 col-sm-6 col-md-12">Pins usage: <q-badge>{{ gpioStatus.data.pinsUsed }}/{{gpioStatus.data.pinsAvailable}}</q-badge></p>
                    <p class="col-12 col-sm-6 col-md-12">GPIO boards: <q-badge>{{ gpioStatus.data.boardsAvailable}}</q-badge></p>
                  </q-card-section>
                </q-card>
              </div>
              <div class="col-12 col-sm-6 col-md-12">
                <q-card flat bordered class="bg-grey-2">
                  <q-card-section class="q-py-xs q-pr-xs bg-cyan-1 row items-center">
                    <div class="col">
                      <p class="text-weight-medium">I2C</p>
                    </div>
                    <div class="col-shrink">
                      <q-btn
                        color="info"
                        label="Configure"
                        :icon="mdiPencilOutline"
                        @click="$router.push({name: 'i2cmanagement'})"
                        dense
                        no-caps
                        size="sm"
                      />
                    </div>
                  </q-card-section>
                  <q-separator />
                  <q-card-section class="row q-py-sm q-col-gutter-xs">
                    <q-skeleton
                      v-if="i2cStatus.loading"
                      class="col-12 col-sm-6 col-md-12" type="text"
                    />
                    <p
                      v-else
                      class="col-12 col-sm-6 col-md-12"
                    >
                      Status:
                      <q-badge
                        :class="{'bg-negative': !i2cStatus.data.enable , 'bg-positive': i2cStatus.data.enable}"
                      >
                        {{i2cStatus.data.enable ? 'enabled' : 'disabled'}}
                      </q-badge>
                    </p>
                  </q-card-section>
                  <q-separator
                    v-if="i2cStatus.data.enable"
                  />
                  <q-card-section
                    v-if="i2cStatus.data.enable"
                    class="row q-py-sm q-col-gutter-xs"
                  >
                    <div class="col-12">
                      <p>SDA-Pin: {{ i2cStatus.data.sdaPin?.nr }}</p>
                      <p>SCL-Pin: {{ i2cStatus.data.sclPin?.nr }}</p>
                    </div>
                  </q-card-section>
                </q-card>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-md-8 col-lg-9 q-gutter-y-md">
        <div class="row">
          <div class="col-12">
            <q-card class="bg-card-primary">
              <q-card-section class="q-py-sm bg-card-secondary">
                <p class="text-weight-medium">Local GPIOs:</p>
              </q-card-section>
              <q-separator />
              <q-card-section class="">
                <div class="row q-col-gutter-sm justify-center">
                  <div
                    v-if="localBoards.loading"
                  >
                    <q-spinner
                      size="xl"
                      class="text-info q-ma-md"
                    />
                  </div>
                  <div class="col-12"
                       v-else-if="localBoards.boards.length === 0"
                  >
                    <q-card
                      flat
                      bordered
                    >
                      <q-card-section>
                        <p>
                          <q-icon
                            :name="mdiAlert"
                            size="sm"
                          /> No boards found!
                        </p>
                      </q-card-section>
                    </q-card>
                  </div>
                  <div
                    v-else
                    class="col-12"
                    v-for="localBoard in localBoards.boards"
                    :key="localBoard.id"
                  >
                    <c-gpio-expander-expansion-item
                      non-editable
                      pin-prefix="BCM "
                      :board="localBoard"
                    />
                  </div>
                </div>
              </q-card-section>
            </q-card>
          </div>
        </div>
        <div class="row">
          <div class="col-12">
            <q-card class="bg-card-primary">
              <q-card-section class="q-py-sm bg-card-secondary">
                <div class="row items-center">
                  <p class="col text-weight-medium">I2C GPIO Expanders:</p>
                  <div class="col-shrink">
                    <q-btn
                      label="Add"
                      color="positive"
                      @click="$router.push({name: 'gpioexpanderadd'})"
                      :icon="mdiPlusCircleOutline"
                      no-caps
                      :disable="!i2cStatus.data?.enable"
                    />
                  </div>
                </div>
              </q-card-section>
              <q-separator :value="10" />
              <q-card-section class="">
                <div class="row q-col-gutter-sm justify-center">
                  <div
                    v-if="i2cBoards.loading"
                  >
                    <q-spinner
                      size="xl"
                      class="text-info q-ma-md"
                    />
                  </div>
                  <div class="col-12"
                       v-else-if="!i2cStatus.data.enable"
                  >
                    <q-card
                      flat
                      bordered
                    >
                      <q-card-section>
                        <p>
                          <q-icon
                            :name="mdiAlert"
                            size="sm"
                          /> I2C disabled!
                        </p>
                      </q-card-section>
                    </q-card>
                  </div>
                  <div class="col-12"
                       v-else-if="i2cBoards.boards.length === 0"
                  >
                    <q-card
                      flat
                      bordered
                    >
                      <q-card-section>
                        <p>
                          <q-icon
                            :name="mdiAlert"
                            size="sm"
                          /> No expanders found!
                        </p>
                      </q-card-section>
                    </q-card>
                  </div>
                  <div class="col-12"
                       v-else
                       :key="board.id"
                       v-for="board in i2cBoards.boards"
                  >
                    <c-gpio-expander-expansion-item
                      :board="board"
                      @clickDelete="onClickDelete(board)"
                    />
                  </div>
                </div>
              </q-card-section>
            </q-card>
          </div>
        </div>
      </div>
    </div>
    <c-question
      :question="'Delete \'' + deleteDialog.board?.name + '\'?'"
      :show="deleteDialog.show"
      ok-color="negative"
      ok-button-text="Delete"
      @clickOk="onConfirmDelete"
      @clickAbort="abortDelete"
    >
      <template
        v-if="deleteDialog.errorMessage"
      >
        <q-banner
          class="bg-negative text-white"
          rounded
          dense
        >
          {{ deleteDialog.errorMessage }}
        </q-banner>
      </template>
    </c-question>
  </q-page>

</template>

<script>
import { mdiAlert, mdiDelete, mdiPencilOutline, mdiPlusCircleOutline } from '@quasar/extras/mdi-v5'
import CGpioExpanderExpansionItem from 'components/gpiosetup/CGpioExpanderExpansionItem.vue'
import GpioService from 'src/services/gpio.service'
import SystemService from 'src/services/system.service'
import CQuestion from 'components/CQuestion.vue'
export default {
  name: 'GpioManagement',
  data: () => {
    return {
      deleteDialog: {
        board: null,
        show: false,
        errorMessage: ''
      },
      localBoards: {
        loading: true,
        boards: []
      },
      i2cBoards: {
        loading: true,
        boards: []
      },
      gpioStatus: {
        loading: true,
        data: {
          pinsUsed: '',
          pinsAvailable: '',
          boardsAvailable: ''
        }
      },
      i2cStatus: {
        loading: true,
        data: {
          enable: false,
          sdlPin: '',
          scaPin: ''
        }
      }
    }
  },
  components: { CQuestion, CGpioExpanderExpansionItem },
  created () {
    this.mdiAlert = mdiAlert
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiPlusCircleOutline = mdiPlusCircleOutline
    this.localBoards.loading = true
    GpioService.getBoardsByType(GpioService.types.LOCAL)
      .then(x => {
        this.localBoards.boards = x
      }).finally(() => {
        this.localBoards.loading = false
      })
    this.gpioStatus.loading = true
    GpioService.getGpioStatus()
      .then(x => {
        this.gpioStatus.data = x
      })
      .finally(() => {
        this.gpioStatus.loading = false
      })
    this.i2cStatus.loading = true
    SystemService.getI2cSettings()
      .then(x => {
        this.i2cStatus.data = x
      })
      .finally(() => {
        this.i2cStatus.loading = false
      })
    this.fetchI2cBoards()
  },
  methods: {
    fetchI2cBoards () {
      this.i2cBoards.loading = true
      GpioService.getBoardsByType(GpioService.types.I2C)
        .then(x => {
          this.i2cBoards.boards = x
        }).finally(() => {
          this.i2cBoards.loading = false
        })
    },
    onClickDelete (board) {
      this.deleteDialog.board = board
      this.deleteDialog.show = true
    },
    abortDelete () {
      this.deleteDialog.board = null
      this.deleteDialog.show = false
      this.deleteDialog.errorMessage = ''
    },
    onConfirmDelete () {
      GpioService.deleteGpioBoard(this.deleteDialog.board.id)
        .then(() => {
          this.fetchI2cBoards()
          this.abortDelete()
        })
        .catch(err => {
          this.deleteDialog.errorMessage = err?.response?.data?.message
        })
    }
  }
}

</script>

<style scoped>

</style>
