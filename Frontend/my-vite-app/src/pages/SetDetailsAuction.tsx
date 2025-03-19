"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { Box, Typography, Button, Paper, Stepper, Step, StepLabel, Alert } from "@mui/material"
import { ArrowLeft } from "lucide-react"
import AuctionDetailsForm from "../components/AuctionDetailsForm"

const SetDetailsAuction: React.FC = () => {
  const navigate = useNavigate()
  const [auctionType, setAuctionType] = useState<"fixed" | "extended" | null>(null)
  const [formValid, setFormValid] = useState(false)
  const [detailsData, setDetailsData] = useState({
    name: "",
    status: "Brand new",
    condition: 50,
    category: "",
  })
  const [hasUploadData, setHasUploadData] = useState(false)

  useEffect(() => {
    // Check if we have data from step 1
    const savedData = sessionStorage.getItem("auctionUploadData")
    if (!savedData) {
      // Redirect back to step 1 if no data
      navigate("/upload-auction")
      return
    }
    setHasUploadData(true)
  }, [navigate])

  const handleAuctionTypeSelect = (type: "fixed" | "extended") => {
    setAuctionType(type)
  }

  const handleDetailsChange = (data: { name: string; status: string; condition: number; category: string }) => {
    setDetailsData(data)
    validateForm(data)
  }

  const validateForm = (data: { name: string; status: string; condition: number; category: string }) => {
    const isValid =
      data.name.trim().length > 0 &&
      data.status.trim().length > 0 &&
      data.category.trim().length > 0 &&
      auctionType !== null

    setFormValid(isValid)
  }

  const handleBackToUpload = () => {
    navigate("/upload-auction")
  }

  const handleUploadAuction = () => {
    // Get upload data from session storage
    const uploadDataStr = sessionStorage.getItem("auctionUploadData")
    let uploadData = { description: "", imageCount: 0 }

    if (uploadDataStr) {
      try {
        uploadData = JSON.parse(uploadDataStr)
      } catch (e) {
        console.error("Error parsing saved auction data", e)
      }
    }

    // In a real app, this would send the auction data to the backend
    console.log("Uploading auction:", {
      uploadData,
      auctionType,
      details: detailsData,
    })

    // Clear session storage after submission
    sessionStorage.removeItem("auctionUploadData")

    // Navigate back to home page after successful upload
    navigate("/")
  }

  if (!hasUploadData) {
    return null // Will redirect in useEffect
  }

  return (
    <Box sx={{ bgcolor: "#333", minHeight: "100vh", pb: 5 }}>
      <Box sx={{ bgcolor: "#000", color: "white", p: 2, mb: 3 }}>
        <Typography variant="h5" fontWeight="bold">
          Creating an auction - Step 2
        </Typography>
      </Box>

      <Box sx={{ maxWidth: 800, mx: "auto", px: 2 }}>
        <Stepper activeStep={1} sx={{ mb: 4, display: { xs: "none", md: "flex" } }}>
          <Step>
            <StepLabel>Upload & Describe</StepLabel>
          </Step>
          <Step>
            <StepLabel>Set Details</StepLabel>
          </Step>
        </Stepper>

        {/* Step 2: Choose auction type and details */}
        <Paper sx={{ p: 4, mb: 4, bgcolor: "#000", color: "white", borderRadius: 3 }}>
          <Typography variant="h5" sx={{ textAlign: "center", mb: 3 }}>
            Choose auction type
          </Typography>

          <Box sx={{ display: "flex", justifyContent: "space-between", gap: 2 }}>
            <Button
              variant={auctionType === "fixed" ? "contained" : "outlined"}
              fullWidth
              onClick={() => handleAuctionTypeSelect("fixed")}
              sx={{
                borderRadius: 20,
                py: 1.5,
                borderColor: "white",
                color: auctionType === "fixed" ? "black" : "white",
                bgcolor: auctionType === "fixed" ? "white" : "transparent",
                "&:hover": {
                  bgcolor: auctionType === "fixed" ? "#eee" : "rgba(255,255,255,0.1)",
                  borderColor: "white",
                },
              }}
            >
              Fixed time auction
            </Button>

            <Button
              variant={auctionType === "extended" ? "contained" : "outlined"}
              fullWidth
              onClick={() => handleAuctionTypeSelect("extended")}
              sx={{
                borderRadius: 20,
                py: 1.5,
                borderColor: "white",
                color: auctionType === "extended" ? "black" : "white",
                bgcolor: auctionType === "extended" ? "white" : "transparent",
                "&:hover": {
                  bgcolor: auctionType === "extended" ? "#eee" : "rgba(255,255,255,0.1)",
                  borderColor: "white",
                },
              }}
            >
              Auto extended auction
            </Button>
          </Box>
        </Paper>

        <Typography variant="h6" sx={{ color: "white", borderBottom: "1px solid #555", pb: 1, mb: 3 }}>
          Details
        </Typography>

        <AuctionDetailsForm onChange={handleDetailsChange} />

        {!auctionType && (
          <Alert severity="info" sx={{ mt: 3 }}>
            Please select an auction type
          </Alert>
        )}

        <Box sx={{ display: "flex", justifyContent: "space-between", mt: 4 }}>
          <Button
            variant="outlined"
            startIcon={<ArrowLeft size={18} />}
            onClick={handleBackToUpload}
            sx={{
              borderColor: "white",
              color: "white",
              borderRadius: 20,
              px: 3,
              "&:hover": {
                borderColor: "#ccc",
                bgcolor: "rgba(255,255,255,0.05)",
              },
            }}
          >
            Back
          </Button>

          <Button
            variant="contained"
            onClick={handleUploadAuction}
            disabled={!formValid}
            sx={{
              bgcolor: "white",
              color: "black",
              borderRadius: 20,
              px: 3,
              "&:hover": {
                bgcolor: "#eee",
              },
              "&.Mui-disabled": {
                bgcolor: "#555",
                color: "#999",
              },
            }}
          >
            Upload auction
          </Button>
        </Box>
      </Box>
    </Box>
  )
}

export default SetDetailsAuction

