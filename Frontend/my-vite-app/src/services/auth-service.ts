// Session-based authentication service for Google OAuth
import axios from "axios"

export const authService = {
  // Initiate Google OAuth login
  loginWithGoogle: () => {
    // Redirect to the backend's OAuth endpoint
    window.location.href = "/oauth2/authorization/google"
  },

  // Check if user is authenticated by calling the backend
  isAuthenticated: async (): Promise<boolean> => {
    try {
      const response = await axios.get("/users/me", { withCredentials: true })
      return response.status === 200
    } catch {
      return false
    }
  },

  // Get current user data
  getCurrentUser: async () => {
    const response = await axios.get("/users/me", { withCredentials: true })
    return response.data
  },

  // Simple logout - just redirect to backend logout and let Spring Security handle it
  logout: () => {
    console.log("Redirecting to backend logout...")
    
    // Clear browser storage
    localStorage.clear()
    sessionStorage.clear()
    
    // Use relative URL to go through Vite proxy
    window.location.href = "/logout"
  },
}
