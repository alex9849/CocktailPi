<template>
  <q-page class="page-content" padding>
    <h5>{{ $t('page.gpio_mgmt.headline') }}</h5>

    <div class="row q-col-gutter-md reverse">
      <div class="col-12 col-md-4 col-lg-3">
        <q-card class="bg-card-body text-card-body">
          <q-card-section class="q-py-sm bg-card-header text-card-header">
            <p class="text-weight-medium">{{ $t('page.gpio_mgmt.status_box.headline') }}</p>
          </q-card-section>
          <q-separator />
          <q-card-section class="q-py-md">
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-6 col-md-12">
                <q-card
                  :dark="color.cardHeaderDark"
                  flat
                  bordered
                  class="bg-card-item-group text-card-item-group"
                >
                  <q-card-section class="q-py-xs bg-card-header text-card-header">
                    <p class="text-weight-medium">{{ $t('page.gpio_mgmt.status_box.pin_box.headline') }}</p>
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
                    <p class="col-12 col-sm-6 col-md-12">
                      {{ $t('page.gpio_mgmt.status_box.pin_box.pin_usage') }}
                      <q-badge>
                        {{ gpioStatus.data.pinsUsed }}/{{gpioStatus.data.pinsAvailable}}
                      </q-badge>
                    </p>
                    <p class="col-12 col-sm-6 col-md-12">
                      {{ $t('page.gpio_mgmt.status_box.pin_box.gpio_boards') }}
                      <q-badge>
                        {{ gpioStatus.data.boardsAvailable}}
                      </q-badge>
                    </p>
                  </q-card-section>
                </q-card>
              </div>
              <div class="col-12 col-sm-6 col-md-12">
                <q-card
                  flat
                  bordered
                  :dark="color.cardHeaderDark"
                  class="bg-card-item-group text-card-item-group"
                >
                  <q-card-section
                    class="q-py-xs q-pr-xs bg-cyan-1 row items-center bg-card-header text-card-header"
                  >
                    <div class="col">
                      <p class="text-weight-medium">{{ $t('page.gpio_mgmt.status_box.i2c_box.headline') }}</p>
                    </div>
                    <div class="col-shrink">
                      <q-btn
                        color="info"
                        :label="$t('page.gpio_mgmt.status_box.i2c_box.configure_btn_label')"
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
                      {{ $t('page.gpio_mgmt.status_box.i2c_box.status') }}
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
                      <p>{{ $t('page.gpio_mgmt.status_box.i2c_box.sda_pin') }} {{ i2cStatus.data.sdaPin?.nr }}</p>
                      <p>{{ $t('page.gpio_mgmt.status_box.i2c_box.scl_pin') }} {{ i2cStatus.data.sclPin?.nr }}</p>
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
            <q-card class="bg-card-body text-card-body">
              <q-card-section class="q-py-sm bg-card-header text-card-header">
                <p class="text-weight-medium">{{ $t('page.gpio_mgmt.local_gpio_box.headline') }}</p>
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
                          />{{ $t('page.gpio_mgmt.local_gpio_box.no_boards_found') }}
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
            <q-card class="bg-card-body text-card-body">
              <q-card-section class="q-py-sm bg-card-header text-card-header">
                <div class="row items-center">
                  <p class="col text-weight-medium">{{ $t('page.gpio_mgmt.i2c_expander_box.headline') }}</p>
                  <div class="col-shrink">
                    <q-btn
                      :label="$t('page.gpio_mgmt.i2c_expander_box.add_btn_label')"
                      :color="i2cStatus.data?.enable ? 'positive': 'grey'"
                      @click="$router.push({name: 'gpioexpanderadd'})"
                      :icon="mdiPlusCircleOutline"
                      no-caps
                      :disable="!i2cStatus.data?.enable"
                    />
                  </div>
                </div>
              </q-card-section>
              <q-separator />
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
                      class="bg-card-item-group text-card-item-group"
                    >
                      <q-card-section>
                        <p>
                          <q-icon
                            :name="mdiAlert"
                            size="sm"
                          /> {{ $t('page.gpio_mgmt.i2c_expander_box.i2c_disabled') }}
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
                      class="bg-card-item-group text-card-item-group"
                    >
                      <q-card-section>
                        <p>
                          <q-icon
                            :name="mdiAlert"
                            size="sm"
                          /> {{ $t('page.gpio_mgmt.i2c_expander_box.no_expanders_found') }}
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
      :question="$t('page.gpio_mgmt.delete_dialog.headline', {boardName: deleteDialog.board?.name})"
      :show="deleteDialog.show"
      @update:show="abortDelete"
      ok-color="negative"
      :ok-button-text="$t('page.gpio_mgmt.delete_dialog.ok_btn_label')"
      :abort-button-text="$t('page.gpio_mgmt.delete_dialog.abort_btn_label')"
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
import { mapGetters } from 'vuex'
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
    this.i2cStatus.loading = true
    SystemService.getI2cSettings()
      .then(x => {
        this.i2cStatus.data = x
      })
      .finally(() => {
        this.i2cStatus.loading = false
      })
    this.fetchI2cBoards()
    this.fetchGpiostatus()
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
    fetchGpiostatus () {
      this.gpioStatus.loading = true
      GpioService.getGpioStatus()
        .then(x => {
          this.gpioStatus.data = x
        })
        .finally(() => {
          this.gpioStatus.loading = false
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
          this.fetchGpiostatus()
          this.fetchI2cBoards()
          this.abortDelete()
        })
        .catch(err => {
          this.deleteDialog.errorMessage = err?.response?.data?.message
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
