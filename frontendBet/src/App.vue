<template>
  <div class="min-h-screen flex flex-col bg-gray-50">
    <header class="bg-gray-900">
      <h1 class="text-3xl font-bold text-orange-500 text-center py-4">A Bet For A Beer</h1>
    </header>

    <nav class="bg-gray-100 border-b">
      <div class="flex items-center justify-between h-12">
        <ul class="flex space-x-6 ml-4">
          <li>
            <router-link :to="{ name: 'home' }" class="nav-cli" active-class="nav-li-active"
              >Classement
            </router-link>
          </li>
          <li>
            <router-link :to="{ name: 'schedule' }" class="nav-cli" active-class="nav-li-active">
              Calendrier
            </router-link>
          </li>
          <li>
            <router-link :to="{ name: 'addMatch' }" class="nav-cli" active-class="nav-li-active">
              Ajout de Match
            </router-link>
          </li>
        </ul>
        <login-button />
      </div>
    </nav>

    <main class="m-4 space-y-6">
      <router-view />
    </main>
  </div>
</template>
