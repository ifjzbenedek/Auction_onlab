"use client"

import type React from "react"
import { useState, useRef, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import {
  Box,
  Typography,
  Button,
  TextField,
  Paper,
  Grid,
  IconButton,
  Stepper,
  Step,
  StepLabel,
  Alert,
  CircularProgress,
  useTheme,
  alpha,
  Chip,
} from "@mui/material"
import { styled } from "@mui/material/styles"
import { Upload, Plus, ArrowRight, Sparkles, X, ImageIcon, FileText, Camera, Info } from "lucide-react"
import auctionApi from "../services/api.ts"
import { useAuctionCreation } from "../contexts/AuctionCreationContext";

interface UploadedImage {
  id: string;
  file: File;
  preview: string;
}

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

const UploadBox = styled(Box)(({ theme }) => ({
  border: `2px dashed ${alpha(theme.palette.primary.main, 0.3)}`,
  borderRadius: theme.shape.borderRadius * 2,
  padding: theme.spacing(3),
  textAlign: "center",
  backgroundColor: alpha(theme.palette.primary.main, 0.03),
  transition: "all 0.2s ease",
  cursor: "pointer",
  "&:hover": {
    backgroundColor: alpha(theme.palette.primary.main, 0.05),
    borderColor: alpha(theme.palette.primary.main, 0.5),
  },
}));

const ImagePreviewContainer = styled(Box)(({ theme }) => ({
  border: `1px solid ${alpha(theme.palette.divider, 0.6)}`,
  borderRadius: theme.shape.borderRadius,
  padding: theme.spacing(2),
}));

const ImagePreview = styled(Box)(({ theme }) => ({
  position: "relative",
  width: "100%",
  height: 100,
  borderRadius: theme.shape.borderRadius,
  overflow: "hidden",
}));

const AddImageBox = styled(Box)(({ theme }) => ({
  width: "100%",
  height: 100,
  border: `1px dashed ${alpha(theme.palette.primary.main, 0.4)}`,
  borderRadius: theme.shape.borderRadius,
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  cursor: "pointer",
  backgroundColor: alpha(theme.palette.primary.main, 0.03),
  transition: "all 0.2s ease",
  "&:hover": {
    backgroundColor: alpha(theme.palette.primary.main, 0.05),
    borderColor: alpha(theme.palette.primary.main, 0.6),
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


const UploadAuction: React.FC = () => {
  const navigate = useNavigate()
  const theme = useTheme()
  const { 
    auctionData, 
    setAuctionDescription, 
    setAuctionImages, 
    setAuctionCategory, 
    setAuctionItemState, 
    setAuctionCondition 
  } = useAuctionCreation();

  // Helyi állapotok a komponens számára, inicializálva a kontextusból
  const [localUploadedImages, setLocalUploadedImages] = useState<UploadedImage[]>(auctionData.images);
  const [localDescription, setLocalDescription] = useState<string>(auctionData.description);

  const [isGeneratingDescription, setIsGeneratingDescription] = useState(false)
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [isProcessingContinue, setIsProcessingContinue] = useState(false) // Átnevezve isUploading-ról

  // Szinkronizáljuk a kontextust, ha a helyi állapot változik
  useEffect(() => {
    setAuctionDescription(localDescription);
  }, [localDescription, setAuctionDescription]);

  useEffect(() => {
    setAuctionImages(localUploadedImages);
  }, [localUploadedImages, setAuctionImages]);



  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      const newFiles = Array.from(event.target.files)

      const newImages = newFiles.map((file) => ({
        id: Math.random().toString(36).substring(2, 9), // Egyszerű egyedi ID generálás
        file,
        preview: URL.createObjectURL(file),
      }))

      setLocalUploadedImages(prevImages => [...prevImages, ...newImages].slice(0, 10)); // Max 10 kép
    }
  }

  const handleRemoveImage = (id: string) => {
    const updatedImages = localUploadedImages.filter((img) => img.id !== id)
    setLocalUploadedImages(updatedImages);
    // Az useEffect frissíti a kontextust
  }

  const handleAutogenerateDescription = async () => {
    if (localUploadedImages.length === 0) return;
  
    setIsGeneratingDescription(true);
  
    try {
      const formData = new FormData();
      localUploadedImages.forEach((img) => {
        formData.append('images', img.file); // Csak a File objektumot küldjük
      });
  
       const response = await auctionApi.post('/auctions/generate-description', formData);
      
      console.log('Raw response from AI:', response.data);
      
      if (response.data) {
        // Handle the new JSON structure from AI
        if (typeof response.data === 'object') {
          const generatedDesc = response.data.description || '';
          const category = response.data.category || '';
          const itemState = response.data.itemState || 'Brand new';
          const condition = response.data.condition || 50;
          
          console.log('Parsed AI data:', { generatedDesc, category, itemState, condition });
          
          setLocalDescription(generatedDesc);
          setAuctionCategory(category);
          setAuctionItemState(itemState);
          setAuctionCondition(condition);
        } else if (typeof response.data === 'string') {
          // Fallback for old format
          setLocalDescription(response.data);
        }
      }
    } catch (error) {
      console.error("Error generating description:", error);
      alert("Failed to generate description. Please check your connection or try again later.");
    } finally {
      setIsGeneratingDescription(false);
    }
  };

  const handleContinue = async () => {
    if (localUploadedImages.length < 1) {
      alert("Please upload at least one image.");
      return;
    }
    if (localDescription.trim().length === 0) {
      alert("Please provide a description for the item.");
      return;
    }
    
    // A kontextus már frissítve van az useEffect hook-ok által
    setIsProcessingContinue(true); 
    // Nincs szükség sessionStorage-re vagy explicit adatküldésre itt a navigáció előtt
    navigate("/set-details-auction");
    // setIsProcessingContinue(false) itt nem szükséges, mert navigálunk, 
    // de ha lenne async művelet, akkor a finally blokkban kellene
  };

  return (
    <PageContainer>
      <Box sx={{ maxWidth: 1000, mx: "auto", px: { xs: 2, md: 4 }, py: 4 }}>
        <Box sx={{ mb: 4, display: "flex", alignItems: "center", justifyContent: "space-between" }}>
          <Typography variant="h4" fontWeight="bold" color="text.primary">
            Create New Auction
          </Typography>
          <Chip
            label="Step 1 of 2"
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
          activeStep={0}
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

        {/* Step 1: Upload photos */}
        <StepContainer>
          <StepIcon>
            <Camera size={80} />
          </StepIcon>

          <StepTitle variant="h5">Upload Photos</StepTitle>

          <UploadBox onClick={() => fileInputRef.current?.click()}>
            <Box
              sx={{
                width: "100%",
                height: 200,
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                justifyContent: "center",
                mb: 2,
              }}
            >
              <ImageIcon size={60} color={alpha(theme.palette.primary.main, 0.7)} />
              <Typography variant="h6" color="primary" sx={{ mt: 2, fontWeight: "medium" }}>
                Drag & Drop your images here
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                or click to browse your files (max 10)
              </Typography>
            </Box>

            <input
              type="file"
              multiple
              accept="image/*"
              style={{ display: "none" }}
              ref={fileInputRef}
              onChange={handleFileSelect}
            />

            <ActionButton
              variant="outlined"
              color="primary"
              startIcon={<Upload size={18} />}
              onClick={(e) => {
                e.stopPropagation() // Megakadályozza, hogy a UploadBox onClick-je is lefusson
                fileInputRef.current?.click()
              }}
            >
              Select Files
            </ActionButton>
          </UploadBox>

          <Box sx={{ mt: 4 }}>
            <Typography variant="h6" fontWeight="medium" sx={{ mb: 2 }}>
              Uploaded Photos ({localUploadedImages.length})
            </Typography>

            <ImagePreviewContainer>
              <Typography variant="body2" sx={{ color: "text.secondary", mb: 2 }}>
                <Info size={14} style={{ verticalAlign: "middle", marginRight: 4 }} />
                You need to upload at least 1 photo of your item. High-quality images from multiple angles will attract
                more bidders.
              </Typography>

              <Grid container spacing={2}>
                {localUploadedImages.map((img) => (
                  <Grid item xs={6} sm={4} md={3} key={img.id}>
                    <ImagePreview>
                      <Box
                        component="img"
                        src={img.preview} // preview URL használata
                        alt="Uploaded preview"
                        sx={{
                          width: "100%",
                          height: "100%",
                          objectFit: "cover",
                        }}
                      />
                      <IconButton
                        size="small"
                        onClick={() => handleRemoveImage(img.id)}
                        sx={{
                          position: "absolute",
                          top: 4,
                          right: 4,
                          bgcolor: "rgba(255,255,255,0.9)",
                          color: theme.palette.error.main,
                          "&:hover": {
                            bgcolor: "rgba(255,255,255,1)",
                          },
                          width: 24,
                          height: 24,
                        }}
                      >
                        <X size={14} />
                      </IconButton>
                    </ImagePreview>
                  </Grid>
                ))}

                {localUploadedImages.length < 10 && (
                  <Grid item xs={6} sm={4} md={3}>
                    <AddImageBox onClick={() => fileInputRef.current?.click()}>
                      <Plus size={24} color={alpha(theme.palette.primary.main, 0.7)} />
                    </AddImageBox>
                  </Grid>
                )}
              </Grid>

              {localUploadedImages.length < 1 && (
                <Alert
                  severity="warning"
                  sx={{
                    mt: 2,
                    borderRadius: 2,
                    "& .MuiAlert-icon": {
                      alignItems: "center",
                    },
                  }}
                >
                  Please upload at least 1 photo to continue
                </Alert>
              )}
            </ImagePreviewContainer>
          </Box>
        </StepContainer>

        {/* Step 2: Description */}
        <StepContainer>
          <StepIcon>
            <FileText size={80} />
          </StepIcon>

          <StepTitle variant="h5">Item Description</StepTitle>

          <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
            <ActionButton
              variant="contained"
              color="primary"
              startIcon={
                isGeneratingDescription ? <CircularProgress size={18} color="inherit" /> : <Sparkles size={18} />
              }
              onClick={handleAutogenerateDescription}
              disabled={isGeneratingDescription || localUploadedImages.length === 0}
              sx={{ mr: 2 }}
            >
              {isGeneratingDescription ? "Generating..." : "Auto-Generate Description"}
            </ActionButton>

            <Typography variant="body2" color="text.secondary">
              Let AI create a description based on your photos
            </Typography>
          </Box>

          <TextField
            multiline
            rows={6}
            fullWidth
            value={localDescription} 
            onChange={(e) => setLocalDescription(e.target.value)} 
            placeholder="Describe your item in detail. Include condition, features, history, and any other relevant information that would interest potential buyers."
            sx={{
              "& .MuiOutlinedInput-root": {
                borderRadius: 2,
              },
            }}
          />

          {localDescription.trim().length === 0 && (
            <Alert
              severity="info"
              sx={{
                mt: 2,
                borderRadius: 2,
                "& .MuiAlert-icon": {
                  alignItems: "center",
                },
              }}
            >
              A detailed description helps buyers understand what you're selling and increases your chances of a
              successful auction
            </Alert>
          )}
        </StepContainer>

        <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
          <ActionButton
            variant="contained"
            color="primary"
            endIcon={isProcessingContinue ? <CircularProgress size={18} color="inherit" /> : <ArrowRight size={18} />}
            onClick={handleContinue}
            disabled={localUploadedImages.length < 1 || localDescription.trim().length === 0 || isProcessingContinue}
            size="large"
          >
            {isProcessingContinue ? "Processing..." : "Continue to Details"}
          </ActionButton>
        </Box>
      </Box>
    </PageContainer>
  )
}

export default UploadAuction;