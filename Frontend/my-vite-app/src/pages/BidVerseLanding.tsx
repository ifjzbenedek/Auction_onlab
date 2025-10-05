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
  const [isSmartSearchActive, setIsSmartSearchActive] = useState(false)
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
    // Ne fusson le, ha smart search aktív
    if (isSmartSearchActive) {
      return
    }

    const fetchAuctionsAndImages = async () => {
      try {
        setLoading(true);
        
        const params: Record<string, string | number> = {};
        
        // Status filters - use "status" (singular) as backend expects
        if (filters.length > 0) {
          const statusMapping: Record<string, string> = {
            'ongoing': 'ACTIVE',
            'finished': 'CLOSED', 
            'upcoming': 'UPCOMING'
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
                startDate: auction.startDate, 
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
                startDate: auction.startDate, 
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
  }, [filters, searchTerm, isImageSearch, selectedCategory, isSmartSearchActive])

  // Szűrők kezelése
  const handleFilterChange = (newFilters: string[]) => {
    console.log('Filter change received:', newFilters); // Debug log
    setFilters(newFilters)
  }

  // Keresés kezelése
  const handleSearch = async (term: string, imageSearch: boolean, smartSearch?: boolean) => {
    console.log('Search received:', term, imageSearch, 'Smart:', smartSearch); // Debug log
    setSearchTerm(term)
    setIsImageSearch(imageSearch)
    
    // Ha üres a keresési kifejezés, ne használjunk smart search-et
    if (!term.trim()) {
      console.log('Empty search term, showing all auctions')
      setIsSmartSearchActive(false)
      // Az eredeti useEffect fogja kezelni az összes aukció megjelenítését
      return
    }
    
    // Ha smart search-et kértek és van érvényes keresési kifejezés
    if (smartSearch && term.trim()) {
      try {
        setIsSmartSearchActive(true)
        setLoading(true)
        console.log('Performing smart search with term:', term)
        
        const response = await auctionApi.smartSearch(term)
        const smartResults: AuctionCardDTO[] = response.data
        
        // Smart search eredmények feldolgozása képekkel
        const auctionsWithImages = await Promise.all(
          smartResults.map(async (auction) => {
            try {
              const imagesResponse = await imageApi.getAuctionImages(auction.id!)
              const images: AuctionImageDTO[] = imagesResponse.data || []
              const imageUrls = images.map(img => img.cloudinaryUrl)
              
              return {
                ...auction,
                images: imageUrls.length > 0 ? imageUrls : undefined,
                imageUrl: imageUrls.length > 0 ? imageUrls[0] : undefined
              } as AuctionCardDTO
            } catch {
              return {
                ...auction,
                images: undefined,
                imageUrl: undefined
              } as AuctionCardDTO
            }
          })
        )
        
        setAuctions(auctionsWithImages)
        setLoading(false)
        console.log('Smart search completed, found:', auctionsWithImages.length, 'auctions')
      } catch (error) {
        console.error('Smart search failed:', error)
        setError('Smart search failed')
        setLoading(false)
        setIsSmartSearchActive(false)
      }
    } else {
      // Normál keresés esetén kapcsoljuk ki a smart search flag-et
      setIsSmartSearchActive(false)
    }
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