"use client"

import type React from "react"
import { useState } from "react"
import TollIcon from "@mui/icons-material/Toll"
import AccountCircleIcon from "@mui/icons-material/AccountCircle"
import { Search, Plus, X, ChevronDown, RefreshCw, Image, User, LogOut, Package, DollarSign, Heart } from "lucide-react"
import {
  Box,
  Typography,
  Button,
  TextField,
  InputAdornment,
  Checkbox,
  Popover,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  InputBase,
  Switch,
  type Theme,
} from "@mui/material"
import { styled } from "@mui/material/styles"
import { useNavigate } from "react-router-dom"

// Custom styled components with proper theme typing
const CategoryChip = styled(Box)(({ theme }: { theme: Theme }) => ({
  display: "inline-flex",
  alignItems: "center",
  padding: "4px 10px",
  borderRadius: "20px",
  border: "1px solid #e0e0e0",
  marginRight: theme.spacing(1),
  backgroundColor: "white",
  transition: "all 0.2s ease",
  "&:hover": {
    borderColor: "#3498db",
    boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
  },
}))

interface FilterButtonProps {
  theme?: Theme
  active?: boolean
  color?: string
}

const FilterButton = styled(Box, {
  shouldForwardProp: (prop) => prop !== "active" && prop !== "color",
})<FilterButtonProps>(({ theme, active, color }) => ({
  display: "flex",
  alignItems: "center",
  gap: theme.spacing(1),
  padding: "6px 16px",
  borderRadius: "20px",
  border: "1px solid #e0e0e0",
  marginRight: theme.spacing(1),
  cursor: "pointer",
  backgroundColor: active ? color || "transparent" : "transparent",
  color: active ? "#fff" : "#2c3e50",
  transition: "all 0.2s ease",
  "&:hover": {
    borderColor: color || "#5b7c99",
    backgroundColor: active ? color || "transparent" : "rgba(0,0,0,0.02)",
  },
}))


const ProfileMenuItem = styled(ListItem)(() => ({
  padding: "8px 16px",
  cursor: "pointer",
  transition: "all 0.2s ease",
  "&:hover": {
    backgroundColor: "rgba(0,0,0,0.04)",
  },
}))

const CategorySearchInput = styled(InputBase)(() => ({
  padding: "4px 8px",
  width: "100%",
  fontSize: "0.875rem",
}))

interface HeaderProps {
  onFilterChange: (newFilters: string[]) => void;
  onSearch: (term: string, imageSearch: boolean) => void;
  onNewAuction?: () => void; 
  onCategoryChange?: (category: string | null) => void;
  categories?: string[];
}

const Header: React.FC<HeaderProps> = ({ onFilterChange, onSearch, onCategoryChange,
  categories  }) => {
  const navigate = useNavigate()
  const [searchTerm, setSearchTerm] = useState("")
  const [isImageSearch, setIsImageSearch] = useState(false)
  const [smartSearch, setSmartSearch] = useState(true)
  const [activeFilters, setActiveFilters] = useState<string[]>([])
  const [selectedCategories, setSelectedCategories] = useState<string[]>([])

  // Profile menu state
  const [profileAnchorEl, setProfileAnchorEl] = useState<null | HTMLElement>(null)
  const profileMenuOpen = Boolean(profileAnchorEl)

  // Category dropdown state
  const [categoryAnchorEl, setCategoryAnchorEl] = useState<null | HTMLElement>(null)
  const categoryMenuOpen = Boolean(categoryAnchorEl)
  const [categorySearchTerm, setCategorySearchTerm] = useState("")

  const filteredCategories = (categories || []).filter(
    (cat) =>
      cat.toLowerCase().includes(categorySearchTerm.toLowerCase()) && // cat már string
      !selectedCategories.includes(cat)
  );

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value
    setSearchTerm(value)
    if (onSearch) {
      onSearch(value, isImageSearch)
    }
  }

  const toggleSearchType = () => {
    setIsImageSearch(!isImageSearch)
    if (onSearch) {
      onSearch(searchTerm, !isImageSearch)
    }
  }

  const toggleFilter = (filter: string) => {
    const newFilters = activeFilters.includes(filter)
      ? activeFilters.filter((f) => f !== filter) // Távolítsd el a szűrőt
      : [...activeFilters, filter]; // Add hozzá a szűrőt
  
    setActiveFilters(newFilters);
    onFilterChange(newFilters); // Küldd el a frissített szűrőket
  };

  const removeCategory = (category: string) => {
    const newCategories = selectedCategories.filter(c => c !== category)
    setSelectedCategories(newCategories)
    onCategoryChange?.(newCategories.length > 0 ? newCategories.join(',') : null)
  }

  const addCategory = (category: string) => {
    if (!selectedCategories.includes(category)) {
      const newCategories = [...selectedCategories, category]
      setSelectedCategories(newCategories)
      onCategoryChange?.(newCategories.join(','))
    }
  }

  const handleProfileClick = (event: React.MouseEvent<HTMLElement>) => {
    setProfileAnchorEl(event.currentTarget)
  }

  const handleProfileClose = () => {
    setProfileAnchorEl(null)
  }

  const handleProfileMenuItemClick = (action: string) => {
    handleProfileClose()
    if (action === "profile") {
      navigate("/users/me")
    } else if (action === "logout") {
      // Handle logout
      console.log("Logging out...")
      // Egyszerűsített logout - valós alkalmazásban API hívás lenne
      navigate("/users/login")
    } else if (action === "myAuctions") {
      navigate("/my-auctions")
    } else if (action === "myBids") {
      navigate("/my-auctions?tab=bids")
    } else if (action === "favorites") {
      navigate("/my-auctions?tab=followed")
    }
  }

  const handleCategoryClick = (event: React.MouseEvent<HTMLElement>) => {
    setCategoryAnchorEl(event.currentTarget)
  }

  const handleCategoryClose = () => {
    setCategoryAnchorEl(null)
    setCategorySearchTerm("")
  }

  const handleNewAuctionClick = () => {
    console.log("Navigating to upload auction")
    navigate("/upload-auction")
  }

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        gap: 1.5,
        p: { xs: 1.5, md: 2 },
        borderBottom: "1px solid #e8e8e8",
        backgroundColor: "white",
      }}
    >
      {/* Top row with logo, search, and icons */}
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: { xs: "wrap", md: "nowrap" },
          gap: 1.5,
        }}
      >
        {/* Logo */}
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            gap: 1,
            cursor: "pointer",
          }}
          onClick={() => {
            console.log("Navigating to home")
            navigate("/")
          }}
        >
          <Box
            sx={{
              width: 32,
              height: 32,
              border: "1px solid #5b7c99",
              borderRadius: "50%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: "#5b7c99",
            }}
          >
            <TollIcon style={{ fontSize: "20px" }} />
          </Box>
          <Typography variant="h6" fontWeight="bold" sx={{ color: "#2c3e50" }}>
            BidVerse
          </Typography>
        </Box>

        {/* Search with toggle */}
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            gap: 1.5,
            flex: { xs: "1 1 100%", md: "0 1 auto" },
            order: { xs: 3, md: 2 },
            width: { xs: "100%", md: "auto" },
            maxWidth: { md: 400 },
          }}
        >
          <Box sx={{ position: "relative", flex: 1 }}>
            <TextField
              fullWidth
              size="small"
              placeholder={isImageSearch ? "Image search..." : "Name..."}
              value={searchTerm}
              onChange={handleSearchChange}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    {isImageSearch ? <Image size={18} color="#888" /> : <Search size={18} color="#888" />}
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <Box
                      sx={{
                        width: 24,
                        height: 24,
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        cursor: "pointer",
                        color: "#5b7c99",
                        transition: "transform 0.3s ease",
                        "&:hover": {
                          transform: "rotate(180deg)",
                        },
                      }}
                      onClick={toggleSearchType}
                    >
                      <RefreshCw size={18} />
                    </Box>
                  </InputAdornment>
                ),
                sx: {
                  borderRadius: 30,
                  height: 40,
                  pr: 1,
                },
              }}
            />
          </Box>

          <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
            <Typography variant="body2" sx={{ fontSize: 12, color: "#5b7c99" }}>
              Smart search
            </Typography>
            <Switch
              checked={smartSearch}
              onChange={() => setSmartSearch(!smartSearch)}
              size="small"
              color="primary"
            />
          </Box>
        </Box>

        {/* Right icons */}
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            gap: 1.5,
            order: { xs: 2, md: 3 },
          }}
        >
          <Box
            sx={{
              width: 40,
              height: 40,
              border: "1px solid #e0e0e0",
              borderRadius: "50%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              cursor: "pointer",
              color: "#5b7c99",
              transition: "all 0.2s ease",
              "&:hover": {
                borderColor: "#3498db",
                boxShadow: "0 2px 4px rgba(0,0,0,0.1)",
              },
            }}
            onClick={handleProfileClick}
          >
            <AccountCircleIcon style={{ fontSize: "40px" }} />          
          </Box>
          <Popover
            open={profileMenuOpen}
            anchorEl={profileAnchorEl}
            onClose={handleProfileClose}
            anchorOrigin={{
              vertical: "bottom",
              horizontal: "right",
            }}
            transformOrigin={{
              vertical: "top",
              horizontal: "right",
            }}
            PaperProps={{
              sx: {
                mt: 1,
                boxShadow: "0 4px 20px rgba(0,0,0,0.1)",
                borderRadius: "8px",
                width: 200,
              },
            }}
          >
            <List sx={{ p: 0 }}>
              <ProfileMenuItem onClick={() => handleProfileMenuItemClick("profile")}>
                <ListItemIcon sx={{ minWidth: 36 }}>
                  <User size={18} />
                </ListItemIcon>
                <ListItemText primary="Profile" />
              </ProfileMenuItem>
              <ProfileMenuItem onClick={() => handleProfileMenuItemClick("logout")}>
                <ListItemIcon sx={{ minWidth: 36 }}>
                  <LogOut size={18} />
                </ListItemIcon>
                <ListItemText primary="Log Out" />
              </ProfileMenuItem>
              <ProfileMenuItem onClick={() => handleProfileMenuItemClick("myAuctions")}>
                <ListItemIcon sx={{ minWidth: 36 }}>
                  <Package size={18} />
                </ListItemIcon>
                <ListItemText primary="My Auctions" />
              </ProfileMenuItem>
              <ProfileMenuItem onClick={() => handleProfileMenuItemClick("myBids")}>
                <ListItemIcon sx={{ minWidth: 36 }}>
                  <DollarSign size={18} />
                </ListItemIcon>
                <ListItemText primary="My Bids" />
              </ProfileMenuItem>
              <ProfileMenuItem onClick={() => handleProfileMenuItemClick("favorites")}>
                <ListItemIcon sx={{ minWidth: 36 }}>
                  <Heart size={18} />
                </ListItemIcon>
                <ListItemText primary="Favorites" />
              </ProfileMenuItem>
            </List>
          </Popover>
          <Box sx={{ display: "flex", gap: 1 }}>
            <Box
              sx={{
                width: 32,
                height: 32,
                border: "1px solid #e0e0e0",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                cursor: "pointer",
                color: "#5b7c99",
                transition: "all 0.2s ease",
                "&:hover": {
                  borderColor: "#3498db",
                  transform: "scale(1.05)",
                },
              }}
            >
              <X size={20} />
            </Box>
            <Box
              sx={{
                width: 32,
                height: 32,
                border: "1px solid #e0e0e0",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                cursor: "pointer",
                color: "#5b7c99",
                transition: "all 0.2s ease",
                "&:hover": {
                  borderColor: "#3498db",
                  transform: "scale(1.05)",
                },
              }}
            >
              <X size={20} />
            </Box>
          </Box>
        </Box>
      </Box>

      {/* Bottom row with filters, categories, and new auction button */}
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          flexWrap: { xs: "wrap", md: "nowrap" },
          gap: 1.5,
        }}
      >
        {/* Filter buttons */}
        <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap" }}>
          {[
            { id: "ACTIVE", label: "Ongoing", color: "#2ecc71" },   // Zöld
            { id: "CLOSED", label: "Finished", color: "#e74c3c" },  // Piros
            { id: "PENDING", label: "Upcoming", color: "#f39c12" }, // Narancs
          ].map((filter) => (
            <FilterButton
              key={filter.id}
              onClick={() => toggleFilter(filter.id)} // Küldjük az "ACTIVE", "CLOSED", stb. értékeket
              active={activeFilters.includes(filter.id)}
              color={filter.color}
            >
              {filter.label}
              <Checkbox
                checked={activeFilters.includes(filter.id)}
                size="small"
                sx={{
                  p: 0,
                  color: activeFilters.includes(filter.id) ? "#fff" : "#aaa",
                  "&.Mui-checked": {
                    color: activeFilters.includes(filter.id) ? "#fff" : filter.color,
                  },
                }}
                onClick={(e) => e.stopPropagation()}
                onChange={() => toggleFilter(filter.id)}
              />
            </FilterButton>
          ))}
        </Box>

        {/* New auction button */}
        <Button
          variant="contained"
          sx={{
            bgcolor: "#3498db",
            color: "white",
            borderRadius: 30,
            textTransform: "none",
            padding: "6px 16px",
            "&:hover": {
              bgcolor: "#2980b9",
              transform: "scale(1.05)",
            },
            transition: "all 0.2s ease",
            order: { xs: 3, md: 2 },
            alignSelf: { xs: "flex-start", md: "center" },
          }}
          endIcon={<Plus size={16} />}
          onClick={handleNewAuctionClick}
        >
          New auction
        </Button>

        {/* Categories */}
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            gap: 1,
            order: { xs: 2, md: 3 },
            width: { xs: "100%", md: "auto" },
          }}
        >
          <Typography variant="body2" sx={{ whiteSpace: "nowrap", color: "#2c3e50" }}>
            Category
          </Typography>
          <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.5 }}>
            {selectedCategories.map((category) => (
              <CategoryChip key={category}>
                <Typography variant="body2" sx={{ mr: 1, color: "#2c3e50" }}>
                  {category}
                </Typography>
                <X size={14} style={{ cursor: "pointer", color: "#5b7c99" }} onClick={() => removeCategory(category)} />
              </CategoryChip>
            ))}
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                cursor: "pointer",
                color: "#5b7c99",
                transition: "all 0.2s ease",
                "&:hover": {
                  color: "#3498db",
                  transform: "scale(1.1)",
                },
              }}
              onClick={handleCategoryClick}
            >
              <ChevronDown size={16} />
            </Box>
            <Popover
              open={categoryMenuOpen}
              anchorEl={categoryAnchorEl}
              onClose={handleCategoryClose}
              anchorOrigin={{
                vertical: "bottom",
                horizontal: "left",
              }}
              transformOrigin={{
                vertical: "top",
                horizontal: "left",
              }}
              PaperProps={{
                sx: {
                  mt: 1,
                  boxShadow: "0 4px 20px rgba(0,0,0,0.1)",
                  borderRadius: "8px",
                  width: 250,
                },
              }}
            >
              <Box sx={{ p: 1 }}>
                <CategorySearchInput
                  placeholder="Search categories..."
                  value={categorySearchTerm}
                  onChange={(e) => setCategorySearchTerm(e.target.value)}
                  autoFocus
                />
              </Box>
              <List sx={{ maxHeight: 250, overflow: "auto", p: 0 }}>
                {filteredCategories.length > 0 ? (
                  filteredCategories.map((category) => (
                    <ListItem
                      key={category}
                      onClick={() => addCategory(category)}
                      sx={{
                        cursor: "pointer",
                        py: 0.5,
                        "&:hover": {
                          bgcolor: "rgba(0,0,0,0.04)",
                        },
                      }}
                    >
                      <ListItemText primary={category} />
                    </ListItem>
                  ))
                ) : (
                  <ListItem>
                    <ListItemText
                      primary={categorySearchTerm ? "No matching categories" : "No more categories available"}
                      sx={{ color: "text.secondary", fontSize: "0.875rem" }}
                    />
                  </ListItem>
                )}
              </List>
            </Popover>
          </Box>
        </Box>
      </Box>
    </Box>
  )
}

export default Header;

