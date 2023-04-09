<!-- Для того, чтобы передать значение переменной show из компонента UserForm в компонент Users 
    с использованием Vue Composition API и ref, можно использовать следующий подход: -->

<!-- Создайте ref в компоненте Users, чтобы хранить значение переменной show: -->

<script>
import { ref } from "vue";

export default {
  setup() {
    const show = ref(false);

    return {
      show,
    };
  },
};
</script>

<!-- В компоненте Users определите метод toggleShow, который будет изменять значение переменной show: -->
<script>
import { ref } from "vue";

export default {
  setup() {
    const show = ref(false);

    const toggleShow = () => {
      show.value = !show.value;
    };

    return {
      show,
      toggleShow,
    };
  },
};
</script>

<!-- В компоненте MyDialog определите проп show, который будет принимать значение переменной show из компонента Users: -->

<script>
import { defineComponent, ref } from "vue";

export default defineComponent({
  props: {
    show: Boolean,
  },
  setup(props) {
    return {
      props,
    };
  },
});
</script>

<!-- В компоненте UserForm определите метод cancel, который будет вызывать метод toggleShow из компонента Users 
    для изменения значения переменной show: -->
<script>
import { defineComponent, ref } from "vue";

export default defineComponent({
  setup() {
    const cancel = () => {
      // Получаем родительский компонент
      const parent = getCurrentInstance().parent;

      // Вызываем метод toggleShow из родительского компонента
      if (parent.toggleShow) {
        parent.toggleShow();
      }
    };

    return {
      cancel,
    };
  },
});
</script>

<!-- В компоненте UserForm определите метод add, который будет вызывать метод toggleShow из компонента Users 
        для изменения значения переменной show: -->
<script>
import { defineComponent, ref } from "vue";

export default defineComponent({
  setup() {
    const add = () => {
      // Получаем родительский компонент
      const parent = getCurrentInstance().parent;

      // Вызываем метод toggleShow из родительского компонента
      if (parent.toggleShow) {
        parent.toggleShow();
      }
    };

    return {
      add,
    };
  },
});
</script>

<!-- В компоненте Users используйте компонент MyDialog и передайте значение переменной show через проп: -->
<template>
  <div>
    <button @click="toggleShow">Открыть модальное окно</button>
    <my-dialog :show="show">
      <user-form />
    </my-dialog>
  </div>
</template>

<script>
import { defineComponent, ref } from "vue";
import MyDialog from "./MyDialog.vue";
import UserForm from "./UserForm.vue";

export default defineComponent({
  components: {
    MyDialog,
    UserForm,
  },
  setup() {
    const show = ref(false);

    const toggleShow = () => {
      show.value = !show.value;
    };

    return {
      show,
      toggleShow,
    };
  },
});
</script>
