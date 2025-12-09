"use client"

import { useState, useEffect, useCallback, useMemo } from "react"
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
  TextField,
  AlertTitle,
  AppBar,
  Toolbar,
  IconButton,
} from "@mui/material"
import ArrowBackIcon from "@mui/icons-material/ArrowBack"
import MyAuctionItem from "../components/auction-list-items/MyAuctionItem"
import MyBidItem from "../components/auction-list-items/MyBidItem"
import FollowedAuctionItem from "../components/auction-list-items/FollowedAuctionItem"
import { auctionApi } from "../services/api"
import type { AuctionCardDTO, AuctionBasicDTO } from "../types/auction"

interface AuctionWithTime extends AuctionCardDTO {
  remainingTime: string
  highestBid: number
  yourBid?: number
}

interface EditAuctionFormData {
  itemName: string
  description: string
  minimumPrice: number
  minStep: number
}

function UserAuctions() {
  const navigate = useNavigate()
  const location = useLocation()
  
  // Calculate tabValue directly from URL using useMemo to avoid recalculation
  const tabValue = useMemo(() => {
    const params = new URLSearchParams(location.search)
    const tab = params.get("tab")
    if (tab === "bids") return 1
    if (tab === "followed") return 2
    return 0
  }, [location.search])
  
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [auctions, setAuctions] = useState<AuctionWithTime[]>([])

  // Dialog state
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [auctionToDelete, setAuctionToDelete] = useState<number | null>(null)

  // Edit dialog state
  const [editDialogOpen, setEditDialogOpen] = useState(false)
  const [auctionToEdit, setAuctionToEdit] = useState<number | null>(null)
  const [originalDescription, setOriginalDescription] = useState("")
  const [editFormData, setEditFormData] = useState<EditAuctionFormData>({
    itemName: "",
    description: "",
    minimumPrice: 0,
    minStep: 0,
  })
  const [editLoading, setEditLoading] = useState(false)
  const [editError, setEditError] = useState<string | null>(null)

  // Snackbar state
  const [snackbarOpen, setSnackbarOpen] = useState(false)
  const [snackbarMessage, setSnackbarMessage] = useState("")

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
  const formatAuctionData = useCallback((auctions: AuctionCardDTO[]): AuctionWithTime[] => {
    return auctions.map((auction) => ({
      ...auction,
      remainingTime: calculateTimeLeft(auction.expiredDate),
      highestBid: auction.lastBid || 0,
    }))
  }, [])

  // Fetch auctions based on current tab
  const fetchAuctions = useCallback(async () => {
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
          setSnackbarMessage("Followed auctions functionality is not fully implemented on the backend")
          setSnackbarOpen(true)
          setAuctions([])
          break
      }
    } catch (err: unknown) {
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
  }, [tabValue, formatAuctionData])

  // Fetch auctions when tab changes OR on initial mount
  useEffect(() => {
    fetchAuctions()
  }, [tabValue, fetchAuctions])

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
    // Update URL to reflect current tab
    const params = new URLSearchParams()
    if (newValue === 1) {
      params.set("tab", "bids")
    } else if (newValue === 2) {
      params.set("tab", "followed")
    }

    const newUrl = params.toString() ? `${location.pathname}?${params.toString()}` : location.pathname
    navigate(newUrl)
  }

  const handleViewAuction = (id: number) => {
    navigate(`/auction/${id}`)
  }

  const handleEditClick = async (id: number) => {
    setAuctionToEdit(id)
    setEditError(null)
    setEditLoading(true)

    try {
      // Fetch the full auction details
      const response = await auctionApi.getAuctionById(id)
      const auction = response.data

      setOriginalDescription(auction.description || "")
      setEditFormData({
        itemName: auction.itemName || "",
        description: auction.description || "",
        minimumPrice: auction.minimumPrice || 0,
        minStep: auction.minStep || 0,
      })

      setEditDialogOpen(true)
    } catch {
      setSnackbarMessage("Failed to load auction details for editing")
      setSnackbarOpen(true)
    } finally {
      setEditLoading(false)
    }
  }

  const handleEditFormChange = (field: keyof EditAuctionFormData, value: string | number) => {
    setEditFormData({
      ...editFormData,
      [field]: value,
    })
  }

  const handleUpdateAuction = async () => {
    if (auctionToEdit === null) return

    // Validate that the description is only extended, not shortened
    if (editFormData.description.length < originalDescription.length) {
      setEditError("The description can only be extended, not shortened.")
      return
    }

    // Check if the description starts with the original description
    if (!editFormData.description.startsWith(originalDescription)) {
      setEditError("You can only add text to the end of the description, not modify existing text.")
      return
    }

    setEditLoading(true)
    setEditError(null)

    try {
      // Get the full auction first
      const response = await auctionApi.getAuctionById(auctionToEdit)
      const fullAuction = response.data

      // Update only the description field
      const updatedAuction: Partial<AuctionBasicDTO> = {
        ...fullAuction,
        description: editFormData.description,
      }

      await auctionApi.updateAuction(auctionToEdit, updatedAuction)

      setEditDialogOpen(false)
      setSnackbarMessage("Auction description updated successfully")
      setSnackbarOpen(true)

      // Refresh the auctions list
      fetchAuctions()
    } catch (err: unknown) {
      if (
        typeof err === "object" &&
        err !== null &&
        "response" in err &&
        (err as { response?: { data?: string } }).response &&
        (err as { response: { data?: string } }).response.data
      ) {
        setEditError(`Failed to update auction: ${(err as { response: { data: string } }).response.data}`)
      } else {
        setEditError("Failed to update auction. Please try again.")
      }
    } finally {
      setEditLoading(false)
    }
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
    } catch {
      setSnackbarMessage("Failed to delete auction. Please try again.")
      setSnackbarOpen(true)
    }
  }

  const handleUnfollowAuction = async (id: number) => {
    try {
      // Since the backend doesn't have an unfollow endpoint yet, show a message
      setSnackbarMessage("Unfollow functionality is not implemented yet")
      setSnackbarOpen(true)

      setAuctions(auctions.filter((auction) => auction.id !== id))
    } catch {
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
      <AppBar position="static" sx={{ bgcolor: "white", boxShadow: 1 }}>
        <Toolbar>
          <IconButton
            edge="start"
            onClick={() => navigate("/")}
            sx={{ mr: 2, color: "#3498db" }}
          >
            <ArrowBackIcon />
          </IconButton>
          <Typography
            variant="h6"
            component="div"
            sx={{ flexGrow: 1, color: "#2c3e50", fontWeight: "bold", cursor: "pointer" }}
            onClick={() => navigate("/")}
          >
            BidVerse
          </Typography>
        </Toolbar>
      </AppBar>

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
                        onEdit={handleEditClick}
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

      {/* Edit Auction Dialog */}
      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Edit Auction Description</DialogTitle>
        <DialogContent>
          <Alert severity="info" sx={{ mb: 3, mt: 1 }}>
            <AlertTitle>Editing Restrictions</AlertTitle>
            You can only edit the description field, and you can only add text to the end of the existing description.
            The original text cannot be modified or shortened.
          </Alert>

          {editError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {editError}
            </Alert>
          )}

          <TextField
            label="Item Name"
            fullWidth
            margin="normal"
            value={editFormData.itemName}
            disabled
            InputProps={{
              readOnly: true,
            }}
          />

          <TextField
            label="Description"
            fullWidth
            margin="normal"
            multiline
            rows={6}
            value={editFormData.description}
            onChange={(e) => handleEditFormChange("description", e.target.value)}
            helperText="You can only add text to the end of the description"
          />

          <TextField
            label="Minimum Price"
            fullWidth
            margin="normal"
            type="number"
            value={editFormData.minimumPrice}
            disabled
            InputProps={{
              readOnly: true,
              startAdornment: (
                <Box component="span" sx={{ mr: 1 }}>
                  $
                </Box>
              ),
            }}
          />

          <TextField
            label="Minimum Bid Increment"
            fullWidth
            margin="normal"
            type="number"
            value={editFormData.minStep}
            disabled
            InputProps={{
              readOnly: true,
              startAdornment: (
                <Box component="span" sx={{ mr: 1 }}>
                  $
                </Box>
              ),
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)} color="primary">
            Cancel
          </Button>
          <Button onClick={handleUpdateAuction} color="primary" variant="contained" disabled={editLoading}>
            {editLoading ? <CircularProgress size={24} /> : "Update Description"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar for notifications */}
      <Snackbar open={snackbarOpen} autoHideDuration={6000} onClose={handleCloseSnackbar} message={snackbarMessage} />
    </Box>
  )
}

export default UserAuctions;