"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import {
  Box,
  Typography,
  Button,
  Paper,
  Stepper,
  Step,
  StepLabel,
  Alert,
  Grid,
  useTheme,
  alpha,
  Chip,
  Divider,
} from "@mui/material"
import { styled } from "@mui/material/styles"
import { ArrowLeft, Check, Clock, Tag, Settings, UploadIcon, AlertCircle } from "lucide-react"
import AuctionDetailsForm from "../components/AuctionDetailsForm"

// Styled components
const PageContainer = styled(Box)(({ theme }) => ({
  backgroundColor: theme.palette.background.default,
  minHeight: "100vh",
  paddingBottom: theme.spacing(5),
}))

const StepContainer = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(4),
  borderRadius: theme.shape.borderRadius * 2,
  boxShadow: "0 8px 30px rgba(0, 0, 0, 0.06)",
  marginBottom: theme.spacing(4),
  position: "relative",
  overflow: "hidden",
}))

const StepTitle = styled(Typography)(({ theme }) => ({
  marginBottom: theme.spacing(3),
  position: "relative",
  display: "inline-block",
  fontWeight: "bold",
  "&:after": {
    content: '""',
    position: "absolute",
    bottom: -8,
    left: 0,
    width: 40,
    height: 4,
    backgroundColor: theme.palette.primary.main,
    borderRadius: 2,
  },
}))

const ActionButton = styled(Button)(({ theme }) => ({
  borderRadius: 30,
  padding: theme.spacing(1.2, 3),
  textTransform: "none",
  fontWeight: 600,
  boxShadow: "0 4px 12px rgba(0, 0, 0, 0.1)",
  transition: "transform 0.2s ease",
  "&:hover": {
    transform: "translateY(-2px)",
    boxShadow: "0 6px 16px rgba(0, 0, 0, 0.15)",
  },
}))

const AuctionTypeCard = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(3),
  borderRadius: theme.shape.borderRadius * 2,
  cursor: "pointer",
  transition: "all 0.2s ease",
  height: "100%",
  display: "flex",
  flexDirection: "column",
  alignItems: "center",
  justifyContent: "center",
  textAlign: "center",
}))

const StepIcon = styled(Box)(({ theme }) => ({
  position: "absolute",
  top: -15,
  right: -15,
  width: 80,
  height: 80,
  opacity: 0.07,
  zIndex: 0,
  color: theme.palette.primary.main,
}))

const SetDetailsAuction: React.FC = () => {
  const navigate = useNavigate()
  const theme = useTheme()
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

  const handleDetailsChange = (data: DetailsData) => {
    setDetailsData(data)
    validateForm(data)
  }

  interface DetailsData {
    name: string;
    status: string;
    condition: number;
    category: string;
  }

  const validateForm = (data: DetailsData) => {
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
    <PageContainer>
      <Box sx={{ maxWidth: 1000, mx: "auto", px: { xs: 2, md: 4 }, py: 4 }}>
        <Box sx={{ mb: 4, display: "flex", alignItems: "center", justifyContent: "space-between" }}>
          <Typography variant="h4" fontWeight="bold" color="text.primary">
            Create New Auction
          </Typography>
          <Chip
            label="Step 2 of 2"
            color="primary"
            sx={{
              borderRadius: 5,
              fontWeight: "medium",
              px: 1,
              backgroundColor: alpha(theme.palette.primary.main, 0.1),
              color: theme.palette.primary.main,
            }}
          />
        </Box>

        <Stepper
          activeStep={1}
          sx={{
            mb: 4,
            display: { xs: "none", md: "flex" },
            "& .MuiStepLabel-label": {
              fontWeight: "medium",
            },
            "& .MuiStepLabel-active": {
              fontWeight: "bold",
              color: theme.palette.primary.main,
            },
          }}
        >
          <Step>
            <StepLabel>Upload & Describe</StepLabel>
          </Step>
          <Step>
            <StepLabel>Set Details</StepLabel>
          </Step>
        </Stepper>

        {/* Auction Type Selection */}
        <StepContainer>
          <StepIcon>
            <Clock size={80} />
          </StepIcon>

          <StepTitle variant="h5">Choose Auction Type</StepTitle>

          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <AuctionTypeCard
                elevation={auctionType === "fixed" ? 4 : 1}
                onClick={() => handleAuctionTypeSelect("fixed")}
                sx={{
                  border: auctionType === "fixed" ? `2px solid ${theme.palette.primary.main}` : "none",
                  backgroundColor:
                    auctionType === "fixed" ? alpha(theme.palette.primary.main, 0.05) : theme.palette.background.paper,
                }}
              >
                <Box
                  sx={{
                    width: 60,
                    height: 60,
                    borderRadius: "50%",
                    backgroundColor: alpha(theme.palette.primary.main, 0.1),
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    mb: 2,
                    color: theme.palette.primary.main,
                  }}
                >
                  <Clock size={30} />
                </Box>
                <Typography variant="h6" fontWeight="bold" sx={{ mb: 1 }}>
                  Fixed Time Auction
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  Auction ends at a specific time regardless of bidding activity
                </Typography>
                {auctionType === "fixed" && (
                  <Chip
                    icon={<Check size={16} />}
                    label="Selected"
                    color="primary"
                    size="small"
                    sx={{ borderRadius: 5 }}
                  />
                )}
              </AuctionTypeCard>
            </Grid>

            <Grid item xs={12} md={6}>
              <AuctionTypeCard
                elevation={auctionType === "extended" ? 4 : 1}
                onClick={() => handleAuctionTypeSelect("extended")}
                sx={{
                  border: auctionType === "extended" ? `2px solid ${theme.palette.primary.main}` : "none",
                  backgroundColor:
                    auctionType === "extended"
                      ? alpha(theme.palette.primary.main, 0.05)
                      : theme.palette.background.paper,
                }}
              >
                <Box
                  sx={{
                    width: 60,
                    height: 60,
                    borderRadius: "50%",
                    backgroundColor: alpha(theme.palette.primary.main, 0.1),
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    mb: 2,
                    color: theme.palette.primary.main,
                  }}
                >
                  <Settings size={30} />
                </Box>
                <Typography variant="h6" fontWeight="bold" sx={{ mb: 1 }}>
                  Auto-Extended Auction
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  Time extends automatically when bids are placed near the end
                </Typography>
                {auctionType === "extended" && (
                  <Chip
                    icon={<Check size={16} />}
                    label="Selected"
                    color="primary"
                    size="small"
                    sx={{ borderRadius: 5 }}
                  />
                )}
              </AuctionTypeCard>
            </Grid>
          </Grid>

          {!auctionType && (
            <Alert
              severity="info"
              sx={{
                mt: 3,
                borderRadius: 2,
                "& .MuiAlert-icon": {
                  alignItems: "center",
                },
              }}
            >
              Please select an auction type to continue
            </Alert>
          )}
        </StepContainer>

        {/* Item Details */}
        <StepContainer>
          <StepIcon>
            <Tag size={80} />
          </StepIcon>

          <StepTitle variant="h5">Item Details</StepTitle>

          <AuctionDetailsForm onChange={handleDetailsChange} />
        </StepContainer>

        {/* Summary */}
        <StepContainer>
          <Box sx={{ display: "flex", alignItems: "center", mb: 3 }}>
            <Box
              sx={{
                width: 40,
                height: 40,
                borderRadius: "50%",
                backgroundColor: formValid ? "success.light" : "warning.light",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                mr: 2,
                color: formValid ? "success.dark" : "warning.dark",
              }}
            >
              {formValid ? <Check size={24} /> : <AlertCircle size={24} />}
            </Box>
            <Box>
              <Typography variant="h6" fontWeight="medium">
                {formValid ? "Ready to Upload" : "Complete Required Fields"}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {formValid
                  ? "Your auction is ready to be published"
                  : "Please complete all required fields before uploading"}
              </Typography>
            </Box>
          </Box>

          <Divider sx={{ my: 2 }} />

          {!formValid && (
            <Alert
              severity="warning"
              sx={{
                mb: 3,
                borderRadius: 2,
                "& .MuiAlert-icon": {
                  alignItems: "center",
                },
              }}
            >
              Please select an auction type and complete all required fields
            </Alert>
          )}
        </StepContainer>

        <Box sx={{ display: "flex", justifyContent: "space-between" }}>
          <ActionButton
            variant="outlined"
            color="primary"
            startIcon={<ArrowLeft size={18} />}
            onClick={handleBackToUpload}
          >
            Back to Photos
          </ActionButton>

          <ActionButton
            variant="contained"
            color="primary"
            endIcon={<UploadIcon size={18} />}
            onClick={handleUploadAuction}
            disabled={!formValid}
            size="large"
          >
            Publish Auction
          </ActionButton>
        </Box>
      </Box>
    </PageContainer>
  )
}

export default SetDetailsAuction;

