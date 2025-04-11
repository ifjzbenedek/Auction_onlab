"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { Box, Typography, Grid, CircularProgress, Alert } from "@mui/material"
import Header from "../components/Header"
import AuctionCard from "../components/AuctionCard"
import { auctionApi } from "../services/api.ts"
import { AuctionCardDTO } from "../types/auction"
import { CategoryDTO } from "../types/category"

const BidVerseLanding: React.FC = () => {
  const [auctions, setAuctions] = useState<AuctionCardDTO[]>([])
  const [filters, setFilters] = useState<string[]>([])
  const [searchTerm, setSearchTerm] = useState("")
  const [isImageSearch, setIsImageSearch] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [categories, setCategories] = useState<CategoryDTO[]>([])
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null)

  // Aukciók lekérése
  useEffect(() => {
    const fetchAuctions = async () => {
      setLoading(true)
      setError(null)
  
      try {
        const params = {
          ...(filters.length > 0 && { status: filters.join(",") }),
          category: selectedCategory || undefined,
          search: searchTerm || undefined,
          imageSearch: isImageSearch ? "true" : undefined,
        }

        const filteredParams = Object.fromEntries(
          Object.entries(params).filter(([, value]) => value !== undefined)
        );

        const response = await auctionApi.getAuctions(filteredParams as Record<string, string | number>)
        setAuctions(response.data)
      } catch (err) {
        setError("Failed to fetch auctions")
        console.error("Error fetching auctions:", err)
      } finally {
        setLoading(false)
      }
    }
  
    fetchAuctions()
  }, [filters, searchTerm, isImageSearch, selectedCategory])

  // Kategóriák lekérése
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await auctionApi.getCategories()
        setCategories(response.data)
      } catch (err) {
        console.error("Hiba a kategóriák lekérésekor:", err)
        setCategories([])
      }
    }
    fetchCategories()
  }, [])

  // Szűrők kezelése
  const handleFilterChange = (newFilters: string[]) => {
    setFilters(newFilters)
  }

  // Keresés kezelése
  const handleSearch = (term: string, imageSearch: boolean) => {
    setSearchTerm(term)
    setIsImageSearch(imageSearch)
  }

  // Kategória váltás
  const handleCategoryChange = (category: string | null) => {
    setSelectedCategory(category)
  }

  return (
    <Box sx={{ bgcolor: "#f8f9fa", minHeight: "100vh" }}>
      <Header
        onFilterChange={handleFilterChange}
        onSearch={handleSearch}
        onCategoryChange={handleCategoryChange}
        categories={categories.map(c => c.categoryName)}
      />

      <Box sx={{
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
        ) : auctions.length === 0 ? (
          <Box sx={{ textAlign: "center", py: 8 }}>
            <Typography variant="h5" color="text.secondary" gutterBottom>
              Nincs találat
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Próbálj más szűrőket vagy keresési feltételeket
            </Typography>
          </Box>
        ) : (
          <Grid container spacing={3}>
            {auctions.map((auction) => (
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