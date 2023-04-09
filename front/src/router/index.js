import { createRouter, createWebHistory } from "vue-router";
import CountersView from "../pages/CountersView.vue";
import HomeView from "../pages/HomeView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView,
    },
    {
      path: "/counters",
      name: "counters",
      component: CountersView,
    },
    {
      path: "/arendators",
      name: "arendators",
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import("../pages/ArendatorsView.vue"),
    },
  ],
});

export default router;
