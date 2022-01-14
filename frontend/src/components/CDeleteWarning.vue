<template>
  <c-question
    v-model:show="open"
    :loading="deleteLoading"
    :question="headline"
    ok-button-text="Delete"
    ok-color="red"
    @clickAbort="closeDialog"
    @clickOk="onClickDelete"
  >
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
  components: [CQuestion],
  props: {
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
    itemNameSingular: {
      type: String,
      required: true
    },
    itemNamePlural: {
      type: String,
      required: true
    }
  },
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
        promises.push(this.deleteMethod(eventAction))
      })
      Promise.all(promises)
        .then(() => {
          vm.$q.notify({
            type: 'positive',
            message: ((vm.deleteItems.length > 1) ? vm.itemNamePlural : this.itemNameSingular) + ' deleted successfully'
          })
          vm.closeDialog()
          vm.deleteLoading = false
          vm.$emit('deleted')
        }, () => {
          vm.deleteLoading = false
          vm.closeDialog()
        })
    }
  },
  computed: {
    headline () {
      if (this.deleteItems.length === 0) {
        return 'No ' + this.itemNamePlural + '  selected!'
      }
      if (this.deleteItems.length === 1) {
        return 'The following ' + this.itemNamePlural + ' will be deleted:'
      }
      return 'The following ' + this.itemNameSingular + ' will be deleted:'
    }
  }
}
</script>

<style scoped>

</style>
