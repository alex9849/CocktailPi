<template>
  <q-dialog
    :value="value"
    @input="$emit('input', $event)"
    :persistent="saving"
    @hide="$emit('clickAbort')"
  >
    <q-card class="width-desktop">
      <q-card-section class="text-center">
        <h5
          v-if="title !== ''"
          style="margin-bottom: 10px"
        >
          {{ title }}
        </h5>
        <q-banner
          v-if="errorMessage !== ''"
          rounded
          dense
          class="text-white bg-red-5"
          style="margin: 10px"
        >
          {{ errorMessage }}
        </q-banner>
        <q-form
          class="innerpadding"
          @submit.prevent="$emit('clickSave')"
        >
          <slot/>
          <div class="q-pa-md q-gutter-sm">
            <q-btn
              color="grey"
              label="Abort"
              style="width: 150px"
              :disable="saving"
              @click="() => {$emit('input', false); $emit('clickAbort')}"
            />
            <q-btn
              color="green"
              label="Save"
              type="submit"
              style="width: 150px"
              :disable="!valid"
              :loading="saving"
            />
          </div>
        </q-form>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script>
export default {
  name: "CEditDialog",
  props: {
    value: {
      type: Boolean,
      required: true
    },
    title: {
      type: String,
      default: ''
    },
    saving: {
      type: Boolean,
      default: false
    },
    valid: {
      type: Boolean,
      default: true
    },
    errorMessage: {
      type: String,
      default: ''
    }
  }
}
</script>

<style scoped>
  @media screen and (min-width: 600px) {
    .width-desktop {
      width: 500px;
    }
  }
</style>
