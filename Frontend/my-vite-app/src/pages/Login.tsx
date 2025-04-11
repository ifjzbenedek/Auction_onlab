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
    const query = new URLSearchParams(location.search)
    const returnUrl = query.get("returnUrl") || "/"
  
    const handlePostLogin = async () => {
      const success = await authService.handlePostLogin()
      if (success) {
        navigate(returnUrl)
      } else {
        setError("Authentication failed")
      }
    }
  
    if (location.pathname === "/auth/success") {
      handlePostLogin()
    }
  }, [location, navigate])

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
