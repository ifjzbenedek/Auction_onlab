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
import { ArrowLeft, Check, Clock, Tag, Settings, Upload as UploadIconLucide, AlertCircle } from "lucide-react"
import AuctionDetailsForm from "../components/AuctionDetailsForm"
import type { UserBasicDTO } from "../types/user";
import type { CategoryDTO } from "../types/category";
import type { AuctionBasicDTO, AuctionDetailsDTO } from "../types/auction"
import { auctionApi, imageApi } from "../services/api"
import axios from "axios"
import { useAuctionCreation } from "../contexts/AuctionCreationContext"; // Importáljuk a kontextus hook-ot

// Styled components
const PageContainer = styled(Box)(({ theme }) => ({
  backgroundColor: theme.palette.background.default,
  minHeight: "100vh",
  paddingBottom: theme.spacing(5),
}));

const StepContainer = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(4),
  borderRadius: theme.shape.borderRadius * 2,
  boxShadow: "0 8px 30px rgba(0, 0, 0, 0.06)",
  marginBottom: theme.spacing(4),
  position: "relative",
  overflow: "hidden",
}));

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
}));

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
}));

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
}));

const StepIcon = styled(Box)(({ theme }) => ({
  position: "absolute",
  top: -15,
  right: -15,
  width: 80,
  height: 80,
  opacity: 0.07,
  zIndex: 0,
  color: theme.palette.primary.main,
}));


const SetDetailsAuction: React.FC = () => {
  const navigate = useNavigate()
  const theme = useTheme()
  const { auctionData, clearAuctionData } = useAuctionCreation(); // Kontextus használata

  const [auctionType, setAuctionType] = useState<"FIXED" | "EXTENDED" | null>(null)
  const [formValid, setFormValid] = useState(false)
  const [detailsData, setDetailsData] = useState<AuctionDetailsDTO>({
    name: "",
    status: "Brand new", // Default item status
    condition: 50,
    category: "",
    minimumPrice: 0,
    minStep: 0,
    expiredDate: "",
    extraTime: "",
  })
  
  const [filesForUpload, setFilesForUpload] = useState<File[]>([]);
  const [descriptionFromContext, setDescriptionFromContext] = useState<string>("");

  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)
  const [hasLoadedInitialData, setHasLoadedInitialData] = useState(false);
  const [, setCategories] = useState<CategoryDTO[]>([]); // Új állapot a kategóriák tárolására

  // 1. useEffect: Adatok szinkronizálása és a betöltött állapot jelzése
  useEffect(() => {
    // Ez a hook az auctionData változásakor lefut.
    // Frissíti a helyi állapotokat a kontextusból származó adatokkal.
    setDescriptionFromContext(auctionData.description);
    setFilesForUpload(auctionData.images.map(uploadedImage => uploadedImage.file));
    
    // Jelzi, hogy az első adatfeldolgozási kísérlet/renderelés megtörtént.
    // Ezt csak egyszer kellene true-ra állítani, de ha az auctionData később is változik,
    // és ez a hook újra lefut, a setHasLoadedInitialData(true) nem okoz problémát, ha már true.
    // Azonban, hogy elkerüljük a felesleges settert, ellenőrizhetjük:
    if (!hasLoadedInitialData) {
        setHasLoadedInitialData(true);
    }
  }, [auctionData, hasLoadedInitialData]); // hasLoadedInitialData itt maradhat, hogy biztosítsa a flag beállítását, de óvatosan

  // 2. useEffect: Visszanavigálási logika
  useEffect(() => {
    // Ez a hook akkor fut le, ha a hasLoadedInitialData vagy az auctionData megváltozik.
    // Csak akkor navigálunk vissza, ha az "adatbetöltési kísérlet" (hasLoadedInitialData = true) már megtörtént,
    // de az auctionData még mindig nem tartalmazza a szükséges adatokat.
    if (hasLoadedInitialData) {
      if (!auctionData.description && auctionData.images.length === 0) {
        console.warn("No auction creation data found in context after initial load attempt, redirecting to upload step.");
        navigate("/upload-auction");
      }
    }
  }, [hasLoadedInitialData, auctionData, navigate]);

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
      (type !== "EXTENDED" || (data.extraTime && Number(data.extraTime) > 0 && !isNaN(Number(data.extraTime))))

    setFormValid(Boolean(isValid))
  }

  const handleBackToUpload = () => {
    // A kontextus miatt az adatok megmaradnak az előző oldalon
    navigate("/upload-auction")
  }

  const handleUploadAuction = async () => {
    if (!formValid) {
      setError("Please complete all required fields correctly.");
      return;
    }
    if (!descriptionFromContext && filesForUpload.length === 0) {
      setError("Missing description or images from the previous step. Please go back.");
      return;
    }
    setIsSubmitting(true)
    setError(null)
    setSuccessMessage(null)

    try {
      const userResponse = await axios.get<UserBasicDTO>("/users/me");
      const currentUser = userResponse.data;

      if (!currentUser || !currentUser.id || !currentUser.userName || !currentUser.emailAddress) { 
        throw new Error("Could not fetch current user details or required user fields (ID, userName, emailAddress) are missing.");
      }

      const categoriesResponse = await auctionApi.getCategories();
      const selectedCategoryObject = categoriesResponse.data.find(
        (cat: CategoryDTO) => cat.categoryName?.toLowerCase() === detailsData.category.toLowerCase()
      );

      if (!selectedCategoryObject || !selectedCategoryObject.id) {
        throw new Error("Selected category not found or category ID is missing.");
      }

      // ⭐ A felhasználó által kiválasztott dátumokat ugyanúgy kezeljük
      // Nem konvertálunk UTC-be, hanem úgy küldjük, ahogy a felhasználó beállította
      const expiredDateFormatted = detailsData.expiredDate; // Ne konvertáljuk!
      const startDateFormatted = detailsData.startDate || null;
      const extraTimeValue = auctionType === "EXTENDED" && detailsData.extraTime
        ? detailsData.extraTime.toString()
        : null;

      const auctionPayload: AuctionBasicDTO = {
        user: { 
          id: currentUser.id, 
          userName: currentUser.userName,
          emailAddress: currentUser.emailAddress,
          phoneNumber: currentUser.phoneNumber 
        } as UserBasicDTO,
        category: { 
          id: selectedCategoryObject.id, 
          categoryName: selectedCategoryObject.categoryName 
        } as CategoryDTO,
        itemName: detailsData.name,
        minimumPrice: Number(detailsData.minimumPrice),
        status: "PENDING", 
        // ⭐ createDate: TÖRÖLVE - backend automatikusan beállítja
        expiredDate: expiredDateFormatted,
        startDate: startDateFormatted, 
        description: descriptionFromContext,
        type: auctionType!,
        extraTime: extraTimeValue,
        itemState: detailsData.status, 
        tags: null, 
        minStep: Number(detailsData.minStep),
        condition: Number(detailsData.condition), 
      };

      console.log('🔍 Submitting auction payload:', auctionPayload); // Debug log

      const createdAuctionResponse = await auctionApi.createAuction(auctionPayload);
      const createdAuction = createdAuctionResponse.data;
      
      if (!createdAuction || typeof createdAuction.id !== 'number') {
        throw new Error("Auction created, but its ID was not returned or is invalid from the server.");
      }
      
      const auctionId: number = createdAuction.id;

      // Image upload logic...
      if (filesForUpload && filesForUpload.length > 0) {
        const imageFormData = new FormData();
        filesForUpload.forEach((fileObject) => {
          imageFormData.append('files', fileObject, fileObject.name); 
        });
        
        await imageApi.uploadAuctionImages(auctionId, imageFormData);
        setSuccessMessage("Auction created and images uploaded successfully!");
      } else {
        setSuccessMessage("Auction created successfully (no new images to upload)!");
      }

      clearAuctionData();
      setFilesForUpload([]);

      setTimeout(() => {
        navigate(`/auction/${auctionId}`);
      }, 2000);

    } catch (err: unknown) {
      console.error("Error creating auction:", err);
      if (axios.isAxiosError(err)) {
        if (err.response) {
          const apiErrorMessage = (err.response.data as { message?: string })?.message || 
                                 (typeof err.response.data === 'string' ? err.response.data : JSON.stringify(err.response.data)) || 
                                 err.message;
          setError(`Failed to create auction: ${apiErrorMessage}`);
        } else if (err.request) {
          setError("Failed to create auction: No response from server. Please check your connection.");
        } else {
          setError(`Failed to create auction: ${err.message}`);
        }
      } else if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("An unknown error occurred while creating the auction. Please try again.");
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCloseSnackbar = () => {
    setError(null);
    setSuccessMessage(null);
  };


  // Check for auction data and navigate if needed
  useEffect(() => {
    if (!filesForUpload.length && !auctionData.description && !auctionType) {
      console.log("No auction creation data found in context after initial load attempt, redirecting to upload step.");
      navigate('/upload-auction');
    }
  }, [filesForUpload.length, auctionData.description, auctionType, navigate]);

  // Fetch categories once on component mount
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await auctionApi.getCategories();
        setCategories(response.data);
      } catch (error) {
        console.error('Error fetching categories:', error);
        setError('Failed to load categories');
      }
    };

    fetchCategories();
  }, []); // Empty dependency array - fetch categories only once

  if (!hasLoadedInitialData) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh" }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <PageContainer>
      <Snackbar 
        open={!!error} 
        autoHideDuration={6000} 
        onClose={handleCloseSnackbar} 
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert onClose={handleCloseSnackbar} severity="error" sx={{ width: "100%" }} variant="filled">
          {error}
        </Alert>
      </Snackbar>

      <Snackbar 
        open={!!successMessage} 
        autoHideDuration={3000} 
        onClose={handleCloseSnackbar} 
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert onClose={handleCloseSnackbar} severity="success" sx={{ width: "100%" }} variant="filled">
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

        {/* Auction Type Selection */}
        <StepContainer>
          <StepIcon>
            <Clock size={80} />
          </StepIcon>
          <StepTitle variant="h5">Choose Auction Type</StepTitle>
          <Grid container spacing={3} sx={{ mb: 3 }}>
            <Grid item xs={12} md={6}>
              <AuctionTypeCard
                elevation={auctionType === "FIXED" ? 4 : 1}
                onClick={() => handleAuctionTypeSelect("FIXED")}
                sx={{
                  border: auctionType === "FIXED" ? `2px solid ${theme.palette.primary.main}` : "none",
                  backgroundColor:
                    auctionType === "FIXED" ? alpha(theme.palette.primary.main, 0.05) : theme.palette.background.paper,
                  minHeight: 220,
                  pb: 3,
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
                  Auction ends at a specific time regardless of bidding activity.
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
                  minHeight: 220,
                  pb: 3,
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
                  Time extends automatically when bids are placed near the end.
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
              sx={{ mt: 5, borderRadius: 2, "& .MuiAlert-icon": { alignItems: "center" } }}
            >
              Please select an auction type to continue.
            </Alert>
          )}
        </StepContainer>

        {/* Item Details Form */}
        <StepContainer>
          <StepIcon>
            <Tag size={80} />
          </StepIcon>
          <StepTitle variant="h5">Item Details</StepTitle>
          <AuctionDetailsForm onChange={handleDetailsChange} auctionType={auctionType} initialData={detailsData} />
        </StepContainer>

        {/* Summary and Actions */}
        <StepContainer>
           <Box sx={{ display: "flex", alignItems: "center", mb: 3 }}>
            <Box
              sx={{
                width: 40,
                height: 40,
                borderRadius: "50%",
                backgroundColor: formValid ? alpha(theme.palette.success.main, 0.1) : alpha(theme.palette.warning.main, 0.1),
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                mr: 2,
                color: formValid ? "success.main" : "warning.main",
              }}
            >
              {formValid ? <Check size={24} /> : <AlertCircle size={24} />}
            </Box>
            <Box>
              <Typography variant="h6" fontWeight="medium">
                {formValid ? "Ready to Publish" : "Complete Required Fields"}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {formValid
                  ? "Your auction is ready to be published."
                  : "Please select an auction type and fill in all required item details."}
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
                "& .MuiAlert-icon": { alignItems: "center" },
              }}
            >
              Please select an auction type and complete all required fields.
            </Alert>
          )}
        </StepContainer>
        
        <Box sx={{ display: "flex", justifyContent: "space-between", mt: 4 }}>
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
            endIcon={isSubmitting ? <CircularProgress size={18} color="inherit" /> : <UploadIconLucide size={18} />}
            onClick={handleUploadAuction}
            disabled={!formValid || isSubmitting}
            size="large"
          >
            {isSubmitting ? "Publishing..." : "Publish Auction"}
          </ActionButton>
        </Box>
      </Box>
    </PageContainer>
  );
};

export default SetDetailsAuction;
