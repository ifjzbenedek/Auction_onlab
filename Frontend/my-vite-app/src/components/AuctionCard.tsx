"use client"

import type React from "react"
import { Box, Typography, Paper } from "@mui/material"
import { styled } from "@mui/material/styles"
import { X } from "lucide-react"
import { useNavigate } from "react-router-dom"

// Styled components
const StyledAuctionCard = styled(Paper)(({ theme }) => ({
  display: "flex",
  flexDirection: "column",
  borderRadius: theme.shape.borderRadius,
  boxShadow: "0 5px 15px rgba(0,0,0,0.1)",
  overflow: "hidden",
  height: "100%",
  cursor: "pointer",
  '&:hover': {
    transform: 'translateY(-5px) scale(1.02)',
    boxShadow: '0 12px 28px rgba(0,0,0,0.15)'
  },
  transition: 'all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1)'
}))

const CardImage = styled(Box)(({ theme }) => ({
  position: "relative",
  backgroundColor: theme.palette.grey[100],
  aspectRatio: "1/1",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
}))

const StatusBadge = styled(Box)<{ statuscolor: string }>(({ theme, statuscolor }) => ({
  position: "absolute",
  top: 0,
  right: 0,
  backgroundColor: statuscolor,
  color: "white",
  padding: theme.spacing(0.5),
  display: "flex",
  flexDirection: "column",
  alignItems: "center",
}))

const CardContent = styled(Box)(({ theme }) => ({
  padding: theme.spacing(1.5),
}))

const BidInfo = styled(Box)(({ theme }) => ({
  backgroundColor: theme.palette.primary.main,
  color: "white",
  padding: theme.spacing(1),
  marginTop: theme.spacing(0.5),
}))

export interface AuctionCardProps {
  id: number
  itemName: string
  createDate: string
  expiredDate: string
  lastBid: number | null
  status: string
  imageUrl?: string
}

const AuctionCard: React.FC<AuctionCardProps> = ({ id, itemName, expiredDate, lastBid, status, imageUrl }) => {
  const navigate = useNavigate()

  const getStatusColor = (status: string) => {
    switch (status) {
      case "ending":
        return "#f39c12" // orange
      case "upcoming":
        return "#2ecc71" // green
      case "finished":
        return "#e74c3c" // red
      default:
        return "#3498db" // default blue
    }
  }

  // Calculate time left
  const calculateTimeLeft = (expiredDate: string): string => {
    const now = new Date()
    const end = new Date(expiredDate)
    const diff = end.getTime() - now.getTime()

    if (diff <= 0) return "00:00:00"

    const hours = Math.floor(diff / (1000 * 60 * 60))
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    const seconds = Math.floor((diff % (1000 * 60)) / 1000)

    return `${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`
  }

  const timeLeft = calculateTimeLeft(expiredDate)

  const handleCardClick = () => {
    navigate(`/auction/${id}`)
  }

  return (
    <StyledAuctionCard onClick={handleCardClick}>
      <CardImage>
        {imageUrl ? (
          <Box
            component="img"
            src={imageUrl}
            alt={itemName}
            sx={{
              width: "100%",
              height: "100%",
              objectFit: "cover",
            }}
          />
        ) : (
          <X size={96} color="#666" />
        )}
        <StatusBadge statuscolor={getStatusColor(status)}>
          <Typography variant="caption" sx={{ textTransform: "capitalize" }}>
            {status}
          </Typography>
          <Typography variant="caption">{timeLeft}</Typography>
        </StatusBadge>
      </CardImage>
      <CardContent>
        <Typography sx={{ color: "#2c3e50", fontWeight: 500, mb: 0.5 }}>{itemName}</Typography>
        <BidInfo>
          <Box sx={{ display: "flex", justifyContent: "space-between" }}>
            <Typography variant="body2">Top Bid</Typography>
            <Typography variant="body2" fontWeight="bold">
              ${lastBid ? lastBid.toFixed(2) : "No bids"}
            </Typography>
          </Box>
        </BidInfo>
      </CardContent>
    </StyledAuctionCard>
  )
}

export default AuctionCard;

