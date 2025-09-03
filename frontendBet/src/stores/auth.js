import { defineStore } from "pinia";

const TOKEN_KEY = "abfab_token";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || null,
    user: null, // { username, role }
  }),
  getters: {
    isAuthenticated: (s) => !!s.token && !!s.user,
    isAdmin: (s) => s.user?.role === "ADMIN",
  },
  actions: {
    async login(username, password) {
      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });
      if (!res.ok) throw new Error((await res.text()) || "Identifiants invalides");
      const data = await res.json(); // { username, role, token }
      this.token = data.token;
      this.user = { username: data.username, role: data.role };
      localStorage.setItem(TOKEN_KEY, this.token);
    },

    async register(username, password) {
      const res = await fetch("/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });
      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Inscription impossible");
      }
      // Auto-login après inscription
      await this.login(username, password);
    },

    logout() {
      this.token = null;
      this.user = null;
      localStorage.removeItem(TOKEN_KEY);
    },

    async fetchMeIfToken() {
      if (!this.token) return;
      const res = await fetch("/api/auth/me", {
        headers: { Authorization: `Bearer ${this.token}` },
      });
      if (res.ok) {
        this.user = await res.json(); // { username, role }
      } else {
        this.logout();
      }
    },

    authHeaders() {
      return this.token ? { Authorization: `Bearer ${this.token}` } : {};
    },
  },
});
