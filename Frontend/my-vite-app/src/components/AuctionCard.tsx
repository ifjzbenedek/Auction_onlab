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
import { calculateAuctionStatus, getStatusColor, getStatusLabel } from '../utils/auctionStatusUtils';

const AuctionCard: React.FC<AuctionCardDTO> = ({ 
  id, 
  itemName, 
  expiredDate,
  lastBid, 
  imageUrl,
  images,
  startDate
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

  // Kiszámoljuk az aktuális státuszt az időpontok alapján
  const { status: currentStatus } = calculateAuctionStatus(startDate, expiredDate);

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
            startDate={startDate}
            variant="compact" 
            size="small"
          />
        </Box>

        {/* Price */}
        <Box sx={{ mb: 2 }}>
          <Typography variant="body2" color="text.secondary">
            Actual price:
          </Typography>
          <Typography variant="h6" color="primary" sx={{ fontWeight: "bold" }}>
            {lastBid ? `$${formatPrice(lastBid)}` : "No bid yet"}
          </Typography>
        </Box>

        {/* Status chip */}
        <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Chip
            label={getStatusLabel(currentStatus)}
            size="small"
            color={getStatusColor(currentStatus)}
            sx={{ fontWeight: "bold" }}
          />
        </Box>
      </CardContent>
    </Card>
  );
};

export default AuctionCard;