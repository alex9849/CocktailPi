<template>
  <q-page class="backgroundImage window-height window-width row justify-center items-center">
    <CLoginCard
      @login-success="handleLoginSuccess"
    />
  </q-page>
</template>

<script>
import CLoginCard from 'components/CLoginCard.vue'

export default {
  name: 'Login',
  components: { CLoginCard },
  methods: {
    handleLoginSuccess () {
      if (this.$route.query.redirectTo) {
        this.$router.push(this.$route.query.redirectTo)
      } else {
        this.$router.push({ name: 'dashboard' })
      }
    }
  }
}
</script>

<style>
  @media (max-width: 571px) {
    .backgroundImage {
      background: url('../assets/login/background_s.jpg') no-repeat center center fixed;
      -webkit-background-size: cover;
      -moz-background-size: cover;
      -o-background-size: cover;
      background-size: cover;
    }
  }
  @media (min-width: 572px) and (max-width: 857px) {
    .backgroundImage {
      background: url('../assets/login/background_m.jpg') no-repeat center center fixed;
      -webkit-background-size: cover;
      -moz-background-size: cover;
      -o-background-size: cover;
      background-size: cover;
    }
  }
  @media (min-width: 858px) and (max-width: 1143px) {
    .backgroundImage {
      background: url('../assets/login/background_l.jpg') no-repeat center center fixed;
      -webkit-background-size: cover;
      -moz-background-size: cover;
      -o-background-size: cover;
      background-size: cover;
    }
  }
  @media (min-width: 1144px) {
    .backgroundImage {
      background: url('../assets/login/background_xl.jpg') no-repeat center center fixed;
      -webkit-background-size: cover;
      -moz-background-size: cover;
      -o-background-size: cover;
      background-size: cover;
    }
  }
</style>
