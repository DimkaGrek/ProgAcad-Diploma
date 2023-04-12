import { createApp } from "vue";
import App from "./App.vue";
import components from "./components/UI";
import translationPlugin from "./hooks/translationPlugin";
import useGlobalState from "./globalState";

import "./assets/style.css";

const app = createApp(App);

components.forEach((component) => app.component(component.name, component));

app.use(translationPlugin);

const globalState = useGlobalState();
app.provide("$globalState", globalState);

app.mount("#app");
