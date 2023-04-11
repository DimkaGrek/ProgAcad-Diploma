import { reactive } from "vue";
import translations from "./translations";

const useTranslation = () => {
  const state = reactive({
    locale: "ua",
    translations: translations,
  });

  const setLocale = (locale) => {
    state.locale = locale;
  };

  const t = (key) => {
    return state.translations[state.locale][key];
  };

  return {
    state,
    setLocale,
    t,
  };
};

export default {
  install(app) {
    app.config.globalProperties.$translate = useTranslation();
  },
};
