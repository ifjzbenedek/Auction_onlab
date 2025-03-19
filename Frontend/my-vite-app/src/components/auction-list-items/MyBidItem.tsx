"use client"

import type React from "react"
import { Box, Typography, TableRow, TableCell, IconButton, Tooltip } from "@mui/material"
import { Eye } from "lucide-react"

interface MyBidItemProps {
  auction: {
    id: number
    name: string
    remainingTime: string
    highestBid: number
    image: string
    yourBid?: number
  }
  onView: (id: number) => void
}

const MyBidItem: React.FC<MyBidItemProps> = ({ auction, onView }) => {
  return (
    <TableRow hover>
      <TableCell>
        <Box
          component="img"
          src={auction.image}
          alt={auction.name}
          sx={{
            width: 60,
            height: 60,
            objectFit: "cover",
            border: "1px solid #eee",
          }}
        />
      </TableCell>
      <TableCell>
        <Typography variant="body1" fontWeight="medium">
          {auction.name}
        </Typography>
      </TableCell>
      <TableCell>{auction.remainingTime}</TableCell>
      <TableCell>
        ${auction.yourBid || auction.highestBid}
        {auction.yourBid && auction.yourBid < auction.highestBid && (
          <Typography
            variant="caption"
            sx={{
              display: "block",
              color: "#e74c3c",
              fontWeight: "medium",
            }}
          >
            (Outbid)
          </Typography>
        )}
      </TableCell>
      <TableCell align="right">
        <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 1 }}>
          <Tooltip title="View">
            <IconButton size="small" onClick={() => onView(auction.id)} sx={{ color: "#3498db" }}>
              <Eye size={18} />
            </IconButton>
          </Tooltip>
        </Box>
      </TableCell>
    </TableRow>
  )
}

export default MyBidItem

