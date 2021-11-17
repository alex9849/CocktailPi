<template>
  <q-page class="row justify-center items-center page-content">
    <b class="quote" style="max-width: 100%">
      {{ randomQuote() }}
    </b>
  </q-page>
</template>

<script>

export default {
  name: 'Dashboard',
  data () {
    return {
      quotes: [
        'What a wonderful day for a cocktail!',
        'Let the evening beGIN...',
        'Keep calm and drink a cocktail!'
      ],
      otherQuotes: [
        'Theres always time for a cocktail!',
        'I\'ll drink to that!',
        'Trust me, you can dance!',
        'You look like you need a drink.',
        'Civilization begins with distillation.',
        'Every good story starts with whisky.'
      ]
    }
  },
  methods: {
    randomQuote () {
      return this.quotes[Math.floor(Math.random() * this.quotes.length)]
    }
  }
}
</script>

<style scoped>
.quote {
  margin-left: -100%;
  margin-right: -100%;
  text-align: center;
  font-size: large;
  max-width: 100%;
}
</style>
