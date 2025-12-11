"use client"

import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { Box, Typography, Button, Paper, Alert, CircularProgress } from "@mui/material"
import { authService } from "../services/auth-service"

function Login() {
  const navigate = useNavigate()
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const checkAuth = async () => {
      const isAuth = await authService.isAuthenticated()
      if (isAuth) {
        navigate("/")
      }
    }
    checkAuth()
  }, [navigate])

  const handleGoogleLogin = () => {
    setLoading(true)
    try {
      authService.loginWithGoogle()
    } catch (err) {
      setLoading(false)
      setError(`Failed to initiate login process: ${err instanceof Error ? err.message : String(err)}`)
    }
  }

  return (
    <Box
      sx={{ display: "flex", justifyContent: "center", alignItems: "center", minHeight: "100vh", bgcolor: "#f8f9fa" }}
    >
      <Paper
        sx={{
          maxWidth: 400,
          width: "100%",
          p: 4,
          textAlign: "center",
          borderRadius: 2,
          boxShadow: "0 4px 20px rgba(0,0,0,0.1)",
        }}
      >
        <Box sx={{ mb: 3 }}>
          <Typography variant="h4" fontWeight="bold" sx={{ color: "#2c3e50", mb: 1 }}>
            BidVerse
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Sign in to access your auctions
          </Typography>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        <Button
          variant="contained"
          fullWidth
          size="large"
          onClick={handleGoogleLogin}
          disabled={loading}
          sx={{
            bgcolor: "#4285F4",
            color: "white",
            py: 1.5,
            mb: 2,
            "&:hover": {
              bgcolor: "#3367D6",
            },
          }}
        >
          {loading ? <CircularProgress size={24} color="inherit" /> : "Sign in with Google"}
        </Button>

        <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
          By signing in, you agree to our Terms of Service and Privacy Policy
        </Typography>
      </Paper>
    </Box>
  )
}

export default Login;