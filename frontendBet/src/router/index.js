import { createRouter, createWebHistory } from "vue-router";
import RankingView from "@/views/RankingView.vue";
import ScheduleView from "@/views/ScheduleView.vue";
import AddMatchView from "@/views/AddMatchView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: RankingView,
    },
    {
      path: "/schedule",
      name: "schedule",
      component: ScheduleView,
    },
    {
      path: "/addMatch",
      name: "addMatch",
      component: AddMatchView,
    },
  ],
});

export default router;
