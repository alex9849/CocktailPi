<template>
  <q-dialog
    :model-value="show"
    @update:model-value="$emit('update:show', $event)"
    :persistent="saving"
    @hide="$emit('clickAbort')"
  >
    <q-card class="full-width">
      <q-card-section class="text-center q-gutter-y-md">
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
        >
          {{ errorMessage }}
        </q-banner>
        <q-form
          @submit.prevent="$emit('clickSave')"
        >
          <slot/>
          <div class="q-pa-md q-gutter-sm">
            <q-btn
              color="grey"
              label="Abort"
              style="width: 150px"
              :disable="saving"
              @click="() => {$emit('update:show', false); $emit('clickAbort')}"
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
  name: 'CEditDialog',
  props: {
    show: {
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
  },
  emits: ['update:show', 'clickAbort', 'clickSave']
}
</script>
