"use client"

import type React from "react"
import { useState, useEffect } from "react"
import {
  Box,
  Typography,
  TextField,
  Paper,
  FormControl,
  Select,
  MenuItem,
  Slider,
  FormHelperText,
  CircularProgress,
  Alert,
  Grid,
  InputAdornment,
} from "@mui/material"
import { Calendar, DollarSign, Timer } from "lucide-react"
import type { AuctionDetailsDTO } from "../types/auction"
import type { CategoryDTO } from "../types/category"
import { auctionApi } from "../services/api"

interface AuctionDetailsFormProps {
  onChange: (data: AuctionDetailsDTO) => void;
  auctionType: "FIXED" | "EXTENDED" | null;
  initialData?: AuctionDetailsDTO; // Add initialData prop
}

const AuctionDetailsForm: React.FC<AuctionDetailsFormProps> = ({ onChange, auctionType, initialData }) => {
  const [formData, setFormData] = useState<AuctionDetailsDTO>(
    initialData || {
      name: "",
      status: "Brand new",
      condition: 50,
      category: "",
      minimumPrice: 0,
      minStep: 0,
      expiredDate: "",
      startDate: "",
      extraTime: "",
    }
  )

  const [errors, setErrors] = useState({
    name: false,
    category: false,
    minimumPrice: false,
    minStep: false,
    expiredDate: false,
    startDate: false,
    extraTime: false,
  })

  const [categories, setCategories] = useState<CategoryDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  // Fetch categories from API
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        setLoading(true)
        const response = await auctionApi.getCategories()
        setCategories(response.data)
        setError(null)
      } catch (err) {
        console.error("Error fetching categories:", err)
        setError("Failed to load categories. Please try refreshing the page.")
      } finally {
        setLoading(false)
      }
    }

    fetchCategories()
  }, [])

  useEffect(() => {
    // Only notify parent of changes when we have a complete form
    onChange(formData)
  }, [formData, onChange])

  const handleChange = (field: string, value: string | number) => {
    setFormData({
      ...formData,
      [field]: value,
    })

    // Clear error when field is filled
    if (field in errors) {
      setErrors({
        ...errors,
        [field]: false,
      })
    }
  }

  const validateField = (field: string, value: string | number | null | undefined) => {
    let hasError = false

    if (field === "name" || field === "category") {
      hasError = typeof value === "string" && value.trim().length === 0
    } else if (field === "minimumPrice" || field === "minStep") {
      hasError = typeof value === "number" && value <= 0
    } else if (field === "expiredDate") {
      hasError = typeof value === "string" && value.trim().length === 0
    } else if (field === "startDate") {
      hasError = false
    } else if (field === "extraTime" && auctionType === "EXTENDED") {
      hasError = typeof value === "string" && (value.trim().length === 0 || Number(value) <= 0)
    }

    setErrors({
      ...errors,
      [field]: hasError,
    })
  }

  const statusOptions = ["Brand new", "Like new", "Lightly used", "Well used", "Heavily used"]

  const conditionMarks = [
    { value: 0, label: "bad" },
    { value: 100, label: "perfect" },
  ]

  if (loading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", p: 4 }}>
        <CircularProgress />
      </Box>
    )
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ mb: 3 }}>
        {error}
      </Alert>
    )
  }

  return (
  <Paper sx={{ p: 3, bgcolor: "white", borderRadius: 3 }}>
    <Box sx={{ mb: 3 }}>
      <Typography variant="subtitle1" sx={{ mb: 1 }}>
        Name
      </Typography>
      <TextField
        fullWidth
        placeholder="Itemname"
        value={formData.name}
        onChange={(e) => handleChange("name", e.target.value)}
        onBlur={(e) => validateField("name", e.target.value)}
        error={errors.name}
        helperText={errors.name ? "Name is required" : ""}
        size="small"
      />
    </Box>

    <Box sx={{ mb: 3 }}>
      <Typography variant="subtitle1" sx={{ mb: 1 }}>
        Status
      </Typography>
      <FormControl fullWidth size="small">
        <Select
          value={formData.status}
          onChange={(e) => handleChange("status", e.target.value)}
          displayEmpty
          sx={{ bgcolor: "#000", color: "white" }}
        >
          {statusOptions.map((option) => (
            <MenuItem key={option} value={option}>
              {option}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </Box>

    <Box sx={{ mb: 3 }}>
      <Typography variant="subtitle1" sx={{ mb: 1 }}>
        Condition
      </Typography>
      <Box sx={{ px: 1 }}>
        <Slider
          value={formData.condition}
          onChange={(_, value) => handleChange("condition", Array.isArray(value) ? value[0] : value)}
          marks={conditionMarks}
          step={1}
          min={0}
          max={100}
          sx={{
            color: "#3498db",
            "& .MuiSlider-thumb": {
              height: 24,
              width: 24,
              backgroundColor: "#fff",
              border: "2px solid currentColor",
            },
            "& .MuiSlider-track": {
              height: 8,
            },
            "& .MuiSlider-rail": {
              height: 8,
              opacity: 0.5,
              backgroundColor: "#bfbfbf",
            },
            "& .MuiSlider-mark": {
              backgroundColor: "#bfbfbf",
              height: 8,
              width: 1,
              marginTop: 0,
            },
            "& .MuiSlider-markLabel": {
              fontSize: "0.75rem",
              color: "#666",
            },
          }}
        />
      </Box>
    </Box>

    <Box sx={{ mb: 3 }}>
      <Typography variant="subtitle1" sx={{ mb: 1 }}>
        Category
      </Typography>
      <FormControl fullWidth size="small" error={errors.category}>
        <Select
          value={formData.category}
          onChange={(e) => handleChange("category", e.target.value)}
          onBlur={() => validateField("category", formData.category)}
          displayEmpty
        >
          <MenuItem value="" disabled>
            <em>Select a category</em>
          </MenuItem>
          {categories.map((category) => (
            <MenuItem key={category.id} value={category.categoryName}>
              {category.categoryName}
            </MenuItem>
          ))}
        </Select>
        {errors.category && <FormHelperText>Category is required</FormHelperText>}
      </FormControl>
    </Box>

    {auctionType && (
        <Box sx={{ mt: 3 }}>
          <Typography variant="subtitle1" fontWeight="medium" color="primary.main" gutterBottom>
            {auctionType === "FIXED" ? "Fixed Time Auction Details" : "Auto-Extended Auction Details"}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
            {auctionType === "FIXED"
              ? "In a Fixed Time Auction, the auction will end exactly at the specified end date and time, regardless of any last-minute bidding activity. The highest bid at the end time wins."
              : "In an Auto-Extended Auction, if a bid is placed within the specified extension time before the end, the auction is automatically extended by that amount of time. This prevents 'sniping' (last-second bidding) and gives all bidders a fair chance to respond."}
          </Typography>

          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <Typography variant="subtitle2" sx={{ mb: 1 }}>
                Minimum Price
              </Typography>
              <TextField
                fullWidth
                size="small"
                type="number"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <DollarSign size={16} />
                    </InputAdornment>
                  ),
                }}
                value={formData.minimumPrice === 0 && errors.minimumPrice ? "" : formData.minimumPrice}
                onChange={(e) => {
                  const value = e.target.value;
                  handleChange("minimumPrice", value === "" ? 0 : Number(value));
                }}
                onBlur={() => validateField("minimumPrice", formData.minimumPrice)}
                error={errors.minimumPrice}
                placeholder="0.00"
              />
              {errors.minimumPrice && (
                <FormHelperText error>Please enter a valid minimum price</FormHelperText>
              )}
            </Grid>

            <Grid item xs={12} md={6}>
              <Typography variant="subtitle2" sx={{ mb: 1 }}>
                Minimum Bid Increment
              </Typography>
              <TextField
                fullWidth
                size="small"
                type="number"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <DollarSign size={16} />
                    </InputAdornment>
                  ),
                }}
                value={formData.minStep === 0 && errors.minStep ? "" : formData.minStep}
                onChange={(e) => {
                  const value = e.target.value;
                  handleChange("minStep", value === "" ? 0 : Number(value));
                }}
                onBlur={() => validateField("minStep", formData.minStep)}
                error={errors.minStep}
                placeholder="0.00"
              />
              {errors.minStep && (
                <FormHelperText error>Please enter a valid minimum increment</FormHelperText>
              )}
            </Grid>

            <Grid item xs={12} md={6}>
              <Typography variant="subtitle2" sx={{ mb: 1 }}>
                Start Date and Time (Optional)
              </Typography>
              <TextField
                fullWidth
                size="small"
                type="datetime-local"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Calendar size={16} />
                    </InputAdornment>
                  ),
                }}
                InputLabelProps={{
                  shrink: true,
                }}
                value={formData.startDate || ""} 
                onChange={(e) => handleChange("startDate", e.target.value || "")} 
                onBlur={() => validateField("startDate", formData.startDate)}
                error={errors.startDate}
                helperText="Leave empty to start immediately"
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <Typography variant="subtitle2" sx={{ mb: 1 }}>
                End Date and Time
              </Typography>
              <TextField
                fullWidth
                size="small"
                type="datetime-local"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Calendar size={16} />
                    </InputAdornment>
                  ),
                }}
                InputLabelProps={{
                  shrink: true,
                }}
                value={formData.expiredDate || ""} 
                onChange={(e) => handleChange("expiredDate", e.target.value || "")} 
                onBlur={() => validateField("expiredDate", formData.expiredDate)}
                error={errors.expiredDate}
              />
              {errors.expiredDate && (
                <FormHelperText error>Please select an end date and time</FormHelperText>
              )}
            </Grid>

            {auctionType === "EXTENDED" && (
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle2" sx={{ mb: 1 }}>
                  Extension Time (minutes)
                </Typography>
                <TextField
                  fullWidth
                  size="small"
                  type="number"
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <Timer size={16} />
                      </InputAdornment>
                    ),
                  }}
                  value={formData.extraTime || ""} 
                  onChange={(e) => {
                    const value = e.target.value;
                    handleChange("extraTime", value || ""); 
                  }}
                  onBlur={() => validateField("extraTime", formData.extraTime)}
                  error={errors.extraTime}
                  placeholder="5"
                />
                {errors.extraTime && (
                  <FormHelperText error>Please enter a valid extension time</FormHelperText>
                )}
              </Grid>
            )}
          </Grid>
        </Box>
      )}
    </Paper>
  )
}

export default AuctionDetailsForm;