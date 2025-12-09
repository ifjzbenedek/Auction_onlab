import axios from "axios"

export const authService = {
  loginWithGoogle: () => {
    window.location.href = "/oauth2/authorization/google"
  },

  isAuthenticated: async (): Promise<boolean> => {
    try {
      const response = await axios.get("/users/me", { withCredentials: true })
      return response.status === 200
    } catch {
      return false
    }
  },

  getCurrentUser: async () => {
    const response = await axios.get("/users/me", { withCredentials: true })
    return response.data
  },

  logout: () => {
    localStorage.clear()
    sessionStorage.clear()
    window.location.href = "/logout"
  },
}
