import React from "react";
import { useNavigate } from "react-router-dom";
import {
  Card,
  CardContent,
  Typography,
  Box,
  Chip
} from "@mui/material";
import { AuctionCardDTO } from "../types/auction";
import TimeDisplay from './TimeDisplay';

const AuctionCard: React.FC<AuctionCardDTO> = ({ 
  id, 
  itemName, 
  expiredDate, 
  lastBid, 
  status, 
  imageUrl,
  images 
}) => {
  const navigate = useNavigate();

  const handleCardClick = () => {
    navigate(`/auction/${id}`);
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('hu-HU').format(price);
  };

  // Használj imageUrl-t vagy az első képet az images tömbből
  const displayImage = imageUrl || (images && images.length > 0 ? images[0] : null);

  const getStatusColor = (status: string): 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' => {
    switch (status?.toUpperCase()) {
      case 'ACTIVE':
        return 'success';
      case 'CLOSED':
        return 'error';
      case 'PENDING':
        return 'warning';
      default:
        return 'default';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status?.toUpperCase()) {
      case 'ACTIVE':
        return 'Ongoing';
      case 'CLOSED':
        return 'Finished';
      case 'PENDING':
        return 'Upcoming';
      default:
        return status || 'Ismeretlen';
    }
  };

  return (
    <Card 
      sx={{ 
        height: "100%", 
        display: "flex", 
        flexDirection: "column",
        cursor: "pointer",
        transition: "transform 0.2s, box-shadow 0.2s",
        "&:hover": {
          transform: "translateY(-4px)",
          boxShadow: 3
        }
      }}
      onClick={handleCardClick}
    >
      {/* Image section */}
      <Box sx={{ position: "relative", height: 200, overflow: "hidden" }}>
        <img
          src={displayImage || "/placeholder-image.jpg"}
          alt={itemName}
          style={{
            width: "100%",
            height: "100%",
            objectFit: "cover"
          }}
          onError={(e) => {
            const target = e.target as HTMLImageElement;
            target.src = "/placeholder-image.jpg";
          }}
        />
      </Box>
      
      <CardContent sx={{ flexGrow: 1, p: 2 }}>
        {/* Auction title */}
        <Typography 
          variant="h6" 
          sx={{ 
            fontWeight: "bold", 
            mb: 1,
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap"
          }}
        >
          {itemName}
        </Typography>

        {/* Time display */}
        <Box sx={{ mb: 2 }}>
          <TimeDisplay 
            expiredDate={expiredDate} 
            variant="compact" 
            size="small"
          />
        </Box>

        {/* Price */}
        <Box sx={{ mb: 2 }}>
          <Typography variant="body2" color="text.secondary">
            Aktuális ár:
          </Typography>
          <Typography variant="h6" color="primary" sx={{ fontWeight: "bold" }}>
            {lastBid ? `${formatPrice(lastBid)} Ft` : "Nincs licit"}
          </Typography>
        </Box>

        {/* Status chip */}
        <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Chip
            label={getStatusLabel(status)}
            size="small"
            color={getStatusColor(status)}
            sx={{ fontWeight: "bold" }}
          />
        </Box>
      </CardContent>
    </Card>
  );
};

export default AuctionCard;