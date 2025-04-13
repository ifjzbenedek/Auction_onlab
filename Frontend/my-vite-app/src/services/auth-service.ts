// Create a new auth service to handle authentication properly
import axios from "axios"

const AUTH_API_URL = "/auth" // This will be proxied to your backend

export const authService = {
  // Initiate Google OAuth login
  loginWithGoogle: () => {
    // Redirect to the backend's OAuth endpoint
    window.location.href = "/oauth2/authorization/google"
  },

  // Check if user is authenticated
  isAuthenticated: () => {
    return !!localStorage.getItem("token")
  },

  // Get the current authentication token
  getToken: () => {
    return localStorage.getItem("token")
  },

  // Logout the user
  logout: async () => {
    try {
      // Call backend logout endpoint if available
      await axios.post("/auth/logout", {}, { withCredentials: true })
    } catch (error) {
      console.error("Logout error:", error)
    }

    // Clear local storage regardless of backend response
    localStorage.removeItem("token")
  },

  // Handle OAuth callback (for implicit flow)
  handleAuthCallback: () => {
    // Check for token in URL hash (for implicit flow)
    const params = new URLSearchParams(window.location.hash.substr(1))
    const token = params.get("access_token")

    // Check for error in URL parameters
    const urlParams = new URLSearchParams(window.location.search)
    const errorParam = urlParams.get("error")

    if (token) {
      localStorage.setItem("token", token)
      return { success: true, error: null }
    } else if (errorParam) {
      return { success: false, error: errorParam }
    }

    return { success: false, error: null }
  },
}
