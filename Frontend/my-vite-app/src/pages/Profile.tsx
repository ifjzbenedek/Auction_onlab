"use client"

import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { Box, Typography, Paper, CircularProgress, useTheme, Button, Grid2, Card, CardContent, CardActions } from "@mui/material"
import { styled } from "@mui/material/styles"
import GavelIcon from "@mui/icons-material/Gavel"
import FavoriteIcon from "@mui/icons-material/Favorite"
import LocalOfferIcon from "@mui/icons-material/LocalOffer"
import MailIcon from "@mui/icons-material/Mail"

interface User {
  userName: string
  emailAddress: string
  phoneNumber: string
}

// Styled Paper component with animation and shadow
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

function Profile() {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const theme = useTheme()
  const navigate = useNavigate()

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await fetch('/users/me', {
          credentials: 'include'
        })
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        
        const userData = await response.json()
        setUser(userData)
      } catch {
        setUser(null)
      } finally {
        setLoading(false)
      }
    }

    fetchUserData()
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
              <Typography variant="subtitle1" sx={{ color: theme.palette.text.secondary, mb: 1, userSelect: "none" }}>
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
              <Typography variant="subtitle1" sx={{ color: theme.palette.text.secondary, mb: 1, userSelect: "none" }}>
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
              <Typography variant="subtitle1" sx={{ color: theme.palette.text.secondary, mb: 1, userSelect: "none" }}>
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
        ) : null}

        {user && (
          <Box sx={{ mt: 4 }}>
            <Typography variant="h5" sx={{ mb: 3, fontWeight: "bold", color: theme.palette.text.primary }}>
              Quick Access
            </Typography>
            <Grid2 container spacing={3}>
              <Grid2 size={{ xs: 12, sm: 6, md: 3 }}>
                <Card
                  sx={{
                    cursor: "pointer",
                    transition: "transform 0.2s, box-shadow 0.2s",
                    "&:hover": {
                      transform: "translateY(-5px)",
                      boxShadow: theme.shadows[8],
                    },
                  }}
                  onClick={() => navigate("/my-auctions")}
                >
                  <CardContent sx={{ textAlign: "center", py: 3 }}>
                    <GavelIcon sx={{ fontSize: 48, color: theme.palette.primary.main, mb: 2 }} />
                    <Typography variant="h6" sx={{ fontWeight: "bold", mb: 1 }}>
                      My Auctions
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      View and manage your auctions
                    </Typography>
                  </CardContent>
                  <CardActions sx={{ justifyContent: "center", pb: 2 }}>
                    <Button size="small" color="primary">
                      Go to Auctions
                    </Button>
                  </CardActions>
                </Card>
              </Grid2>

              <Grid2 size={{ xs: 12, sm: 6, md: 3 }}>
                <Card
                  sx={{
                    cursor: "pointer",
                    transition: "transform 0.2s, box-shadow 0.2s",
                    "&:hover": {
                      transform: "translateY(-5px)",
                      boxShadow: theme.shadows[8],
                    },
                  }}
                  onClick={() => navigate("/my-auctions?tab=bids")}
                >
                  <CardContent sx={{ textAlign: "center", py: 3 }}>
                    <LocalOfferIcon sx={{ fontSize: 48, color: theme.palette.secondary.main, mb: 2 }} />
                    <Typography variant="h6" sx={{ fontWeight: "bold", mb: 1 }}>
                      My Bids
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Track your active bids
                    </Typography>
                  </CardContent>
                  <CardActions sx={{ justifyContent: "center", pb: 2 }}>
                    <Button size="small" color="primary">
                      View Bids
                    </Button>
                  </CardActions>
                </Card>
              </Grid2>

              <Grid2 size={{ xs: 12, sm: 6, md: 3 }}>
                <Card
                  sx={{
                    cursor: "pointer",
                    transition: "transform 0.2s, box-shadow 0.2s",
                    "&:hover": {
                      transform: "translateY(-5px)",
                      boxShadow: theme.shadows[8],
                    },
                  }}
                  onClick={() => navigate("/my-auctions?tab=followed")}
                >
                  <CardContent sx={{ textAlign: "center", py: 3 }}>
                    <FavoriteIcon sx={{ fontSize: 48, color: theme.palette.error.main, mb: 2 }} />
                    <Typography variant="h6" sx={{ fontWeight: "bold", mb: 1 }}>
                      Followed
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Your favorite auctions
                    </Typography>
                  </CardContent>
                  <CardActions sx={{ justifyContent: "center", pb: 2 }}>
                    <Button size="small" color="primary">
                      View Followed
                    </Button>
                  </CardActions>
                </Card>
              </Grid2>

              <Grid2 size={{ xs: 12, sm: 6, md: 3 }}>
                <Card
                  sx={{
                    cursor: "pointer",
                    transition: "transform 0.2s, box-shadow 0.2s",
                    "&:hover": {
                      transform: "translateY(-5px)",
                      boxShadow: theme.shadows[8],
                    },
                  }}
                  onClick={() => navigate("/mailbox")}
                >
                  <CardContent sx={{ textAlign: "center", py: 3 }}>
                    <MailIcon sx={{ fontSize: 48, color: theme.palette.info.main, mb: 2 }} />
                    <Typography variant="h6" sx={{ fontWeight: "bold", mb: 1 }}>
                      Mailbox
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Check your messages
                    </Typography>
                  </CardContent>
                  <CardActions sx={{ justifyContent: "center", pb: 2 }}>
                    <Button size="small" color="primary">
                      Open Mailbox
                    </Button>
                  </CardActions>
                </Card>
              </Grid2>
            </Grid2>
          </Box>
        )}

        {!loading && !user && (
          <Typography variant="h6" sx={{ textAlign: "center", color: theme.palette.error.main, userSelect: "none" }}>
            User not found
          </Typography>
        )}
      </Box>
    </Box>
  )
}

export default Profile;
