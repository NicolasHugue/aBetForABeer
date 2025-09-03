<script setup>
import { ref, computed } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useRouter } from "vue-router";

const open = ref(false);
const mode = ref("login"); // "login" | "register"

const username = ref("");
const password = ref("");
const confirm = ref("");

const auth = useAuthStore();
const router = useRouter();

const title = computed(() => (mode.value === "login" ? "Connexion" : "Inscription"));

async function submit() {
  try {
    if (mode.value === "login") {
      await auth.login(username.value.trim(), password.value);
    } else {
      if (password.value !== confirm.value) {
        throw new Error("Les mots de passe ne correspondent pas");
      }
      await auth.register(username.value.trim(), password.value);
    }
    resetAndClose();
  } catch (e) {
    alert(e.message || "Opération échouée");
  }
}

function resetAndClose() {
  open.value = false;
  username.value = "";
  password.value = "";
  confirm.value = "";
  mode.value = "login";
}

function doLogout() {
  auth.logout();
  router.push({ name: "home" });
}
</script>

<template>
  <div class="mr-4">
    <button
      v-if="!auth.isAuthenticated"
      class="px-3 py-1 rounded bg-gray-900 text-white cursor-pointer"
      @click="open = true"
    >
      Connexion
    </button>

    <div v-else class="flex items-center gap-2">
      <button class="px-3 py-1 rounded bg-gray-900 text-white cursor-pointer" @click="doLogout">
        Déconnexion
      </button>
    </div>

    <div
      v-if="open"
      class="fixed inset-0 flex justify-center items-center bg-black/30"
      @click="resetAndClose"
    >
      <form class="p-4 w-80 bg-white space-y-3 rounded-lg" @click.stop @submit.prevent="submit">
        <h3 class="text-lg font-semibold">{{ title }}</h3>

        <div class="flex gap-2 border-b pb-2">
          <button
            type="button"
            class="px-2 py-1 rounded cursor-pointer"
            :class="mode === 'login' ? 'bg-gray-900 text-white' : 'bg-gray-100'"
            @click="mode = 'login'"
          >
            Se connecter
          </button>
          <button
            type="button"
            class="px-2 py-1 rounded cursor-pointer"
            :class="mode === 'register' ? 'bg-gray-900 text-white' : 'bg-gray-100'"
            @click="mode = 'register'"
          >
            S'inscrire
          </button>
        </div>

        <input
          class="w-full border rounded px-2 py-1"
          placeholder="Pseudo"
          v-model.trim="username"
          required
          minlength="3"
          maxlength="50"
        />

        <input
          class="w-full border rounded px-2 py-1"
          placeholder="Mot de passe"
          type="password"
          v-model="password"
          required
          minlength="6"
        />

        <input
          v-if="mode === 'register'"
          class="w-full border rounded px-2 py-1"
          placeholder="Confirmer le mot de passe"
          type="password"
          v-model="confirm"
          required
          minlength="6"
        />

        <p class="text-xs text-gray-500">min 3 caractères pour le pseudo</p>

        <div class="flex justify-end gap-2 pt-2">
          <button type="button" class="px-3 py-1 cursor-pointer" @click="resetAndClose">
            Annuler
          </button>
          <button type="submit" class="px-3 py-1 bg-gray-900 text-white rounded cursor-pointer">
            {{ mode === "login" ? "Entrer" : "Créer le compte" }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
dialog::backdrop {
  background: rgba(0, 0, 0, 0.35);
}
</style>
