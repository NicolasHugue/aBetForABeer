<script>
import { useAuthStore } from "@/stores/auth";
import MatchCard from "@/components/MatchCard.vue";

export default {
  name: "ScheduleView",
  components: { MatchCard },
  data() {
    return {
      auth: useAuthStore(),
      week: 1, // semaine courante par défaut
      matches: [],
      loading: false,
      error: null,
    };
  },
  created() {
    this.fetchMatches();
  },
  methods: {
    currentIsoWeek() {
      // ISO week number
      const d = new Date();
      // copie, UTC
      const date = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
      // jeudi = jour 4 selon ISO
      const dayNum = (date.getUTCDay() + 6) % 7;
      date.setUTCDate(date.getUTCDate() - dayNum + 3);
      const firstThursday = new Date(Date.UTC(date.getUTCFullYear(), 0, 4));
      const weekNum =
        1 +
        Math.round(
          ((date - firstThursday) / 86400000 - 3 + ((firstThursday.getUTCDay() + 6) % 7)) / 7
        );
      return Math.min(Math.max(weekNum, 1), 52);
    },
    async fetchMatches() {
      this.loading = true;
      this.error = null;
      this.matches = [];
      try {
        const res = await fetch(`/api/matches/week/${this.week}`, {
          headers: { ...this.auth.authHeaders() }, // USER/ADMIN requis
        });
        if (!res.ok) throw new Error(await res.text());
        this.matches = await res.json();
      } catch (e) {
        this.error = e.message || "Impossible de charger les matchs.";
      } finally {
        this.loading = false;
      }
    },
    changeWeek(delta) {
      const w = this.week + delta;
      if (w >= 1 && w <= 52) {
        this.week = w;
        this.fetchMatches();
      }
    },
  },
};
</script>

<template>
  <div class="max-w-3xl mx-auto space-y-4">
    <div class="flex items-center justify-between">
      <h2 class="text-xl font-semibold">Calendrier — Semaine {{ week }}</h2>
      <div class="flex items-center gap-2">
        <button class="px-3 py-1 border rounded" @click="changeWeek(-1)" :disabled="week <= 1">
          ←
        </button>
        <input
          type="number"
          min="1"
          max="52"
          class="w-20 border rounded px-2 py-1"
          v-model.number="week"
          @change="fetchMatches"
        />
        <button class="px-3 py-1 border rounded" @click="changeWeek(1)" :disabled="week >= 52">
          →
        </button>
        <button class="px-3 py-1 border rounded" @click="fetchMatches">Aller</button>
      </div>
    </div>

    <div v-if="loading" class="p-3 bg-gray-50 rounded border">Chargement…</div>
    <div v-else-if="error" class="p-3 bg-red-50 text-red-700 rounded border">{{ error }}</div>
    <div v-else-if="matches.length === 0" class="p-3 bg-yellow-50 text-yellow-800 rounded border">
      Aucun match pour cette semaine.
    </div>

    <div class="space-y-2" v-else>
      <MatchCard v-for="m in matches" :key="m.id" :match="m" />
    </div>
  </div>
</template>
