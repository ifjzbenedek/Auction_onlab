import type React from "react"
import { useEffect, useState } from "react"
import { CircularProgress, Box } from "@mui/material"

interface AuthGuardProps {
  children: React.ReactNode
  fallback?: React.ReactNode
}

export const AuthGuard: React.FC<AuthGuardProps> = ({ 
  children, 
  fallback = (
    <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "50vh" }}>
      <CircularProgress size={60} />
    </Box>
  )
}) => {
  const [isChecking, setIsChecking] = useState(true)
  const [isAuthenticated, setIsAuthenticated] = useState(false)

  useEffect(() => {
    const checkAuth = async () => {
      try {
        console.log('AuthGuard: Checking authentication...')
        
        const response = await fetch('/users/me', {
          credentials: 'include'
        })

        if (response.status === 401) {
          console.log('AuthGuard: Not authenticated, redirecting...')
          // Authentication szükséges
          const data = await response.json()
          // Redirect a Google OAuth-ra
          window.location.href = data.authUrl
          return
        }

        if (response.ok) {
          console.log('AuthGuard: Authentication successful')
          setIsAuthenticated(true)
        } else {
          console.log('AuthGuard: Unexpected response status:', response.status)
          // Fallback redirect
          window.location.href = '/oauth2/authorization/google'
        }
      } catch (error) {
        console.error('AuthGuard: Auth check failed:', error)
        // Ha bármilyen hiba történik, redirectelünk az OAuth-ra
        window.location.href = '/oauth2/authorization/google'
      } finally {
        setIsChecking(false)
      }
    }

    checkAuth()
  }, [])

  // Még ellenőrzés alatt van
  if (isChecking) {
    return <>{fallback}</>
  }

  // Ha nem authentikált, akkor már redirecteltünk, de fallback-et mutatunk
  if (!isAuthenticated) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "50vh" }}>
        <div>Redirecting to login...</div>
      </Box>
    )
  }

  // Ha authentikált, rendereljük a gyerek komponenst
  return <>{children}</>
}

export default AuthGuard