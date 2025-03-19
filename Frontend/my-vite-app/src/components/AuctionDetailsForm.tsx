"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { Box, Typography, TextField, Paper, FormControl, Select, MenuItem, Slider, FormHelperText } from "@mui/material"

interface AuctionDetailsFormProps {
  onChange: (data: { name: string; status: string; condition: number; category: string }) => void
}

const AuctionDetailsForm: React.FC<AuctionDetailsFormProps> = ({ onChange }) => {
  const [formData, setFormData] = useState({
    name: "",
    status: "Brand new",
    condition: 50,
    category: "",
  })

  const [errors, setErrors] = useState({
    name: false,
    category: false,
  })

  useEffect(() => {
    onChange(formData)
  }, [formData, onChange])

  const handleChange = (field: string, value: string | number) => {
    setFormData({
      ...formData,
      [field]: value,
    })

    // Clear error when field is filled
    if (field === "name" || field === "category") {
      setErrors({
        ...errors,
        [field]: typeof value === "string" && value.trim().length === 0,
      })
    }
  }

  const validateField = (field: string, value: string) => {
    if (field === "name" || field === "category") {
      setErrors({
        ...errors,
        [field]: value.trim().length === 0,
      })
    }
  }

  const categories = [
    "Electronics",
    "Fashion",
    "Home & Garden",
    "Sports",
    "Collectibles",
    "Art",
    "Vehicles",
    "Toys",
    "Books",
    "Music",
    "Movies",
    "Jewelry",
    "Kitchen tools",
  ]

  const statusOptions = ["Brand new", "Like new", "Lightly used", "Well used", "Heavily used"]

  const conditionMarks = [
    { value: 0, label: "bad" },
    { value: 100, label: "perfect" },
  ]

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
              <MenuItem key={category} value={category}>
                {category}
              </MenuItem>
            ))}
          </Select>
          {errors.category && <FormHelperText>Category is required</FormHelperText>}
        </FormControl>
      </Box>
    </Paper>
  )
}

export default AuctionDetailsForm;
