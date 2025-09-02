<script>
import RankingTable from "@/components/RankingTable.vue";

export default {
  components: { RankingTable },
  data() {
    return {
      teams: {},
    };
  },
  methods: {
    async fetchData() {
      const url = "/api/teams/ranking";
      this.teams = await (await fetch(url)).json();
    },
  },
  created() {
    this.fetchData();
  },
};
</script>

<template>
  <h2 class="text-2xl px-20">Classement championnat 3<sup>ème</sup> provincial :</h2>

  <div class="px-20">
    <ranking-table
      :headers="['Equipes', 'PTS', 'M', 'V', 'N', 'D', '+', '-', '+/-']"
      :fields="[
        'name',
        'points',
        'totalMatch',
        'wins',
        'draws',
        'losses',
        'goalsFor',
        'goalsAgainst',
        'goalDifference',
      ]"
      :items="teams"
    >
    </ranking-table>
  </div>
</template>
