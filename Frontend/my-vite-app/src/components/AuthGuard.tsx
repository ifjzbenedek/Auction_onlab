import { useEffect, useState } from "react"
import { CircularProgress, Box } from "@mui/material"

interface AuthGuardProps {
  children: React.ReactNode
  fallback?: React.ReactNode
}

export const AuthGuard = ({ 
  children, 
  fallback = (
    <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "50vh" }}>
      <CircularProgress size={60} />
    </Box>
  )
}: AuthGuardProps) => {
  const [isChecking, setIsChecking] = useState(true)
  const [isAuthenticated, setIsAuthenticated] = useState(false)

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await fetch('/users/me', {
          credentials: 'include'
        })

        if (response.status === 401) {
          const data = await response.json()
          window.location.href = data.authUrl
          return
        }

        if (response.ok) {
          setIsAuthenticated(true)
        } else {
          window.location.href = '/oauth2/authorization/google'
        }
      } catch (error) {
        console.error('Auth check failed:', error)
        window.location.href = '/oauth2/authorization/google'
      } finally {
        setIsChecking(false)
      }
    }

    checkAuth()
  }, [])

  if (isChecking) {
    return <>{fallback}</>
  }

  if (!isAuthenticated) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "50vh" }}>
        <div>Redirecting to login...</div>
      </Box>
    )
  }

  return <>{children}</>
}

export default AuthGuard