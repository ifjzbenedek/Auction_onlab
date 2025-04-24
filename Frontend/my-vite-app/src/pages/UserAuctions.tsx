"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useNavigate, useLocation } from "react-router-dom"
import {
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  Tabs,
  Tab,
  CircularProgress,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Snackbar,
} from "@mui/material"
import Header from "../components/Header"
import MyAuctionItem from "../components/auction-list-items/MyAuctionItem"
import MyBidItem from "../components/auction-list-items/MyBidItem"
import FollowedAuctionItem from "../components/auction-list-items/FollowedAuctionItem"
import { auctionApi } from "../services/api"
import type { AuctionCardDTO } from "../types/auction"

interface AuctionWithTime extends AuctionCardDTO {
  remainingTime: string
  highestBid: number
  yourBid?: number
}

const UserAuctions: React.FC = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const [tabValue, setTabValue] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [auctions, setAuctions] = useState<AuctionWithTime[]>([])

  // Dialog state
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [auctionToDelete, setAuctionToDelete] = useState<number | null>(null)

  // Snackbar state
  const [snackbarOpen, setSnackbarOpen] = useState(false)
  const [snackbarMessage, setSnackbarMessage] = useState("")

  // Set initial tab based on URL query parameter
  useEffect(() => {
    const params = new URLSearchParams(location.search)
    const tab = params.get("tab")

    if (tab === "bids") {
      setTabValue(1)
    } else if (tab === "followed") {
      setTabValue(2)
    } else {
      setTabValue(0)
    }
  }, [location.search])

  // Calculate remaining time for an auction
  const calculateTimeLeft = (expiredDate: string): string => {
    const now = new Date()
    const end = new Date(expiredDate)
    const diff = end.getTime() - now.getTime()

    if (diff <= 0) return "Ended"

    const hours = Math.floor(diff / (1000 * 60 * 60))
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    const seconds = Math.floor((diff % (1000 * 60)) / 1000)

    return `${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`
  }

  // Format auction data for display
  const formatAuctionData = (auctions: AuctionCardDTO[]): AuctionWithTime[] => {
    return auctions.map((auction) => ({
      ...auction,
      remainingTime: calculateTimeLeft(auction.expiredDate),
      highestBid: auction.lastBid || 0,
    }))
  }

  // Fetch auctions based on current tab
  const fetchAuctions = async () => {
    setLoading(true)
    setError(null)

    try {
      let response

      switch (tabValue) {
        case 0: // My Auctions
          response = await auctionApi.getMyAuctions()
          setAuctions(formatAuctionData(response.data))
          break
        case 1: // My Bids
          response = await auctionApi.getBiddedAuctions()
          setAuctions(formatAuctionData(response.data))
          break
        case 2: // Followed
          // Since there's no backend endpoint for watched auctions yet
          setSnackbarMessage("Followed auctions functionality is not fully implemented on the backend")
          setSnackbarOpen(true)
          setAuctions([])
          break
      }
    } catch (err: unknown) {
      console.error("Error fetching auctions:", err)

      // Check if this is an authentication error
      if (typeof err === "object" && err !== null && "isAuthError" in err && (err as { isAuthError?: boolean }).isAuthError) {
        setError("Authentication required. Please log in to view your auctions.")
      } else if (
        typeof err === "object" &&
        err !== null &&
        "response" in err &&
        (err as { response?: { data?: string } }).response &&
        (err as { response: { data?: string } }).response.data
      ) {
        setError(`Failed to load auctions: ${(err as { response: { data: string } }).response.data}`)
      } else {
        setError("Failed to load auctions. Please try again later.")
      }
    } finally {
      setLoading(false)
    }
  }

  // Fetch auctions when tab changes
  useEffect(() => {
    fetchAuctions()

    // Update URL to reflect current tab
    const params = new URLSearchParams()
    if (tabValue === 1) {
      params.set("tab", "bids")
    } else if (tabValue === 2) {
      params.set("tab", "followed")
    }

    const newUrl = params.toString() ? `${location.pathname}?${params.toString()}` : location.pathname
    navigate(newUrl, { replace: true })
  }, [tabValue, navigate, location.pathname])

  // Add a function to refresh the timer for auctions
  useEffect(() => {
    // Update the remaining time every second
    const timer = setInterval(() => {
      if (auctions.length > 0) {
        setAuctions((prevAuctions) =>
          prevAuctions.map((auction) => ({
            ...auction,
            remainingTime: calculateTimeLeft(auction.expiredDate),
          })),
        )
      }
    }, 1000)

    return () => clearInterval(timer)
  }, [auctions.length])

  const handleTabChange = (_: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue)
  }

  const handleViewAuction = (id: number) => {
    navigate(`/auction/${id}`)
  }

  const handleEditAuction = () => {
    // For now, we'll just show a message since we don't have an edit page
    // In a real implementation, you would navigate to an edit page
    setSnackbarMessage("Edit functionality is available through the API but not implemented in the UI yet")
    setSnackbarOpen(true)
  }

  const handleDeleteConfirmation = (id: number) => {
    setAuctionToDelete(id)
    setDeleteDialogOpen(true)
  }

  const handleDeleteAuction = async () => {
    if (auctionToDelete === null) return

    setDeleteDialogOpen(false)

    try {
      await auctionApi.deleteAuction(auctionToDelete)
      setSnackbarMessage("Auction deleted successfully")
      setSnackbarOpen(true)

      // Refresh the auctions list
      fetchAuctions()
    } catch (err) {
      console.error("Error deleting auction:", err)
      setSnackbarMessage("Failed to delete auction. Please try again.")
      setSnackbarOpen(true)
    }
  }

  const handleUnfollowAuction = async (id: number) => {
    try {
      // Since the backend doesn't have an unfollow endpoint yet, show a message
      setSnackbarMessage("Unfollow functionality is not implemented yet")
      setSnackbarOpen(true)

      // For demo purposes, we'll remove it from the UI
      setAuctions(auctions.filter((auction) => auction.id !== id))
    } catch (err) {
      console.error("Error unfollowing auction:", err)
      setSnackbarMessage("Failed to unfollow auction. Please try again.")
      setSnackbarOpen(true)
    }
  }

  const handleCreateAuction = () => {
    navigate("/upload-auction")
  }

  const handleCloseSnackbar = () => {
    setSnackbarOpen(false)
  }

  const getTabLabel = (index: number) => {
    switch (index) {
      case 0:
        return "My Auctions"
      case 1:
        return "My Bids"
      case 2:
        return "Followed"
      default:
        return ""
    }
  }

  return (
    <Box sx={{ bgcolor: "#f8f9fa", minHeight: "100vh", userSelect: "none" }}>
      <Header
        onFilterChange={(filter) => console.log("Filter changed:", filter)}
        onSearch={(query) => console.log("Search query:", query)}
      />

      <Box sx={{ maxWidth: 1200, mx: "auto", p: { xs: 2, md: 3 } }}>
        <Tabs
          value={tabValue}
          onChange={handleTabChange}
          sx={{
            mb: 3,
            "& .MuiTab-root": {
              textTransform: "none",
              fontWeight: "medium",
              fontSize: "1rem",
            },
            "& .Mui-selected": {
              color: "#3498db",
              fontWeight: "bold",
            },
            "& .MuiTabs-indicator": {
              backgroundColor: "#3498db",
            },
          }}
        >
          <Tab label="My Auctions" />
          <Tab label="My Bids" />
          <Tab label="Followed" />
        </Tabs>

        <Typography variant="h4" fontWeight="bold" sx={{ mb: 3 }}>
          {getTabLabel(tabValue)}
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        <TableContainer component={Paper} sx={{ boxShadow: "0 2px 10px rgba(0,0,0,0.05)" }}>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: "#f5f5f5", userSelect: "none" }}>
                <TableCell>Name</TableCell>
                <TableCell>Remaining time</TableCell>
                <TableCell>{tabValue === 0 ? "Highest Bid" : tabValue === 1 ? "Your Bid" : "Current Bid"}</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={4} align="center" sx={{ py: 4 }}>
                    <CircularProgress size={30} sx={{ color: "#3498db" }} />
                  </TableCell>
                </TableRow>
              ) : auctions.length > 0 ? (
                <>
                  {tabValue === 0 &&
                    // My Auctions tab
                    auctions.map((auction) => (
                      <MyAuctionItem
                        key={auction.id}
                        auction={auction}
                        onView={handleViewAuction}
                        onEdit={handleEditAuction}
                        onDelete={handleDeleteConfirmation}
                      />
                    ))}
                  {tabValue === 1 &&
                    // My Bids tab
                    auctions.map((auction) => (
                      <MyBidItem key={auction.id} auction={auction} onView={handleViewAuction} />
                    ))}
                  {tabValue === 2 &&
                    // Followed tab
                    auctions.map((auction) => (
                      <FollowedAuctionItem
                        key={auction.id}
                        auction={auction}
                        onView={handleViewAuction}
                        onUnfollow={handleUnfollowAuction}
                      />
                    ))}
                </>
              ) : (
                <TableRow>
                  <TableCell colSpan={4} align="center" sx={{ py: 4 }}>
                    <Typography color="text.secondary">
                      {tabValue === 0
                        ? "You haven't created any auctions yet"
                        : tabValue === 1
                          ? "You haven't placed any bids yet"
                          : "You aren't following any auctions yet"}
                    </Typography>

                    {tabValue === 0 && (
                      <Button
                        variant="contained"
                        onClick={handleCreateAuction}
                        sx={{
                          mt: 2,
                          bgcolor: "#3498db",
                          "&:hover": {
                            bgcolor: "#2980b9",
                          },
                        }}
                      >
                        Create an auction
                      </Button>
                    )}
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle>Delete Auction</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete this auction? This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)} color="primary">
            Cancel
          </Button>
          <Button onClick={handleDeleteAuction} color="error" autoFocus>
            Delete
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar for notifications */}
      <Snackbar open={snackbarOpen} autoHideDuration={6000} onClose={handleCloseSnackbar} message={snackbarMessage} />
    </Box>
  )
}

export default UserAuctions;