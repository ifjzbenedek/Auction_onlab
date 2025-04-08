"use client"

import { useEffect } from "react"
import { useLocation, useNavigate } from "react-router-dom"
import authService from "../auth/auth-service.ts" 
import { useState } from "react"

// This is a partial update to your Login.tsx file
// Add this code to handle the OAuth callback and return URL

// Inside your Login component:
const Login = () => {
  const location = useLocation()
  const navigate = useNavigate()
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    // Parse query parameters
    const query = new URLSearchParams(location.search)
    const code = query.get("code")
    const returnUrl = query.get("returnUrl") || "/"

    // Handle OAuth callback if code is present
    if (code) {
      handleOAuthCallback(code, returnUrl)
    }

    // Check if user is already logged in
    const token = localStorage.getItem("token")
    if (token) {
      // Redirect to return URL or home
      navigate(returnUrl)
    }
  }, [location, navigate])

  const handleOAuthCallback = async (code: string, returnUrl: string) => {
    try {
      const success = await authService.handleOAuthCallback(code)
      if (success) {
        navigate(returnUrl)
      } else {
        // Show error message
        setError("Authentication failed. Please try again.")
      }
    } catch (error) {
      console.error("OAuth callback error:", error)
      setError("An unexpected error occurred during login.")
    }
  }

  // Add this to your login button click handler
  const handleGoogleLogin = () => {
    authService.login()
  }

  return (
    <div>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <button onClick={handleGoogleLogin}>Login with Google</button>
    </div>
  )
}

export default Login;
