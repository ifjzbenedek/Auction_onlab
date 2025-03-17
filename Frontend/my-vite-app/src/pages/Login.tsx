"use client"

import type React from "react"
import { useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { Box, Typography, CircularProgress } from "@mui/material"

const Login: React.FC = () => {
  const navigate = useNavigate()

  useEffect(() => {
    // Redirect to Google login after a short delay
    const timer = setTimeout(() => {
      window.location.href = "https://localhost:8081/oauth2/authorization/google"
    }, 1500)

    return () => clearTimeout(timer)
  }, [navigate])

  return (
    <Box>
      <Box sx={{ maxWidth: 1200, mx: "auto", p: 2, textAlign: "center", mt: 4 }}>
        <CircularProgress sx={{ mb: 2 }} />
        <Typography variant="h6">Redirecting to Google Login...</Typography>
      </Box>
    </Box>
  )
}

export default Login

