<script>
import DocEnglish from "./DocEnglish.vue";
import DocUkrainian from "./DocUkrainian.vue";
import DocRussian from "./DocRussian.vue";

export default {
  components: {
    DocEnglish,
    DocUkrainian,
    DocRussian,
  },
};
</script>

<template>
  <div class="container">
    <div class="menu-space"></div>
    <DocEnglish v-if="$translate.state.locale === 'en'" />
    <DocUkrainian v-if="$translate.state.locale === 'ua'" />
    <DocRussian v-if="$translate.state.locale === 'ru'" />
  </div>
</template>

<style scoped></style>
