<template>
  <q-page
    padding
    class="page-content row justify-center items-center"
  >
    <div class="col-12 q-col-gutter-md">
      <div
        v-if="hasCocktailProgress"
        class="row q-col-gutter-md"
      >
        <div class="col-12 q-gutter-y-md">
          <h4 class="text-center">{{ $t('page.simple_cocktail_progress.headline') }}</h4>
          <div class="text-center text-h5" style="display: block ruby">{{ $t('page.simple_cocktail_progress.producing') }}
            <p class="text-italic">{{ cocktailProgress.recipe.name }}</p>
          </div>
          <div
            class="row justify-center"
          >
            <div class="col-12 col-sm-7 col-md-6 text-black">
              <c-cocktail-production-manual-step-card />
            </div>
          </div>
        </div>
      </div>
      <div
        class="row"
        v-else
      >
        <div class="col-12">
          <h4 class="text-center">{{ $t('page.simple_cocktail_progress.no_cocktail_in_progress') }}</h4>
        </div>
      </div>
      <div class="row justify-center q-col-gutter-md">
        <div class="col-auto">
          <q-btn
            size="lg"
            dense
            color="sv-btn-primary"
            :label="$t('page.simple_cocktail_progress.go_back_btn_label')"
            no-caps
            @click="$router.go(-1)"
          />
        </div>
        <div class="col-auto">
          <q-btn
            v-if="hasCocktailProgress"
            class="bg-negative text-white"
            :label="$t('page.simple_cocktail_progress.cancel_btn_label')"
            no-caps
            size="lg"
            dense
            :loading="canceling"
            :disable="cocktailProgress.state === 'CANCELLED' || cocktailProgress.state === 'FINISHED'"
            @click="onCancelCocktail"
          />
        </div>
      </div>
    </div>
  </q-page>
</template>

<script>
import { mapGetters } from 'vuex'
import CocktailService from 'src/services/cocktail.service'
import CCocktailProductionManualStepCard from 'components/CCocktailProductionManualStepCard'

export default {
  name: 'SimpleOrderProgress',
  components: { CCocktailProductionManualStepCard },
  data () {
    return {
      canceling: false
    }
  },
  computed: {
    ...mapGetters({
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      cocktailProgress: 'cocktailProgress/getCocktailProgress'
    })
  },
  methods: {
    onCancelCocktail () {
      this.canceling = true
      CocktailService.cancelCocktail()
        .finally(() => {
          this.canceling = false
        })
    }
  }
}
</script>

<style scoped>

</style>
