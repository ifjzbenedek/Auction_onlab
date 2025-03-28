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
  TextField,
  InputAdornment,
  IconButton,
  Chip,
  Skeleton,
} from "@mui/material"
import { X, Clock, DollarSign, ChevronLeft, ChevronRight, ArrowLeft, Tag } from 'lucide-react'
import { auctionApi } from "../services/api.ts"
import { AuctionBasicDTO } from "../types/auction"



const AuctionDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [auction, setAuction] = useState<AuctionBasicDTO | null>()
  const [loading, setLoading] = useState(true)
  const [bidAmount, setBidAmount] = useState("")
  const [activeImageIndex, setActiveImageIndex] = useState(0)

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
        
        setAuction({
          ...auctionData,
          images: auctionData.images || [], // Üres tömb ha nincs adat
        })
    
        const initialBid = auctionData.lastBid 
          ? auctionData.lastBid + 1 
          : auctionData.minimumPrice || 0
          
        setBidAmount(initialBid.toString())
      } catch (error) {
        console.error("Error fetching auction:", error)
      } finally {
        setLoading(false)
      }
    }
    
    fetchAuction()
  }, [id])

  const handleBidAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setBidAmount(e.target.value)
  }

  const handleMakeBid = () => {
    console.log(`Making bid of $${bidAmount}`)
    alert(`Bid of $${bidAmount} placed successfully!`)
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
        return "#2ecc71" // green
      case "closed":
        return "#e74c3c" // red
      case "pending":
        return "#f39c12" // orange
      default:
        return "#2c3e50" // default dark blue
    }
  }

  const getConditionText = (condition: number) => {
    switch (condition) {
      case 1: return "Új"
      case 2: return "Újszerű"
      case 3: return "Használt"
      default: return "Ismeretlen"
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
                {auction.images.length > 0 ? (
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
                    display: activeImageIndex === auction.images.length - 1 ? "none" : "flex",
                  }}
                  onClick={handleNextImage}
                >
                  <ChevronRight />
                </IconButton>

                {/* Category tag */}
                <Chip
                  label={auction.category.categoryName}
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
                {auction.images.map((image, index) => (
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
          </Grid>

          {/* Right column - Details */}
          <Grid item xs={12} md={6}>
            <Typography variant="h4" fontWeight="bold" color="#2c3e50" gutterBottom>
              {auction.itemName}
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
                {auction.status} in {calculateTimeLeft(auction.expiredDate)}
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
                  ${auction.lastBid?.toFixed(2) || "No bids"}
                </Typography>
              </Box>
            </Box>

            {/* Details grid */}
            <Grid container spacing={2} sx={{ mt: 2 }}>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  Minimum increment
                </Typography>
                <Typography variant="h6" fontWeight="medium" color="#2c3e50">
                  ${auction.minStep?.toFixed(2) || "-"}
                </Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  Minimum price
                </Typography>
                <Typography variant="h6" fontWeight="medium" color="#2c3e50">
                  ${auction.minimumPrice.toFixed(2)}
                </Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  Condition
                </Typography>
                <Typography variant="h6" fontWeight="medium" color="#2c3e50">
                  {getConditionText(auction.condition)}
                </Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  Category
                </Typography>
                <Typography variant="h6" fontWeight="medium" color="#2c3e50">
                  {auction.category.categoryName}
                </Typography>
              </Grid>
            </Grid>

            {/* Bid input */}
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
                  "&:hover": { bgcolor: "#2980b9" },
                }}
              >
                Make a bid
              </Button>
            </Box>

            {/* Description */}
            <Box sx={{ mt: 4 }}>
              <Typography variant="h6" gutterBottom color="#2c3e50">
                Description
              </Typography>
              <Typography variant="body1" color="#2c3e50" sx={{ whiteSpace: "pre-line" }}>
                {auction.description}
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </Box>
    </Box>
  )
}

export default AuctionDetails;