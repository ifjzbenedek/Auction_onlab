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
  CircularProgress,
  Snackbar,
} from "@mui/material"
import { styled } from "@mui/material/styles"
import { ArrowLeft, Check, Clock, Tag, Settings, UploadIcon, AlertCircle } from "lucide-react"
import AuctionDetailsForm from "../components/AuctionDetailsForm"
import type { AuctionBasicDTO, AuctionDetailsDTO } from "../types/auction"
import { auctionApi } from "../services/api"
import axios from "axios"

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
  const [auctionType, setAuctionType] = useState<"FIXED" | "EXTENDED" | null>(null)
  const [formValid, setFormValid] = useState(false)
  const [detailsData, setDetailsData] = useState<AuctionDetailsDTO>({
    name: "",
    status: "Brand new",
    condition: 50,
    category: "",
    minimumPrice: 0,
    minStep: 0,
    expiredDate: "",
    extraTime: "",
  })
  const [hasUploadData, setHasUploadData] = useState(false)
  const [uploadData, setUploadData] = useState<{ description: string; images: string[] }>({
    description: "",
    images: [],
  })
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  useEffect(() => {
    // Check if we have data from step 1
    const savedData = sessionStorage.getItem("auctionUploadData")
    if (!savedData) {
      // Redirect back to step 1 if no data
      navigate("/upload-auction")
      return
    }

    try {
      const parsedData = JSON.parse(savedData)
      setUploadData(parsedData)
      setHasUploadData(true)
    } catch (e) {
      console.error("Error parsing saved auction data", e)
      navigate("/upload-auction")
    }
  }, [navigate])

  const handleAuctionTypeSelect = (type: "FIXED" | "EXTENDED") => {
    setAuctionType(type)
    validateForm({ ...detailsData }, type)
  }

  const handleDetailsChange = (data: AuctionDetailsDTO) => {
    setDetailsData(data)
    validateForm(data, auctionType)
  }

  const validateForm = (data: AuctionDetailsDTO, type: "FIXED" | "EXTENDED" | null) => {
    const isValid =
      data.name.trim().length > 0 &&
      data.status.trim().length > 0 &&
      data.category.trim().length > 0 &&
      type !== null &&
      data.minimumPrice > 0 &&
      data.minStep > 0 &&
      data.expiredDate.trim().length > 0 &&
      (type !== "EXTENDED" || (data.extraTime && Number(data.extraTime) > 0))

    setFormValid(Boolean(isValid))
  }

  const handleBackToUpload = () => {
    navigate("/upload-auction")
  }

  const handleUploadAuction = async () => {
    try {
      // 1. Get current user
      const userResponse = await axios.get("/users/me");
      const currentUser = userResponse.data;
  
      // 2. Get category details
      const categoriesResponse = await auctionApi.getCategories();
      const selectedCategory = categoriesResponse.data.find(
        (cat: { categoryName: string }) => cat.categoryName.toLowerCase() === detailsData.category.toLowerCase()
      );
  
      if (!selectedCategory) {
        throw new Error("Selected category not found");
      }
  
      // 3. Prepare dates
      const expiredDate = new Date(detailsData.expiredDate).toISOString();
      const extraTime = auctionType === "EXTENDED" && detailsData.extraTime
        ? new Date(detailsData.extraTime).toISOString()
        : null;
  
      // 4. Create payload
      const auctionData: AuctionBasicDTO = {
        user: currentUser,
        category: selectedCategory,
        itemName: detailsData.name,
        minimumPrice: detailsData.minimumPrice,
        status: "PENDING", // Default status
        expiredDate: expiredDate,
        description: uploadData.description,
        type: auctionType as "FIXED" | "EXTENDED",
        extraTime: extraTime,
        itemState: detailsData.status,
        tags: null,
        minStep: detailsData.minStep,
        condition: detailsData.condition
      };

      // Call the API to create the auction
      await auctionApi.createAuction(auctionData)

      // Show success message
      setSuccessMessage("Auction created successfully!")

      // Clear session storage after submission
      sessionStorage.removeItem("auctionUploadData")

      // Navigate back to home page after successful upload
      setTimeout(() => {
        navigate("/")
      }, 1500)
    } catch (err: unknown) {
      console.error("Error creating auction:", err)

      // Check if this is an authentication error
      if (typeof err === "object" && err !== null && "isAuthError" in err && (err as { isAuthError?: boolean }).isAuthError) {
        setError("Authentication required. Please log in to create an auction.")
        // The redirect will be handled by the API interceptor
      } else {
        setError(err instanceof Error ? err.message : "Failed to create auction. Please try again.")
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleCloseSnackbar = () => {
    setError(null)
    setSuccessMessage(null)
  }

  if (!hasUploadData) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh" }}>
        <CircularProgress />
      </Box>
    )
  }

  return (
    <PageContainer>
      <Snackbar open={!!error} autoHideDuration={6000} onClose={handleCloseSnackbar}>
        <Alert onClose={handleCloseSnackbar} severity="error" sx={{ width: "100%" }}>
          {error}
        </Alert>
      </Snackbar>

      <Snackbar open={!!successMessage} autoHideDuration={3000} onClose={handleCloseSnackbar}>
        <Alert onClose={handleCloseSnackbar} severity="success" sx={{ width: "100%" }}>
          {successMessage}
        </Alert>
      </Snackbar>

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

        <StepContainer>
          <StepIcon>
            <Clock size={80} />
          </StepIcon>

          <StepTitle variant="h5">Choose Auction Type</StepTitle>

          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <AuctionTypeCard
                elevation={auctionType === "FIXED" ? 4 : 1}
                onClick={() => handleAuctionTypeSelect("FIXED")}
                sx={{
                  border: auctionType === "FIXED" ? `2px solid ${theme.palette.primary.main}` : "none",
                  backgroundColor:
                    auctionType === "FIXED" ? alpha(theme.palette.primary.main, 0.05) : theme.palette.background.paper,
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
                {auctionType === "FIXED" && (
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
                elevation={auctionType === "EXTENDED" ? 4 : 1}
                onClick={() => handleAuctionTypeSelect("EXTENDED")}
                sx={{
                  border: auctionType === "EXTENDED" ? `2px solid ${theme.palette.primary.main}` : "none",
                  backgroundColor:
                    auctionType === "EXTENDED"
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
                {auctionType === "EXTENDED" && (
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

          <AuctionDetailsForm onChange={handleDetailsChange} auctionType={auctionType} />
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
            disabled={isSubmitting}
          >
            Back to Photos
          </ActionButton>

          <ActionButton
            variant="contained"
            color="primary"
            endIcon={isSubmitting ? <CircularProgress size={18} color="inherit" /> : <UploadIcon size={18} />}
            onClick={handleUploadAuction}
            disabled={!formValid || isSubmitting}
            size="large"
          >
            {isSubmitting ? "Publishing..." : "Publish Auction"}
          </ActionButton>
        </Box>
      </Box>
    </PageContainer>
  )
}

export default SetDetailsAuction;