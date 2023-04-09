<!-- Чтобы передать переменную show из компонента UserForm в компонент Users, можно использовать 
    Vue Composition API и рефы. -->

<!-- В компоненте Users создайте реф для переменной show: -->

import { ref } from 'vue' export default { setup() { const show = ref(false)
return { show } } }

<!-- В компоненте MyDialog создайте пропс show и передайте его в компонент UserForm: -->

<template>
  <div>
    <UserForm :show="show" />
  </div>
</template>

<script>
export default {
  props: {
    show: {
      type: Boolean,
      required: true,
    },
  },
};
</script>

<!-- В компоненте UserForm принимайте пропс show и передавайте его в реф show: -->

import { ref, watch } from 'vue' export default { props: { show: { type:
Boolean, required: true } }, setup(props) { const show = ref(props.show)
watch(() => props.show, (newVal) => { show.value = newVal }) return { show } } }

<!-- В компоненте MyDialog используйте пропс show для управления переменной show в компоненте Users: -->

<template>
  <div>
    <UserForm :show="show" />
    <button @click="show = true">Open Dialog</button>
    <MyDialog :show="show" @update:show="show = $event" />
  </div>
</template>

<script>
import MyDialog from "./MyDialog.vue";
import UserForm from "./UserForm.vue";

export default {
  components: {
    MyDialog,
    UserForm,
  },
  setup() {
    const show = ref(false);

    return {
      show,
    };
  },
};
</script>

<!-- Теперь при клике на кнопку Cancel или Add в компоненте UserForm, вы можете изменять значение переменной show, 
    которая будет передаваться обратно в компонент Users через событие update:show. -->
