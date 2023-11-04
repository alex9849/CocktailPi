<template>
  <c-question
    v-model:show="open"
    :loading="deleteLoading"
    :question="internalHeadline"
    ok-button-text="Delete"
    ok-color="red"
    @clickAbort="closeDialog"
    @clickOk="onClickDelete"
  >
    <template v-slot:error-area>
      <div v-if="bannerWarning && deleteItems.length !== 0">
        <q-banner class="text-white bg-red-5"
                  dense
                  rounded
                  style="margin: 10px"
        >
          {{ bannerWarning }}
        </q-banner>
      </div>
    </template>
    <template v-slot:buttons>
      <q-btn
        v-if="deleteItems.length === 0"
        color="grey"
        style="width: 150px"
        @click="closeDialog"
      >
        Ok
      </q-btn>
    </template>
    <template v-slot:default>
      <ul style="padding: 0; list-style: none">
        <li
          v-for="(deleteItem, index) in deleteItems"
          :key="index"
        >
          {{ listPointMethod(deleteItem) }}
        </li>
      </ul>
    </template>
  </c-question>
</template>

<script>

import CQuestion from 'components/CQuestion'

export default {
  name: 'CDeleteWarning',
  components: { CQuestion },
  props: {
    bannerWarning: {
      type: String,
      required: false
    },
    deleteMethod: {
      type: Function,
      required: true
    },
    idGetterMethod: {
      type: Function,
      default: x => x.id
    },
    listPointMethod: {
      type: Function,
      required: true
    },
    headline: {
      type: String,
      required: true
    }
  },
  emits: ['deleteSuccess', 'deleteFailure'],
  data () {
    return {
      deleteLoading: false,
      open: false,
      deleteItems: []
    }
  },
  methods: {
    openForItems (items) {
      if (items) {
        this.deleteItems.push(...items)
      }
      this.open = true
    },
    closeDialog () {
      this.deleteItems.splice(0, this.deleteItems.length)
      this.open = false
    },
    onClickDelete () {
      this.deleteLoading = true
      const vm = this
      const promises = []
      this.deleteItems.forEach(eventAction => {
        promises.push(this.deleteMethod(this.idGetterMethod(eventAction)))
      })
      Promise.all(promises)
        .then(() => {
          vm.$q.notify({
            type: 'positive',
            message: ((vm.deleteItems.length > 1) ? vm.itemNamePlural : this.itemNameSingular) + ' deleted successfully'
          })
          vm.closeDialog()
          vm.deleteLoading = false
          vm.$emit('deleteSuccess')
        }, () => {
          vm.deleteLoading = false
          vm.closeDialog()
          vm.$emit('deleteFailure')
        })
    }
  },
  computed: {
    internalHeadline () {
      if (this.deleteItems.length === 0) {
        return 'Nothing selected!'
      }
      return this.headline
    }
  }
}
</script>

<style scoped>

</style>
