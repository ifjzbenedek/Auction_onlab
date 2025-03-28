"use client"

import type React from "react"
import { useEffect, useState } from "react"
import { Box, Typography, Paper, CircularProgress, useTheme } from "@mui/material"
import { styled } from "@mui/material/styles"

interface User {
  userName: string
  emailAddress: string
  phoneNumber: string
}

// Styled Paper komponens animációval és árnyékkal
const StyledPaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(4),
  borderRadius: theme.shape.borderRadius * 2,
  boxShadow: theme.shadows[4],
  transition: "transform 0.3s ease, box-shadow 0.3s ease",
  "&:hover": {
    transform: "translateY(-5px)",
    boxShadow: theme.shadows[8],
  },
}))

const Profile: React.FC = () => {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const theme = useTheme()

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
    <Box sx={{ bgcolor: theme.palette.background.default, minHeight: "100vh", py: 4 }}>
      <Box sx={{ maxWidth: 1200, mx: "auto", px: { xs: 2, md: 4 }, py: 2 }}>
        {loading ? (
          <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "50vh" }}>
            <CircularProgress size={60} />
          </Box>
        ) : user ? (
          <StyledPaper>
            <Typography
              variant="h4"
              sx={{
                mb: 3,
                fontWeight: "bold",
                color: theme.palette.text.primary,
                userSelect: "none", 
              }}
            >
              Profile
            </Typography>
            <Box sx={{ mb: 2 }}>
              <Typography
                variant="subtitle1"
                sx={{ color: theme.palette.text.secondary, mb: 1, userSelect: "none" }}
              >
                Username
              </Typography>
              <Typography
                variant="body1"
                sx={{ fontWeight: "medium", color: theme.palette.text.primary, userSelect: "none" }}
              >
                {user.userName}
              </Typography>
            </Box>
            <Box sx={{ mb: 2 }}>
              <Typography
                variant="subtitle1"
                sx={{ color: theme.palette.text.secondary, mb: 1, userSelect: "none" }}
              >
                Email
              </Typography>
              <Typography
                variant="body1"
                sx={{ fontWeight: "medium", color: theme.palette.text.primary, userSelect: "none" }}
              >
                {user.emailAddress}
              </Typography>
            </Box>
            <Box>
              <Typography
                variant="subtitle1"
                sx={{ color: theme.palette.text.secondary, mb: 1, userSelect: "none" }}
              >
                Phone
              </Typography>
              <Typography
                variant="body1"
                sx={{ fontWeight: "medium", color: theme.palette.text.primary, userSelect: "none" }}
              >
                {user.phoneNumber}
              </Typography>
            </Box>
          </StyledPaper>
        ) : (
          <Typography
            variant="h6"
            sx={{ textAlign: "center", color: theme.palette.error.main, userSelect: "none" }}
          >
            User not found
          </Typography>
        )}
      </Box>
    </Box>
  )
}
 
export default Profile;