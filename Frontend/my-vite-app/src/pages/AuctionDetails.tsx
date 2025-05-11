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
import { auctionApi, imageApi } from "../services/api.ts" // imageApi importálva
import type { AuctionBasicDTO } from "../types/auction"
import type { BidDTO } from "../types/bid"
import axios from "axios"

// Ideális esetben ez a típus a src/types mappában lenne definiálva és onnan importálva
interface AuctionImageDTO {
  id: number; // Vagy bármilyen más azonosító
  url: string; // A kép URL-je
}

interface TabPanelProps {
// ...existing code...
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
    // Itt API hívás történhet a követés mentésére a backend-en
    alert(`You have ${following ? "unfollowed" : "followed"} this auction!`)
  }

  // Idő számítás
  const calculateTimeLeft = (expiredDate: string): string => {
// ...existing code...
    const now = new Date()
    const end = new Date(expiredDate)
    const diff = end.getTime() - now.getTime()

    if (diff <= 0) return "00:00:00"

    const hours = Math.floor(diff / (1000 * 60 * 60))
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    const seconds = Math.floor((diff % (1000 * 60)) / 1000)

    return `${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`
  }

  // Aukció adatok és képek lekérése
  useEffect(() => {
    const fetchAuctionAndImages = async () => {
      if (!id) {
        setLoading(false);
        setAuction(null);
        console.error("Auction ID is missing.");
        return;
      }
      setLoading(true);
      try {
        const auctionResponse = await auctionApi.getAuctionById(Number(id));
        const auctionData: AuctionBasicDTO | null = auctionResponse.data;

        if (!auctionData) {
          setAuction(null);
          // Consider navigating to a 404 page or showing a more prominent "not found" message
          throw new Error(`Auction with ID ${id} not found`);
        }

        let imageURLs: string[] = auctionData.images || [];

        // Ha az aukciós adatok nem tartalmazzák a képeket, vagy üres a tömb,
        // akkor próbáljuk meg őket külön lekérni.
        if (!imageURLs.length) {
          try {
            const imagesResponse = await imageApi.getAuctionImages(Number(id));
            // Feltételezzük, hogy imagesResponse.data AuctionImageDTO[] típusú
            if (imagesResponse.data && Array.isArray(imagesResponse.data)) {
              imageURLs = imagesResponse.data.map((img: AuctionImageDTO) => img.url);
            }
          } catch (imgError) {
            console.error(`Error fetching images for auction ${id}:`, imgError);
            // Hiba esetén marad az auctionData-ból kapott (valószínűleg üres) képlista
            imageURLs = auctionData.images || [];
          }
        }
        
        setAuction({
          ...auctionData,
          images: imageURLs, // Biztosítjuk, hogy az images mindig stringek tömbje legyen
        });

      } catch (error) {
        console.error("Error fetching auction details:", error);
        setAuction(null); // Hiba esetén az aukciót null-ra állítjuk
      } finally {
        setLoading(false);
      }
    };

    fetchAuctionAndImages();
  }, [id]);

  useEffect(() => {
// ...existing code...
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
// ...existing code...
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
        
        // Újra lekérjük a képeket is, ha szükséges, vagy frissítjük a meglévő aukciós adatokat
        const updatedAuctionData = auctionResponse.data;
        let updatedImageURLs = updatedAuctionData.images || [];
        if(!updatedImageURLs.length) {
            try {
                const imagesResponse = await imageApi.getAuctionImages(Number(id));
                if (imagesResponse.data && Array.isArray(imagesResponse.data)) {
                    updatedImageURLs = imagesResponse.data.map((img: AuctionImageDTO) => img.url);
                }
            } catch (imgError) {
                console.error(`Error fetching images for auction ${id} after bid:`, imgError);
            }
        }

        setAuction({...updatedAuctionData, images: updatedImageURLs });
        setBids(bidsResponse.data);
        
        retries = 0; // Kilépés a ciklusból
      } catch (error: unknown) {
        retries--;
  
        // Optimista zárolási hiba kezelése
        if (
          axios.isAxiosError(error) &&
          error.response?.status === 401 // Unauthorized
        ) {
          setAuthError(true);
          setBidError("Your session has expired or you are not logged in. Please log in to place a bid.");
          retries = 0; // Stop retrying on auth error
          break; 
        } else if (
          axios.isAxiosError(error) &&
          error.response?.data?.message?.includes("ObjectOptimisticLockingFailure")
        ) {
          // Frissítsd az adatokat új próbálkozás előtt
          const auctionResponseRetry = await auctionApi.getAuctionById(Number(id));
          const auctionDataRetry = auctionResponseRetry.data;
          let imagesRetry = auctionDataRetry.images || [];
           if(!imagesRetry.length) {
            try {
                const imagesResponseRetry = await imageApi.getAuctionImages(Number(id));
                if (imagesResponseRetry.data && Array.isArray(imagesResponseRetry.data)) {
                    imagesRetry = imagesResponseRetry.data.map((img: AuctionImageDTO) => img.url);
                }
            } catch (imgError) {
                console.error(`Error fetching images for auction ${id} during retry:`, imgError);
            }
        }
          setAuction({...auctionDataRetry, images: imagesRetry});
          
          setBidError("Someone else bid or the auction details changed. We've updated the information. Please try again if your bid is still valid.");
          await new Promise((resolve) => setTimeout(resolve, 1000)); // Várakozás
          
          if (retries === 0) {
            setBidError("There were too many conflicts. Please refresh the page and try again.");
          }
        } 
        else {
          // Handle other errors (e.g., bid too low, auction closed)
          if (axios.isAxiosError(error) && error.response) {
            setBidError(error.response.data?.message || "Failed to place bid. Please try again.");
          } else if (error instanceof Error) {
            setBidError(error.message);
          } else {
            setBidError("An unknown error occurred while placing the bid.");
          }
          retries = 0; // Stop retrying for other errors
          break;
        }
      }
    }
    setBidLoading(false);
  };

  const handleLoginRedirect = () => {
// ...existing code...
    // Redirect to login page using the proxy
    window.location.href = "/oauth2/authorization/google"
  }

  const handlePrevImage = () => {
    if (auction && auction.images && activeImageIndex > 0) {
      setActiveImageIndex(activeImageIndex - 1)
    }
  }

  const handleNextImage = () => {
    if (auction && auction.images && activeImageIndex < auction.images.length - 1) {
      setActiveImageIndex(activeImageIndex + 1)
    }
  }

  const handleThumbnailClick = (index: number) => {
// ...existing code...
    setActiveImageIndex(index)
  }

  const handleBackToList = () => {
    navigate("/")
  }

  const getStatusColor = (status: string) => {
// ...existing code...
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
          Auction not found or failed to load.
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
        onClose={() => { setAuthError(false); setBidError(null);}} // Clear bidError as well
        anchorOrigin={{ vertical: "top", horizontal: "center" }}
      >
        <Alert
          severity="error"
          onClose={() => { setAuthError(false); setBidError(null);}}
          action={
            <Button color="inherit" size="small" onClick={handleLoginRedirect}>
              Login
            </Button>
          }
        >
          {bidError || "Your session has expired. Please log in again."}
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
                {auction.images && auction.images.length > 0 ? (
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
                    display: !(auction.images && auction.images.length > 1 && activeImageIndex > 0) ? "none" : "flex",
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
                    display: !(auction.images && auction.images.length > 1 && activeImageIndex < auction.images.length - 1) ? "none" : "flex",
                  }}
                  onClick={handleNextImage}
                >
                  <ChevronRight />
                </IconButton>

                {/* Category chip */}
                <Chip
                  label={auction.category?.categoryName || "Unknown Category"}
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
              <Box sx={{ display: "flex", gap: 1, mt: 1, justifyContent: "center", flexWrap: "wrap" }}>
              {auction.images && auction.images.map((image, index) => (
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
              {auction.itemName || "Unknown Item"}
            </Typography>

            {/* Status and timer */}
            <Box
              sx={{
                display: "inline-flex",
                alignItems: "center",
                gap: 1,
                bgcolor: getStatusColor(auction.status || "unknown"),
                color: "white",
                px: 2,
                py: 1,
                borderRadius: 2,
                mb: 2,
              }}
            >
              <Clock size={18} />
              <Typography variant="subtitle1" sx={{ textTransform: "capitalize" }}>
                {`${auction.status} in ${calculateTimeLeft(auction.expiredDate)}`}
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
                    ${auction.minimumPrice?.toFixed(2) || "-"}
                  </Typography>
                </Grid>
                <Grid item xs={4}>
                  <Typography variant="body2" color="text.secondary">
                    Minimum increment
                  </Typography>
                  <Typography variant="h6" fontWeight="bold" color="#2c3e50">
                    ${auction.minStep?.toFixed(2) || "-"}
                  </Typography>
                </Grid>
                <Grid item xs={4}>
                  <Typography variant="body2" color="text.secondary">
                    Top bid
                  </Typography>
                  <Typography variant="h6" fontWeight="bold" color="#1e88e5">
                    ${auction.lastBid?.toFixed(2) || "No bids yet"}
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
                type="number" // Ensure numeric input
                value={bidAmount}
                onChange={handleBidAmountChange}
                InputProps={{
                  startAdornment: <InputAdornment position="start">$</InputAdornment>,
                }}
                sx={{ mb: 2 }}
                disabled={bidLoading || auction.status?.toLowerCase() !== 'active'}
                error={!!bidError}
                helperText={bidError || (auction.status?.toLowerCase() !== 'active' ? `Auction is ${auction.status}. Bidding not allowed.` : '')}
              />

              {bidSuccess && (
                <Alert severity="success" sx={{ mb: 2 }} onClose={() => setBidSuccess(false)}>
                  Your bid was placed successfully!
                </Alert>
              )}

              <Box sx={{ display: "flex", gap: 2 }}>
                <Button
                  variant="contained"
                  fullWidth
                  size="large"
                  onClick={handleMakeBid}
                  disabled={bidLoading || !bidAmount || auction.status?.toLowerCase() !== 'active'}
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
                    <Heart size={18} fill={following ? "#e74c3c" : "none"} color={following ? "#e74c3c" : "#6c757d"} />
                  }
                  sx={{
                    borderRadius: 30,
                    py: 1.5,
                    textTransform: "none",
                    fontWeight: "medium",
                    fontSize: "1rem",
                    borderColor: following ? "#e74c3c" : "#ced4da",
                    color: following ? "#e74c3c" : "#495057",
                    "&:hover": {
                      borderColor: following ? "#c0392b" : "#adb5bd",
                      color: following ? "#c0392b" : "#343a40",
                      bgcolor: alpha(following ? "#e74c3c" : "#6c757d", 0.04)
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
                {auction.description || "No description available"}
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
                    sx={{ color: getStatusColor(auction.itemState || "unknown") }}
                  >
                    {auction.itemState || "Unknown"}
                  </Typography>
                </Box>
                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    Condition Rating
                  </Typography>
                  <Typography variant="h6" fontWeight="medium" color="#2c3e50">
                    {auction.condition.toString() || "Unknown"}
                  </Typography>
                </Box>
                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    Category
                  </Typography>
                  <Typography variant="h6" fontWeight="medium" color="#2c3e50">
                    {auction.category?.categoryName || "Unknown Category"}
                  </Typography>
                </Box>
              </Box>
            </TabPanel>
            <TabPanel value={tabValue} index={2}>
              <Box sx={{ maxHeight: 400, overflowY: "auto", pr: 2 }}>
              {Array.isArray(bids) && bids.length > 0 ? (
                  bids.sort((a, b) => new Date(b.timeStamp).getTime() - new Date(a.timeStamp).getTime()) // Sort bids by time, newest first
                  .map((bid) => (
                    <Paper key={bid.id} sx={{ p: 2, mb: 2, bgcolor: "#fff", borderRadius: 2, boxShadow: "0 2px 10px rgba(0,0,0,0.05)" }}>
                      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        <Box>
                          <Typography variant="body1" fontWeight="medium" color="#2c3e50">
                            {bid.bidder.userName}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            {new Date(bid.timeStamp).toLocaleString("hu-HU", {
                              year: "numeric",
                              month: "short",
                              day: "numeric",
                              hour: "2-digit",
                              minute: "2-digit",
                            })}
                          </Typography>
                        </Box>
                        <Box sx={{ textAlign: "right" }}>
                          <Typography variant="h6" color="#1e88e5" fontWeight="bold">
                            ${bid.value.toFixed(2)}
                          </Typography>
                          {/* A backendnek kellene megmondania, hogy melyik a nyerő licit, ha az aukció lezárult */}
                          {/* Példa: if (auction.status === 'closed' && bid.isWinning) */}
                          {/* Jelenleg a legmagasabb licit nem feltétlenül a nyerő, ha az aukció még aktív */}
                        </Box>
                      </Box>
                    </Paper>
                  ))
                ) : (
                  <Typography color="text.secondary" sx={{textAlign: 'center', py:3}}>No bids placed yet.</Typography>
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