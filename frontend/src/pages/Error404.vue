<template>
  <div class="window-height window-width row bg-blue items-center text-white">
    <div class="col-12 col-sm-6">
      <div class="text-center">
        <div style="font-size: 24vh">
          404
        </div>
        <div class="text-h2" style="opacity:.4; font-size: 7vh">
          {{ $t('404.message') }}
        </div>

        <q-btn
          class="q-mt-xl"
          color="white"
          text-color="blue"
          size="xl"
          unelevated
          to="/"
          :label="$t('404.go_home_btn_label')"
          no-caps
        />
      </div>
    </div>
    <div class="col-0 col-sm-6 row justify-center">
      <img style="height: 90vh; max-width: inherit; object-fit: contain" src="~assets/cocktailfaces/orange_juice_character_6.svg">
    </div>
  </div>
</template>

<script >
export default {
  name: 'Error404'
}
</script>
