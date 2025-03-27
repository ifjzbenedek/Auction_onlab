"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { Box, Typography, Grid, CircularProgress, Alert } from "@mui/material"
import Header from "../components/Header"
import AuctionCard, { type AuctionCardProps } from "../components/AuctionCard"
import { auctionApi } from "../services/api.ts"

interface AuctionResponse {
  id: number
  itemName: string
  createDate: string
  expiredDate: string
  lastBid: number | null
}

const BidVerseLanding: React.FC = () => {
  const [auctions, setAuctions] = useState<AuctionCardProps[]>([])
  const [filters, setFilters] = useState<string[]>([])
  const [searchTerm, setSearchTerm] = useState("")
  const [isImageSearch, setIsImageSearch] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [categories, setCategories] = useState<string[]>([])
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null)

  // Fetch auctions from backend
  useEffect(() => {
    const fetchAuctions = async () => {
      setLoading(true)
      setError(null)
  
      try {
        const params = {
          ...(filters.length > 0 && { status: filters.join(",") }),
          ...(selectedCategory && { category: selectedCategory }),
          ...(searchTerm && { search: searchTerm }),
        }
  
        const response = await auctionApi.getAuctions(params)
  
        const transformedAuctions = response.data.map((auction: AuctionResponse) => ({
          id: auction.id,
          itemName: auction.itemName,
          createDate: auction.createDate,
          expiredDate: auction.expiredDate,
          lastBid: auction.lastBid,
        }))
  
        setAuctions(transformedAuctions)
      } catch {
        // ... hibakezelés
      } finally {
        setLoading(false)
      }
    }
  
    fetchAuctions()
  }, [filters, searchTerm, isImageSearch, selectedCategory])

  // Fetch categories for filtering
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await auctionApi.getCategories()
        const categoryNames = response.data.map(
          (cat: { categoryName: string }) => cat.categoryName
        )
        setCategories(categoryNames)
      } catch (err) {
        console.error("Error fetching categories:", err)
        setCategories([])
      }
    }
    fetchCategories()
  }, [])

  const handleFilterChange = (newFilters: string[]) => {
    setFilters(newFilters)
  }

  const handleSearch = (term: string, imageSearch: boolean) => {
    setSearchTerm(term)
    setIsImageSearch(imageSearch)
  }

  const handleCategoryChange = (category: string | null) => {
    setSelectedCategory(category)
  }

  // Filter auctions based on selected filters
  const filteredAuctions =
    filters.length > 0 ? auctions.filter((auction) => filters.includes(auction.status)) : auctions

  return (
    <Box sx={{ bgcolor: "#f8f9fa", minHeight: "100vh" }}>
      {/* Header Component */}
      <Header
        onFilterChange={handleFilterChange}
        onSearch={handleSearch}
        onCategoryChange={handleCategoryChange}
        categories={Array.isArray(categories) ? categories : []}
        />

      {/* Auction grid */}
      <Box
        sx={{
          maxWidth: { xs: "100%", lg: "1400px" },
          mx: "auto",
          px: { xs: 2, sm: 3 },
          py: 3,
        }}
      >
        {loading ? (
          <Box sx={{ display: "flex", justifyContent: "center", py: 8 }}>
            <CircularProgress />
          </Box>
        ) : error ? (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        ) : filteredAuctions.length === 0 ? (
          <Box sx={{ textAlign: "center", py: 8 }}>
            <Typography variant="h5" color="text.secondary" gutterBottom>
              No auctions found
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Try adjusting your filters or search criteria
            </Typography>
          </Box>
        ) : (
          <Grid container spacing={3}>
            {filteredAuctions.map((auction) => (
              <Grid item xs={12} sm={6} md={4} lg={3} key={auction.id}>
                <AuctionCard {...auction} />
              </Grid>
            ))}
          </Grid>
        )}
      </Box>
    </Box>
  )
}

export default BidVerseLanding;

