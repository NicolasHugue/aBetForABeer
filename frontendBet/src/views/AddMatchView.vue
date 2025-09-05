<script>
import { useAuthStore } from "@/stores/auth";
import { useRouter } from "vue-router";

export default {
  data() {
    return {
      auth: useAuthStore(),
      router: useRouter(),

      teams: [],
      form: {
        homeTeamId: null,
        awayTeamId: null,
        week: 1,
        dateTimeLocal: "",
      },
      loading: false,
      message: null,
      error: null,
    };
  },
  computed: {
    canSubmit() {
      return (
        this.form.homeTeamId &&
        this.form.awayTeamId &&
        this.form.homeTeamId !== this.form.awayTeamId &&
        this.form.week >= 1 &&
        this.form.dateTimeLocal
      );
    },
  },
  mounted() {
    if (!this.auth.isAdmin) {
      this.router.replace({ name: "home" });
      return;
    }
    this.fetchTeams();
  },
  methods: {
    async fetchTeams() {
      try {
        const res = await fetch("/api/teams");
        this.teams = await res.json();
      } catch (e) {
        this.error = "Impossible de charger les équipes. " + e;
      }
    },
    toIsoOffset(dtLocal) {
      const d = new Date(dtLocal);
      return d.toISOString();
    },
    async submit() {
      this.error = null;
      this.message = null;
      if (!this.canSubmit) {
        this.error = "Formulaire incomplet ou invalide.";
        return;
      }
      this.loading = true;
      try {
        const payload = {
          homeTeamId: Number(this.form.homeTeamId),
          awayTeamId: Number(this.form.awayTeamId),
          week: Number(this.form.week),
          matchDate: this.toIsoOffset(this.form.dateTimeLocal),
        };
        const res = await fetch("/api/matches", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            ...this.auth.authHeaders(),
          },
          body: JSON.stringify(payload),
        });
        if (!res.ok) {
          const txt = await res.text();
          throw new Error(txt || "Erreur serveur");
        }
        this.message = "Match créé avec succès.";
        this.form.homeTeamId = null;
        this.form.awayTeamId = null;
      } catch (e) {
        this.error = e.message || "Echec de création.";
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<template>
  <div class="max-w-2xl mx-auto bg-white rounded-lg shadow-lg p-6">
    <h2 class="text-2xl font-semibold mb-4">Ajouter un match</h2>

    <div v-if="error" class="mb-3 p-2 bg-red-50 text-red-700 rounded">{{ error }}</div>
    <div v-if="message" class="mb-3 p-2 bg-green-50 text-green-700 rounded">{{ message }}</div>

    <form class="space-y-4" @submit.prevent="submit">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium mb-1">Équipe à domicile</label>
          <select class="w-full border rounded px-2 py-1" v-model="form.homeTeamId" required>
            <option :value="null" disabled>-- Sélectionner --</option>
            <option v-for="t in teams" :key="t.id" :value="t.id">{{ t.name }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium mb-1">Équipe à l'extérieur</label>
          <select class="w-full border rounded px-2 py-1" v-model="form.awayTeamId" required>
            <option :value="null" disabled>-- Sélectionner --</option>
            <option v-for="t in teams" :key="t.id" :value="t.id">{{ t.name }}</option>
          </select>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium mb-1">Semaine</label>
          <input
            type="number"
            min="1"
            max="52"
            class="w-full border rounded px-2 py-1"
            v-model="form.week"
            required
          />
        </div>
        <div>
          <label class="block text-sm font-medium mb-1">Date & heure</label>
          <input
            type="datetime-local"
            class="w-full border rounded px-2 py-1"
            v-model="form.dateTimeLocal"
            required
          />
        </div>
      </div>

      <div class="flex justify-end">
        <button
          :disabled="!canSubmit || loading"
          class="px-4 py-2 bg-gray-900 text-white rounded disabled:opacity-60 cursor-pointer"
        >
          {{ loading ? "Enregistrement..." : "Créer le match" }}
        </button>
      </div>
    </form>
  </div>
</template>
