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
  Chip,
  Avatar,
  Skeleton,
} from "@mui/material"
import { X, Heart, Clock, DollarSign, ChevronLeft, ChevronRight, ArrowLeft, User, Tag, Share2, MessageCircle } from 'lucide-react'

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

interface Auction {
  id: number
  name: string
  description: string
  details: string
  topBid: number
  minimumIncrement: number
  minimumPrice: number
  timeLeft: string
  status: string
  seller: {
    name: string
    rating: number
  }
  images: string[]
  category: string
}

const AuctionDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [auction, setAuction] = useState<Auction | null>(null)
  const [loading, setLoading] = useState(true)
  const [tabValue, setTabValue] = useState(0)
  const [bidAmount, setBidAmount] = useState("")
  const [following, setFollowing] = useState(false)
  const [activeImageIndex, setActiveImageIndex] = useState(0)

  // Mock fetch auction data
  useEffect(() => {
    // Simulate API call
    setTimeout(() => {
      const mockAuction: Auction = {
        id: Number.parseInt(id || "1"),
        name: "Vintage Camera Collection",
        description:
          "A beautiful collection of vintage cameras from the 1950s and 1960s. All cameras are in working condition and have been carefully restored. The collection includes 5 cameras from different manufacturers.",
        details: "Condition: Excellent\nYear: 1950-1965\nBrand: Various\nShipping: Worldwide\nReturns: Not Accepted",
        topBid: 130,
        minimumIncrement: 5,
        minimumPrice: 115,
        timeLeft: "20:30:12",
        status: "ending",
        seller: {
          name: "VintageCollector",
          rating: 4.8,
        },
        images: [
          "/placeholder.svg?height=400&width=400",
          "/placeholder.svg?height=400&width=400",
          "/placeholder.svg?height=400&width=400",
          "/placeholder.svg?height=400&width=400",
        ],
        category: "Collectibles",
      }
      setAuction(mockAuction)
      setLoading(false)
      setBidAmount((mockAuction.topBid + mockAuction.minimumIncrement).toString())
    }, 800)
  }, [id])

  const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue)
  }

  const handleBidAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setBidAmount(e.target.value)
  }

  const handleMakeBid = () => {
    // In a real app, this would send the bid to the backend
    console.log(`Making bid of $${bidAmount}`)
    alert(`Bid of $${bidAmount} placed successfully!`)

    if (auction) {
      setAuction({
        ...auction,
        topBid: Number.parseFloat(bidAmount),
      })
    }
  }

  const handleFollowAuction = () => {
    setFollowing(!following)
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
    switch (status) {
      case "ending":
        return "#f39c12" // orange
      case "upcoming":
        return "#2ecc71" // green
      case "finished":
        return "#e74c3c" // red
      default:
        return "#2c3e50" // default dark blue
    }
  }

  if (loading) {
    return (
      <Box sx={{ bgcolor: "#f8f9fa", minHeight: "100vh", py: 4 }}>
        <Box sx={{ maxWidth: 1200, mx: "auto", px: { xs: 2, md: 3 } }}>
          <Button startIcon={<ArrowLeft size={18} />} sx={{ mb: 3, color: "#3498db" }} onClick={handleBackToList}>
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
      <Box sx={{ maxWidth: 1200, mx: "auto", px: { xs: 2, md: 3 } }}>
        <Button startIcon={<ArrowLeft size={18} />} sx={{ mb: 3, color: "#3498db" }} onClick={handleBackToList}>
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
                    alt={auction.name}
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
                    color: "#3498db",
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
                    color: "#3498db",
                    display: auction.images && activeImageIndex === auction.images.length - 1 ? "none" : "flex",
                  }}
                  onClick={handleNextImage}
                >
                  <ChevronRight />
                </IconButton>

                {/* Category tag */}
                <Chip
                  label={auction.category}
                  size="small"
                  icon={<Tag size={14} />}
                  sx={{
                    position: "absolute",
                    left: 16,
                    top: 16,
                    bgcolor: "rgba(255,255,255,0.9)",
                    color: "#3498db",
                    fontWeight: 500,
                    "& .MuiChip-icon": { color: "#3498db" },
                  }}
                />
              </Paper>

              {/* Thumbnails */}
              <Box sx={{ display: "flex", gap: 1, mt: 1, justifyContent: "center" }}>
                {auction.images &&
                  auction.images.map((image, index) => (
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
                        border: index === activeImageIndex ? "2px solid #3498db" : "2px solid transparent",
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

            {/* Seller info */}
            <Paper sx={{ p: 2, borderRadius: 2, mt: 2 }}>
              <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                <Avatar sx={{ bgcolor: "#3498db" }}>
                  <User size={20} />
                </Avatar>
                <Box>
                  <Typography variant="subtitle2">{auction.seller.name}</Typography>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
                    <Typography variant="body2" color="text.secondary">
                      Rating:
                    </Typography>
                    <Typography variant="body2" fontWeight="bold" color="#f39c12">
                      {auction.seller.rating}/5
                    </Typography>
                  </Box>
                </Box>
                <Button
                  variant="outlined"
                  size="small"
                  sx={{ ml: "auto", borderRadius: 20, textTransform: "none" }}
                  startIcon={<MessageCircle size={16} />}
                >
                  Contact
                </Button>
              </Box>
            </Paper>

            {/* Share button */}
            <Button
              fullWidth
              variant="outlined"
              sx={{ mt: 2, borderRadius: 2, textTransform: "none" }}
              startIcon={<Share2 size={18} />}
            >
              Share this auction
            </Button>
          </Grid>

          {/* Right column - Details */}
          <Grid item xs={12} md={6}>
            <Typography variant="h4" fontWeight="bold" color="#2c3e50" gutterBottom>
              {auction.name}
            </Typography>

            {/* Status and timer */}
            <Box
              sx={{
                display: "inline-flex",
                alignItems: "center",
                gap: 1,
                bgcolor: getStatusColor(auction.status),
                color: "white",
                px: 2,
                py: 1,
                borderRadius: 2,
                mb: 2,
              }}
            >
              <Clock size={18} />
              <Typography variant="subtitle1" sx={{ textTransform: "capitalize" }}>
                {auction.status} in {auction.timeLeft}
              </Typography>
            </Box>

            {/* Current bid */}
            <Box sx={{ mt: 3 }}>
              <Typography variant="body1" color="text.secondary">
                Current bid
              </Typography>
              <Box sx={{ display: "flex", alignItems: "baseline", gap: 1 }}>
                <DollarSign size={24} color="#3498db" />
                <Typography variant="h3" fontWeight="bold" color="#3498db">
                  {auction.topBid}
                </Typography>
              </Box>
            </Box>

            {/* Minimum info */}
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  Minimum increment
                </Typography>
                <Typography variant="h6" fontWeight="medium" color="#2c3e50">
                  ${auction.minimumIncrement}
                </Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  Minimum price
                </Typography>
                <Typography variant="h6" fontWeight="medium" color="#2c3e50">
                  ${auction.minimumPrice}
                </Typography>
              </Grid>
            </Grid>

            {/* Bid input and buttons */}
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
              />

              <Box sx={{ display: "flex", gap: 2 }}>
                <Button
                  variant="contained"
                  fullWidth
                  size="large"
                  onClick={handleMakeBid}
                  sx={{
                    bgcolor: "#3498db",
                    color: "white",
                    borderRadius: 30,
                    py: 1.5,
                    textTransform: "none",
                    fontWeight: "bold",
                    fontSize: "1rem",
                    "&:hover": {
                      bgcolor: "#2980b9",
                    },
                  }}
                >
                  Make a bid
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

            {/* Tabs for description and details */}
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
                    color: "#3498db",
                    fontWeight: "bold",
                  },
                  "& .MuiTabs-indicator": {
                    backgroundColor: "#3498db",
                  },
                }}
              >
                <Tab label="Description" id="auction-tab-0" aria-controls="auction-tabpanel-0" />
                <Tab label="Details" id="auction-tab-1" aria-controls="auction-tabpanel-1" />
              </Tabs>
            </Box>

            <TabPanel value={tabValue} index={0}>
              <Typography variant="body1" color="#2c3e50" sx={{ whiteSpace: "pre-line" }}>
                {auction.description}
              </Typography>
            </TabPanel>

            <TabPanel value={tabValue} index={1}>
              <Typography variant="body1" color="#2c3e50" sx={{ whiteSpace: "pre-line" }}>
                {auction.details}
              </Typography>
            </TabPanel>
          </Grid>
        </Grid>
      </Box>
    </Box>
  )
}

export default AuctionDetails;
