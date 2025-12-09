"use client"

import { useState, useEffect } from "react"
import { Box, Typography, CircularProgress, Alert, Grid2 } from "@mui/material"
import Header from "../components/Header"
import AuctionCard from "../components/AuctionCard"
import { auctionApi, imageApi } from "../services/api.ts" 
import { AuctionCardDTO } from "../types/auction"
import { AuctionImageDTO } from "../types/image"
import { CategoryDTO } from "../types/category"

function BidVerseLanding() {
  const [auctions, setAuctions] = useState<AuctionCardDTO[]>([])
  const [filters, setFilters] = useState<string[]>([])
  const [searchTerm, setSearchTerm] = useState("")
  const [isImageSearch, setIsImageSearch] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null)
  const [isSmartSearchActive, setIsSmartSearchActive] = useState(false)
  const [categories, setCategories] = useState<CategoryDTO[]>([])

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await auctionApi.getCategories();
        setCategories(response.data);
      } catch { /* empty */ }
    };

    fetchCategories();
  }, []);

  useEffect(() => {
    if (isSmartSearchActive) {
      return
    }

    const fetchAuctionsAndImages = async () => {
      try {
        setLoading(true);
        
        const params: Record<string, string | number> = {};
        
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
            params.status = backendStatuses;
          }
        }
        
        if (selectedCategory) {
          params.category = selectedCategory;
        }
        
        if (searchTerm.trim()) {
          params.search = searchTerm.trim();
        }

        const auctionsResponse = await auctionApi.getAuctions(params);
        const auctionsData: AuctionCardDTO[] = auctionsResponse.data;

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
      } catch {
        setError("Failed to load auctions");
      } finally {
        setLoading(false);
      }
    };
  
    fetchAuctionsAndImages()
  }, [filters, searchTerm, isImageSearch, selectedCategory, isSmartSearchActive])

  const handleFilterChange = (newFilters: string[]) => {
    setFilters(newFilters)
  }

  const handleSearch = async (term: string, imageSearch: boolean, smartSearch?: boolean) => {
    setSearchTerm(term)
    setIsImageSearch(imageSearch)
    
    if (!term.trim()) {
      setIsSmartSearchActive(false)
      return
    }
    
    if (smartSearch && term.trim()) {
      try {
        setIsSmartSearchActive(true)
        setLoading(true)
        
        const response = await auctionApi.smartSearch(term)
        const smartResults: AuctionCardDTO[] = response.data
        
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
      } catch {
        setError('Smart search failed')
        setLoading(false)
        setIsSmartSearchActive(false)
      }
    } else {
      setIsSmartSearchActive(false)
    }
  }

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
              Próbálj más szűrőket vagy keresési feltételeket.
            </Typography>
            <Typography variant="caption" color="text.secondary" sx={{ mt: 2, display: 'block' }}>
              Filters: {JSON.stringify(filters)}, Category: {selectedCategory}, Search: {searchTerm}
            </Typography>
          </Box>
        ) : (
          <Grid2 container spacing={3}>
            {auctions.map((auction) => (
              <Grid2 size={{ xs: 12, sm: 6, md: 4, lg: 3 }} key={auction.id}>
                <AuctionCard {...auction} />
              </Grid2>
            ))}
          </Grid2>
        )}
      </Box>
    </Box>
  )
}

export default BidVerseLanding;