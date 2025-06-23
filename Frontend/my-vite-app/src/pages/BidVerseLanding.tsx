"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { Box, Typography, Grid, CircularProgress, Alert } from "@mui/material"
import Header from "../components/Header"
import AuctionCard from "../components/AuctionCard"
// Import imageApi as well
import { auctionApi, imageApi } from "../services/api.ts" 
import { AuctionCardDTO } from "../types/auction"
import { AuctionImageDTO } from "../types/image"
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
  

  // Aukciók és képeik lekérése
  useEffect(() => {
    // Aukciók és képeik lekérése
const fetchAuctionsAndImages = async () => {
  try {
    setLoading(true);
    
    const params: Record<string, string | number> = {};
    if (selectedCategory) {
      params.categoryId = selectedCategory;
    }
    if (searchTerm.trim()) {
      params.search = searchTerm.trim();
    }

    const auctionsResponse = await auctionApi.getAuctions(params);
    const auctionsData: AuctionCardDTO[] = auctionsResponse.data;

    // Képek lekérése minden aukcióhoz
    const auctionsWithImages = await Promise.all(
      auctionsData.map(async (auction) => {
        try {
          const imagesResponse = await imageApi.getAuctionImages(auction.id!);
          const images: AuctionImageDTO[] = imagesResponse.data || [];
          
          // Sort images by orderIndex and isPrimary (primary first, then by orderIndex)
          const sortedImages = images.sort((a, b) => {
            if (a.isPrimary && !b.isPrimary) return -1;
            if (!a.isPrimary && b.isPrimary) return 1;
            return a.orderIndex - b.orderIndex;
          });
          
          // Extract cloudinaryUrl from sorted images
          const imageUrls = sortedImages.map(img => img.cloudinaryUrl);
          
          return {
            id: auction.id!,
            itemName: auction.itemName,
            createDate: auction.createDate!,
            expiredDate: auction.expiredDate,
            lastBid: auction.lastBid,
            status: auction.status,
            images: imageUrls.length > 0 ? imageUrls : undefined,
            imageUrl: imageUrls.length > 0 ? imageUrls[0] : undefined // First image URL
          } as AuctionCardDTO;
        } catch {
          console.log(`No images found for auction ${auction.id}, using placeholder`);
          
          // Ha nincs kép, visszaadjuk az aukciót placeholder nélkül
          return {
            id: auction.id!,
            itemName: auction.itemName,
            createDate: auction.createDate!,
            expiredDate: auction.expiredDate,
            lastBid: auction.lastBid,
            status: auction.status,
            images: undefined,
            imageUrl: undefined
          } as AuctionCardDTO;
        }
      })
    );

    setAuctions(auctionsWithImages);
  } catch (error) {
    console.error("Error fetching auctions:", error);
    setError("Failed to load auctions");
  } finally {
    setLoading(false);
  }
};
  
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