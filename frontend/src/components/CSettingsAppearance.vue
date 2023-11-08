<template>
  <q-card
    flat
    bordered
    class="bg-card-primary q-pa-md"
  >
    <div class="q-col-gutter-md">
      <div class="row">
        <q-card
          class="col"
          flat
          bordered
        >
          <q-card-section>

            <q-select
              label="Language"
              :options="['English', 'German']"
              outlined
              hide-bottom-space
            />
          </q-card-section>
        </q-card>
      </div>
      <div class="row">
        <q-card
          class="col"
          flat
          bordered
        >
          <p class="text-subtitle2 q-pt-sm q-pl-md">Interface colors</p>
          <q-card-section class="q-pt-sm q-gutter-sm">
            <q-input
              outlined
              label="Primary"
              readonly
              :model-value="colors.normal.primary"
            >
              <template v-slot:append>
                <q-btn
                  icon="colorize"
                  label="Pick"
                  no-caps
                  :style="{backgroundColor: colors.normal.primary}"
                >
                  <q-popup-proxy
                    transition-show="scale"
                    transition-hide="scale">
                    <q-color
                      no-header-tabs
                      format-model="hex"
                      v-model:model-value="colors.normal.primary"
                    />
                  </q-popup-proxy>
                </q-btn>
              </template>
            </q-input>
            <q-input
              outlined
              label="Secondary"
              readonly
              :model-value="colors.normal.secondary"
            >
              <template v-slot:append>
                <q-btn
                  icon="colorize"
                  label="Pick"
                  no-caps
                  :style="{backgroundColor: colors.normal.secondary}"
                >
                  <q-popup-proxy
                    transition-show="scale"
                    transition-hide="scale">
                    <q-color
                      no-header-tabs
                      format-model="hex"
                      v-model:model-value="colors.normal.secondary"
                    />
                  </q-popup-proxy>
                </q-btn>
              </template>
            </q-input>
          </q-card-section>
        </q-card>
      </div>
      <div class="row">
        <q-card
          class="col"
          flat
          bordered
        >
          <p class="text-subtitle2 q-pt-sm q-pl-md">Simple view colors</p>
          <q-card-section class="q-pt-sm q-gutter-sm">
            <q-input
              outlined
              label="Primary"
              readonly
              :model-value="colors.simpleView.primary"
            >
              <template v-slot:append>
                <q-btn
                  icon="colorize"
                  label="Pick"
                  no-caps
                  :style="{backgroundColor: colors.simpleView.primary}"
                >
                  <q-popup-proxy
                    transition-show="scale"
                    transition-hide="scale">
                    <q-color
                      no-header-tabs
                      format-model="hex"
                      v-model:model-value="colors.simpleView.primary"
                    />
                  </q-popup-proxy>
                </q-btn>
              </template>
            </q-input>
            <q-input
              outlined
              label="Secondary"
              readonly
              :model-value="colors.simpleView.secondary"
            >
              <template v-slot:append>
                <q-btn
                  icon="colorize"
                  label="Pick"
                  no-caps
                  :style="{backgroundColor: colors.simpleView.secondary}"
                >
                  <q-popup-proxy
                    transition-show="scale"
                    transition-hide="scale">
                    <q-color
                      no-header-tabs
                      format-model="hex"
                      v-model:model-value="colors.simpleView.secondary"
                    />
                  </q-popup-proxy>
                </q-btn>
              </template>
            </q-input>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-card>
</template>

<script>

export default {
  name: 'CSettingsAppearance',
  data () {
    return {
      colors: {
        normal: {
          primary: '#ffcccc',
          secondary: '#ffcccc'
        },
        simpleView: {
          primary: '#ffcccc',
          secondary: '#ffcccc'
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
