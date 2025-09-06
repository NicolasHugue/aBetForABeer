<script setup>
import LoginButton from "@/components/LoginButton.vue";
import { useAuthStore } from "./stores/auth";
const auth = useAuthStore();
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gray-50">
    <header class="bg-gray-900">
      <h1 class="text-2xl font-bold text-orange-500 text-center py-2">A Bet For A Beer</h1>
    </header>

    <nav class="bg-gray-100 border-b">
      <div class="flex items-center justify-between h-12 px-2">
        <div class="overflow-x-auto whitespace-nowrap no-scrollbar">
          <ul class="flex gap-2">
            <li>
              <router-link :to="{ name: 'home' }" class="nav-link" active-class="nav-link-active">
                Classement
              </router-link>
            </li>
            <li v-if="auth.isAuthenticated">
              <router-link
                :to="{ name: 'schedule' }"
                class="nav-link"
                active-class="nav-link-active"
              >
                Calendrier
              </router-link>
            </li>
            <li v-if="auth.isAdmin">
              <router-link
                :to="{ name: 'addMatch' }"
                class="nav-link"
                active-class="nav-link-active"
              >
                Ajout de Match
              </router-link>
            </li>
          </ul>
        </div>
        <login-button />
      </div>
    </nav>

    <main class="m-4 space-y-6">
      <router-view />
    </main>
  </div>
</template>
