<script>
export default {
  name: "MatchCard",
  props: {
    match: { type: Object, required: true }, // { homeTeamName, awayTeamName, matchDate, status, homeGoals, awayGoals }
  },
  computed: {
    // Affiche heure locale Europe/Brussels
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
    scoreText() {
      // si tu ajoutes status: afficher '–' si pas FINISHED
      if (this.match.status && this.match.status !== "FINISHED") return "–";
      // fallback : si 0/0 et pas de status, on considère non joué => "–"
      if (!this.match.status && this.match.homeGoals === 0 && this.match.awayGoals === 0)
        return "–";
      return `${this.match.homeGoals} - ${this.match.awayGoals}`;
    },
  },
};
</script>

<template>
  <div class="flex items-center justify-between p-3 rounded border bg-white shadow-sm">
    <div class="flex items-center gap-2">
      <span class="font-medium">{{ match.homeTeamName }}</span>
      <span>vs</span>
      <span class="font-medium">{{ match.awayTeamName }}</span>
    </div>
    <div class="text-sm text-gray-600">{{ dateTimeLocal }}</div>
    <div class="text-lg font-semibold min-w-[3rem] text-center">{{ scoreText }}</div>
  </div>
</template>
