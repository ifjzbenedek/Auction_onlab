"use client"

import React from "react"
import { Box, Typography, Paper, CircularProgress, useTheme } from "@mui/material"
import { styled } from "@mui/material/styles"
import { useAuth } from "../auth-context"

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
  const { user, isAuthenticated } = useAuth()
  const theme = useTheme()

  return (
    <Box sx={{ 
      bgcolor: theme.palette.background.default, 
      minHeight: "100vh", 
      py: 4 
    }}>
      <Box sx={{ 
        maxWidth: 1200, 
        mx: "auto", 
        px: { xs: 2, md: 4 }, 
        py: 2 
      }}>
        {!isAuthenticated ? (
          <Typography 
            variant="h6" 
            sx={{ 
              textAlign: "center", 
              color: theme.palette.error.main, 
              userSelect: "none" 
            }}
          >
            Please log in to view your profile
          </Typography>
        ) : user ? (
          <StyledPaper>
            <Typography 
              variant="h4" 
              sx={{ 
                mb: 3, 
                fontWeight: "bold", 
                color: theme.palette.text.primary, 
                userSelect: "none" 
              }}
            >
              Profile
            </Typography>
            
            {/* Felhasználói adatok megjelenítése */}
            <ProfileField 
              label="Username" 
              value={user.userName} 
              theme={theme} 
            />
            <ProfileField 
              label="Email" 
              value={user.emailAddress} 
              theme={theme} 
            />
            <ProfileField 
              label="Phone" 
              value={user.phoneNumber} 
              theme={theme} 
            />
          </StyledPaper>
        ) : (
          <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "50vh" }}>
            <CircularProgress size={60} />
          </Box>
        )}
      </Box>
    </Box>
  )
}

// Reusable komponens mezőkhöz
const ProfileField = ({ label, value, theme }: { 
  label: string, 
  value: string, 
  theme: any 
}) => (
  <Box sx={{ mb: 2 }}>
    <Typography 
      variant="subtitle1" 
      sx={{ 
        color: theme.palette.text.secondary, 
        mb: 1, 
        userSelect: "none" 
      }}
    >
      {label}
    </Typography>
    <Typography 
      variant="body1" 
      sx={{ 
        fontWeight: "medium", 
        color: theme.palette.text.primary, 
        userSelect: "none" 
      }}
    >
      {value}
    </Typography>
  </Box>
)

export default Profile;