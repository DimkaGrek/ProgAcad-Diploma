import { computed, reactive } from "vue";
import { useField } from "./field";

export function useForm(init = {}) {
  const form = reactive({});
  const validKey = "valid";

  for (const [key, value] of Object.entries(init)) {
    console.log(key, value);
    form[key] = useField(value);
  }

  const withoutValid = (k) => k !== validKey;

  // Если хотя бы одно из полей формы имеет ошибку, мы получаем form[validKey] = false,
  form[validKey] = computed(() => {
    return Object.keys(form)
      .filter(withoutValid) // отфильтровуем массив ключей, чтобы там не было поля valid
      .reduce((acc, k) => {
        if (acc === false || form[k].valid === false) {
          return false;
        }
        return true;
      }, true);
  });
  return form;
}
