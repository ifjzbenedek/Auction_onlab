"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { Box, Typography, Grid, CircularProgress, Alert } from "@mui/material"
import Header from "../components/Header"
import AuctionCard from "../components/AuctionCard"
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
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null)
  // Remove the empty array and actually fetch categories
  const [categories, setCategories] = useState<CategoryDTO[]>([])

  // Fetch categories on component mount
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await auctionApi.getCategories();
        setCategories(response.data);
        console.log('Loaded categories:', response.data); // Debug log
      } catch (error) {
        console.error("Error fetching categories:", error);
      }
    };

    fetchCategories();
  }, []);

  // Aukciók és képeik lekérése
  useEffect(() => {
    const fetchAuctionsAndImages = async () => {
      try {
        setLoading(true);
        
        const params: Record<string, string | number> = {};
        
        // Status filters - use "status" (singular) as backend expects
        if (filters.length > 0) {
          const statusMapping: Record<string, string> = {
            'ongoing': 'ACTIVE',
            'finished': 'CLOSED', 
            'upcoming': 'PENDING'
          };
          
          const backendStatuses = filters
            .map(filter => statusMapping[filter])
            .filter(status => status)
            .join(',');
            
          if (backendStatuses) {
            params.status = backendStatuses; // Changed from "statuses" to "status"
          }
        }
        
        // Category filter - use "category" (singular) as backend expects
        if (selectedCategory) {
          params.category = selectedCategory; // Changed from "categories" to "category"
        }
        
        // Search term
        if (searchTerm.trim()) {
          params.search = searchTerm.trim();
        }

        console.log('API params:', params); // Debug log
        console.log('Current filters:', filters); // Debug log
        console.log('Selected category:', selectedCategory); // Debug log

        const auctionsResponse = await auctionApi.getAuctions(params);
        const auctionsData: AuctionCardDTO[] = auctionsResponse.data;

        console.log('Received auctions from API:', auctionsData.length); // Debug log

        // Képek lekérése minden aukcióhoz
        const auctionsWithImages = await Promise.all(
          auctionsData.map(async (auction) => {
            try {
              const imagesResponse = await imageApi.getAuctionImages(auction.id!);
              const images: AuctionImageDTO[] = imagesResponse.data || [];
              
              const imageUrls = images.map(img => img.cloudinaryUrl);
              
              return {
                id: auction.id!,
                itemName: auction.itemName,
                createDate: auction.createDate!,
                expiredDate: auction.expiredDate,
                lastBid: auction.lastBid,
                status: auction.status,
                images: imageUrls.length > 0 ? imageUrls : undefined,
                imageUrl: imageUrls.length > 0 ? imageUrls[0] : undefined
              } as AuctionCardDTO;
            } catch {
              console.log(`No images found for auction ${auction.id}, using placeholder`);
              
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

  // Szűrők kezelése
  const handleFilterChange = (newFilters: string[]) => {
    console.log('Filter change received:', newFilters); // Debug log
    setFilters(newFilters)
  }

  // Keresés kezelése
  const handleSearch = (term: string, imageSearch: boolean) => {
    console.log('Search received:', term, imageSearch); // Debug log
    setSearchTerm(term)
    setIsImageSearch(imageSearch)
  }

  // Kategória váltás
  const handleCategoryChange = (category: string | null) => {
    console.log('Category change received:', category); // Debug log
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
              Próbálj más szűrőket vagy keresési feltételeket.
            </Typography>
            {/* Debug info */}
            <Typography variant="caption" color="text.secondary" sx={{ mt: 2, display: 'block' }}>
              Filters: {JSON.stringify(filters)}, Category: {selectedCategory}, Search: {searchTerm}
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