// auth-service.ts
import axios from "axios"

const API_BASE_URL = "https://localhost:8081"

// Create a separate axios instance for auth to avoid circular dependencies
const authApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Important for OAuth flows
})

export interface AuthUser {
  id: number
  userName: string
  email?: string
  roles?: string[]
}

class AuthService {
  private currentUser: AuthUser | null = null
  private isAuthenticated = false

  constructor() {
    // Try to load user from localStorage on initialization
    this.loadUserFromStorage()
  }

  private loadUserFromStorage() {
    const token = localStorage.getItem("token")
    const userJson = localStorage.getItem("user")

    if (token && userJson) {
      try {
        this.currentUser = JSON.parse(userJson)
        this.isAuthenticated = true
        console.log("User loaded from storage:", this.currentUser)
      } catch (error) {
        console.error("Failed to parse user from localStorage:", error)
        this.logout() // Clear invalid data
      }
    }
  }

  async handlePostLogin() {
    try {
      const response = await authApi.get('/auth/success')
      const token = response.data.token
      localStorage.setItem("token", token)
      await this.checkAuthStatus()
      return true
    } catch (error) {
      console.error("Post-login error:", error)
      return false
    }
  }
  
  // Initiate OAuth2 login
  login() {
    window.location.href = `${API_BASE_URL}/oauth2/authorization/google`
  }

  // Handle OAuth2 callback and token storage
  async handleOAuthCallback(code: string): Promise<boolean> {
    try {
      const response = await authApi.post("/auth/token", { code })
      const { token, user } = response.data

      // Store token and user info
      localStorage.setItem("token", token)
      localStorage.setItem("user", JSON.stringify(user))

      this.currentUser = user
      this.isAuthenticated = true

      return true
    } catch (error) {
      console.error("OAuth callback error:", error)
      return false
    }
  }

  // Check if user is authenticated
  isLoggedIn(): boolean {
    return this.isAuthenticated
  }

  // Get current user
  getUser(): AuthUser | null {
    return this.currentUser
  }

  // Logout
  logout() {
    localStorage.removeItem("token")
    localStorage.removeItem("user")
    this.currentUser = null
    this.isAuthenticated = false

    // Optionally call backend logout endpoint
    authApi.post("/auth/logout").catch((err) => console.error("Logout error:", err))
  }

  // Check authentication status with server
  async checkAuthStatus(): Promise<boolean> {
    try {
      const response = await authApi.get("/auth/status")
      const { authenticated, user } = response.data

      if (authenticated && user) {
        // Update local storage with latest user data
        localStorage.setItem("user", JSON.stringify(user))
        this.currentUser = user
        this.isAuthenticated = true
        return true
      } else {
        // Clear invalid auth state
        this.logout()
        return false
      }
    } catch (error) {
      console.error("Auth check error:", error)
      this.isAuthenticated = false
      return false
    }
  }
}

// Create singleton instance
const authService = new AuthService()
export default authService;
