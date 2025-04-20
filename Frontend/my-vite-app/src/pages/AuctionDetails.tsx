"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useParams, useNavigate } from "react-router-dom"
import {
  Box,
  Typography,
  Button,
  Grid,
  Paper,
  Tabs,
  Tab,
  TextField,
  InputAdornment,
  IconButton,
  Skeleton,
  Chip,
  Alert,
  CircularProgress,
  Snackbar,
} from "@mui/material"
import { X, Heart, Clock, ChevronLeft, ChevronRight, ArrowLeft, Tag } from "lucide-react"
import { auctionApi } from "../services/api.ts"
import type { AuctionBasicDTO } from "../types/auction"
import type { BidDTO } from "../types/bid"
import axios from "axios"

interface TabPanelProps {
  children?: React.ReactNode
  index: number
  value: number
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`auction-tabpanel-${index}`}
      aria-labelledby={`auction-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ py: 3 }}>{children}</Box>}
    </div>
  )
}

const AuctionDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [auction, setAuction] = useState<AuctionBasicDTO | null>(null);
  const [bids, setBids] = useState<BidDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [tabValue, setTabValue] = useState(0)
  const [bidAmount, setBidAmount] = useState("")
  const [following, setFollowing] = useState(false)
  const [activeImageIndex, setActiveImageIndex] = useState(0)

  // New state variables for bid functionality
  const [bidLoading, setBidLoading] = useState(false)
  const [bidError, setBidError] = useState<string | null>(null)
  const [bidSuccess, setBidSuccess] = useState(false)
  const [authError, setAuthError] = useState(false)

  const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue)
  }

  const handleFollowAuction = () => {
    setFollowing(!following)
    alert(`You have ${following ? "unfollowed" : "followed"} this auction!`)
  }

  // Idő számítás
  const calculateTimeLeft = (expiredDate: string): string => {
    const now = new Date()
    const end = new Date(expiredDate)
    const diff = end.getTime() - now.getTime()

    if (diff <= 0) return "00:00:00"

    const hours = Math.floor(diff / (1000 * 60 * 60))
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    const seconds = Math.floor((diff % (1000 * 60)) / 1000)

    return `${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`
  }

  // Aukció adatok lekérése
  useEffect(() => {
    const fetchAuction = async () => {
      try {
        const response = await auctionApi.getAuctionById(Number(id))
        const auctionData = response.data || {}
        // Biztosítjuk, hogy az images mindig legyen tömb
        setAuction({
          ...auctionData,
          images: auctionData.images || [],
        })
      } catch (error) {
        console.error("Error fetching auction:", error)
      } finally {
        setLoading(false)
      }
    }
    fetchAuction()
  }, [id])

  useEffect(() => {
    const fetchBids = async () => {
      try {
        const response = await auctionApi.getAuctionBids(Number(id))
        setBids(response.data)
      } catch (error) {
        console.error("Error fetching bids:", error)
      }
    }
    fetchBids()
  }, [id])

  const handleBidAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setBidAmount(e.target.value)
  }

  const handleMakeBid = async () => {
    setBidError(null);
    setBidSuccess(false);
    setAuthError(false);
  
    if (!bidAmount || isNaN(Number(bidAmount)) || Number(bidAmount) <= 0) {
      setBidError("Kérjük, adj meg egy érvényes összeget");
      return;
    }
  
    setBidLoading(true);
    let retries = 3; // Maximum 3 próbálkozás
  
    while (retries > 0) {
      try {
        await auctionApi.placeBid(Number(id), Number(bidAmount));
        
        // Sikeres licit esetén:
        setBidSuccess(true);
        setBidAmount("");
  
        // Frissítsd az aukció adatait ÉS a verziószámot
        const [auctionResponse, bidsResponse] = await Promise.all([
          auctionApi.getAuctionById(Number(id)),
          auctionApi.getAuctionBids(Number(id))
        ]);
        
        setAuction(auctionResponse.data);
        setBids(bidsResponse.data);
        
        retries = 0; // Kilépés a ciklusból
      } catch (error: unknown) {
        retries--;
  
        // Optimista zárolási hiba kezelése
        if (
          axios.isAxiosError(error) &&
          error.response?.data?.message?.includes("ObjectOptimisticLockingFailure")
        ) {
          // Frissítsd az adatokat új próbálkozás előtt
          const auctionResponse = await auctionApi.getAuctionById(Number(id));
          setAuction(auctionResponse.data);
          
          setBidError("Valaki más licitált. Frissítjük az adatokat...");
          await new Promise((resolve) => setTimeout(resolve, 1000)); // Várakozás
          
          if (retries === 0) {
            setBidError("Túl sok ütközés. Kérlek, frissítsd az oldalt!");
          }
        } 
        else {
          break;
        }
      }
    }
    setBidLoading(false);
  };

  const handleLoginRedirect = () => {
    // Redirect to login page using the proxy
    window.location.href = "/oauth2/authorization/google"
  }

  const handlePrevImage = () => {
    if (auction && activeImageIndex > 0) {
      setActiveImageIndex(activeImageIndex - 1)
    }
  }

  const handleNextImage = () => {
    if (auction && activeImageIndex < auction.images.length - 1) {
      setActiveImageIndex(activeImageIndex + 1)
    }
  }

  const handleThumbnailClick = (index: number) => {
    setActiveImageIndex(index)
  }

  const handleBackToList = () => {
    navigate("/")
  }

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case "active":
        return "#00c853" // brighter green
      case "closed":
        return "#f44336" // brighter red
      case "pending":
        return "#ff9800" // brighter orange
      default:
        return "#2c3e50" // default dark blue
    }
  }

  if (loading) {
    return (
      <Box sx={{ bgcolor: "#f8f9fa", minHeight: "100vh", py: 4 }}>
        <Box sx={{ maxWidth: 1200, mx: "auto", px: { xs: 2, md: 3 } }}>
          <Button startIcon={<ArrowLeft size={18} />} sx={{ mb: 3, color: "#1e88e5" }} onClick={handleBackToList}>
            Back to auctions
          </Button>

          <Grid container spacing={4}>
            <Grid item xs={12} md={6}>
              <Skeleton variant="rectangular" height={400} sx={{ borderRadius: 2 }} />
              <Box sx={{ display: "flex", mt: 1, gap: 1 }}>
                {[1, 2, 3, 4].map((_, index) => (
                  <Skeleton key={index} variant="rectangular" width={80} height={80} sx={{ borderRadius: 1 }} />
                ))}
              </Box>
            </Grid>
            <Grid item xs={12} md={6}>
              <Skeleton variant="text" height={60} width="80%" />
              <Skeleton variant="text" height={30} width="40%" sx={{ mt: 2 }} />
              <Skeleton variant="text" height={40} width="60%" sx={{ mt: 2 }} />
              <Skeleton variant="text" height={30} width="30%" sx={{ mt: 2 }} />
              <Skeleton variant="text" height={30} width="30%" sx={{ mt: 1 }} />
              <Box sx={{ display: "flex", gap: 2, mt: 3 }}>
                <Skeleton variant="rectangular" height={50} width="60%" sx={{ borderRadius: 30 }} />
                <Skeleton variant="rectangular" height={50} width="40%" sx={{ borderRadius: 30 }} />
              </Box>
            </Grid>
          </Grid>
        </Box>
      </Box>
    )
  }

  if (!auction) {
    return (
      <Box
        sx={{ bgcolor: "#f8f9fa", minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center" }}
      >
        <Typography variant="h5" color="text.secondary">
          Auction not found
        </Typography>
      </Box>
    )
  }

  return (
    <Box sx={{ bgcolor: "#f8f9fa", minHeight: "100vh", py: 4 }}>
      {/* Authentication error snackbar */}
      <Snackbar
        open={authError}
        autoHideDuration={6000}
        onClose={() => setAuthError(false)}
        anchorOrigin={{ vertical: "top", horizontal: "center" }}
      >
        <Alert
          severity="error"
          onClose={() => setAuthError(false)}
          action={
            <Button color="inherit" size="small" onClick={handleLoginRedirect}>
              Login
            </Button>
          }
        >
          Your session has expired. Please log in again.
        </Alert>
      </Snackbar>

      <Box sx={{ maxWidth: 1200, mx: "auto", px: { xs: 2, md: 3 } }}>
        <Button startIcon={<ArrowLeft size={18} />} sx={{ mb: 3, color: "#1e88e5" }} onClick={handleBackToList}>
          Back to auctions
        </Button>

        <Grid container spacing={4}>
          {/* Left column - Images */}
          <Grid item xs={12} md={6}>
            <Box sx={{ position: "relative", mb: 2 }}>
              <Paper
                elevation={3}
                sx={{
                  borderRadius: 2,
                  overflow: "hidden",
                  aspectRatio: "1/1",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  bgcolor: "#f0f0f0",
                  position: "relative",
                }}
              >
                {auction?.images?.length > 0 ? (
                  <Box
                    component="img"
                    src={auction.images[activeImageIndex]}
                    alt={auction.itemName}
                    sx={{
                      width: "100%",
                      height: "100%",
                      objectFit: "cover",
                    }}
                  />
                ) : (
                  <X size={120} color="#666" />
                )}

                {/* Navigation arrows */}
                <IconButton
                  sx={{
                    position: "absolute",
                    left: 10,
                    top: "50%",
                    transform: "translateY(-50%)",
                    bgcolor: "rgba(255,255,255,0.8)",
                    "&:hover": { bgcolor: "rgba(255,255,255,0.9)" },
                    color: "#1e88e5",
                    display: activeImageIndex === 0 ? "none" : "flex",
                  }}
                  onClick={handlePrevImage}
                >
                  <ChevronLeft />
                </IconButton>

                <IconButton
                  sx={{
                    position: "absolute",
                    right: 10,
                    top: "50%",
                    transform: "translateY(-50%)",
                    bgcolor: "rgba(255,255,255,0.8)",
                    "&:hover": { bgcolor: "rgba(255,255,255,0.9)" },
                    color: "#1e88e5",
                    display: auction && activeImageIndex === auction.images.length - 1 ? "none" : "flex",
                  }}
                  onClick={handleNextImage}
                >
                  <ChevronRight />
                </IconButton>

                {/* Category chip */}
                <Chip
                  label={auction?.category?.categoryName || "Unknown Category"}
                  size="small"
                  icon={<Tag size={14} />}
                  sx={{
                    position: "absolute",
                    left: 16,
                    top: 16,
                    bgcolor: "rgba(255,255,255,0.9)",
                    color: "#1e88e5",
                    fontWeight: 500,
                    "& .MuiChip-icon": { color: "#1e88e5" },
                  }}
                />
              </Paper>

              {/* Thumbnails */}
              <Box sx={{ display: "flex", gap: 1, mt: 1, justifyContent: "center" }}>
              {auction?.images?.map((image, index) => (
                  <Box
                    key={index}
                    component="img"
                    src={image}
                    alt={`Thumbnail ${index + 1}`}
                    onClick={() => handleThumbnailClick(index)}
                    sx={{
                      width: 80,
                      height: 80,
                      objectFit: "cover",
                      cursor: "pointer",
                      borderRadius: 1,
                      border: index === activeImageIndex ? "2px solid #1e88e5" : "2px solid transparent",
                      opacity: index === activeImageIndex ? 1 : 0.7,
                      transition: "all 0.2s ease",
                      "&:hover": {
                        opacity: 1,
                      },
                    }}
                  />
                ))}
              </Box>
            </Box>
          </Grid>

          {/* Right column - Details */}
          <Grid item xs={12} md={6}>
            <Typography variant="h4" fontWeight="bold" color="#2c3e50" gutterBottom>
              {auction?.itemName || "Unknown Item"}
            </Typography>

            {/* Status and timer */}
            <Box
              sx={{
                display: "inline-flex",
                alignItems: "center",
                gap: 1,
                bgcolor: getStatusColor(auction?.status || "unknown"),
                color: "white",
                px: 2,
                py: 1,
                borderRadius: 2,
                mb: 2,
              }}
            >
              <Clock size={18} />
              <Typography variant="subtitle1" sx={{ textTransform: "capitalize" }}>
                {auction ? `${auction.status} in ${calculateTimeLeft(auction.expiredDate)}` : "Loading..."}
              </Typography>
            </Box>

            {/* Price Information */}
            <Box sx={{ mt: 3, mb: 3 }}>
              <Grid container spacing={2}>
                <Grid item xs={4}>
                  <Typography variant="body2" color="text.secondary">
                    Minimum price
                  </Typography>
                  <Typography variant="h6" fontWeight="bold" color="#2c3e50">
                    ${auction?.minimumPrice?.toFixed(2) || "-"}
                  </Typography>
                </Grid>
                <Grid item xs={4}>
                  <Typography variant="body2" color="text.secondary">
                    Minimum increment
                  </Typography>
                  <Typography variant="h6" fontWeight="bold" color="#2c3e50">
                    ${auction?.minStep?.toFixed(2) || "-"}
                  </Typography>
                </Grid>
                <Grid item xs={4}>
                  <Typography variant="body2" color="text.secondary">
                    Top bid
                  </Typography>
                  <Typography variant="h6" fontWeight="bold" color="#1e88e5">
                    ${auction?.lastBid?.toFixed(2) || "No bids yet"}
                  </Typography>
                </Grid>
              </Grid>
            </Box>

            {/* Bid section */}
            <Box sx={{ mt: 3 }}>
              <TextField
                fullWidth
                label="Your bid amount"
                variant="outlined"
                value={bidAmount}
                onChange={handleBidAmountChange}
                InputProps={{
                  startAdornment: <InputAdornment position="start">$</InputAdornment>,
                }}
                sx={{ mb: 2 }}
                disabled={bidLoading}
                error={!!bidError}
                helperText={bidError}
              />

              {bidSuccess && (
                <Alert severity="success" sx={{ mb: 2 }}>
                  Your bid was placed successfully!
                </Alert>
              )}

              <Box sx={{ display: "flex", gap: 2 }}>
                <Button
                  variant="contained"
                  fullWidth
                  size="large"
                  onClick={handleMakeBid}
                  disabled={bidLoading || !bidAmount}
                  sx={{
                    bgcolor: "#1e88e5",
                    color: "white",
                    borderRadius: 30,
                    py: 1.5,
                    textTransform: "none",
                    fontWeight: "bold",
                    "&:hover": {
                      bgcolor: "#1565c0",
                    },
                    boxShadow: "0 4px 10px rgba(30, 136, 229, 0.3)",
                  }}
                >
                  {bidLoading ? <CircularProgress size={24} color="inherit" /> : "Make a bid"}
                </Button>
                <Button
                  variant="outlined"
                  fullWidth
                  size="large"
                  onClick={handleFollowAuction}
                  startIcon={
                    <Heart size={18} fill={following ? "#e74c3c" : "none"} color={following ? "#e74c3c" : undefined} />
                  }
                  sx={{
                    borderRadius: 30,
                    py: 1.5,
                    textTransform: "none",
                    fontWeight: "medium",
                    fontSize: "1rem",
                    borderColor: following ? "#e74c3c" : undefined,
                    color: following ? "#e74c3c" : undefined,
                    "&:hover": {
                      borderColor: following ? "#c0392b" : undefined,
                      color: following ? "#c0392b" : undefined,
                    },
                  }}
                >
                  {following ? "Following" : "Follow auction"}
                </Button>
              </Box>
            </Box>
            {/* Tabs */}
            <Box sx={{ mt: 4, borderBottom: 1, borderColor: "divider" }}>
              <Tabs
                value={tabValue}
                onChange={handleTabChange}
                sx={{
                  "& .MuiTab-root": {
                    textTransform: "none",
                    fontWeight: "medium",
                    fontSize: "1rem",
                  },
                  "& .Mui-selected": {
                    color: "#1e88e5",
                    fontWeight: "bold",
                  },
                  "& .MuiTabs-indicator": {
                    backgroundColor: "#1e88e5",
                  },
                }}
              >
                <Tab label="Description" />
                <Tab label="Details" />
                <Tab label="Bids" />
              </Tabs>
            </Box>

            <TabPanel value={tabValue} index={0}>
              <Typography variant="body1" color="#2c3e50" sx={{ whiteSpace: "pre-line" }}>
                {auction ? auction.description : "No description available"}
              </Typography>
            </TabPanel>

            <TabPanel value={tabValue} index={1}>
              <Box sx={{ mt: 1 }}>
                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    Item State
                  </Typography>
                  <Typography
                    variant="h6"
                    fontWeight="medium"
                    sx={{ color: getStatusColor(auction?.itemState || "unknown") }}
                  >
                    {auction?.itemState || "Unknown"}
                  </Typography>
                </Box>
                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    Condition Rating
                  </Typography>
                  <Typography variant="h6" fontWeight="medium" color="#2c3e50">
                    {auction ? auction.condition.toString() : "Unknown"}
                  </Typography>
                </Box>
                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    Category
                  </Typography>
                  <Typography variant="h6" fontWeight="medium" color="#2c3e50">
                    {auction?.category?.categoryName || "Unknown Category"}
                  </Typography>
                </Box>
              </Box>
            </TabPanel>
            <TabPanel value={tabValue} index={2}>
              <Box sx={{ maxHeight: 400, overflowY: "auto", pr: 2 }}>
              {Array.isArray(bids) && bids.length > 0 ? (
                  bids.map((bid) => (
                    <Paper key={bid.id} sx={{ p: 2, mb: 2, bgcolor: "#fff", borderRadius: 2 }}>
                      <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                        <Box>
                          <Typography variant="body1" fontWeight="medium">
                            {bid.bidder.userName}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            {new Date(bid.timeStamp).toLocaleString("hu-HU", {
                              year: "numeric",
                              month: "long",
                              day: "numeric",
                              hour: "2-digit",
                              minute: "2-digit",
                            })}
                          </Typography>
                        </Box>
                        <Box sx={{ textAlign: "right" }}>
                          <Typography variant="body1" color="#1e88e5" fontWeight="bold">
                            ${bid.value.toFixed(2)}
                          </Typography>
                          {bid.isWinning && (
                            <Chip label="Winning" size="small" sx={{ bgcolor: "#00c853", color: "white", mt: 0.5 }} />
                          )}
                        </Box>
                      </Box>
                    </Paper>
                  ))
                ) : (
                  <Typography color="text.secondary">No bids yet</Typography>
                )}
              </Box>
            </TabPanel>
          </Grid>
        </Grid>
      </Box>
    </Box>
  )
}

export default AuctionDetails;
