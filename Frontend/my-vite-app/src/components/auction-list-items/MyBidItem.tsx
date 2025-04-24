"use client"

import type React from "react"
import { Box, Typography, TableRow, TableCell, IconButton, Tooltip } from "@mui/material"
import { Eye } from "lucide-react"

interface MyBidItemProps {
  auction: {
    id: number
    itemName: string
    remainingTime: string
    highestBid: number
    yourBid?: number
  }
  onView: (id: number) => void
}

const MyBidItem: React.FC<MyBidItemProps> = ({ auction, onView }) => {
  return (
    <TableRow hover>
      <TableCell>
        <Typography variant="body1" fontWeight="medium" sx={{ cursor: "pointer" }} onClick={() => onView(auction.id)}>
          {auction.itemName}
        </Typography>
      </TableCell>
      <TableCell>{auction.remainingTime}</TableCell>
      <TableCell>
        ${auction.yourBid ? auction.yourBid.toFixed(2) : auction.highestBid.toFixed(2)}
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

export default MyBidItem;