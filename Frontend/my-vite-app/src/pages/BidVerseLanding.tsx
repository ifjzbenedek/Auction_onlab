"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { Box, Typography, Grid, CircularProgress, Alert } from "@mui/material"
import Header from "../components/Header"
import AuctionCard from "../components/AuctionCard"
// Import imageApi as well
import { auctionApi, imageApi } from "../services/api.ts" 
import { AuctionCardDTO } from "../types/auction"
import { CategoryDTO } from "../types/category"
// Assuming your image API returns an array of objects with at least a URL
// Define a simple type for what an image object might look like from your API
interface AuctionImageDTO {
  id: number;
  url: string; // Or whatever property holds the image URL
  // other properties like isPrimary, etc.
}

const BidVerseLanding: React.FC = () => {
  const [auctions, setAuctions] = useState<AuctionCardDTO[]>([])
  const [filters, setFilters] = useState<string[]>([])
  const [searchTerm, setSearchTerm] = useState("")
  const [isImageSearch, setIsImageSearch] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [categories, setCategories] = useState<CategoryDTO[]>([])
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null)
  

  // Aukciók és képeik lekérése
  useEffect(() => {
    const fetchAuctionsAndImages = async () => {
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
        const auctionsData: AuctionCardDTO[] = response.data

        // Ha az auctionsData még nem tartalmaz imageUrl-t, itt lekérjük
        // Ez feltételezi, hogy az AuctionCardDTO-ban van egy 'imageUrl' mező
        if (auctionsData.length > 0 && auctionsData.some(auction => auction.imageUrl === undefined || auction.imageUrl === null || auction.imageUrl === "")) {
          const auctionsWithImages = await Promise.all(
            auctionsData.map(async (auction) => {
              try {
                // Hívd meg az imageApi.getAuctionImages-t minden aukcióhoz
                // Ez visszaad egy tömböt a képekkel (AuctionImageDTO[])
                const imagesResponse = await imageApi.getAuctionImages(auction.id);
                const images: AuctionImageDTO[] = imagesResponse.data;
                
                // Válaszd ki az első képet, vagy egy logikát az elsődleges kép kiválasztására
                const primaryImage = images && images.length > 0 ? images[0] : null;
                
                return { 
                  ...auction, 
                  // Győződj meg róla, hogy az AuctionCardDTO-nak van imageUrl mezője
                  imageUrl: primaryImage ? primaryImage.url : undefined 
                };
              } catch (imgErr) {
                console.error(`Error fetching images for auction ${auction.id}:`, imgErr);
                return { ...auction, imageUrl: undefined }; // Hiba esetén nincs kép URL
              }
            })
          );
          setAuctions(auctionsWithImages);
        } else {
          // Ha az aukciók már tartalmaznak kép URL-eket (pl. a backend adja őket)
          setAuctions(auctionsData);
        }

      } catch (err) {
        setError("Failed to fetch auctions")
        console.error("Error fetching auctions:", err)
      } finally {
        setLoading(false)
      }
    }
  
    fetchAuctionsAndImages()
  }, [filters, searchTerm, isImageSearch, selectedCategory])

  // Kategóriák lekérése
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await auctionApi.getCategories()
        setCategories(response.data)
      } catch (err) {
        console.error("Hiba a kategóriák lekérésekor:", err)
        setCategories([]) // Hiba esetén üres tömb
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
        categories={categories.map(c => c.categoryName)} // Feltételezve, hogy a CategoryDTO-nak van categoryName mezője
      />

      <Box sx={{
          maxWidth: { xs: "100%", lg: "1400px" }, // Vagy a kívánt maximális szélesség
          mx: "auto",
          px: { xs: 2, sm: 3 }, // Reszponzív padding
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
              Próbálj más szűrőket vagy keresési feltételeket.
            </Typography>
          </Box>
        ) : (
          <Grid container spacing={3}> {/* Reszponzív spacing */}
            {auctions.map((auction) => (
              // Az AuctionCardDTO-nak tartalmaznia kell az imageUrl-t
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