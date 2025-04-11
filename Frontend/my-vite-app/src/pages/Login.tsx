"use client"

import React from "react"
import { useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { Box, Typography, CircularProgress } from "@mui/material"
import { useAuth } from "../auth-context"

const Login: React.FC = () => {
  const { login } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    const params = new URLSearchParams(window.location.hash.substr(1))
    const token = params.get("access_token")

    if (token) {
      login(token) // 🔑 Kontextus frissítése
      navigate("/")
    } else {
      const timer = setTimeout(() => {
        window.location.href = "https://localhost:8081/oauth2/authorization/google"
      }, 1500)
      return () => clearTimeout(timer)
    }
  }, [login, navigate])

  return (
    <Box>
      <Box sx={{ maxWidth: 1200, mx: "auto", p: 2, textAlign: "center", mt: 4 }}>
        <CircularProgress sx={{ mb: 2 }} />
        <Typography variant="h6">Redirecting to Google Login...</Typography>
      </Box>
    </Box>
  )
}

export default Login;
