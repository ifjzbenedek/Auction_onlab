"use client"

import type React from "react"
import { useEffect, useState } from "react"
import { Box, Typography, Paper, CircularProgress } from "@mui/material"

interface User {
  userName: string
  emailAddress: string
  phoneNumber: string
}

const Profile: React.FC = () => {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // Felhasználó adatainak lekérése a backendtől
    fetch("/users/me", { credentials: "include" })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch user data")
        }
        return response.json()
      })
      .then((data) => {
        setUser(data)
        setLoading(false)
      })
      .catch((error) => {
        console.error(error)
        setLoading(false)
      })
  }, [])

  return (
    <Box>
      <Box sx={{ maxWidth: 1200, mx: "auto", p: 2 }}>
        {loading ? (
          <Box sx={{ display: "flex", justifyContent: "center", p: 4 }}>
            <CircularProgress />
          </Box>
        ) : user ? (
          <Paper sx={{ p: 3 }}>
            <Typography variant="h4" sx={{ mb: 2 }}>
              Profile
            </Typography>
            <Typography variant="body1" sx={{ mb: 1 }}>
              Username: {user.userName}
            </Typography>
            <Typography variant="body1" sx={{ mb: 1 }}>
              Email: {user.emailAddress}
            </Typography>
            <Typography variant="body1">Phone: {user.phoneNumber}</Typography>
          </Paper>
        ) : (
          <Typography>User not found</Typography>
        )}
      </Box>
    </Box>
  )
}

export default Profile

