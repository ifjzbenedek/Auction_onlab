"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { X } from "lucide-react"
import { Box, Typography, Grid, Paper } from "@mui/material"
import { styled } from "@mui/material/styles"
import { useNavigate } from "react-router-dom" // Importáljuk a useNavigate hookot
import Header from "../components/Header"

// Custom styled components with proper theme typing
const AuctionCard = styled(Paper)({
  display: "flex",
  flexDirection: "column",
  borderRadius: 0,
  boxShadow: "none",
  overflow: "hidden",
  height: "100%",
  transition: "all 0.3s ease",
  "&:hover": {
    transform: "translateY(-5px)",
    boxShadow: "0 10px 20px rgba(0,0,0,0.1)",
  },
});

interface Auction {
  id: number
  name: string
  topBid: number
  timeLeft: string
  status: string
}

const BidVerseLanding: React.FC = () => {
  const [auctions, setAuctions] = useState<Auction[]>([])
  const [filters, setFilters] = useState<string[]>([])
  const [searchTerm, setSearchTerm] = useState("")
  const [isImageSearch, setIsImageSearch] = useState(false)
  const navigate = useNavigate() // Inicializáljuk a navigate függvényt

  // Mock fetch auctions - in a real app, this would call your backend API
  useEffect(() => {
    // This would be replaced with a real API call like:
    // fetch(`/auctions?status=${filter}&search=${searchTerm}`)
    //   .then(response => response.json())
    //   .then(data => setAuctions(data));

    // For now, we'll use mock data
    const mockAuctions = Array(9)
      .fill(null)
      .map((_, i) => ({
        id: i + 1,
        name: "Name",
        topBid: 203,
        timeLeft: "00:22:13",
        status: i % 3 === 0 ? "upcoming" : i % 3 === 1 ? "ending" : "finished",
      }))

    setAuctions(mockAuctions)
  }, [filters, searchTerm, isImageSearch])

  const handleFilterChange = (newFilters: string[]) => {
    setFilters(newFilters)
  }

  const handleSearch = (term: string, imageSearch: boolean) => {
    setSearchTerm(term)
    setIsImageSearch(imageSearch)
  }

  const handleNewAuction = () => {
    console.log("Create new auction")
    // This would navigate to a new auction creation page or open a modal
  }

  // Filter auctions based on selected filters
  const filteredAuctions =
    filters.length > 0 ? auctions.filter((auction) => filters.includes(auction.status)) : auctions

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

  return (
    <Box sx={{ bgcolor: "#f8f9fa", minHeight: "100vh" }}>
      {/* Header Component */}
      <Header onFilterChange={handleFilterChange} onSearch={handleSearch} onNewAuction={handleNewAuction} />

      {/* Auction grid */}
      <Box
        sx={{
          maxWidth: { xs: "100%", lg: "1400px" },
          mx: "auto",
          px: { xs: 1, sm: 2 },
          py: 1,
        }}
      >
        <Grid container spacing={1}>
          {filteredAuctions.map((auction) => (
            <Grid item xs={12} sm={6} md={4} key={auction.id}>
              <AuctionCard onClick={() => navigate(`/auction/${auction.id}`)}> {/* Navigáció hozzáadva */}
                <Box sx={{ position: "relative" }}>
                  <Box
                    sx={{
                      bgcolor: "#f0f0f0",
                      aspectRatio: "1/1",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                    }}
                  >
                    <X size={96} color="#666" />
                  </Box>
                  <Box
                    sx={{
                      position: "absolute",
                      top: 0,
                      right: 0,
                      bgcolor: getStatusColor(auction.status),
                      color: "white",
                      p: 0.5,
                      display: "flex",
                      flexDirection: "column",
                      alignItems: "center",
                    }}
                  >
                    <Typography variant="caption" sx={{ textTransform: "capitalize" }}>
                      {auction.status}
                    </Typography>
                    <Typography variant="caption">{auction.timeLeft}</Typography>
                  </Box>
                </Box>
                <Box sx={{ p: 1 }}>
                  <Typography sx={{ color: "#2c3e50", fontWeight: 500, mb: 0.5 }}>{auction.name}</Typography>
                  <Box sx={{ bgcolor: "#3498db", color: "white", p: 1 }}>
                    <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                      <Typography variant="body2">Top Bid</Typography>
                      <Typography variant="body2" fontWeight="bold">
                        ${auction.topBid}
                      </Typography>
                    </Box>
                  </Box>
                </Box>
              </AuctionCard>
            </Grid>
          ))}
        </Grid>
      </Box>
    </Box>
  )
}

export default BidVerseLanding