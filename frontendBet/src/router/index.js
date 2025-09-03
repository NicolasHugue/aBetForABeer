import { createRouter, createWebHistory } from "vue-router";
import RankingView from "@/views/RankingView.vue";
import ScheduleView from "@/views/ScheduleView.vue";
import AddMatchView from "@/views/AddMatchView.vue";
import { useAuthStore } from "@/stores/auth";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: RankingView,
      meta: { public: true },
    },
    {
      path: "/schedule",
      name: "schedule",
      component: ScheduleView,
      meta: { roles: ["USER", "ADMIN"] },
    },
    {
      path: "/addMatch",
      name: "addMatch",
      component: AddMatchView,
      meta: { roles: ["ADMIN"] },
    },
  ],
});

router.beforeEach((to) => {
  const auth = useAuthStore();
  const roles = to.meta?.roles;

  // public → toujours OK
  if (!roles) return true;

  // besoin d'être connecté
  if (!auth.isAuthenticated) {
    return { name: "home" };
  }

  // besoin d’un rôle spécifique
  if (!roles.includes(auth.user.role)) {
    return { name: "home" };
  }

  return true;
});

export default router;
