"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
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
} from "@mui/material"
import Header from "../components/Header"
import MyAuctionItem from "../components/auction-list-items/MyAuctionItem"
import MyBidItem from "../components/auction-list-items/MyBidItem"
import FollowedAuctionItem from "../components/auction-list-items/FollowedAuctionItem"

interface Auction {
  id: number
  name: string
  remainingTime: string
  highestBid: number
  image: string
  yourBid?: number
}

const UserAuctions: React.FC = () => {
  const navigate = useNavigate()
  const [tabValue, setTabValue] = useState(0)
  const [loading, setLoading] = useState(true)
  const [auctions, setAuctions] = useState<Auction[]>([])

  useEffect(() => {
    // Simulate API call
    setTimeout(() => {
      const mockAuctions = Array(5)
        .fill(null)
        .map((_, i) => ({
          id: i + 1,
          name: `Vintage Item ${i + 1}`,
          remainingTime: `${Math.floor(Math.random() * 24)}:${Math.floor(Math.random() * 60)}:${Math.floor(Math.random() * 60)}`,
          highestBid: Math.floor(Math.random() * 500) + 50,
          image: "/placeholder.svg?height=100&width=100",
          yourBid: tabValue === 1 ? Math.floor(Math.random() * 450) + 50 : undefined, // Only for My Bids tab
        }))

      setAuctions(mockAuctions)
      setLoading(false)
    }, 800)
  }, [tabValue])

  const handleTabChange = (_: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue)
    setLoading(true) // Reset loading state when changing tabs
  }

  const handleViewAuction = (id: number) => {
    navigate(`/auction/${id}`)
  }

  const handleEditAuction = (id: number) => {
    // In a real app, this would navigate to an edit page
    console.log("Edit auction:", id)
  }

  const handleDeleteAuction = (id: number) => {
    // In a real app, this would show a confirmation dialog
    if (window.confirm("Are you sure you want to delete this auction?")) {
      setAuctions(auctions.filter((auction) => auction.id !== id))
    }
  }

  const handleUnfollowAuction = (id: number) => {
    // In a real app, this would make an API call to unfollow
    if (window.confirm("Are you sure you want to unfollow this auction?")) {
      setAuctions(auctions.filter((auction) => auction.id !== id))
    }
  }

  const handleCreateAuction = () => {
    navigate("/upload-auction")
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
    <Box sx={{ bgcolor: "#f8f9fa", minHeight: "100vh" }}>
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

        <TableContainer component={Paper} sx={{ boxShadow: "0 2px 10px rgba(0,0,0,0.05)" }}>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: "#f5f5f5" }}>
                <TableCell width="50"></TableCell>
                <TableCell>Name</TableCell>
                <TableCell>Remaining time</TableCell>
                <TableCell>{tabValue === 0 ? "Highest Bid" : tabValue === 1 ? "Your Bid" : "Current Bid"}</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={5} align="center" sx={{ py: 4 }}>
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
                        onDelete={handleDeleteAuction}
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
                  <TableCell colSpan={5} align="center" sx={{ py: 4 }}>
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
    </Box>
  )
}

export default UserAuctions;

