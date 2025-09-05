<script>
import { useAuthStore } from "@/stores/auth";

export default {
  name: "MatchCard",
  props: { match: { type: Object, required: true } },
  emits: ["score-updated"],
  data() {
    return {
      auth: useAuthStore(),

      // Mon pari (si existe)
      myBet: null, // { username, homeGoals, awayGoals, createdAt }
      myBetLoading: false,
      myBetError: null,

      // Formulaire de pari
      bet: { homeGoals: 0, awayGoals: 0 },
      betLoading: false,
      betError: null,
      betPlacedMsg: null,

      // Liste des paris
      bets: [],
      betsLoading: false,
      betsError: null,

      // Admin : saisie du score
      scoreOpen: false,
      score: { homeGoals: 0, awayGoals: 0 },
      scoreLoading: false,
      scoreError: null,
    };
  },
  computed: {
    started() {
      try {
        return new Date() >= new Date(this.match.matchDate);
      } catch {
        return false;
      }
    },
    blockedTeams() {
      return (
        this.match.homeTeamId === 1 ||
        this.match.awayTeamId === 1 ||
        this.match.homeTeamName === "U.S. Ploegsteert-Bizet" ||
        this.match.awayTeamName === "U.S. Ploegsteert-Bizet"
      );
    },
    canBet() {
      return this.auth.isAuthenticated && !this.started && !this.blockedTeams && !this.myBet;
    },
    scoreText() {
      if (this.match.homeGoals === 0 && this.match.awayGoals === 0 && !this.started) return "–";
      return `${this.match.homeGoals} - ${this.match.awayGoals}`;
    },
    dateTimeLocal() {
      try {
        const d = new Date(this.match.matchDate);
        return d.toLocaleString("fr-BE", {
          timeZone: "Europe/Brussels",
          weekday: "short",
          year: "numeric",
          month: "2-digit",
          day: "2-digit",
          hour: "2-digit",
          minute: "2-digit",
        });
      } catch {
        return this.match.matchDate;
      }
    },
  },
  created() {
    this.fetchMyBet();
    if (this.started) this.loadBets();
  },
  watch: {
    // Quand le match démarre (changement d’état), charge la liste
    started(newVal) {
      if (newVal) this.loadBets();
    },
  },
  methods: {
    async fetchMyBet() {
      if (!this.auth.isAuthenticated) return;
      this.myBetLoading = true;
      this.myBetError = null;
      try {
        const res = await fetch(`/api/bets/match/${this.match.id}/me`, {
          headers: { ...this.auth.authHeaders() },
        });
        if (res.status === 204) {
          this.myBet = null;
        } else if (res.ok) {
          this.myBet = await res.json();
        } else {
          throw new Error(await res.text());
        }
      } catch (e) {
        this.myBetError = e.message || "Impossible de récupérer votre pari";
      } finally {
        this.myBetLoading = false;
      }
    },

    async placeBet() {
      this.betError = null;
      this.betPlacedMsg = null;
      if (!this.canBet) return;
      this.betLoading = true;
      try {
        const res = await fetch("/api/bets", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            ...this.auth.authHeaders(),
          },
          body: JSON.stringify({
            matchId: this.match.id,
            homeGoals: Number(this.bet.homeGoals),
            awayGoals: Number(this.bet.awayGoals),
          }),
        });
        if (!res.ok) throw new Error(await res.text());
        this.betPlacedMsg = "Pari enregistré ✅";
        // Mets à jour "mon pari" localement (pas besoin d’attendre le re-fetch)
        this.myBet = {
          id: 0,
          username: this.auth.user.username,
          homeGoals: this.bet.homeGoals,
          awayGoals: this.bet.awayGoals,
          createdAt: new Date().toISOString(),
        };
      } catch (e) {
        this.betError = e.message || "Échec de l'enregistrement du pari";
      } finally {
        this.betLoading = false;
      }
    },

    async loadBets() {
      // Liste complète : visible seulement après coup d’envoi (le backend refusera avant)
      this.betsLoading = true;
      this.betsError = null;
      try {
        const res = await fetch(`/api/bets/match/${this.match.id}`, {
          headers: { ...this.auth.authHeaders() },
        });
        if (!res.ok) throw new Error(await res.text());
        this.bets = await res.json();
      } catch (e) {
        this.betsError = e.message || "Impossible de charger les paris";
      } finally {
        this.betsLoading = false;
      }
    },

    async saveScore() {
      if (!this.auth.isAdmin) return;
      if (!confirm("Confirmer la mise à jour du score ?")) return;

      this.scoreLoading = true;
      this.scoreError = null;
      try {
        const res = await fetch("/api/matches/score", {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            ...this.auth.authHeaders(),
          },
          body: JSON.stringify({
            matchId: this.match.id,
            homeGoals: Number(this.score.homeGoals),
            awayGoals: Number(this.score.awayGoals),
          }),
        });
        if (!res.ok) throw new Error(await res.text());

        // Émettre l’évènement vers le parent (ne pas muter la prop)
        this.$emit("score-updated", {
          matchId: this.match.id,
          homeGoals: this.score.homeGoals,
          awayGoals: this.score.awayGoals,
        });

        this.scoreOpen = false;
      } catch (e) {
        this.scoreError = e.message || "Impossible d'enregistrer le score";
      } finally {
        this.scoreLoading = false;
      }
    },
  },
};
</script>

<template>
  <div class="p-3 rounded border bg-white shadow-sm space-y-2">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-2">
        <span class="font-medium">{{ match.homeTeamName }}</span>
        <span>vs</span>
        <span class="font-medium">{{ match.awayTeamName }}</span>
      </div>
      <div class="text-sm text-gray-600">{{ dateTimeLocal }}</div>
      <div class="text-lg font-semibold min-w-[3rem] text-center">{{ scoreText }}</div>
    </div>

    <!-- Toujours afficher la section "Paris" -->
    <div class="space-y-2">
      <!-- Mon pari (si présent) -->
      <div v-if="myBet" class="text-sm">
        <span class="text-gray-600">Votre pari :</span>
        <span class="font-semibold">{{ myBet.homeGoals }} - {{ myBet.awayGoals }}</span>
      </div>

      <!-- Formulaire de pari (seulement si autorisé & pas déjà parié) -->
      <div v-if="canBet" class="flex items-center gap-2">
        <label class="text-sm text-gray-600">Parier :</label>
        <input
          type="number"
          min="0"
          class="w-16 border rounded px-2 py-1"
          v-model.number="bet.homeGoals"
        />
        <span>-</span>
        <input
          type="number"
          min="0"
          class="w-16 border rounded px-2 py-1"
          v-model.number="bet.awayGoals"
        />
        <button
          class="px-2 py-1 bg-gray-900 text-white rounded"
          :disabled="betLoading"
          @click="placeBet"
        >
          {{ betLoading ? "..." : "Valider" }}
        </button>
        <span v-if="betPlacedMsg" class="text-green-700 text-sm">{{ betPlacedMsg }}</span>
        <span v-if="betError" class="text-red-700 text-sm">{{ betError }}</span>
      </div>

      <!-- Message si on ne peut pas parier -->
      <div
        v-else-if="auth.isAuthenticated && !myBet && (started || blockedTeams)"
        class="text-xs text-gray-500"
      >
        {{ started ? "Paris fermés (match commencé)" : "Paris bloqués pour ce match" }}
      </div>

      <!-- Liste des paris (toujours visible, mais contenu caché avant le début) -->
      <div class="pt-1 border-t">
        <div class="text-sm font-medium mb-1">Paris des utilisateurs</div>

        <div v-if="!started" class="text-xs text-gray-500">
          Les paris seront visibles après le début du match.
        </div>

        <div v-else>
          <div v-if="betsLoading" class="text-sm text-gray-500">Chargement…</div>
          <div v-else-if="betsError" class="text-sm text-red-700">{{ betsError }}</div>
          <ul v-else class="space-y-1">
            <li v-for="b in bets" :key="b.id" class="text-sm flex justify-between">
              <span class="font-medium">{{ b.username }}</span>
              <span>{{ b.homeGoals }} - {{ b.awayGoals }}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Admin : saisie du score -->
    <div v-if="auth.isAdmin" class="pt-2">
      <button class="px-2 py-1 border rounded" @click="scoreOpen = !scoreOpen">
        {{ scoreOpen ? "Annuler" : "Saisir le score" }}
      </button>
      <div v-if="scoreOpen" class="mt-2 flex items-center gap-2">
        <input
          type="number"
          min="0"
          class="w-16 border rounded px-2 py-1"
          v-model.number="score.homeGoals"
        />
        <span>-</span>
        <input
          type="number"
          min="0"
          class="w-16 border rounded px-2 py-1"
          v-model.number="score.awayGoals"
        />
        <button
          class="px-2 py-1 bg-gray-900 text-white rounded"
          :disabled="scoreLoading"
          @click="saveScore"
        >
          {{ scoreLoading ? "..." : "Confirmer" }}
        </button>
        <span v-if="scoreError" class="text-red-700 text-sm">{{ scoreError }}</span>
      </div>
    </div>
  </div>
</template>
